package io.nano.tex.test;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

import io.nano.tex.Graphics2D;
import io.nano.tex.LaTeX;
import io.nano.tex.TeXRender;

/**
 * Created by nano on 18-11-12
 */
public class TeXView extends View {

    public TeXView(Context context) {
        super(context);
    }

    public TeXView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TeXView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TeXView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private int textSize = 40;
    private TeXRender render;
    private Graphics2D g2 = new Graphics2D();

    public void setLaTeX(String ltx) {
        int w = getWidth();
        if (w == 0) w = 2048;
        render = LaTeX.instance().parse(ltx, w, textSize, 10, Color.DKGRAY);
        requestLayout();
    }

    public void setTextSize(int size) {
        textSize = size;
        if (render != null) render.setTextSize(size);
        requestLayout();
    }

    public void invalidateRender() {
        render.invalidateDrawingCache();
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (render == null) return;
        int h = render.getHeight();
        setMeasuredDimension(getMeasuredWidth(), h + getPaddingTop() + getPaddingBottom());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (render == null) return;
        g2.setCanvas(canvas);
        render.draw(g2, getPaddingLeft(), getPaddingTop());
    }
}
