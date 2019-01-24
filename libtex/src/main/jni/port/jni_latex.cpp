#include "jni_def.h"
#include "jni_help.h"
#include "jni_log.h"
#include "latex.h"

#include <ctype.h>
#include <string>

using namespace std;
using namespace tex;

static jboolean LaTeX_init(JNIEnv* env, jclass clazz, jstring resDir) {
    try {
        const char* rootDir = env->GetStringUTFChars(resDir, NULL);
        LOGI("Resources root dir: %s, initialize now...", rootDir);
        LaTeX::init(rootDir);
        env->ReleaseStringUTFChars(resDir, rootDir);
        LOGI("Initialized successfully.");
        return true;
    } catch (ex_tex& e) {
        LOGE("Failed to initialize the LaTeX engine, caused by: %s", e.what());
        return false;
    }
}

static void LaTeX_release(JNIEnv* env, jclass clazz) {
    LOGI("Release LaTeX engine...");
    LaTeX::release();
}

static jlong LaTeX_parse(
    JNIEnv* env, jclass clazz,
    jstring ltx, jint width, jfloat textSize, jfloat lineSpace, jint foreground) {
    wstring value;
    const jchar* raw = env->GetStringChars(ltx, NULL);
    size_t len = env->GetStringLength(ltx);
    value.assign(raw, raw + len);
    env->ReleaseStringChars(ltx, raw);
    const char* str = env->GetStringUTFChars(ltx, NULL);
    LOGI("Parse: %s", str);
    env->ReleaseStringUTFChars(ltx, str);
    try {
        TeXRender* r = LaTeX::parse(value, width, textSize, lineSpace, foreground);
        return reinterpret_cast<long>(r);
    } catch (exception& e) {
        LOGE("Failed to parse LaTeX, caused by: %s", e.what());
        return 0;
    }
}

static void LaTeX_setDebug(JNIEnv* env, jclass clazz, jboolean debug) {
    bool b = static_cast<bool>(debug);
    LaTeX::setDebug(b);
}

static JNINativeMethod sMethods[] = {
    {"nInit", "(Ljava/lang/String;)Z", (void*)LaTeX_init},
    {"nFree", "()V", (void*)LaTeX_release},
    {"nParse", "(Ljava/lang/String;IFFI)J", (void*)LaTeX_parse},
    {"nSetDebug", "(Z)V", (void*)LaTeX_setDebug}};

int registerLaTeX(JNIEnv* env) {
    return jniRegisterNativeMethods(
        env, "io/nano/tex/LaTeX", sMethods, NELEM(sMethods));
}
