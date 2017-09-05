#ifndef BASEMODEL_H
#define BASEMODEL_H

#include "glm/glm.hpp"
#include "glm/gtc/matrix_transform.hpp"
#include "glm/gtc/type_ptr.hpp"
#include "GLES2Utils.h"

#define VB_COUNT 2
#define VB_POSITION 0
#define VB_TEX_COORDS 1

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

class BasicModel {

protected:
    EGLContext mEglContext;
    GLfloat *mVertexData;
    GLuint mVertexDataSize; // Size of mVertexData
    GLuint mCountPerVertex;
    GLfloat *mTexCoords;
    GLuint mTexCoordsDataSize; // Size of mTexCoords
    GLuint mCountPerTexCoords;

    GLuint mProgram;
    GLuint mVB[VB_COUNT];

    GLint mPositionHandle;
    GLint mMvpMatrixHandle;
    GLuint mTexDataHandle;
    GLint mTexCoordsHandle;
    GLint mTexSampler2DHandle;

    glm::mat4 mModelMatrix;
    glm::mat4 mViewMatrix;
    glm::mat4 mProjectionMatrix;
    glm::mat4 mMvpMatrix;
    glm::vec3 mTrans;
    float mAngle;

public:
    BasicModel() {
        mEglContext = eglGetCurrentContext();
        mVertexDataSize = 0;
        mCountPerVertex = 0;
        mCountPerTexCoords = 0;
        mTexCoordsDataSize = 0;
        mProgram = 0;
        mPositionHandle = 0;
        mMvpMatrixHandle = 0;
        mTexDataHandle = 0;
        mTexCoordsHandle = 0;
        mTexSampler2DHandle = 0;
        mAngle = 0.0f;
        mTrans = glm::vec3(0.0f, 0.0f, 0.0f);
    }

    virtual ~BasicModel() {
        if (mVertexData) {
            delete[] mVertexData;
            mVertexData = NULL;
        }

        if (mTexCoords) {
            delete[] mTexCoords;
            mTexCoords = NULL;
        }

        if (eglGetCurrentContext() == mEglContext) {
            for (int i = 0; i < VB_COUNT; ++i) {
                glDeleteBuffers(1, &mVB[i]);
            }

            glDeleteProgram(mProgram);
        }
    }

    virtual void init() {
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
        bindBufferData();
    }

    void bindBufferData() {
        // Delete buffer before generating new buffer
        glDeleteBuffers(VB_COUNT, mVB);

        glGenBuffers(VB_COUNT, mVB);
        glBindBuffer(GL_ARRAY_BUFFER, mVB[VB_POSITION]);
        glBufferData(GL_ARRAY_BUFFER, mVertexDataSize * sizeof(GLfloat), mVertexData,
                     GL_STATIC_DRAW);
        checkGlError("glBufferData - vertex data");

        glBindBuffer(GL_ARRAY_BUFFER, mVB[VB_TEX_COORDS]);
        glBufferData(GL_ARRAY_BUFFER, mTexCoordsDataSize * sizeof(GLfloat), mTexCoords,
                     GL_STATIC_DRAW);
        checkGlError("glBufferData - texture coordinates data");
    }

    virtual void setUseTexture(bool isUseSolidBackground) = 0;

    virtual void setVerticesAndTexCoords(GLfloat *vertexData, GLfloat *texCoordsData) {
        if (mVertexData && vertexData) {
            delete[] mVertexData;
            mVertexData = NULL;
            mVertexData = vertexData;
        }

        if (mTexCoords && texCoordsData) {
            delete[] mTexCoords;
            mTexCoords = NULL;
            mTexCoords = texCoordsData;
        }

        bindBufferData();
    }

    virtual void setAngleAndTranslation(float angle, glm::vec3 trans) {
        mAngle = angle;
        mTrans = trans;
    }

    virtual void resize(int width, int height) {
        glViewport(0, 0, width, height);
    }

    virtual void draw() = 0;

    virtual void passDataToOpenGl() {
        glUseProgram(mProgram);
        checkGlError("glUseProgram");

        // Pass vertex data
        glBindBuffer(GL_ARRAY_BUFFER, mVB[VB_POSITION]);
        glEnableVertexAttribArray(mPositionHandle);
        glVertexAttribPointer(mPositionHandle, mCountPerVertex, GL_FLOAT, GL_FALSE,
                              mCountPerVertex * sizeof(GLfloat), 0);
        checkGlError("glVertexAttribPointer - pass vertex data");

        // Pass texture coordinate data
        glBindBuffer(GL_ARRAY_BUFFER, mVB[VB_TEX_COORDS]);
        glEnableVertexAttribArray(mTexCoordsHandle);
        glVertexAttribPointer(mTexCoordsHandle, mCountPerTexCoords, GL_FLOAT, GL_FALSE,
                              mCountPerTexCoords * sizeof(GLfloat), 0);
        checkGlError("glVertexAttribPointer - pass texture coordinates data");

        // Pass texture data
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, mTexDataHandle);
        glUniform1i(mTexSampler2DHandle, 0);
        checkGlError("glUniform1i - pass texture data");

        // Pass MVP matrix data
        calculateMvpMatrix(mAngle, mTrans);
        glUniformMatrix4fv(mMvpMatrixHandle, 1, GL_FALSE, glm::value_ptr(mMvpMatrix));
        checkGlError("glUniformMatrix4fv - pass MVP matrix data");
    }

    virtual void calculateMvpMatrix(GLfloat angle, glm::vec3 trans) {
        // model * view * projection
        mModelMatrix = glm::mat4(1.0f);
        mModelMatrix = glm::translate(mModelMatrix, trans);
        mModelMatrix = glm::rotate(mModelMatrix, glm::radians(angle), glm::vec3(0, 0, 1.0f));
        mMvpMatrix = mProjectionMatrix * mViewMatrix * mModelMatrix;
    }
};

#endif
