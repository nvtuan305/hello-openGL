#include <android/log.h>
#include "GLES2Utils.h"

#define LOG_TAG "TRIANGLE_CPP"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

const char mVertexShaderCode[] =
        "attribute vec4 a_Position;"
                "uniform mat4 u_MVPMatrix;"
                "attribute vec2 a_TexCoords;"
                "varying vec2 v_TexCoords;"
                "void main() {"
                "    v_TexCoords = a_TexCoords;"
                "    gl_Position = u_MVPMatrix * a_Position;"
                "}";

const char mFragShaderCode[] =
        "precision mediump float;"
                "varying vec2 v_TexCoords;"
                "uniform sampler2D u_Texture;"
                "void main() {"
                "    gl_FragColor = texture2D(u_Texture, v_TexCoords);"
                "}";

class Triangle : public BasicModel {

public:
    Triangle();

    ~Triangle();

    virtual void init();

    void resize(int width, int height) override;

    virtual void draw();
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
    mTexCoords = new GLfloat[6]{
            0.5f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f
    };
    mProgram = 0;
    mPositionHandle = 0;
    mMvpMatrixHandle = 0;
    mTexDataHandle = 0;
    mTexCoordsHandle = 0;
    mTexSampler2DHandle = 0;
}

Triangle::~Triangle() {
    // TODO
}

void Triangle::init() {
    // Create program only one time
    if (mProgram == 0) {
        mProgram = createProgram(mVertexShaderCode, mFragShaderCode);
        if (mProgram == 0) return;
    }

    mPositionHandle = glGetAttribLocation(mProgram, "a_Position");
    mTexCoordsHandle = glGetAttribLocation(mProgram, "a_TexCoords");
    mMvpMatrixHandle = glGetUniformLocation(mProgram, "u_MVPMatrix");
    mTexSampler2DHandle = glGetUniformLocation(mProgram, "u_Texture");

    // Bind vertices data, texture coordinates data
    glGenBuffers(VB_COUNT, mVB);
    glBindBuffer(GL_ARRAY_BUFFER, mVB[VB_POSITION]);
    glBufferData(GL_ARRAY_BUFFER, 9 * sizeof(GLfloat), mVertexData, GL_STATIC_DRAW);
    checkGlError("glBufferData - vertex data");

    glBindBuffer(GL_ARRAY_BUFFER, mVB[VB_TEX_COORDS]);
    glBufferData(GL_ARRAY_BUFFER, 6 * sizeof(GLfloat), mTexCoords, GL_STATIC_DRAW);
    checkGlError("glBufferData - texture coordinates data");

    mViewMatrix = glm::lookAt(
            glm::vec3(0, 0, 3.0f),
            glm::vec3(0, 0, 0),
            glm::vec3(0, 1.0f, 0)
    );

    GLubyte solidColor[] = {0, 206, 203, 255};
    mTexDataHandle = loadTextureColor(solidColor);

    LOGD("Init triangle: SUCCESSFUL...................");
}

void Triangle::resize(int width, int height) {
    BasicModel::resize(width, height);
    float ratio = (float) width / (float) height;
    //mProjectionMatrix = glm::frustum(-ratio, ratio, -1.0f, 1.0f, 3.0f, 5.0f);
    mProjectionMatrix = glm::ortho(-ratio, ratio, -1.0f, 1.0f, 1.0f, 5.0f);
}

void Triangle::draw() {
    /*if (!glIsProgram(mProgram)) {
        LOGE("Program has error");
        glDeleteProgram(mProgram);
        init();
    }*/

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
    glUniform1f(mTexSampler2DHandle, 0);

    // Pass MVP matrix data
    calculateMvpMatrix(0.0f, glm::vec3(0.0f, 0.0f, 0.0f));
    glUniformMatrix4fv(mMvpMatrixHandle, 1, GL_FALSE, glm::value_ptr(mMvpMatrix));
    /*for (int i = 0; i < 4; ++i) {
        LOGD("%f %f %f %f\n", mMvpMatrix[i][0], mMvpMatrix[i][1], mMvpMatrix[i][2],
             mMvpMatrix[i][3]);
    }*/
    checkGlError("glUniformMatrix4fv - pass MVP matrix data");

    /*glUniform4fv(mColorHandle, 1, mColorData);
    checkGlError("glUniform4fv");*/

    glDrawArrays(GL_TRIANGLES, 0, 3);

    checkGlError("glDrawArrays");
    glDisableVertexAttribArray(mPositionHandle);
    glDisableVertexAttribArray(mTexCoordsHandle);
}
