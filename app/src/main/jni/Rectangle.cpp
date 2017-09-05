#include <android/log.h>
#include "GLES2Utils.h"
#include "BaseModel.h"

#define LOG_TAG "RECTANGLE_CPP"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

class Rectangle : public BasicModel {
public:
    Rectangle();

    ~Rectangle();

    virtual void init() override;

    void resize(int width, int height) override;

    virtual void draw() override;
};

Rectangle::Rectangle() {

}

Rectangle::~Rectangle() {

}

void Rectangle::init() {

}

void Rectangle::resize(int width, int height) {
    BasicModel::resize(width, height);
}

void Rectangle::draw() {

}
