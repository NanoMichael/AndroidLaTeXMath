#include "config.h"

#ifdef __OS_Android__

#include "graphics_android.h"
#include "jni_def.h"
#include "jni_help.h"
#include "jni_log.h"

#include "latex.h"

using namespace tex;

static void TeXRender_draw(
    JNIEnv* env, jclass clazz,
    jlong ptr, jobject recorder, jint x, jint y) {
    TeXRender* r = reinterpret_cast<TeXRender*>(ptr);
    LOGI("Draw: %p, in position (%d, %d), with recorder: %p", r, x, y, recorder);
    Graphics2D_Android g2(recorder);
    r->draw(g2, x, y);
}

static jfloat TeXRender_getTextSize(JNIEnv* env, jclass clazz, jlong ptr) {
    TeXRender* r = reinterpret_cast<TeXRender*>(ptr);
    return r->getTextSize();
}

static jint TeXRender_getHeight(JNIEnv* env, jclass clazz, jlong ptr) {
    TeXRender* r = reinterpret_cast<TeXRender*>(ptr);
    return r->getHeight();
}

static jint TeXRender_getDepth(JNIEnv* env, jclass clazz, jlong ptr) {
    TeXRender* r = reinterpret_cast<TeXRender*>(ptr);
    return r->getDepth();
}

static jint TeXRender_getWidth(JNIEnv* env, jclass clazz, jlong ptr) {
    TeXRender* r = reinterpret_cast<TeXRender*>(ptr);
    return r->getWidth();
}

static jfloat TeXRender_getBaseline(JNIEnv* env, jclass clazz, jlong ptr) {
    TeXRender* r = reinterpret_cast<TeXRender*>(ptr);
    return r->getBaseline();
}

static void TeXRender_setTextSize(JNIEnv* env, jclass clazz, jlong ptr, jfloat size) {
    TeXRender* r = reinterpret_cast<TeXRender*>(ptr);
    r->setTextSize((float)size);
}

static void TeXRender_setForeground(JNIEnv* env, jclass clazz, jlong ptr, jint c) {
    TeXRender* r = reinterpret_cast<TeXRender*>(ptr);
    r->setForeground((color)c);
}

static void TeXRender_setWidth(JNIEnv* env, jclass clazz, jlong ptr, jint width, jint align) {
    TeXRender* r = reinterpret_cast<TeXRender*>(ptr);
    r->setWidth(width, align);
}

static void TeXRender_setHeight(JNIEnv* env, jclass clazz, jlong ptr, jint height, jint align) {
    TeXRender* r = reinterpret_cast<TeXRender*>(ptr);
    r->setHeight(height, align);
}

static void TeXRender_finalize(JNIEnv* env, jclass clazz, jlong ptr) {
    TeXRender* r = reinterpret_cast<TeXRender*>(ptr);
    LOGI("TeXRender finalize: %p", r);
    delete r;
}

static JNINativeMethod sMethods[] = {
    {"nDraw", "(JLio/nano/tex/ActionRecorder;II)V", (void*)TeXRender_draw},
    {"nGetTextSize", "(J)F", (void*)TeXRender_getTextSize},
    {"nGetHeight", "(J)I", (void*)TeXRender_getHeight},
    {"nGetDepth", "(J)I", (void*)TeXRender_getDepth},
    {"nGetWidth", "(J)I", (void*)TeXRender_getWidth},
    {"nGetBaseline", "(J)F", (void*)TeXRender_getBaseline},
    {"nSetTextSize", "(JF)V", (void*)TeXRender_setTextSize},
    {"nSetForeground", "(JI)V", (void*)TeXRender_setForeground},
    {"nSetWidth", "(JII)V", (void*)TeXRender_setWidth},
    {"nSetHeight", "(JII)V", (void*)TeXRender_setHeight},
    {"nFinalize", "(J)V", (void*)TeXRender_finalize}};

int registerTeXRender(JNIEnv* env) {
    return jniRegisterNativeMethods(
        env, "io/nano/tex/TeXRender", sMethods, NELEM(sMethods));
}

#endif  // __OS_Android__
