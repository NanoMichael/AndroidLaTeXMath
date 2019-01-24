package io.nano.tex;

import android.graphics.Typeface;

/**
 * Created by nano on 18-11-10
 */
public final class Font {

    private Typeface typeface;
    private float size;

    private Font(Typeface typeface, float size) {
        this.typeface = typeface;
        this.size = size;
    }

    public Font deriveFont(int style) {
        if (typeface.getStyle() == style) return this;
        Typeface typeface = Typeface.create(this.typeface, style);
        return new Font(typeface, size);
    }

    public Typeface getTypeface() {
        return typeface;
    }

    public float getSize() {
        return size;
    }

    public static Font create(String name, int style, float size) {
        Typeface typeface = Typeface.create(name, style);
        return new Font(typeface, size);
    }

    public static Font create(String file, float size) {
        Typeface tf = Typeface.createFromFile(file);
        return new Font(tf, size);
    }
}
