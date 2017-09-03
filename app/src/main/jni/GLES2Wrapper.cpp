#include <jni.h>
#include <GLES2/gl2.h>
#include <android/log.h>
#include "GLES2Utils.h"

#define LOG_TAG "GLES2JNIWRAPPER_CPP"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

BasicModel *mTriangle = NULL;

void init() {
    if (mTriangle != NULL) {
        delete mTriangle;
        mTriangle = NULL;
    }

    // Create a new triangle model
    mTriangle = createTriangle();
}

extern "C" {
JNIEXPORT void JNICALL
Java_com_blueeagle_helloopengl_gl_MyGLRenderer_jniOnSurfaceCreated__(JNIEnv *env, jobject instance);

JNIEXPORT void JNICALL
Java_com_blueeagle_helloopengl_gl_MyGLRenderer_jniOnSurfaceChanged__II(JNIEnv *env, jobject instance,
                                                                    jint width, jint height);

JNIEXPORT void JNICALL
Java_com_blueeagle_helloopengl_gl_MyGLRenderer_jniDrawFrame(JNIEnv *env, jobject instance);
};

JNIEXPORT void JNICALL
Java_com_blueeagle_helloopengl_gl_MyGLRenderer_jniOnSurfaceCreated__(JNIEnv *env, jobject instance) {
    LOGD("jniOnSurfaceCreated");
    init();
}

JNIEXPORT void JNICALL
Java_com_blueeagle_helloopengl_gl_MyGLRenderer_jniOnSurfaceChanged__II(JNIEnv *env, jobject instance,
                                                                    jint width, jint height) {
    if (mTriangle) {
        LOGD("jniOnSurfaceChanged");
        mTriangle->resize(width, height);
    }
}

JNIEXPORT void JNICALL
Java_com_blueeagle_helloopengl_gl_MyGLRenderer_jniDrawFrame(JNIEnv *env, jobject instance) {
    if (mTriangle) {
        LOGD("jniDrawFrame");
        glClearColor(0.43f, 0.27f, 1.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT);
        mTriangle->draw();
    }
}