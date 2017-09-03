#include <android/log.h>
#include "glm/glm.hpp"
#include "glm/gtc/matrix_transform.hpp"
#include "glm/gtc/type_ptr.hpp"
#include "GLES2Utils.h"

#define LOG_TAG "TRIANGLE_CPP"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

const char mVertexShaderCode[] =
        "attribute vec4 v_Position;"
                "uniform mat4 v_MVPMatrix;"
                "void main() {"
                "    gl_Position = v_MVPMatrix * v_Position;"
                "}";

const char mFragShaderCode[] =
        "precision mediump float;"
                "uniform vec4 v_Color;"
                "void main() {"
                "    gl_FragColor = v_Color;"
                "}";

class Triangle : public BasicModel {

private:
    GLfloat *mColorData;
    GLint mColorHandle;

public:
    Triangle();

    ~Triangle();

    virtual void init();

    void resize(int width, int height) override;

    virtual void draw();

    virtual void calculateMvpMatrix(float angle, glm::vec3 trans);
};

BasicModel *createTriangle() {
    BasicModel *model = new Triangle;
    model->init();
    return model;
}

Triangle::Triangle() {
    mVertexData = new GLfloat[9]{
            0.0f, 0.5f, 0.0f,
            -0.5f, -0.3f, 0.0f,
            0.5f, -0.3f, 0.0f
    };
    mColorData = new GLfloat[4]{1.0f, 1.0f, 1.0f, 1.0f};
    mProgram = 0;
    mPositionHandle = 0;
    mColorHandle = 0;
    mMvpMatrixHandle = 0;
    mVB = 0;
}

Triangle::~Triangle() {
    if (mColorData) {
        delete[] mColorData;
        mColorData = NULL;
    }
}

void Triangle::init() {
    if (mProgram != 0)
        glDeleteProgram(mProgram);

    mProgram = createProgram(mVertexShaderCode, mFragShaderCode);
    if (mProgram == 0) return;

    mPositionHandle = glGetAttribLocation(mProgram, "v_Position");
    mMvpMatrixHandle = glGetUniformLocation(mProgram, "v_MVPMatrix");
    mColorHandle = glGetUniformLocation(mProgram, "v_Color");

    glGenBuffers(1, &mVB);
    glBindBuffer(GL_ARRAY_BUFFER, mVB);
    glBufferData(GL_ARRAY_BUFFER, 9 * sizeof(GLfloat), mVertexData, GL_STATIC_DRAW);
    checkGlError("glBufferData-vbo");

    mViewMatrix = glm::lookAt(
            glm::vec3(0, 0, 3.0f),
            glm::vec3(0, 0, 0),
            glm::vec3(0, 1.0f, 0)
    );

    LOGD("Init triangle: SUCCESSFUL...................");
}

void Triangle::resize(int width, int height) {
    BasicModel::resize(width, height);
    float ratio = (float) width / (float) height;
    //mProjectionMatrix = glm::frustum(-ratio, ratio, -1.0f, 1.0f, 3.0f, 5.0f);
    mProjectionMatrix = glm::ortho(-ratio, ratio, -1.0f, 1.0f, 1.0f, 5.0f);
}

void Triangle::draw() {
    if (!glIsProgram(mProgram)) {
        LOGE("Program has error");
        glDeleteProgram(mProgram);
        init();
    }

    glUseProgram(mProgram);
    checkGlError("glUseProgram");

    glBindBuffer(GL_ARRAY_BUFFER, mVB);
    glEnableVertexAttribArray(mPositionHandle);
    glVertexAttribPointer(mPositionHandle, 3, GL_FLOAT, GL_FALSE, 3 * sizeof(GLfloat), 0);
    checkGlError("glVertexAttribPointer");

    calculateMvpMatrix(0.0f, glm::vec3(0.0f, 0.0f, 0.0f));
    glUniformMatrix4fv(mMvpMatrixHandle, 1, GL_FALSE, &mMvpMatrix[0][0]);
    checkGlError("glUniformMatrix4fv-MvpMatrix");

    glUniform4fv(mColorHandle, 1, mColorData);
    checkGlError("glUniform4fv");

    glDrawArrays(GL_TRIANGLES, 0, 3);

    checkGlError("glDrawArrays");
    glDisableVertexAttribArray(mPositionHandle);
}

void Triangle::calculateMvpMatrix(float angle, glm::vec3 trans) {
    // model * view * projection
    mModelMatrix = glm::mat4(1.0f);
    mModelMatrix = glm::translate(mModelMatrix, trans);
    mModelMatrix = glm::rotate(mModelMatrix, angle, glm::vec3(0, 0, 1.0f));
    mMvpMatrix = mProjectionMatrix * mViewMatrix * mModelMatrix;
}
