#ifndef JNI_HELP_INCLUDED
#define JNI_HELP_INCLUDED

#include <jni.h>

#ifndef NELEM
#define NELEM(x) ((int)(sizeof(x) / sizeof((x)[0])))
#endif

#ifdef __cplusplus
extern "C" {
#endif

/*
 * Register one or more native methods with a particular class.
 * "className" looks like "java/lang/String". Aborts on failure.
 * TODO: fix all callers and change the return type to void.
 */
int jniRegisterNativeMethods(
    C_JNIEnv* env, const char* className,
    const JNINativeMethod* gMethods, int numMethods);

#ifdef __cplusplus
}
#endif

#if defined(__cplusplus)
inline int jniRegisterNativeMethods(
    JNIEnv* env, const char* className,
    const JNINativeMethod* gMethods, int numMethods) {
    return jniRegisterNativeMethods(&env->functions, className, gMethods, numMethods);
}
#endif

#endif  // JNI_HELP_INCLUDED
