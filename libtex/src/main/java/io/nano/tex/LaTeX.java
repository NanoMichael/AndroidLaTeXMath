package io.nano.tex;

import android.content.Context;

import io.nano.tex.res.ResManager;

/**
 * Created by nano on 18-11-10
 */
public final class LaTeX {

    private static boolean libLoaded = false;
    private static LaTeX instance;

    /**
     * Get the instance of the LaTeX engine.
     */
    public synchronized static LaTeX instance() {
        if (!libLoaded) {
            System.loadLibrary("tex");
            libLoaded = true;
        }
        if (instance == null) instance = new LaTeX();
        return instance;
    }

    private volatile boolean initialized = false;
    private volatile boolean isDebug = false;

    private LaTeX() {
    }

    /**
     * Initialize the LaTeX engine. Call of this function will copy the "TeX resources" from apk into the
     * data files directory of the host application, and parse the "TeX resources", it may takes long time,
     * you may call it from a background thread.
     */
    public synchronized void init(Context context) {
        ResManager rm = new ResManager(context);
        rm.unpackResources();
        boolean success = nInit(rm.getResourcesRootDirectory());
        if (!success) {
            throw new TeXException("Failed to initialize LaTeX engine.");
        }
        initialized = true;
    }

    /**
     * Check if the LaTeX engine is initialized.
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * Release the LaTeX engine.
     */
    public synchronized void release() {
        nFree();
        initialized = false;
    }

    private void check() {
        if (!initialized) throw new IllegalStateException(
            "The engine was not initialized, call init(Context) before use.");
    }

    /**
     * Parse a TeX formatted code with specified text size and foreground color.
     */
    public synchronized TeXRender parse(String ltx, float textSize, int foreground) {
        check();
        long ptr = nParse(ltx, 0, textSize, 0, foreground);
        if (ptr == 0) throw new TeXException("Failed to parse LaTeX: " + ltx);
        return new TeXRender(ptr);
    }

    /**
     * Parse a TeX code
     *
     * @param ltx        the TeX formatted code
     * @param width      the width of the 2D graphics context
     * @param textSize   the text size to draw
     * @param lineSpace  the space between two lines
     * @param foreground the foreground color
     */
    public synchronized TeXRender parse(
        String ltx, int width,
        float textSize, float lineSpace,
        int foreground) {
        check();
        long ptr = nParse(ltx, width, textSize, lineSpace, foreground);
        if (ptr == 0) throw new TeXException("Failed to parse LaTeX: " + ltx);
        // We got a very long formula, scale to fit the width
        TeXRender r = new TeXRender(ptr);
        if (r.getWidth() > width) {
            float w = r.getWidth();
            float scale = width / w;
            r.setTextSize(scale * textSize);
        }
        return r;
    }

    /**
     * Set if debug
     */
    public synchronized void setDebug(boolean debug) {
        nSetDebug(debug);
        isDebug = debug;
    }

    /**
     * Check if is in debug mode.
     */
    public boolean isDebug() {
        return isDebug;
    }

    private static native boolean nInit(String resDir);

    private static native void nFree();

    private static native long nParse(
        String ltx, int width,
        float textSize, float lineSpace,
        int foreground);

    private static native void nSetDebug(boolean debug);
}
