package io.deephaven.parquet.compress;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.compress.CodecPool;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionCodecFactory;
import org.apache.hadoop.io.compress.CompressionInputStream;
import org.apache.hadoop.io.compress.Decompressor;
import org.apache.parquet.bytes.BytesInput;
import org.apache.parquet.hadoop.metadata.CompressionCodecName;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Deephaven flavor of the Hadoop/Parquet CompressionCodec factory, offering support for picking codecs from
 * configuration or from the classpath (via service loaders), while still offering the ability to get a
 * CompressionCodecName enum value having loaded the codec in this way.
 */
public class DeephavenCodecFactory {

    // Default codecs to list in the configuration rather than rely on the classloader
    private static final Set<String> DEFAULT_CODECS = Set.of(
            // Use the aircompressor codec for snappy by default - this is implemented in Java, rather than using
            // the native xerial implementation.
            "io.airlift.compress.snappy.SnappyCodec");
    private static final List<Class<?>> CODECS = io.deephaven.configuration.Configuration.getInstance()
            .getStringSetFromPropertyWithDefault("DeephavenCodecFactory.codecs", DEFAULT_CODECS).stream()
            .map((String className) -> {
                try {
                    return Class.forName(className);
                } catch (ClassNotFoundException e) {
                    throw new IllegalStateException("Can't find codec with name " + className);
                }
            }).collect(Collectors.toList());

    private static volatile DeephavenCodecFactory INSTANCE;

    public static synchronized void setInstance(DeephavenCodecFactory factory) {
        if (INSTANCE != null) {
            throw new IllegalStateException("Can't assign an instance when one is already set");
        }
        INSTANCE = factory;
    }

    public static DeephavenCodecFactory getInstance() {
        if (INSTANCE == null) {
            synchronized (DeephavenCodecFactory.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DeephavenCodecFactory(CODECS);
                }
            }
        }
        return INSTANCE;
    }

    public static class CodecWrappingCompressor implements Compressor {
        private final CompressionCodec compressionCodec;

        private CodecWrappingCompressor(CompressionCodec compressionCodec) {
            this.compressionCodec = compressionCodec;
        }

        @Override
        public OutputStream compress(OutputStream os) throws IOException {
            return compressionCodec.createOutputStream(os);
        }

        @Override
        public InputStream decompress(InputStream is) throws IOException {
            return compressionCodec.createInputStream(is);
        }

        @Override
        public CompressionCodecName getCodecName() {
            return Stream.of(CompressionCodecName.values())
                    .filter(codec -> compressionCodec.getDefaultExtension().equals(codec.getExtension()))
                    .findAny().get();
        }

        @Override
        public BytesInput decompress(InputStream inputStream, int compressedSize, int uncompressedSize)
                throws IOException {
            Decompressor decompressor = CodecPool.getDecompressor(compressionCodec);
            if (decompressor != null) {
                // It is permitted for a decompressor to be null, otherwise we want to reset() it to
                // be ready for a new stream.
                // Note that this strictly shouldn't be necessary, since returnDecompressor will reset
                // it as well, but this is the pattern copied from CodecFactory.decompress.
                decompressor.reset();
            }

            try {
                // Note that we don't close this, we assume the caller will close their input stream when ready,
                // and this won't need to be closed.
                InputStream buffered = IOUtils.buffer(inputStream, compressedSize);
                CompressionInputStream decompressed = compressionCodec.createInputStream(buffered, decompressor);
                return BytesInput.copy(BytesInput.from(decompressed, uncompressedSize));
            } finally {
                // Always return it, the pool will decide if it should be reused or not.
                // CodecFactory has no logic around only returning after successful streams,
                // and the instance appears to leak otherwise.
                CodecPool.returnDecompressor(decompressor);
            }
        }
    }

    private static Configuration configurationWithCodecClasses(List<Class<?>> codecClasses) {
        Configuration conf = new Configuration();
        // noinspection unchecked, rawtypes
        CompressionCodecFactory.setCodecClasses(conf, (List) codecClasses);
        return conf;
    }

    private final CompressionCodecFactory compressionCodecFactory;

    public DeephavenCodecFactory(List<Class<?>> codecClasses) {
        this(configurationWithCodecClasses(codecClasses));
    }

    public DeephavenCodecFactory(Configuration configuration) {
        compressionCodecFactory = new CompressionCodecFactory(configuration);
    }

    /**
     * Returns a compressor with the given codec name. Do not use this to get a "no-op" codec, instead use
     * {@link Compressor#PASSTHRU}. Names are identified using the {@link CompressionCodecFactory} rules (roughly, the
     * first word in the class's name).
     *
     * @param codecName the name of the codec to search for.
     * @return a compressor instance with a name matching the given codec.
     */
    public Compressor getByName(String codecName) {
        CompressionCodec codec = compressionCodecFactory.getCodecByName(codecName);
        if (codec == null) {
            throw new IllegalArgumentException("Failed to find a compression codec with name " + codecName);
        }
        return new CodecWrappingCompressor(codec);
    }
}
