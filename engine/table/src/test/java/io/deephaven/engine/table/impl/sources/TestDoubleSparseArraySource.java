/* ---------------------------------------------------------------------------------------------------------------------
 * AUTO-GENERATED CLASS - DO NOT EDIT MANUALLY - for any changes edit TestCharacterSparseArraySource and regenerate
 * ------------------------------------------------------------------------------------------------------------------ */
package io.deephaven.engine.table.impl.sources;

import io.deephaven.engine.table.ChunkSink;
import io.deephaven.engine.table.ChunkSource;
import io.deephaven.engine.table.impl.DefaultGetContext;
import io.deephaven.engine.table.impl.TestSourceSink;
import io.deephaven.engine.table.ColumnSource;
import io.deephaven.engine.updategraph.UpdateGraphProcessor;
import io.deephaven.chunk.*;
import io.deephaven.chunk.attributes.Values;
import io.deephaven.chunk.DoubleChunk;
import io.deephaven.chunk.WritableDoubleChunk;
import io.deephaven.engine.rowset.RowSet;
import io.deephaven.engine.rowset.RowSetBuilderSequential;
import io.deephaven.engine.rowset.RowSetFactory;
import io.deephaven.engine.rowset.RowSequence;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Random;

// region boxing imports
import static io.deephaven.util.QueryConstants.NULL_DOUBLE;
// endregion boxing imports

import static junit.framework.TestCase.*;

public class TestDoubleSparseArraySource {
    @Before
    public void setUp() throws Exception {
        UpdateGraphProcessor.DEFAULT.enableUnitTestMode();
        UpdateGraphProcessor.DEFAULT.resetForUnitTests(false);
    }

    @After
    public void tearDown() throws Exception {
        UpdateGraphProcessor.DEFAULT.resetForUnitTests(true);
    }

    @Test
    public void testFillChunk() {
        final Random random = new Random(0);

        for (int chunkSize = 1024; chunkSize <= 16384; chunkSize *= 2) {
            testFill(random, chunkSize);
        }
    }

    private void testFill(Random random, int chunkSize) {
        final DoubleSparseArraySource source = new DoubleSparseArraySource();

        final ColumnSource.FillContext fillContext = source.makeFillContext(chunkSize);
        final WritableDoubleChunk<Values> dest = WritableDoubleChunk.makeWritableChunk(chunkSize);

        source.fillChunk(fillContext, dest, RowSetFactory.fromRange(0, 1023));
        for (int ii = 0; ii < 1024; ++ii) {
            checkFromSource("null check: " + ii, NULL_DOUBLE, dest.get(ii));
        }

        final int expectedBlockSize = 1024;
        final double [] expectations = new double[16384];
        // region arrayFill
        Arrays.fill(expectations, NULL_DOUBLE);
        // endregion arrayFill
        final double [] randomDoubles = ArrayGenerator.randomDoubles(random, expectations.length / 2);
        for (int ii = 0; ii < expectations.length; ++ii) {
            final int block = ii / expectedBlockSize;
            if (block % 2 == 0) {
                final double randomDouble = randomDoubles[(block / 2 * expectedBlockSize) + (ii % expectedBlockSize)];
                expectations[ii] = randomDouble;
                source.set(ii, randomDouble);
            }
        }

        // before we have the previous tracking enabled, prev should just fall through to get
        for (boolean usePrev : new boolean[]{false, true}) {
            checkRangeFill(chunkSize, source, fillContext, dest, expectations, 0, expectations.length - 1, usePrev);
            checkRangeFill(chunkSize, source, fillContext, dest, expectations, 100, expectations.length - 100, usePrev);
            checkRangeFill(chunkSize, source, fillContext, dest, expectations, 200, expectations.length - 1124, usePrev);
            checkRangeFill(chunkSize, source, fillContext, dest, expectations, 100, 700, usePrev);
            checkRangeFill(chunkSize, source, fillContext, dest, expectations, 100, 1024, usePrev);
            checkRangeFill(chunkSize, source, fillContext, dest, expectations, 250, 250, usePrev);
            checkRangeFill(chunkSize, source, fillContext, dest, expectations, 250, 251, usePrev);

            // lets make a few random indices
            for (int seed = 0; seed < 100; ++seed) {
                final RowSet rowSet = generateIndex(random, expectations.length, 1 + random.nextInt(31));
                checkRandomFill(chunkSize, source, fillContext, dest, expectations, rowSet, usePrev);
            }
        }

        fillContext.close();
    }

