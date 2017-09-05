#ifndef BASEMODEL_H
#define BASEMODEL_H

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
    GLfloat *mTexCoords;
    GLuint mTexCoordsDataSize; // Size of mTexCoords

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

public:
    BasicModel() {
        mEglContext = eglGetCurrentContext();
        mVertexDataSize = 0;
        mTexCoordsDataSize = 0;
        mProgram = 0;
        mPositionHandle = 0;
        mMvpMatrixHandle = 0;
        mTexDataHandle = 0;
        mTexCoordsHandle = 0;
        mTexSampler2DHandle = 0;
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

    virtual void resize(int width, int height) {
        glViewport(0, 0, width, height);
    }

    virtual void draw() = 0;

    virtual void calculateMvpMatrix(float angle, glm::vec3 trans) {
        // model * view * projection
        mModelMatrix = glm::mat4(1.0f);
        mModelMatrix = glm::translate(mModelMatrix, trans);
        mModelMatrix = glm::rotate(mModelMatrix, angle, glm::vec3(0, 0, 1.0f));
        mMvpMatrix = mProjectionMatrix * mViewMatrix * mModelMatrix;
    }
};

#endif
