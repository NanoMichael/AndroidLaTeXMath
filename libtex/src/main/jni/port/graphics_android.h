#include "config.h"

#ifdef __OS_Android__

#ifndef GRAPHICS_ANDROID_INCLUDED
#define GRAPHICS_ANDROID_INCLUDED

#include <jni.h>
#include "graphic/graphic.h"

using namespace tex;

class Font_Android : public Font {
private:
    float _size;
    shared_ptr<_jobject> _javaFont;

    Font_Android(const Font_Android&);
    void operator=(const Font_Android&);

public:
    Font_Android() : _size(1), _javaFont(nullptr) {}

    void setSize(float size);
    void setJavaFont(jobject font);
    shared_ptr<_jobject> getJavaFont() const;

    virtual float getSize() const override;
    virtual shared_ptr<Font> deriveFont(int style) const override;
    virtual bool operator==(const Font& f) const override;
    virtual bool operator!=(const Font& f) const override;
};

class TextLayout_Android : public TextLayout {
private:
    shared_ptr<Font> _font;
    const wstring _txt;

    TextLayout_Android(const TextLayout_Android&);
    void operator=(const TextLayout_Android&);

public:
    TextLayout_Android(const wstring& txt, const shared_ptr<Font> font)
        : _txt(txt), _font(font) {}

    virtual void getBounds(_out_ Rect& bounds) override;
    virtual void draw(Graphics2D& g2, float x, float y) override;
};

class Graphics2D_Android : public Graphics2D {
private:
    jobject _jrecorder;
    float* _t;
    color _color;
    const Font* _font;
    Stroke _stroke;

    void makeRecord(int action, jobject arg, const jfloat* args, int argsLen);

    Graphics2D_Android(const Graphics2D_Android&);
    void operator=(const Graphics2D_Android&);

public:
    Graphics2D_Android(jobject jrecorder);

    ~Graphics2D_Android();

    virtual void setColor(color c) override;
    virtual color getColor() const override;
    virtual void setStroke(const Stroke& s) override;
    virtual const Stroke& getStroke() const override;
    virtual void setStrokeWidth(float w) override;
    virtual const Font* getFont() const override;
    virtual void setFont(const Font* font) override;
    virtual void translate(float dx, float dy) override;
    virtual void scale(float sx, float sy) override;
    virtual void rotate(float angle) override;
    virtual void rotate(float angle, float px, float py) override;
    virtual void reset() override;
    virtual float sx() const override;
    virtual float sy() const override;
    virtual void drawChar(wchar_t c, float x, float y) override;
    virtual void drawText(const wstring& c, float x, float y) override;
    virtual void drawLine(float x1, float y1, float x2, float y2) override;
    virtual void drawRect(float x, float y, float w, float h) override;
    virtual void fillRect(float x, float y, float w, float h) override;
    virtual void drawRoundRect(float x, float y, float w, float h, float rx, float ry) override;
    virtual void fillRoundRect(float x, float y, float w, float h, float rx, float ry) override;
};

#endif  // GRAPHICS_ANDROID_INCLUDED
#endif  // __OS_Android__
