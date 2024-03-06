package org.cqfn.astranaut.core;

/**
 * Common position.
 */
public class CommonPosition implements Position {
    /**
     * Position index.
     */
    private final int index;

    /**
     * Constructor.
     *
     * @param index Index value
     */
    public CommonPosition(int index) {
        this.index = index;
    }

    @Override
    public int getIndex() {
        return index;
    }
}
