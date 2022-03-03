package io.deephaven.db.v2;

/**
 * Base class containing common code for implementers of HierarchicalTableInfo
 */
public abstract class AbstractHierarchicalTableInfo implements HierarchicalTableInfo {
    private final transient String[] columnFormats;

    AbstractHierarchicalTableInfo(String[] columnFormats) {
        this.columnFormats = columnFormats;
    }

    @Override
    public String[] getColumnFormats() {
        return columnFormats;
    }
}