package io.github.maybeec.html2text;

/**
 *
 */
public class TableModel {

    private int[] columnWidth;

    private String[][][] cells;

    private boolean hasHeadline;

    /**
     * @param columnWidth
     * @param cells
     */
    public TableModel(int[] columnWidth, String[][][] cells, boolean hasHeadline) {
        super();
        this.columnWidth = columnWidth;
        this.cells = cells;
        this.hasHeadline = hasHeadline;
    }

    public int[] getColumnWidth() {
        return columnWidth;
    }

    public String[][][] getCells() {
        return cells;
    }

    public boolean isHasHeadline() {
        return hasHeadline;
    }
}
