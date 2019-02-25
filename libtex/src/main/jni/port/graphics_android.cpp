#include "config.h"

#ifdef __OS_Android__

#include "jni_def.h"
#include "jni_log.h"

#include "common.h"
#include "graphics_android.h"

#include <cstring>

using namespace tex;
using namespace std;

/******************************************* Android Font *****************************************/

static void _delete_java_ref(jobject ptr) {
    JNIEnv* env = getJNIEnv();
    env->DeleteGlobalRef(ptr);
    LOGI("Delete global java ref: %p", ptr);
}

void Font_Android::setSize(float size) {
    _size = size;
}

void Font_Android::setJavaFont(jobject font) {
    jobject ref = getJNIEnv()->NewGlobalRef(font);
    _javaFont = shared_ptr<_jobject>(ref, _delete_java_ref);
}

shared_ptr<_jobject> Font_Android::getJavaFont() const {
    return _javaFont;
}

float Font_Android::getSize() const {
    return _size;
}

shared_ptr<Font> Font_Android::deriveFont(int style) const {
    Font_Android* f = new Font_Android();
    JNIEnv* env = getJNIEnv();
    jobject obj = env->CallObjectMethod(_javaFont.get(), gMethodDeriveFont, style);
    f->_size = _size;
    if (env->IsSameObject(_javaFont.get(), obj)) {
        f->_javaFont = _javaFont;
    } else {
        f->setJavaFont(obj);
    }
    LOGI("Derive font, java font: %p, style: %d, derived java font: %p",
         _javaFont.get(), style, f->_javaFont.get());
    return shared_ptr<Font>(f);
}

bool Font_Android::operator==(const Font& f) const {
    return _javaFont.get() == static_cast<const Font_Android&>(f)._javaFont.get();
}

bool Font_Android::operator!=(const Font& f) const {
    return !(*this == f);
}

Font* Font::create(const string& file, float size) {
    Font_Android* f = new Font_Android();
    JNIEnv* env = getJNIEnv();
    jstring str = env->NewStringUTF(file.c_str());
    jobject obj = env->CallStaticObjectMethod(
        gClassFont, gMethodCreateFontFromFile, str, size);
    f->setSize(size);
    f->setJavaFont(obj);
    env->DeleteLocalRef(str);
    return f;
}

shared_ptr<Font> Font::_create(const string& name, int style, float size) {
    Font_Android* f = new Font_Android();
    JNIEnv* env = getJNIEnv();
    jstring str = env->NewStringUTF(name.c_str());
    jobject obj = env->CallStaticObjectMethod(
        gClassFont, gMethodCreateFontFromName, str, style, size);
    f->setSize(size);
    f->setJavaFont(obj);
    env->DeleteLocalRef(str);
    return shared_ptr<Font>(f);
}

/******************************************* Text layout ******************************************/

void TextLayout_Android::getBounds(_out_ Rect& bounds) {
    JNIEnv* env = getJNIEnv();
    shared_ptr<_jobject> jfont = static_cast<Font_Android*>(_font.get())->getJavaFont();
    jstring str = env->NewStringUTF(wide2utf8(_txt.c_str()).c_str());
    jobject obj = env->CallStaticObjectMethod(
        gClassTextLayout, gMethodGetBounds, str, jfont.get());
    bounds.x = env->GetFloatField(obj, gFieldRectX);
    bounds.y = env->GetFloatField(obj, gFieldRectY);
    bounds.w = env->GetFloatField(obj, gFieldRectW);
    bounds.h = env->GetFloatField(obj, gFieldRectH);

    env->DeleteLocalRef(str);
    env->DeleteLocalRef(obj);

    LOGI("TextLayout::getBounds: [%.2f, %.2f, %.2f, %.2f]", bounds.x, bounds.y, bounds.w, bounds.h);
}

void TextLayout_Android::draw(Graphics2D& g2, float x, float y) {
    const Font* oldFont = g2.getFont();
    g2.setFont(_font.get());
    string str = wide2utf8(_txt.c_str());
    LOGI("TextLayout::draw, text: %s", str.c_str());
    g2.drawText(_txt, x, y);
    g2.setFont(oldFont);
}

shared_ptr<TextLayout> TextLayout::create(const wstring& txt, const shared_ptr<Font>& font) {
    return shared_ptr<TextLayout>(new TextLayout_Android(txt, font));
}

/******************************************* Graphics 2D ******************************************/

/**
 * Keep in sync with io/nano/tex/ActionRecorder
 */
enum Action {
    ACT_setFont,
    ACT_setColor,
    ACT_setStrokeWidth,
    ACT_setStroke,
    ACT_translate,
    ACT_scale,
    ACT_rotate,
    ACT_reset,
    ACT_drawChar,
    ACT_drawText,
    ACT_drawLine,
    ACT_drawRect,
    ACT_fillRect,
    ACT_drawRoundRect,
    ACT_fillRoundRect
};

enum AffineTransformIndex {
    SX,
    SY,
    TX,
    TY,
    R,
    PX,
    PY
};

Graphics2D_Android::Graphics2D_Android(jobject jrecorder) : _font(nullptr), _stroke() {
    _t = new float[9]();
    _t[SX] = _t[SY] = 1;
    _color = black;
    JNIEnv* env = getJNIEnv();
    _jrecorder = env->NewGlobalRef(jrecorder);
}

