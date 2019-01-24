#include "jni_def.h"
#include "jni_log.h"

JavaVM* gJVM;

/** io/nano/tex/Rect */
jfieldID gFieldRectX, gFieldRectY, gFieldRectW, gFieldRectH;

/** io/nano/tex/TextLayout */
jclass gClassTextLayout;
jmethodID gMethodGetBounds;

/** io/nano/tex/Font */
jclass gClassFont;
jmethodID gMethodDeriveFont;
jmethodID gMethodCreateFontFromName;
jmethodID gMethodCreateFontFromFile;

/** io/nano/tex/ActionRecorder */
jmethodID gMethodRecord;

JNIEnv* getJNIEnv() {
    JNIEnv* env = 0;
    gJVM->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6);
    return env;
}

static void load(JNIEnv* env) {
    // io/nano/tex/Rect
    jclass rect = env->FindClass("io/nano/tex/Rect");
    gFieldRectX = env->GetFieldID(rect, "x", "F");
    gFieldRectY = env->GetFieldID(rect, "y", "F");
    gFieldRectW = env->GetFieldID(rect, "w", "F");
    gFieldRectH = env->GetFieldID(rect, "h", "F");
    // io/nano/tex/TextLayout
    jclass layout = env->FindClass("io/nano/tex/TextLayout");
    gMethodGetBounds = env->GetStaticMethodID(
        layout,
        "getBounds",
        "(Ljava/lang/String;Lio/nano/tex/Font;)Lio/nano/tex/Rect;");
    gClassTextLayout = (jclass)env->NewGlobalRef(layout);
    // io/nano/tex/Font
    jclass font = env->FindClass("io/nano/tex/Font");
    gClassFont = (jclass)env->NewGlobalRef(font);
    gMethodDeriveFont = env->GetMethodID(
        gClassFont,
        "deriveFont",
        "(I)Lio/nano/tex/Font;");
    gMethodCreateFontFromName = env->GetStaticMethodID(
        gClassFont,
        "create",
        "(Ljava/lang/String;IF)Lio/nano/tex/Font;");
    gMethodCreateFontFromFile = env->GetStaticMethodID(
        gClassFont,
        "create",
        "(Ljava/lang/String;F)Lio/nano/tex/Font;");
    // io/nano/tex/ActionRecorder
    jclass recorder = env->FindClass("io/nano/tex/ActionRecorder");
    gMethodRecord = env->GetMethodID(
        recorder,
        "record",
        "(ILjava/lang/Object;[F)V");
}

extern int registerLaTeX(JNIEnv* env);
extern int registerTeXRender(JNIEnv* env);

extern "C" JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) {
    gJVM = vm;

    LOGI("Load jni, initialize java methods...");

    JNIEnv* env = 0;
    vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6);

    registerLaTeX(env);
    registerTeXRender(env);

    load(env);

    return JNI_VERSION_1_6;
}
