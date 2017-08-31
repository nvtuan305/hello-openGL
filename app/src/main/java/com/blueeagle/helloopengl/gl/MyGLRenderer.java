package com.blueeagle.helloopengl.gl;

/*
 * Created by tuan.nv on 8/28/2017.
 */

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.blueeagle.helloopengl.R;
import com.blueeagle.helloopengl.models.Rectangle;
import com.blueeagle.helloopengl.models.Triangle;

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
        //mTriangle.draw();
        //mTriangle1.draw();

        // Set the camera position (View Matrix)
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        if (mRectangle != null) {
            Log.d(TAG, "Drawing a rectangle...");
            mRectangle.drawWithTexture(mMVPMatrix);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        // Set the OpenGL to the same size as the surface
        GLES20.glViewport(0, 0, width, height);

        // Calculate the projection matrix
        float ratio = (float) width / height;
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }
}
