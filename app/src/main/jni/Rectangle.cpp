#include <android/log.h>
#include "GLES2Utils.h"
#include "BaseModel.h"

#define LOG_TAG "RECTANGLE_CPP"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

class Rectangle : public BasicModel {
private:
    short *mDrawOrder;
    GLuint mElementHandle;

public:
    Rectangle();

    ~Rectangle();

    virtual void init() override;

    virtual void setUseTexture(bool isUseSolidBackground) override;

    void resize(int width, int height) override;

    virtual void draw() override;
};

BasicModel *createRectangle() {
    BasicModel *model = new Rectangle;
    model->init();
    return model;
}

Rectangle::Rectangle() : BasicModel() {
    mVertexData = new GLfloat[12]{
            -0.6f, 0.50706f, 0.0f,
            -0.6f, -0.50706f, 0.0f,
            0.6f, -0.50706f, 0.0f,
            0.6f, 0.50706f, 0.0f
    };
    mTexCoords = new GLfloat[8]{
            0.0f, 0.005f,
            0.0f, 0.780f,
            1.0f, 0.780f,
            1.0f, 0.005f
    };
    mDrawOrder = new short[6]{0, 1, 2, 0, 2, 3};
    mVertexDataSize = 12;
    mTexCoordsDataSize = 8;
    mCountPerVertex = 3;
    mCountPerTexCoords = 2;
}

Rectangle::~Rectangle() {

}

void Rectangle::init() {
    BasicModel::init();
    mViewMatrix = glm::lookAt(
            glm::vec3(0, 0, 3.0f),
            glm::vec3(0, 0, 0),
            glm::vec3(0, 1.0f, 0)
    );

    glGenBuffers(1, &mElementHandle);
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, mElementHandle);
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, 6 * sizeof(short), mDrawOrder, GL_STATIC_DRAW);
    checkGlError("glBufferData - draw elements");

    mTexDataHandle = loadTexture("/sdcard/Pictures/hello-opengl/texture_logo_gianty.png");
    LOGD("Init triangle: SUCCESSFUL...................");
}

void Rectangle::setUseTexture(bool isUseSolidBackground) {
    // TODO
}

void Rectangle::resize(int width, int height) {
    BasicModel::resize(width, height);
    float ratio = (float) width / (float) height;
    //mProjectionMatrix = glm::ortho(-ratio, ratio, -1.0f, 1.0f, 1.0f, 5.0f);
    mProjectionMatrix = glm::frustum(-ratio, ratio, -1.0f, 1.0f, 2.0f, 5.0f);
}

void Rectangle::draw() {
    BasicModel::passDataToOpenGl();
    glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_SHORT, 0);
    checkGlError("glDrawElements");
    glDisableVertexAttribArray(mPositionHandle);
    glDisableVertexAttribArray(mTexCoordsHandle);
}