    @Test
    public void testGetChunk() {
        final Random random = new Random(0);

        for (int chunkSize = 1024; chunkSize <= 16384; chunkSize *= 2) {
            testGet(random, chunkSize);
        }
    }

    private void testGet(Random random, int chunkSize) {
        final DoubleSparseArraySource source = new DoubleSparseArraySource();

        final ColumnSource.GetContext getContext = source.makeGetContext(chunkSize);

        // the asChunk is not needed here, but it's needed when replicated to Boolean
        final DoubleChunk<Values> result = source.getChunk(getContext, RowSetFactory.fromRange(0, 1023)).asDoubleChunk();
        for (int ii = 0; ii < 1024; ++ii) {
            checkFromSource("null check: " + ii, NULL_DOUBLE, result.get(ii));
        }

        final int expectedBlockSize = 1024;
        final double [] expectations = new double[16384];
        // region arrayFill
        Arrays.fill(expectations, NULL_DOUBLE);
        // endregion arrayFill
        final double [] randomDoubles = ArrayGenerator.randomDoubles(random, expectations.length / 2);
        for (int ii = 0; ii < expectations.length; ++ii) {
            final int block = ii / expectedBlockSize;
            if (block % 2 == 0) {
                final double randomDouble = randomDoubles[(block / 2 * expectedBlockSize) + (ii % expectedBlockSize)];
                expectations[ii] = randomDouble;
                source.set(ii, randomDouble);
            }
        }

        // before we have the previous tracking enabled, prev should just fall through to get
        for (boolean usePrev : new boolean[]{false, true}) {
            checkRangeGet(chunkSize, source, getContext, expectations, 0, expectations.length - 1, usePrev);
            checkRangeGet(chunkSize, source, getContext, expectations, 100, expectations.length - 100, usePrev);
            checkRangeGet(chunkSize, source, getContext, expectations, 200, expectations.length - 1124, usePrev);
            checkRangeGet(chunkSize, source, getContext, expectations, 100, 700, usePrev);
            checkRangeGet(chunkSize, source, getContext, expectations, 100, 1024, usePrev);
            checkRangeGet(chunkSize, source, getContext, expectations, 250, 250, usePrev);
            checkRangeGet(chunkSize, source, getContext, expectations, 250, 251, usePrev);
            checkRangeGet(chunkSize, source, getContext, expectations, 0, 1023, usePrev);
            checkRangeGet(chunkSize, source, getContext, expectations, 1024, 2047, usePrev);
            checkRangeGet(chunkSize, source, getContext, expectations, 1100, 1200, usePrev);
            checkRangeGet(chunkSize, source, getContext, expectations, 1200, 1200, usePrev);
            checkRangeGet(chunkSize, source, getContext, expectations, 1200, 1201, usePrev);
        }

        getContext.close();
    }

    private RowSet generateIndex(Random random, int maxsize, int runLength) {
        final RowSetBuilderSequential builder = RowSetFactory.builderSequential();
        int nextKey = random.nextInt(runLength);
        while (nextKey < maxsize) {
            int lastKey;
            if (random.nextBoolean()) {
                final int length = Math.min(random.nextInt(runLength) + 1, maxsize - nextKey);
                lastKey =  nextKey + length - 1;
                builder.appendRange(nextKey, lastKey);
            } else {
                builder.appendKey(lastKey = nextKey);
            }
            nextKey = lastKey + 1 + random.nextInt(runLength + 1);
        }

        return builder.build();
    }

