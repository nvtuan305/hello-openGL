package com.blueeagle.helloopengl;

/*
 * Created by tuan.nv on 8/28/2017.
 */

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer implements GLSurfaceView.Renderer {

    private Triangle mTriangle, mTriangle1;
    private Rectangle mRectangle;

    /**
     * Store the projection matrix. This is used to project the scene onto a 2D viewport.
     */
    private float[] mProjectionMatrix = new float[16];
    /**
     * Store the model matrix. This matrix is used to move models
     * from object space (where each model can be thought
     * of being located at the center of the universe) to world space.
     */
    private float[] mModelMatrix = new float[16];

    /**
     * Store the view matrix. This can be thought of as our camera.
     * This matrix transforms world space to eye space;
     * it positions things relative to our eye.
     */
    private float[] mViewMatrix = new float[16];

    /**
     * Allocate storage for the final combined matrix.
     * This will be passed into the shader program.
     */
    private float[] mMVPMatrix = new float[16];

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glClearColor(0.43f, 0.27f, 1.0f, 1.0f);
        mTriangle = new Triangle();

        mTriangle1 = new Triangle(
                new float[]{0.0f, 0.0f, 1.0f,
                        0.5f, 0.5f, 1.0f,
                        0.7f, 0.0f, 1.0f},
                new float[]{0.0f, 0.82f, 0.75f, 1.0f});

        mRectangle = new Rectangle();
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        mTriangle.draw();
        mTriangle1.draw();
        mRectangle.draw();
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        // Set the OpenGL to the same size as the surface
        GLES20.glViewport(0, 0, width, height);
    }

    public static int loadShader(int type, String shaderCode) {
        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // Add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }
}
