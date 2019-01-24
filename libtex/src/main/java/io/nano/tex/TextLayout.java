package io.nano.tex;

import android.graphics.Typeface;
import android.text.TextPaint;

/**
 * Created by nano on 18-11-10
 */
public final class TextLayout {

    private static final TextPaint TMP_PAINT = new TextPaint();
    /**
     * Keep in sync with the size of TextRenderingBox::_font
     */
    private static final float FACTORED_TEXT_SIZE = 10.f;

    static {
        TMP_PAINT.setTextSize(FACTORED_TEXT_SIZE);
    }

    public static Rect getBounds(String txt, Font font) {
        Typeface oldTypeface = TMP_PAINT.getTypeface();
        TMP_PAINT.setTypeface(font.getTypeface());
        android.graphics.Rect ar = new android.graphics.Rect();
        TMP_PAINT.getTextBounds(txt, 0, txt.length(), ar);
        Rect r = new Rect(ar.left, ar.top, ar.width(), ar.height());
        // Reset typeface to avoid memory leak
        TMP_PAINT.setTypeface(oldTypeface);
        return r;
    }
}
