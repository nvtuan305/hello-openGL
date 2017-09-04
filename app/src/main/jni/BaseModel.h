#ifndef BASEMODEL_H
#define BASEMODEL_H

#include <GLES2/gl2.h>
#include <EGL/egl.h>
#include "glm/glm.hpp"
#include "glm/gtc/matrix_transform.hpp"
#include "glm/gtc/type_ptr.hpp"

#define VB_COUNT 2
#define VB_POSITION 0
#define VB_TEX_COORDS 1

class BasicModel {

protected:
    EGLContext mEglContext;
    GLfloat *mVertexData;
    GLfloat *mTexCoords;

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

    virtual void init() = 0;

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
