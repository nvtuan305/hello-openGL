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

    void resize(int width, int height) override;

    virtual void draw() override;
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
    LOGD("%d %d %d", mPositionHandle, mTexDataHandle, mProgram);
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
    //mTexDataHandle = loadTextureColor(solidColor);
    mTexDataHandle = loadTexture("/sdcard/Giddylizer/texure.jpg");

    LOGD("Init triangle: SUCCESSFUL...................");
}

void Triangle::resize(int width, int height) {
    BasicModel::resize(width, height);
    float ratio = (float) width / (float) height;
    //mProjectionMatrix = glm::frustum(-ratio, ratio, -1.0f, 1.0f, 3.0f, 5.0f);
    mProjectionMatrix = glm::ortho(-ratio, ratio, -1.0f, 1.0f, 1.0f, 5.0f);
}

void Triangle::draw() {
    glUseProgram(mProgram);
    checkGlError("glUseProgram");

    // Pass vertex data
    glBindBuffer(GL_ARRAY_BUFFER, mVB[VB_POSITION]);
    glEnableVertexAttribArray(mPositionHandle);
    glVertexAttribPointer(mPositionHandle, 3, GL_FLOAT, GL_FALSE, 3 * sizeof(GLfloat), 0);
    checkGlError("glVertexAttribPointer - pass vertex data");

    // Pass texture coordinate data
    glBindBuffer(GL_ARRAY_BUFFER, mVB[VB_TEX_COORDS]);
    glEnableVertexAttribArray(mTexCoordsHandle);
    glVertexAttribPointer(mTexCoordsHandle, 2, GL_FLOAT, GL_FALSE, 2 * sizeof(GLfloat), 0);
    checkGlError("glVertexAttribPointer - pass texture coordinates data");

    // Pass texture data
    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, mTexDataHandle);
    glUniform1i(mTexSampler2DHandle, 0);
    checkGlError("glUniform1i - pass texture data");

    // Pass MVP matrix data
    calculateMvpMatrix(0.0f, glm::vec3(0.0f, 0.0f, 0.0f));
    glUniformMatrix4fv(mMvpMatrixHandle, 1, GL_FALSE, glm::value_ptr(mMvpMatrix));
    checkGlError("glUniformMatrix4fv - pass MVP matrix data");

    glDrawArrays(GL_TRIANGLES, 0, 3);

    checkGlError("glDrawArrays");
    glDisableVertexAttribArray(mPositionHandle);
    glDisableVertexAttribArray(mTexCoordsHandle);
}
