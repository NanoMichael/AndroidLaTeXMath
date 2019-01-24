#ifndef JNI_LOG_H_INCLUDED
#define JNI_LOG_H_INCLUDED

#include <jni.h>
#include <android/log.h>
#define __JNI_DEBUG
#define LOG_TAG "tex"

#ifndef LOGD
    #define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#endif

#ifndef LOGE
    #define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#endif

#ifndef LOGI
    #define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#endif

#endif // JNI_LOG_H_INCLUDED
