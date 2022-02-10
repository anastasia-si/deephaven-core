package io.deephaven.tuple.generated;

import gnu.trove.map.TIntObjectMap;
import io.deephaven.tuple.CanonicalizableTuple;
import io.deephaven.tuple.serialization.SerializationUtils;
import io.deephaven.tuple.serialization.StreamingExternalizable;
import io.deephaven.util.compare.IntComparisons;
import io.deephaven.util.compare.ObjectComparisons;
import io.deephaven.util.compare.ShortComparisons;
import org.jetbrains.annotations.NotNull;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * <p>3-Tuple (triple) key class composed of short, int, and Object elements.
 * <p>Generated by io.deephaven.replicators.TupleCodeGenerator.
 */
public class ShortIntObjectTuple implements Comparable<ShortIntObjectTuple>, Externalizable, StreamingExternalizable, CanonicalizableTuple<ShortIntObjectTuple> {

    private static final long serialVersionUID = 1L;

    private short element1;
    private int element2;
    private Object element3;

    private transient int cachedHashCode;

    public ShortIntObjectTuple(
            final short element1,
            final int element2,
            final Object element3
    ) {
        initialize(
                element1,
                element2,
                element3
        );
    }

    /** Public no-arg constructor for {@link Externalizable} support only. <em>Application code should not use this!</em> **/
    public ShortIntObjectTuple() {
    }

    private void initialize(
            final short element1,
            final int element2,
            final Object element3
    ) {
        this.element1 = element1;
        this.element2 = element2;
        this.element3 = element3;
        cachedHashCode = ((31 +
                Short.hashCode(element1)) * 31 +
                Integer.hashCode(element2)) * 31 +
                Objects.hashCode(element3);
    }

    public final short getFirstElement() {
        return element1;
    }

    public final int getSecondElement() {
        return element2;
    }

    public final Object getThirdElement() {
        return element3;
    }

    @Override
    public final int hashCode() {
        return cachedHashCode;
    }

    @Override
    public final boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        final ShortIntObjectTuple typedOther = (ShortIntObjectTuple) other;
        // @formatter:off
        return element1 == typedOther.element1 &&
               element2 == typedOther.element2 &&
               ObjectComparisons.eq(element3, typedOther.element3);
        // @formatter:on
    }

    @Override
    public final int compareTo(@NotNull final ShortIntObjectTuple other) {
        if (this == other) {
            return 0;
        }
        int comparison;
        // @formatter:off
        return 0 != (comparison = ShortComparisons.compare(element1, other.element1)) ? comparison :
               0 != (comparison = IntComparisons.compare(element2, other.element2)) ? comparison :
               ObjectComparisons.compare(element3, other.element3);
        // @formatter:on
    }

    @Override
    public void writeExternal(@NotNull final ObjectOutput out) throws IOException {
        out.writeShort(element1);
        out.writeInt(element2);
        out.writeObject(element3);
    }

    @Override
    public void readExternal(@NotNull final ObjectInput in) throws IOException, ClassNotFoundException {
        initialize(
                in.readShort(),
                in.readInt(),
                in.readObject()
        );
    }

    @Override
    public void writeExternalStreaming(@NotNull final ObjectOutput out, @NotNull final TIntObjectMap<SerializationUtils.Writer> cachedWriters) throws IOException {
        out.writeShort(element1);
        out.writeInt(element2);
        StreamingExternalizable.writeObjectElement(out, cachedWriters, 2, element3);
    }

    @Override
    public void readExternalStreaming(@NotNull final ObjectInput in, @NotNull final TIntObjectMap<SerializationUtils.Reader> cachedReaders) throws Exception {
        initialize(
                in.readShort(),
                in.readInt(),
                StreamingExternalizable.readObjectElement(in, cachedReaders, 2)
        );
    }

    @Override
    public String toString() {
        return "ShortIntObjectTuple{" +
                element1 + ", " +
                element2 + ", " +
                element3 + '}';
    }

    @Override
    public ShortIntObjectTuple canonicalize(@NotNull final UnaryOperator<Object> canonicalizer) {
        final Object canonicalizedElement3 = canonicalizer.apply(element3);
        return canonicalizedElement3 == element3
                ? this : new ShortIntObjectTuple(element1, element2, canonicalizedElement3);
    }
}