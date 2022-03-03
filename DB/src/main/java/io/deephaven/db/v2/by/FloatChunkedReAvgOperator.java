/*
 * Copyright (c) 2016-2021 Deephaven Data Labs and Patent Pending
 */

package io.deephaven.db.v2.by;

import io.deephaven.db.v2.sources.ColumnSource;
import io.deephaven.db.v2.sources.FloatArraySource;
import io.deephaven.db.v2.sources.chunk.*;
import io.deephaven.db.v2.sources.chunk.Attributes.ChunkLengths;
import io.deephaven.db.v2.sources.chunk.Attributes.ChunkPositions;
import io.deephaven.db.v2.sources.chunk.Attributes.KeyIndices;
import io.deephaven.db.v2.sources.chunk.Attributes.Values;
import io.deephaven.db.v2.utils.OrderedKeys;

import java.util.Collections;
import java.util.Map;

/**
 * Iterative average operator.
 */
class FloatChunkedReAvgOperator implements IterativeChunkedAggregationOperator {
    private final FloatArraySource resultColumn;
    private final String name;

    private final DoubleChunkedSumOperator sumSum;
    private final LongChunkedSumOperator nncSum;
    private final LongChunkedSumOperator nanSum;
    private final LongChunkedSumOperator picSum;
    private final LongChunkedSumOperator nicSum;

    FloatChunkedReAvgOperator(String name, DoubleChunkedSumOperator sumSum, LongChunkedSumOperator nncSum, LongChunkedSumOperator nanSum, LongChunkedSumOperator picSum, LongChunkedSumOperator nicSum) {
        this.name = name;
        this.sumSum = sumSum;
        this.nncSum = nncSum;
        this.nanSum = nanSum;
        this.picSum = picSum;
        this.nicSum = nicSum;
        resultColumn = new FloatArraySource();
    }

    @Override
    public void addChunk(BucketedContext context, Chunk<? extends Values> values, LongChunk<? extends KeyIndices> inputIndices, IntChunk<KeyIndices> destinations, IntChunk<ChunkPositions> startPositions, IntChunk<ChunkLengths> length, WritableBooleanChunk<Values> stateModified) {
        doBucketedUpdate((ReAvgContext) context, destinations, startPositions, stateModified);
    }

    @Override
    public void removeChunk(BucketedContext context, Chunk<? extends Values> values, LongChunk<? extends KeyIndices> inputIndices, IntChunk<KeyIndices> destinations, IntChunk<ChunkPositions> startPositions, IntChunk<ChunkLengths> length, WritableBooleanChunk<Values> stateModified) {
        doBucketedUpdate((ReAvgContext) context, destinations, startPositions, stateModified);
    }

    @Override
    public void modifyChunk(BucketedContext context, Chunk<? extends Values> previousValues, Chunk<? extends Values> newValues, LongChunk<? extends KeyIndices> postShiftIndices, IntChunk<KeyIndices> destinations, IntChunk<ChunkPositions> startPositions, IntChunk<ChunkLengths> length, WritableBooleanChunk<Values> stateModified) {
        doBucketedUpdate((ReAvgContext) context, destinations, startPositions, stateModified);
    }

    private void doBucketedUpdate(ReAvgContext context, IntChunk<KeyIndices> destinations, IntChunk<ChunkPositions> startPositions, WritableBooleanChunk<Values> stateModified) {
        context.keyIndices.setSize(startPositions.size());
        for (int ii = 0; ii < startPositions.size(); ++ii) {
            final int startPosition = startPositions.get(ii);
            context.keyIndices.set(ii, destinations.get(startPosition));
        }
        try (final OrderedKeys destinationOk = OrderedKeys.wrapKeyIndicesChunkAsOrderedKeys(context.keyIndices)) {
            updateResult(context, destinationOk, stateModified);
        }
    }

