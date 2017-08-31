package com.blueeagle.helloopengl;

/*
 * Created by tuan.nv on 8/28/2017.
 */

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import java.lang.ref.WeakReference;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer implements GLSurfaceView.Renderer {

    private Triangle mTriangle, mTriangle1;
    private Rectangle mRectangle;
    private final String TAG = "MyGLRenderer";

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

    private WeakReference<Context> contextWeakReference;

    public MyGLRenderer(Context context) {
        contextWeakReference = new WeakReference<>(context);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glClearColor(0.43f, 0.27f, 1.0f, 1.0f);
        mTriangle = new Triangle();

        mTriangle1 = new Triangle(
                new float[]{0.0f, 0.0f, 1.0f,
                        0.5f, 0.5f, 1.0f,
                        0.7f, 0.0f, 1.0f},
                new float[]{0.0f, 0.82f, 0.75f, 1.0f});

        //mRectangle = new Rectangle();
        Context context = contextWeakReference.get();
        if (context != null) {
            mRectangle = new Rectangle(context, R.drawable.texture_basic_1);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        mTriangle.draw();
        mTriangle1.draw();

        if (mRectangle != null) {
            Log.d(TAG, "Drawing a rectangle...");
            mRectangle.drawWithTexture();
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        // Set the OpenGL to the same size as the surface
        GLES20.glViewport(0, 0, width, height);
    }

    public static int loadShader(int type, String shaderCode) throws RuntimeException {
        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shaderHandle = GLES20.glCreateShader(type);

        // Add the source code to the shader and compile it
        GLES20.glShaderSource(shaderHandle, shaderCode);
        GLES20.glCompileShader(shaderHandle);
        checkCompileShaderError(shaderHandle);
        return shaderHandle;
    }

    private static void checkCompileShaderError(int shaderHandle) {
        final int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

        if (compileStatus[0] == 0) {
            GLES20.glDeleteShader(shaderHandle);
            shaderHandle = 0;
            throw new RuntimeException("Error compiling shader: " + GLES20.glGetShaderInfoLog(shaderHandle));
        }
    }
}
