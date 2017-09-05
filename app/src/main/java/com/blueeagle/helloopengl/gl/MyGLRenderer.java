package com.blueeagle.helloopengl.gl;

/*
 * Created by tuan.nv on 8/28/2017.
 */

import android.content.Context;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer implements GLSurfaceView.Renderer {
    private int mCurrentModel = 0;
    private final String TAG = "MyGLRenderer";

    public MyGLRenderer(Context context) {

    }

    /**
     * Set triangle type to draw
     *
     * @param mCurrentModel value in {0, 1, 2}
     */
    public void setModelToDraw(int mCurrentModel) {
        this.mCurrentModel = mCurrentModel;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        jniOnSurfaceCreated();
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        jniOnSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        jniDrawFrame(mCurrentModel);
    }

    /*
     * -----------------------------------------------------------
     * Drawing function in JNI
     * -----------------------------------------------------------
     */
    static {
        System.loadLibrary("GLES2NativeLib");
    }

    private native void jniOnSurfaceCreated();

    private native void jniOnSurfaceChanged(int width, int height);

    private native void jniDrawFrame(int model);
}
