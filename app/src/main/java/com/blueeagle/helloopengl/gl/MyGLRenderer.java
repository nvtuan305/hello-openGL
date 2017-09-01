package com.blueeagle.helloopengl.gl;

/*
 * Created by tuan.nv on 8/28/2017.
 */

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;

import com.blueeagle.helloopengl.R;
import com.blueeagle.helloopengl.models.MagicRectangle;
import com.blueeagle.helloopengl.models.Triangle;

import java.lang.ref.WeakReference;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer implements GLSurfaceView.Renderer {

    private Triangle mSimpleTriangle, mTriangleWithTex;
    private MagicRectangle mMagicRectangle;
    private final String TAG = "MyGLRenderer";

    private float[] mProjectionMatrix = new float[16];
    private float[] mModelMatrix = new float[16];
    private float[] mViewMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];
    private float[] mTransOpt = new float[3];

    private int mCurrentModel = 0;
    private float mAngleToRotate = 0f;
    private final int SIMPLE_TRIANGLE = 0;
    private final int TRIANGLE_WITH_TEXTURE = 1;
    private final int RECTANGLE_WITH_TEXTURE = 2;

    private WeakReference<Context> contextWeakReference;

    public MyGLRenderer(Context context) {
        contextWeakReference = new WeakReference<>(context);
    }

    /**
     * Set model type to draw
     *
     * @param mCurrentModel value in {0, 1, 2}
     */
    public void setModelToDraw(int mCurrentModel) {
        this.mCurrentModel = mCurrentModel;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        // Enable blend transparent color
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glEnable(GLES20.GL_BLEND);

        //mRectangle = new Rectangle();
        Context context = contextWeakReference.get();
        if (context != null) {
            // Init a simple triangle model
            float[] tCoords = new float[]{
                    0.0f, 0.5f, 0.0f,
                    -0.5f, -0.5f, 0.0f,
                    0.5f, -0.5f, 0.0f};
            float[] tColor = new float[]{0.0f, 0.82f, 0.75f, 1.0f};
            mSimpleTriangle = new Triangle(context, tCoords, tColor);

            float[] texCoords = {
                    0.5f, 0f,
                    0.25f, 1f,
                    0.75f, 1f
            };
            mTriangleWithTex = new Triangle(context, texCoords, tCoords, R.drawable.texture_basic);

            // Init a rectangle with texture
            float[] rectVerticesData = {
                    -0.6f, 0.50706f, 0.0f,
                    -0.6f, -0.50706f, 0.0f,
                    0.6f, -0.50706f, 0.0f,
                    0.6f, 0.50706f, 0.0f};
            float[] textureCoordinateData = {
                    0.0f, 0.005f,
                    0f, 0.780f,
                    1f, 0.780f,
                    1f, 0.005f};
            mMagicRectangle = new MagicRectangle(context, R.drawable.texture_logo_gianty,
                    rectVerticesData, textureCoordinateData);
        }

        // Set the camera position (View Matrix)
        // (eyeX, eyeY, eyeZ): The position of camera
        // (centerX, centerY, centerZ): The position to be centered of the visible view
        // (upX, upY, upZ): Z axis
        Matrix.setLookAtM(mViewMatrix, 0,
                0f, 0f, 3f,
                0f, 0f, 0f,
                0f, 1.0f, 0.0f);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        if (mCurrentModel == RECTANGLE_WITH_TEXTURE)
            GLES20.glClearColor(1f, 1f, 1.0f, 1.0f);
        else GLES20.glClearColor(0.43f, 0.27f, 1.0f, 1.0f);

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        switch (mCurrentModel) {
            case SIMPLE_TRIANGLE:
                if (mSimpleTriangle != null) {
                    Log.d(TAG, "Drawing a simple triangle...");
                    long time = SystemClock.uptimeMillis() % 10000L;
                    mAngleToRotate = (360f / 10000f) * (int) time;
                    calculateMVPMatrix();
                    mSimpleTriangle.drawWithSolidBackground(mMVPMatrix);
                }
                break;

            case TRIANGLE_WITH_TEXTURE:
                if (mTriangleWithTex != null) {
                    Log.d(TAG, "Drawing a triangle with texture...");
                    mAngleToRotate = 0f;
                    calculateMVPMatrix();
                    mTriangleWithTex.drawWithTexture(mMVPMatrix);
                }
                break;

            case RECTANGLE_WITH_TEXTURE:
                if (mMagicRectangle != null) {
                    Log.d(TAG, "Drawing a magic rectangle...");
                    drawLogo();
                }
                break;
        }
    }

    private void drawLogo() {
        // Draw the center logo
        float[] rectVerticesData = {
                -0.6f, 0.50706f, 0.0f,
                -0.6f, -0.50706f, 0.0f,
                0.6f, -0.50706f, 0.0f,
                0.6f, 0.50706f, 0.0f};
        float[] textureCoordinateData = {
                0.0f, 0.005f,
                0f, 0.780f,
                1f, 0.780f,
                1f, 0.005f};
        mMagicRectangle.setVerticesAndTexCoords(rectVerticesData, textureCoordinateData);
        drawLogo(0, 0, 0, 0);

        // Draw the bottom logo using translation and rotation transform
        rectVerticesData = new float[]{
                -0.36f, 0.08f, 0.0f,
                -0.36f, -0.08f, 0.0f,
                0.36f, -0.08f, 0.0f,
                0.36f, 0.08f, 0.0f};
        textureCoordinateData = new float[]{
                0.0005f, 0.8312f,
                0.0005f, 1f,
                1f, 1f,
                1f, 0.8312f};
        mMagicRectangle.setVerticesAndTexCoords(rectVerticesData, textureCoordinateData);
        drawLogo(0, 0.0f, -0.902f, 0.0f);

        // Draw the right logo using translation and rotation transform
        drawLogo(-90f, 0.902f, 0f, 0.0f);

        // Draw the top logo by updating vertices and texture coordinates
        rectVerticesData = new float[]{
                -0.36f, 1f, 0.0f,
                -0.36f, 0.84f, 0.0f,
                0.36f, 0.84f, 0.0f,
                0.36f, 1f, 0.0f};
        textureCoordinateData = new float[]{
                0.0005f, 0.8312f,
                0.0005f, 1f,
                1f, 1f,
                1f, 0.8312f};
        mMagicRectangle.setVerticesAndTexCoords(rectVerticesData, textureCoordinateData);
        drawLogo(0, 0, 0, 0);

        // Draw the left logo by updating vertices and texture coordinates
        rectVerticesData = new float[]{
                -1f, -0.36f, 0.0f,
                -0.84f, -0.36f, 0.0f,
                -0.84f, 0.36f, 0.0f,
                -1f, 0.36f, 0.0f};
        textureCoordinateData = new float[]{
                0.0005f, 0.8312f,
                0.0005f, 1f,
                1f, 1f,
                1f, 0.8312f};
        mMagicRectangle.setVerticesAndTexCoords(rectVerticesData, textureCoordinateData);
        drawLogo(0, 0, 0, 0);
    }

    private void drawLogo(float angle, float xDelta, float yDelta, float zDelta) {
        mAngleToRotate = angle;
        setTranslateOptions(xDelta, yDelta, zDelta);
        calculateMVPMatrix();
        mMagicRectangle.draw(mMVPMatrix);
    }

    private void setTranslateOptions(float xDelta, float yDelta, float zDelta) {
        mTransOpt[0] = xDelta;
        mTransOpt[1] = yDelta;
        mTransOpt[2] = zDelta;
    }

    private void calculateMVPMatrix() {
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, mTransOpt[0], mTransOpt[1], mTransOpt[2]);
        Matrix.rotateM(mModelMatrix, 0, mAngleToRotate, 0.0f, 0.0f, 1.0f);
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        // Calculate the projection and view transformation: projection * view * model
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        // Set the OpenGL to the same size as the surface
        GLES20.glViewport(0, 0, width, height);

        // Calculate the projection matrix
        float ratio = (float) width / height;
        // Keep the height, scale the width: -1 => -ratio, 1 => ratio
        Matrix.frustumM(mProjectionMatrix, 0,
                -ratio, ratio, -1f, 1f,
                // Camera <--> Near = 2 => Near(0, 0, 3)
                // Camera <--> Far = 7 => Far(0, 0, -2)
                // The view will be clamped between near and far
                2f, 5.0f);
    }
}
