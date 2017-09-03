#ifndef BASEMODEL_H
#define BASEMODEL_H

#include <GLES2/gl2.h>
#include <EGL/egl.h>
#include "glm/detail/type_mat.hpp"
#include "glm/detail/type_mat4x4.hpp"

class BasicModel {

protected:
    GLfloat *mVertexData;
    EGLContext mEglContext;
    GLuint mProgram;
    GLuint mVB;
    GLint mPositionHandle;
    GLint mMvpMatrixHandle;
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

        if (eglGetCurrentContext() == mEglContext) {
            glDeleteBuffers(1, &mVB);
            glDeleteProgram(mProgram);
        }
    }

    virtual void init() = 0;

    virtual void resize(int width, int height) {
        glViewport(0, 0, width, height);
    }

    virtual void draw() = 0;
};

#endif