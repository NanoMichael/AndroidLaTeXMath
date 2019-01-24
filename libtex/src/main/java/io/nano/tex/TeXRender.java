package io.nano.tex;

/**
 * Created by nano on 18-11-10
 */
public final class TeXRender {

    /**
     * Delegate class to release native resources while finalize.
     */
    private static class Finalizer {

        private long nativePtr;

        Finalizer(long nativePtr) {
            this.nativePtr = nativePtr;
        }

        @Override
        protected void finalize() throws Throwable {
            if (nativePtr != 0) {
                nFinalize(nativePtr);
                nativePtr = 0;
            }
            super.finalize();
        }
    }

    private long nativePtr;
    private ActionRecorder recorder;
    private Finalizer finalizer;

    TeXRender(long nativePtr) {
        this.nativePtr = nativePtr;
        finalizer = new Finalizer(nativePtr);
    }

    /**
     * Get the text size
     */
    public float getTextSize() {
        return nGetTextSize(nativePtr);
    }

    /**
     * Get the height
     */
    public int getHeight() {
        return nGetHeight(nativePtr);
    }

    /**
     * Get the depth (descent in positive)
     */
    public int getDepth() {
        return nGetDepth(nativePtr);
    }

    public int getWidth() {
        return nGetWidth(nativePtr);
    }

    public float getBaseline() {
        return nGetBaseline(nativePtr);
    }

    /**
     * Set the text size
     */
    public void setTextSize(float size) {
        nSetTextSize(nativePtr, size);
    }

    /**
     * Set the foreground color
     */
    public void setForeground(int color) {
        // Cancel previous records
        recorder = null;
        nSetForeground(nativePtr, color);
    }

    /**
     * Set the width
     *
     * @param width the width, in pixel
     * @param align the alignment, must be one of the value defined in {@link Alignment}
     */
    public void setWidth(int width, int align) {
        nSetWidth(nativePtr, width, align);
    }

    /**
     * Set the height
     *
     * @param height the height, in pixel
     * @param align  the alignment, must be one of the value defined in {@link Alignment}
     */
    public void setHeight(int height, int align) {
        nSetHeight(nativePtr, height, align);
    }

    /**
     * Draw the formula
     *
     * @param g2 the 2D graphics context
     * @param x  the left position to draw
     * @param y  the top position to draw
     */
    public void draw(Graphics2D g2, int x, int y) {
        if (recorder == null) {
            recorder = new ActionRecorder();
            nDraw(nativePtr, recorder, 0, 0);
        }
        recorder.setPosition(x, y);
        recorder.play(g2);
    }

    /**
     * Invalidate the drawing cache, refill the cache when next call of
     * {@link TeXRender#draw(Graphics2D, int, int)}
     */
    public void invalidateDrawingCache() {
        recorder = null;
    }

    private static native void nDraw(long ptr, ActionRecorder recorder, int x, int y);

    private static native float nGetTextSize(long ptr);

    private static native int nGetHeight(long ptr);

    private static native int nGetDepth(long ptr);

    private static native int nGetWidth(long ptr);

    private static native float nGetBaseline(long ptr);

    private static native void nSetTextSize(long ptr, float size);

    private static native void nSetForeground(long ptr, int color);

    private static native void nSetWidth(long ptr, int width, int align);

    private static native void nSetHeight(long ptr, int height, int align);

    private static native void nFinalize(long ptr);
}