    private void checkRandomFill(int chunkSize, DoubleSparseArraySource source, ColumnSource.FillContext fillContext,
                                 WritableDoubleChunk<Values> dest, double[] expectations, RowSet rowSet, boolean usePrev) {
        for (final RowSequence.Iterator rsIt = rowSet.getRowSequenceIterator(); rsIt.hasMore(); ) {
            final RowSequence nextOk = rsIt.getNextRowSequenceWithLength(chunkSize);

            if (usePrev) {
                source.fillChunk(fillContext, dest, nextOk);
            } else {
                source.fillPrevChunk(fillContext, dest, nextOk);
            }

            int ii = 0;
            for (final RowSet.Iterator indexIt = nextOk.asRowSet().iterator(); indexIt.hasNext(); ii++) {
                final long next = indexIt.nextLong();
                checkFromValues("expectations[" + next + "] vs. dest[" + ii + "]", expectations[(int)next], dest.get(ii));
            }
        }
    }

    private void checkRangeFill(int chunkSize, DoubleSparseArraySource source, ColumnSource.FillContext fillContext,
                                WritableDoubleChunk<Values> dest, double[] expectations, int firstKey, int lastKey, boolean usePrev) {
        int offset;
        final RowSet rowSet = RowSetFactory.fromRange(firstKey, lastKey);
        offset = firstKey;
        for (final RowSequence.Iterator it = rowSet.getRowSequenceIterator(); it.hasMore(); ) {
            final RowSequence nextOk = it.getNextRowSequenceWithLength(chunkSize);

            if (usePrev) {
                source.fillPrevChunk(fillContext, dest, nextOk);
            } else {
                source.fillChunk(fillContext, dest, nextOk);
            }
            checkRangeResults(expectations, offset, nextOk, dest);
            offset += nextOk.size();
        }
    }

    private void checkRangeGet(int chunkSize, DoubleSparseArraySource source, ColumnSource.GetContext getContext, double[] expectations, int firstKey, int lastKey, boolean usePrev) {
        int offset;
        final RowSet rowSet = RowSetFactory.fromRange(firstKey, lastKey);
        offset = firstKey;
        for (final RowSequence.Iterator it = rowSet.getRowSequenceIterator(); it.hasMore(); ) {
            final RowSequence nextOk = it.getNextRowSequenceWithLength(chunkSize);

            final DoubleChunk<Values> result;
            if (usePrev) {
                result = source.getPrevChunk(getContext, nextOk).asDoubleChunk();
            } else {
                result = source.getChunk(getContext, nextOk).asDoubleChunk();
            }
            checkRangeResults(expectations, offset, nextOk, result);
            // region samecheck
            final int firstBlock = firstKey / 1024;
            final int lastBlock = lastKey / 1024;
            if (!usePrev && (firstBlock == lastBlock) && (firstBlock % 2 == 0)) {
                assertTrue(DefaultGetContext.isMyResettableChunk(getContext, result));
            }
            // endregion samecheck
            offset += nextOk.size();
        }
    }

    private void checkRangeResults(double[] expectations, int offset, RowSequence nextOk, DoubleChunk<Values> result) {
        for (int ii = 0; ii < nextOk.size(); ++ii) {
            checkFromValues("expectations[" + offset + " + " + ii + " = " + (ii + offset) + "] vs. dest[" + ii + "]", expectations[ii + offset], result.get(ii));
        }
    }

    // region fromvalues
    private void checkFromValues(String msg, double fromValues, double fromChunk) {
        assertEquals(msg, fromValues, fromChunk);
    }
    // endregion fromvalues

    // region fromsource
    private void checkFromSource(String msg, double fromSource, double fromChunk) {
        assertEquals(msg, fromSource, fromChunk);
    }
    // endregion fromsource

    @Test
    public void testSourceSink() {
        TestSourceSink.runTests(ChunkType.Double, size -> {
            final DoubleSparseArraySource src = new DoubleSparseArraySource();
            src.ensureCapacity(size);
            return src;
        });
    }

