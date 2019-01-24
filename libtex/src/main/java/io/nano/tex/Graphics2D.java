package io.nano.tex;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextPaint;

import java.lang.ref.WeakReference;

/**
 * Created by nano on 18-11-10
 * <p>
 * Represents a 2D graphics context, wrap the Android's 2D graphics API. The context doesn't use the
 * Android API to perform the affine transformations, the rendering result is unpredictable when we
 * do it. Android do not support the scale operations to draw texts on {@link Canvas} directly when
 * have hardware accelerations since Android 3.0, the reason behind is complicated. We maintain a
 * transform matrix ({@link Graphics2D#T}) manually to represents the "affine transformations".
 */
public final class Graphics2D {

    static final String TAG = "io.nano.tex.Graphics2D";

    /**
     * Keep in sync with tex::Cap
     */
    private static final int CAP_BUTT = 0, CAP_ROUND = 1, CAP_SQUARE = 2;
    /**
     * Keep in sync with tex::Join
     */
    private static final int JOIN_BEVEL = 0, JOIN_MITER = 1, JOIN_ROUND = 2;

    private static final int SX = Matrix.MSCALE_X;
    private static final int SY = Matrix.MSCALE_Y;
    private static final int TX = Matrix.MTRANS_X;
    private static final int TY = Matrix.MTRANS_Y;
    private static final int R = Matrix.MPERSP_0;
    private static final int PX = Matrix.MPERSP_1;
    private static final int PY = Matrix.MPERSP_2;

    private TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private WeakReference<Canvas> canvas;
    private float[] T = new float[]{1, 0, 0, 0, 1, 0, 0, 0, 0};
    private boolean colorLocked = false;

    public Graphics2D() {
        this(null);
    }

    public Graphics2D(Canvas canvas) {
        if (canvas != null) this.canvas = new WeakReference<>(canvas);
        init();
    }

    private void init() {
        paint.setAntiAlias(true);
        // Do not set dither here since we don't draw any bitmap to accelerate the draw speed
        // paint.setDither(true);
        /*
         * Do not use subpixel feature here since the subpixel OP will cause the drawing element
         * change its position dynamically, we need exactly the right position here
         */
        // paint.setSubpixelText(true);
        paint.setTextSize(46);
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = new WeakReference<>(canvas);
    }

    public Canvas getCanvas() {
        if (canvas == null) return null;
        return canvas.get();
    }

    public Paint getPaint() {
        return paint;
    }

    public void setFont(Font font) {
        if (font == null) {
            paint.setTypeface(Typeface.DEFAULT);
            paint.setTextSize(46);
        } else {
            paint.setTypeface(font.getTypeface());
            paint.setTextSize(font.getSize());
        }
    }

    public void lockColor() {
        colorLocked = true;
    }

    public void unlockColor() {
        colorLocked = false;
    }

    public void setColor(int c) {
        // Do not allow the drawing op to change the color of the context if the color is locked
        if (colorLocked) return;
        paint.setColor(c);
    }

    public void setStrokeWidth(float width) {
        paint.setStrokeWidth(width);
    }

    public void setStroke(float width, float miterLimit, int cap, int join) {
        paint.setStrokeWidth(width);
        paint.setStrokeMiter(miterLimit);
        switch (cap) {
            case CAP_BUTT:
                paint.setStrokeCap(Paint.Cap.BUTT);
                break;
            case CAP_ROUND:
                paint.setStrokeCap(Paint.Cap.ROUND);
                break;
            case CAP_SQUARE:
                paint.setStrokeCap(Paint.Cap.SQUARE);
                break;
            default:
                break;
        }
        switch (join) {
            case JOIN_BEVEL:
                paint.setStrokeJoin(Paint.Join.BEVEL);
                break;
            case JOIN_MITER:
                paint.setStrokeJoin(Paint.Join.MITER);
                break;
            case JOIN_ROUND:
                paint.setStrokeJoin(Paint.Join.ROUND);
                break;
            default:
                break;
        }
    }

    public void translate(float dx, float dy) {
        T[TX] += T[SX] * dx;
        T[TY] += T[SY] * dy;
    }

    public void scale(float sx, float sy) {
        T[SX] *= sx;
        T[SY] *= sy;
    }

