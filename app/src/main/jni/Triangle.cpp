#include <android/log.h>
#include "GLES2Utils.h"
#include "BaseModel.h"

#define LOG_TAG "TRIANGLE_CPP"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

class Triangle : public BasicModel {

public:
    Triangle();

    ~Triangle();

    virtual void init() override;

    void setUseTexture(bool isUseSolidBackground) override;

    void resize(int width, int height) override;

    void draw() override;
};

BasicModel *createTriangle() {
    BasicModel *model = new Triangle;
    model->init();
    return model;
}

Triangle::Triangle() : BasicModel() {
    mVertexData = new GLfloat[9]{
            0.0f, 0.5f, 0.0f,
            -0.5f, -0.3f, 0.0f,
            0.5f, -0.3f, 0.0f
    };
    mTexCoords = new GLfloat[6]{
            0.5f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f
    };
    mVertexDataSize = 9;
    mTexCoordsDataSize = 6;
    mCountPerVertex = 3;
    mCountPerTexCoords = 2;
}

Triangle::~Triangle() {
    // TODO
}

void Triangle::init() {
    // Create program, get handle to GLSL variables
    BasicModel::init();
    mViewMatrix = glm::lookAt(
            glm::vec3(0, 0, 3.0f),
            glm::vec3(0, 0, 0),
            glm::vec3(0, 1.0f, 0)
    );

    GLubyte solidColor[] = {0, 206, 203, 255};
    mTexDataHandle = loadTextureColor(solidColor);

    LOGD("Init triangle: SUCCESSFUL...................");
}

void Triangle::setUseTexture(bool isUseSolidBackground) {
    // Delete the previous texture
    glDeleteTextures(1, &mTexDataHandle);
    checkGlError("glDeleteTextures");

    if (!isUseSolidBackground) {
        mTexDataHandle = loadTexture("/sdcard/Giddylizer/texure.jpg");
    } else {
        GLubyte solidColor[] = {0, 206, 203, 255};
        mTexDataHandle = loadTextureColor(solidColor);
    }
}

void Triangle::resize(int width, int height) {
    BasicModel::resize(width, height);
    float ratio = (float) width / (float) height;
    //mProjectionMatrix = glm::frustum(-ratio, ratio, -1.0f, 1.0f, 3.0f, 5.0f);
    mProjectionMatrix = glm::ortho(-ratio, ratio, -1.0f, 1.0f, 1.0f, 5.0f);
}

void Triangle::draw() {
    BasicModel::passDataToOpenGl();
    glDrawArrays(GL_TRIANGLES, 0, 3);

    checkGlError("glDrawArrays");
    glDisableVertexAttribArray(mPositionHandle);
    glDisableVertexAttribArray(mTexCoordsHandle);
}
