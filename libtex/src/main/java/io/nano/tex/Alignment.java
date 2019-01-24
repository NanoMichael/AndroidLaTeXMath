package io.nano.tex;

/**
 * Created by nano on 18-11-10
 * <p>
 * Keep in sync with the tex::TeXConstants
 */
public class Alignment {
    /**
     * Alignment constant: extra space will be added to the right of the formula
     */
    public static final int LEFT = 0;

    /**
     * Alignment constant: extra space will be added to the left of the formula
     */
    public static final int RIGHT = 1;

    /**
     * Alignment constant: the formula will be centered in the middle. This
     * constant can be used for both horizontal and vertical alignment.
     */
    public static final int CENTER = 2;

    /**
     * Alignment constant: extra space will be added under the formula
     */
    public static final int TOP = 3;

    /**
     * Alignment constant: extra space will be added above the formula
     */
    public static final int BOTTOM = 4;
}