Graphics2D_Android::~Graphics2D_Android() {
    delete _t;
    JNIEnv* env = getJNIEnv();
    env->DeleteGlobalRef(_jrecorder);
}

void Graphics2D_Android::makeRecord(int action, jobject arg, const jfloat* args, int argsLen) {
    jfloatArray arr = 0;
    JNIEnv* env = getJNIEnv();
    if (argsLen != 0) {
        arr = env->NewFloatArray(argsLen);
        env->SetFloatArrayRegion(arr, 0, argsLen, args);
    }
    env->CallVoidMethod(_jrecorder, gMethodRecord, action, arg, arr);
    if (arr) env->DeleteLocalRef(arr);
}

void Graphics2D_Android::setColor(color c) {
    _color = c;
    jfloat p[] = {static_cast<jfloat>((int)c)};
    makeRecord(ACT_setColor, NULL, p, 1);
}

color Graphics2D_Android::getColor() const {
    return _color;
}

void Graphics2D_Android::setStroke(const Stroke& s) {
    _stroke = s;
    jfloat p[] = {
        s.lineWidth,
        s.miterLimit,
        static_cast<jfloat>(s.cap),
        static_cast<jfloat>(s.join)};
    makeRecord(ACT_setStroke, NULL, p, 4);
}

const Stroke& Graphics2D_Android::getStroke() const {
    return _stroke;
}

void Graphics2D_Android::setStrokeWidth(float w) {
    _stroke.lineWidth = w;
    jfloat p[] = {w};
    makeRecord(ACT_setStrokeWidth, NULL, p, 1);
}

const Font* Graphics2D_Android::getFont() const {
    return _font;
}

void Graphics2D_Android::setFont(const Font* font) {
    _font = font;
    if (font == nullptr) {
        makeRecord(ACT_setFont, NULL, NULL, 0);
        return;
    }
    const Font_Android* f = static_cast<const Font_Android*>(font);
    makeRecord(ACT_setFont, f->getJavaFont().get(), NULL, 0);
}

void Graphics2D_Android::translate(float dx, float dy) {
    _t[TX] += _t[SX] * dx;
    _t[TY] += _t[SY] * dy;
    jfloat p[] = {dx, dy};
    makeRecord(ACT_translate, NULL, p, 2);
}

void Graphics2D_Android::scale(float sx, float sy) {
    _t[SX] *= sx;
    _t[SY] *= sy;
    jfloat p[] = {sx, sy};
    makeRecord(ACT_scale, NULL, p, 2);
}

void Graphics2D_Android::rotate(float angle) {
    rotate(angle, 0, 0);
}

void Graphics2D_Android::rotate(float angle, float px, float py) {
    float r = (float)(angle / PI * 180);
    _t[R] += r;
    _t[PX] = px * _t[SX] + _t[TX];
    _t[PY] = py * _t[SY] + _t[TY];
    jfloat p[] = {angle, px, py};
    makeRecord(ACT_rotate, NULL, p, 3);
}

void Graphics2D_Android::reset() {
    memset(_t, 0, sizeof(float) * 9);
    _t[SX] = _t[SY] = 1;
    makeRecord(ACT_reset, NULL, NULL, 0);
}

float Graphics2D_Android::sx() const {
    return _t[SX];
}

float Graphics2D_Android::sy() const {
    return _t[SY];
}

void Graphics2D_Android::drawChar(wchar_t c, float x, float y) {
    jfloat p[] = {static_cast<jfloat>(c), x, y};
    makeRecord(ACT_drawChar, NULL, p, 3);
}

void Graphics2D_Android::drawText(const wstring& c, float x, float y) {
    string str = wide2utf8(c.c_str());
    JNIEnv* env = getJNIEnv();
    jstring jstr = env->NewStringUTF(str.c_str());
    jfloat p[] = {x, y};
    makeRecord(ACT_drawText, jstr, p, 2);
    env->DeleteLocalRef(jstr);
}

void Graphics2D_Android::drawLine(float x1, float y1, float x2, float y2) {
    jfloat p[] = {x1, y1, x2, y2};
    makeRecord(ACT_drawLine, NULL, p, 4);
}

void Graphics2D_Android::drawRect(float x, float y, float w, float h) {
    jfloat p[] = {x, y, w, h};
    makeRecord(ACT_drawRect, NULL, p, 4);
}

void Graphics2D_Android::fillRect(float x, float y, float w, float h) {
    jfloat p[] = {x, y, w, h};
    makeRecord(ACT_fillRect, NULL, p, 4);
}

void Graphics2D_Android::drawRoundRect(float x, float y, float w, float h, float rx, float ry) {
    jfloat p[] = {x, y, w, h, rx, ry};
    makeRecord(ACT_drawRoundRect, NULL, p, 6);
}

void Graphics2D_Android::fillRoundRect(float x, float y, float w, float h, float rx, float ry) {
    jfloat p[] = {x, y, w, h, rx, ry};
    makeRecord(ACT_fillRoundRect, NULL, p, 6);
}

#endif  // __OS_Android__
