#include "jni_help.h"
#include "jni_log.h"

#include <string>

template <typename T>
class scoped_local_ref {
private:
    C_JNIEnv* _env;
    T _localRef;

    scoped_local_ref(const scoped_local_ref&);
    void operator=(const scoped_local_ref&);

public:
    scoped_local_ref(C_JNIEnv* env, T localRef = nullptr)
        : _env(env), _localRef(localRef) {}

    void reset(T localRef = nullptr) {
        if (_localRef != nullptr) {
            (*_env)->DeleteLocalRef(reinterpret_cast<JNIEnv*>(_env), _localRef);
            _localRef = nullptr;
        }
    }

    T get() const {
        return _localRef;
    }

    ~scoped_local_ref() {
        reset();
    }
};

static jclass findClass(C_JNIEnv* env, const char* className) {
    JNIEnv* e = reinterpret_cast<JNIEnv*>(env);
    return (*env)->FindClass(e, className);
}

extern "C" int jniRegisterNativeMethods(
    C_JNIEnv* env, const char* className,
    const JNINativeMethod* gMethods, int numMethods) {
    JNIEnv* e = reinterpret_cast<JNIEnv*>(env);

    LOGI("Registering %s's %d native methods...", className, numMethods);

    scoped_local_ref<jclass> c(env, findClass(env, className));
    if (c.get() == NULL) {
        char* msg;
        asprintf(&msg, "Native registration unable to find class '%s'; aborting...", className);
        e->FatalError(msg);
    }

    if ((*env)->RegisterNatives(e, c.get(), gMethods, numMethods) < 0) {
        char* msg;
        asprintf(&msg, "RegisterNatives failed for '%s'; aborting...", className);
        e->FatalError(msg);
    }

    return 0;
}
