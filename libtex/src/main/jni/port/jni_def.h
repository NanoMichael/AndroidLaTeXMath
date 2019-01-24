#ifndef JNI_DEF_INCLUDED
#define JNI_DEF_INCLUDED

#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif

/** io/nano/tex/Rect */
extern jfieldID gFieldRectX, gFieldRectY, gFieldRectW, gFieldRectH;

/** io/nano/tex/TextLayout */
extern jclass gClassTextLayout;
extern jmethodID gMethodGetBounds;

/** io/nano/tex/Font */
extern jclass gClassFont;
extern jmethodID gMethodDeriveFont;
extern jmethodID gMethodCreateFontFromName;
extern jmethodID gMethodCreateFontFromFile;

/** io/nano/tex/ActionRecorder */
extern jmethodID gMethodRecord;

#ifdef __cplusplus
}
#endif

/** Get the jni environment */
JNIEnv* getJNIEnv();

#endif  // JNI_DEF_INCLUDED
