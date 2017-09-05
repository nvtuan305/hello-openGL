#include <jni.h>
#include <GLES2/gl2.h>
#include <android/log.h>
#include "BaseModel.h"
#include "ShapeGlobal.h"

#define LOG_TAG "GLES2JNIWRAPPER_CPP"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

BasicModel *mTriangle = NULL;
BasicModel *mRectangle = NULL;

void init() {
    if (mTriangle != NULL) {
        delete mTriangle;
        mTriangle = NULL;
    }

    if (mRectangle != NULL) {
        delete mRectangle;
        mRectangle = NULL;
    }

    // Create a new triangle model
    mTriangle = createTriangle();
    mRectangle = createRectangle();

    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    glEnable(GL_BLEND);
}

void drawTriangle(bool isSolidBackground) {
    if (mTriangle == NULL)
        return;

    mTriangle->setUseTexture(isSolidBackground);
    mTriangle->draw();
}

void drawSingleLogo(float angle, glm::vec3 trans) {
    mRectangle->setAngleAndTranslation(angle, trans);
    mRectangle->draw();
}

void drawLogo() {
    // Draw the center logo
    float* vertexData = new float[12]{
            -0.6f, 0.50706f, 0.0f,
            -0.6f, -0.50706f, 0.0f,
            0.6f, -0.50706f, 0.0f,
            0.6f, 0.50706f, 0.0f};
    float* texCoords = new float[8]{
            0.0f, 0.005f,
            0.0f, 0.780f,
            1.0f, 0.780f,
            1.0f, 0.005f};
    mRectangle->setVerticesAndTexCoords(vertexData, texCoords);
    drawSingleLogo(0.0f, glm::vec3(0.0f, 0.0f, 0.0f));

    // Draw the bottom logo using translation and rotation transform
    vertexData = new float[12]{
            -0.36f, 0.08f, 0.0f,
            -0.36f, -0.08f, 0.0f,
            0.36f, -0.08f, 0.0f,
            0.36f, 0.08f, 0.0f};
    texCoords = new float[8]{
            0.0005f, 0.8312f,
            0.0005f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.8312f};
    mRectangle->setVerticesAndTexCoords(vertexData, texCoords);
    drawSingleLogo(0.0f, glm::vec3(0.0f, -0.902f, 0.0f));

    // Draw the right logo using translation and rotation transform
    drawSingleLogo(90.0f, glm::vec3(0.902f, 0.0f, 0.0f));

    // Draw the top logo by updating vertices and texture coordinates
    vertexData = new float[12]{
            -0.36f, 1.0f, 0.0f,
            -0.36f, 0.84f, 0.0f,
            0.36f, 0.84f, 0.0f,
            0.36f, 1.0f, 0.0f};
    texCoords = new float[8]{
            0.0005f, 0.8312f,
            0.0005f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.8312f};
    mRectangle->setVerticesAndTexCoords(vertexData, texCoords);
    drawSingleLogo(0.0f, glm::vec3(0.0f, 0.0f, 0.0f));

    // Draw the left logo by updating vertices and texture coordinates
    vertexData = new float[12]{
            -1.0f, -0.36f, 0.0f,
            -0.84f, -0.36f, 0.0f,
            -0.84f, 0.36f, 0.0f,
            -1.0f, 0.36f, 0.0f};
    texCoords = new float[8]{
            0.0005f, 0.8312f,
            0.0005f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.8312f};
    mRectangle->setVerticesAndTexCoords(vertexData, texCoords);
    drawSingleLogo(0.0f, glm::vec3(0.0f, 0.0f, 0.0f));
}

extern "C"
JNIEXPORT void JNICALL
Java_com_blueeagle_helloopengl_gl_MyGLRenderer_jniOnSurfaceCreated__(JNIEnv *env,
                                                                     jobject instance) {
    LOGD("jniOnSurfaceCreated");
    init();
}

extern "C"
JNIEXPORT void JNICALL
Java_com_blueeagle_helloopengl_gl_MyGLRenderer_jniOnSurfaceChanged__II(JNIEnv *env,
                                                                       jobject instance,
                                                                       jint width, jint height) {
    if (mTriangle) mTriangle->resize(width, height);
    if (mRectangle) mRectangle->resize(width, height);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_blueeagle_helloopengl_gl_MyGLRenderer_jniDrawFrame(JNIEnv *env, jobject inst, jint model) {
    if (model == 2)
        glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
    else glClearColor(0.43f, 0.27f, 1.0f, 1.0f);
    glClear(GL_COLOR_BUFFER_BIT);

    switch (model) {
        case 0:
            drawTriangle(true);
            break;

        case 1:
            drawTriangle(false);
            break;

        case 2:
            drawLogo();
            break;
    }
}