    @Test
    public void confirmAliasingForbidden() {
        final Random rng = new Random(438269476);
        final int arraySize = 100;
        final int rangeStart = 20;
        final int rangeEnd = 80;
        final DoubleSparseArraySource source = new DoubleSparseArraySource();
        source.ensureCapacity(arraySize);

        final double[] data = ArrayGenerator.randomDoubles(rng, arraySize);
        for (int ii = 0; ii < data.length; ++ii) {
            source.set(ii, data[ii]);
        }
        // super hack
        final double[] peekedBlock = source.ensureBlock(0, 0, 0);

        try (RowSet srcKeys = RowSetFactory.fromRange(rangeStart, rangeEnd)) {
            try (RowSet destKeys = RowSetFactory.fromRange(rangeStart + 1, rangeEnd + 1)) {
                try (ChunkSource.GetContext srcContext = source.makeGetContext(arraySize)) {
                    try (ChunkSink.FillFromContext destContext = source.makeFillFromContext(arraySize)) {
                        Chunk chunk = source.getChunk(srcContext, srcKeys);
                        if (chunk.isAlias(peekedBlock)) {
                            // If the ArraySource gives out aliases of its blocks, then it should throw when we try to
                            // fill from that aliased chunk
                            boolean testFailed;
                            try {
                                source.fillFromChunk(destContext, chunk, destKeys);
                                testFailed = true;
                            } catch (UnsupportedOperationException uoe) {
                                testFailed = false;
                            }
                            assertFalse(testFailed);
                        }
                    }
                }
            }
        }
    }

    // This code tickles a bug where the act of trying to fill a chunk activates the prevFlusher, but the fact that
    // there's no data in the chunk means that the prev arrays were never changed from null. This would trigger a
    // null reference exception at commit time. The fix is to have the chunk methods bail out early if there is nothing
    // to do.
    @Test
    public void testFilllEmptyChunkWithPrev() {
        final DoubleSparseArraySource src = new DoubleSparseArraySource();
        src.startTrackingPrevValues();
        UpdateGraphProcessor.DEFAULT.startCycleForUnitTests();
        try (final RowSet keys = RowSetFactory.empty();
             final WritableDoubleChunk<Values> chunk = WritableDoubleChunk.makeWritableChunk(0)) {
            // Fill from an empty chunk
            src.fillFromChunkByKeys(keys, chunk);
        }
        // NullPointerException in DoubleSparseArraySource.commitUpdates()
        UpdateGraphProcessor.DEFAULT.completeCycleForUnitTests();
    }

    @Test
    public void testSerialization() throws Exception {
        final Random random = new Random(36403335);
        final int numValues = 100_000;
        final long[] randomKeys = new long[numValues];
        for (int ii = 0; ii < numValues; ++ii) {
            // No negative keys please.
            randomKeys[ii] = random.nextLong() & ~(1L << 63);
        }
        final double[] randomValues = ArrayGenerator.randomDoubles(random, numValues);
        // Make every one out of 10 null
        for (int ii = 0; ii < numValues; ii += 10) {
            // region arrayFill
            randomValues[ii] = NULL_DOUBLE;
            // endregion arrayFill
        }

        final DoubleSparseArraySource source = new DoubleSparseArraySource();
        for (int ii = 0; ii < numValues; ++ii) {
            source.set(randomKeys[ii], randomValues[ii]);
        }

        final byte[] bytes;
        try (final ByteArrayOutputStream bos = new ByteArrayOutputStream();
             final ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(source);
            oos.flush();
            bytes = bos.toByteArray();
        }

        final DoubleSparseArraySource dest;
        try (final ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            final ObjectInputStream ois = new ObjectInputStream(bis)) {
            dest = (DoubleSparseArraySource) ois.readObject();
        }

        // region elementGet
        for (long key : randomKeys) {
            final double srcKey = source.getDouble(key);
            final double destKey = dest.getDouble(key);
            assertEquals(srcKey, destKey);
        }
        // endregion elementGet
    }
}