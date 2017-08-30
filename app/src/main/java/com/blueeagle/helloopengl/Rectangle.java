package com.blueeagle.helloopengl;

/*
 * Created by tuan.nv on 8/30/2017.
 */

import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Rectangle {

    private FloatBuffer mVertexBuffer;
    private ShortBuffer mDrawListBuffer;
    static final int BYTE_PER_FLOAT = 4;
    private int mProgram;

    float[] rectVerticesData = {
            // (X, Y, Z) - Position  (R, G, B, A)
            -1.0f, 0.0f, 1.0f, 0.95f, 0.83f, 0.01f, 1.0f,
            -1.0f, -1.0f, 1.0f, 0.95f, 0.83f, 0.01f, 1.0f,
            0.0f, -1.0f, 1.0f, 0.95f, 0.83f, 0.01f, 1.0f,
            0.0f, 0.0f, 1.0f, 0.95f, 0.83f, 0.01f, 1.0f
    };

    short[] drawOrder = {0, 1, 2, 0, 2, 3};

    static final String mVertexShaderCode =
            // A constant representing the combined model/view/projection matrix
            "uniform mat4 u_MVPMatrix;" +
                    // Per-vertex position information
                    "attribute vec4 a_Position;" +
                    // Per-vertex color information
                    "attribute vec4 a_Color;" +
                    // Out-value color which will be passed into fragment shader
                    "varying vec4 v_Color;" +
                    "void main() {" +
                    // The color will be interpolated across the triangle.
                    "    v_Color = a_Color;" +
                    // gl_Position is a special variable used to store the final position.
                    // Multiply the vertex by the matrix to get the final point in
                    // normalized screen coordinates.
                    "    gl_Position = a_Position;" +
                    "}";

    static final String mFragmentShaderCode =
            // Set the default precision to medium. We don't need as high of a\n
            // precision in the fragment shader
            "precision mediump float;" +
                    // This is the color from the vertex shader interpolated across the
                    // triangle per fragment
                    "varying vec4 v_Color;" +
                    "void main() {" +
                    // Pass the color directly through the pipeline.
                    "    gl_FragColor = v_Color;" +
                    "}";

    static final String TAG = "Rectangle";

    public Rectangle() {
        // Build vertex buffer
        mVertexBuffer = ByteBuffer.allocateDirect(rectVerticesData.length * BYTE_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mVertexBuffer.put(rectVerticesData)
                .position(0);

        // Build draw list buffer
        mDrawListBuffer = ByteBuffer.allocateDirect(drawOrder.length * 2)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer();
        mDrawListBuffer.put(drawOrder).position(0);

        // Load shader
        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, mVertexShaderCode);
        int fragShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, mFragmentShaderCode);

        if (vertexShader == 0) {
            Log.e(TAG, "Fail to load vertex shader.");
        }

        if (fragShader == 0) {
            Log.e(TAG, "Fail to load fragment shader.");
        }

        if (vertexShader == 0 || fragShader == 0) return;

        // Link shader to a program
        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragShader);
        GLES20.glLinkProgram(mProgram);
    }

    private int mMvpMatrixHandle;
    private int mPositionHandle;
    private int mColorHandle;

    private float[] mMVPMatrix = new float[16];
    private final int mStrideBytes = 7 * BYTE_PER_FLOAT;
    private final int mPositionOffset = 0;
    private final int mPositionDataSize = 3;
    private final int mColorOffset = 3;
    private final int mColorDataSize = 4;

    //public void draw(float[] mViewMatrix, float[] mModelMatrix, float[] mProjectionMatrix) {
    public void draw() {
        GLES20.glUseProgram(mProgram);

        mMvpMatrixHandle = GLES20.glGetAttribLocation(mProgram, "u_MVPMatrix");
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "a_Position");
        mColorHandle = GLES20.glGetAttribLocation(mProgram, "a_Color");

        // Pass in the position information
        mVertexBuffer.position(mPositionOffset);
        GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT,
                false, mStrideBytes, mVertexBuffer);
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Pass in the color information
        mVertexBuffer.position(mColorOffset);
        GLES20.glVertexAttribPointer(mColorHandle, mColorDataSize, GLES20.GL_FLOAT,
                false, mStrideBytes, mVertexBuffer);
        GLES20.glEnableVertexAttribArray(mColorHandle);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, mDrawListBuffer);

        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mColorHandle);
    }
}
