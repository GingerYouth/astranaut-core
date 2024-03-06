package org.cqfn.astranaut.core;

/**
 * Common source code fragment.
 */
public final class CommonFragment implements Fragment {
    /**
     * The source.
     */
    private final Source source;

    /**
     * The first position.
     */
    private final Position begin;

    /**
     * The last position.
     */
    private final Position end;

    /**
     * Constructor.
     *
     * @param source The source
     * @param begin The first position
     * @param end The last position
     */
    public CommonFragment(final Source source, final Position begin, final Position end) {
        this.source = source;
        this.begin = begin;
        this.end = end;
    }

    /**
     * Constructor.
     *
     * @param source The source
     * @param begin The first position number
     * @param end The last position number
     */
    public CommonFragment(final String source, final int begin, final int end) {
        this.source = (start, end1) -> source;
        this.begin = new CommonPosition(begin);
        this.end = new CommonPosition(end);
    }

    @Override
    public Source getSource() {
        return this.source;
    }

    @Override
    public Position getBegin() {
        return this.begin;
    }

    @Override
    public Position getEnd() {
        return this.end;
    }
}