    private void updateResult(ReAvgContext reAvgContext, OrderedKeys destinationOk, WritableBooleanChunk<Values> stateModified) {
        final DoubleChunk<Values> sumSumChunk = sumSum.getChunk(reAvgContext.sumContext, destinationOk).asDoubleChunk();
        final LongChunk<? extends Values> nncSumChunk = nncSum.getChunk(reAvgContext.nncContext, destinationOk).asLongChunk();
        final LongChunk<? extends Values> nanSumChunk = nanSum.getChunk(reAvgContext.nanContext, destinationOk).asLongChunk();
        final LongChunk<? extends Values> picSumChunk = picSum.getChunk(reAvgContext.picContext, destinationOk).asLongChunk();
        final LongChunk<? extends Values> nicSumChunk = nicSum.getChunk(reAvgContext.nicContext, destinationOk).asLongChunk();

        final int size = reAvgContext.keyIndices.size();
        for (int ii = 0; ii < size; ++ii) {
            stateModified.set(ii, updateResult(reAvgContext.keyIndices.get(ii), nncSumChunk.get(ii), nanSumChunk.get(ii), picSumChunk.get(ii), nicSumChunk.get(ii), sumSumChunk.get(ii)));
        }
    }

    @Override
    public boolean addChunk(SingletonContext context, int chunkSize, Chunk<? extends Values> values, LongChunk<? extends KeyIndices> inputIndices, long destination) {
        return updateResult(destination);
    }

    @Override
    public boolean removeChunk(SingletonContext context, int chunkSize, Chunk<? extends Values> values, LongChunk<? extends KeyIndices> inputIndices, long destination) {
        return updateResult(destination);
    }

    @Override
    public boolean modifyChunk(SingletonContext context, int chunkSize, Chunk<? extends Values> previousValues, Chunk<? extends Values> newValues, LongChunk<? extends KeyIndices> postShiftIndices, long destination) {
        return updateResult(destination);
    }

    private boolean updateResult(long destination) {
        final long nncValue = nncSum.getResult(destination);
        final long nanValue = nanSum.getResult(destination);
        final long picValue = picSum.getResult(destination);
        final long nicValue = nicSum.getResult(destination);
        final double sumSumValue = sumSum.getRunningSum(destination);

        return updateResult(destination, nncValue, nanValue, picValue, nicValue, sumSumValue);
    }

    private boolean updateResult(long destination, long nncValue, long nanValue, long picValue, long nicValue, double sumSumValue) {
        if (nanValue > 0 || (picValue > 0 && nicValue > 0) || nncValue == 0) {
            return !Float.isNaN(resultColumn.getAndSetUnsafe(destination, Float.NaN));
        } else if (picValue > 0) {
            return resultColumn.getAndSetUnsafe(destination, Float.POSITIVE_INFINITY) != Float.POSITIVE_INFINITY;
        } else if (nicValue > 0) {
            return resultColumn.getAndSetUnsafe(destination, Float.NEGATIVE_INFINITY) != Float.NEGATIVE_INFINITY;
        } else {
            final float newValue = (float)(sumSumValue / nncValue);
            return resultColumn.getAndSetUnsafe(destination, newValue) != newValue;
        }
    }

    @Override
    public void ensureCapacity(long tableSize) {
        resultColumn.ensureCapacity(tableSize);
    }

    @Override
    public Map<String, ColumnSource<?>> getResultColumns() {
        return Collections.singletonMap(name, resultColumn);
    }

    @Override
    public void startTrackingPrevValues() {
        resultColumn.startTrackingPrevValues();
    }

    private class ReAvgContext implements BucketedContext {
        final WritableLongChunk<Attributes.OrderedKeyIndices> keyIndices;
        final ChunkSource.GetContext sumContext;
        final ChunkSource.GetContext nncContext;
        final ChunkSource.GetContext nanContext;
        final ChunkSource.GetContext picContext;
        final ChunkSource.GetContext nicContext;

        private ReAvgContext(int size) {
            keyIndices = WritableLongChunk.makeWritableChunk(size);
            sumContext = sumSum.makeGetContext(size);
            nncContext = nncSum.makeGetContext(size);
            nanContext = nanSum.makeGetContext(size);
            nicContext = nicSum.makeGetContext(size);
            picContext = picSum.makeGetContext(size);
        }

        @Override
        public void close() {
            keyIndices.close();
            sumContext.close();
            nncContext.close();
            nanContext.close();
            picContext.close();
            nicContext.close();
        }
    }

    @Override
    public BucketedContext makeBucketedContext(int size) {
        return new ReAvgContext(size);
    }
}