    public void rotate(float angle, float px, float py) {
        float r = (float) (angle / Math.PI * 180);
        T[R] += r;
        T[PX] = x(px);
        T[PY] = y(py);

        Canvas canvas = getCanvas();
        if (canvas == null) return;
        canvas.rotate(r, px(), py());
    }

    public void reset() {
        float r = r(), px = px(), py = py();
        Canvas canvas = getCanvas();
        T = new float[]{1, 0, 0, 0, 1, 0, 0, 0, 0};
        if (canvas == null) return;
        canvas.rotate(-r, px, py);
    }

    public float sx() {
        return T[SX];
    }

    public float sy() {
        return T[SY];
    }

    public float px() {
        return T[PX];
    }

    public float py() {
        return T[PY];
    }

    public float r() {
        return T[R];
    }

    public float x(float x) {
        return x * T[SX] + T[TX];
    }

    public float y(float y) {
        return y * T[SY] + T[TY];
    }

    public float w(float w) {
        return T[SX] * w;
    }

    public float h(float h) {
        return T[SY] * h;
    }

    private StringBuilder sb = new StringBuilder();

    public void drawChar(char c, float x, float y) {
        sb.delete(0, sb.length());
        sb.append(c);
        drawText(sb.toString(), x, y);
    }

    public void drawText(String txt, float x, float y) {
        Canvas canvas = getCanvas();
        if (canvas == null) return;
        float s = paint.getTextSize();
        float sx = paint.getTextScaleX();
        paint.setTextSize(s * sy());
        paint.setTextScaleX(sx() / sy());
        canvas.drawText(txt, x(x), y(y), paint);
        paint.setTextSize(s);
        paint.setTextScaleX(sx);
    }

    public void drawLine(float x1, float y1, float x2, float y2) {
        Canvas canvas = getCanvas();
        if (canvas == null) return;
        float th = paint.getStrokeWidth();
        float sw = h(th);
        if (sw < 1.f) sw = 1.f;
        paint.setStrokeWidth(sw);
        float xx1 = x(x1);
        float yy1 = y(y1);
        float xx2 = x(x2);
        float yy2 = y(y2);
        canvas.drawLine(xx1, yy1, xx2, yy2, paint);
        paint.setStrokeWidth(th);
    }

    private void renderRect(float x, float y, float w, float h, Paint.Style s) {
        Canvas canvas = getCanvas();
        if (canvas == null) return;
        Paint.Style style = paint.getStyle();
        // draw
        paint.setStyle(s);
        float th = paint.getStrokeWidth();
        paint.setStrokeWidth(h(th));
        float xx = x(x);
        float yy = y(y);
        float ww = w(w);
        float hh = h(h);
        canvas.drawRect(xx, yy, xx + ww, yy + hh, paint);
        // reset
        paint.setStyle(style);
        paint.setStrokeWidth(th);
    }

    public void drawRect(float x, float y, float w, float h) {
        renderRect(x, y, w, h, Paint.Style.STROKE);
    }

    public void fillRect(float x, float y, float w, float h) {
        renderRect(x, y, w, h, Paint.Style.FILL);
    }

    private RectF tr = new RectF();

    private void renderRoundRect(float x, float y, float w, float h, float rx, float ry, Paint.Style s) {
        Canvas canvas = getCanvas();
        if (canvas == null) return;
        Paint.Style style = paint.getStyle();
        paint.setStyle(s);
        float th = paint.getStrokeWidth();
        paint.setStrokeWidth(h(th));
        tr.left = x(x);
        tr.top = y(y);
        tr.right = tr.left + w(w);
        tr.bottom = tr.top + h(h);
        float rxx = w(rx);
        float ryy = h(ry);
        canvas.drawRoundRect(tr, rxx, ryy, paint);
        // reset
        paint.setStyle(style);
        paint.setStrokeWidth(th);
    }

    public void drawRoundRect(float x, float y, float w, float h, float rx, float ry) {
        renderRoundRect(x, y, w, h, rx, ry, Paint.Style.STROKE);
    }

    public void fillRoundRect(float x, float y, float w, float h, float rx, float ry) {
        renderRoundRect(x, y, w, h, rx, ry, Paint.Style.FILL);
    }
}
