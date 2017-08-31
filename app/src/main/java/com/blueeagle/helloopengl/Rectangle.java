package com.blueeagle.helloopengl;

/*
 * Created by tuan.nv on 8/30/2017.
 */

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Rectangle {

    private FloatBuffer mVertexBuffer;
    private FloatBuffer mTexCoordsBuffer;
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

    static final String TAG = "MMRectangle";

    private int mMvpMatrixHandle;
    private int mPositionHandle;
    private int mColorHandle;
    private int mTexSampler2DHandle;
    private int mTextureCoordinateHandle;
    private int mTextureDataHandle;

    private float[] mMVPMatrix = new float[16];
    private final int mStrideBytes = 7 * BYTE_PER_FLOAT;
    private final int mPositionOffset = 0;
    private final int mPositionDataSize = 3;
    private final int mColorOffset = 3;
    private final int mColorDataSize = 4;
    private final int mTextureCoordinateDataSize = 2;

    public Rectangle() {
        init(mVertexShaderCode, mFragmentShaderCode);
    }

    public Rectangle(Context context, int textureId) {
        float[] textureCoordinateData = {
                0.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 0.0f,
                1.0f, 1.0f
        };

        mTexCoordsBuffer = ByteBuffer.allocateDirect(textureCoordinateData.length * BYTE_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mTexCoordsBuffer.put(textureCoordinateData).position(0);

        String vertexShaderCode = RawResourceReader.readTextFromRawFileResource(context,
                R.raw.rect_vertex_shader_code);
        String fragmentShaderCode = RawResourceReader.readTextFromRawFileResource(context,
                R.raw.rect_fragment_shader_code);

        Log.d(TAG, vertexShaderCode);
        Log.d(TAG, fragmentShaderCode);

        // Init
        init(vertexShaderCode, fragmentShaderCode);

        // Load texture
        mTextureDataHandle = TextureHelper.loadTexture(context, textureId);
    }

    private void init(String vertexShaderCode, String fragmentShaderCode) {
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
        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

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

        int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(mProgram, GLES20.GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] == 0) {
            Log.e(TAG, "Error linking program: " + GLES20.glGetProgramInfoLog(mProgram));
            GLES20.glDeleteProgram(mProgram);
        }
    }

    //public void draw(float[] mViewMatrix, float[] mModelMatrix, float[] mProjectionMatrix) {
    public void draw() {
        passDataToOpenGL();

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, mDrawListBuffer);

        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mColorHandle);
    }

    private void passDataToOpenGL() {
        GLES20.glUseProgram(mProgram);

        mMvpMatrixHandle = GLES20.glGetAttribLocation(mProgram, "u_MVPMatrix");
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "a_Position");
        mColorHandle = GLES20.glGetAttribLocation(mProgram, "a_Color");

        // Pass in the position information
        mVertexBuffer.position(mPositionOffset);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT,
                false, mStrideBytes, mVertexBuffer);

        // Pass in the color information
        mVertexBuffer.position(mColorOffset);
        GLES20.glEnableVertexAttribArray(mColorHandle);
        GLES20.glVertexAttribPointer(mColorHandle, mColorDataSize, GLES20.GL_FLOAT,
                false, mStrideBytes, mVertexBuffer);
    }

    public void drawWithTexture() {
        passDataToOpenGL();

        mTexSampler2DHandle = GLES20.glGetUniformLocation(mProgram, "u_Texture");
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgram, "a_TexCoordinate");

        // Set the active texture unit to texture unit 0
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        // Bind the texture to this unit
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle);

        // Tell the texture uniform sampler (u_Texture) to use this texture in
        // the shader by binding to texture unit 0
        GLES20.glUniform1i(mTexSampler2DHandle, 0);

        // Pass texture coordinate information
        mTexCoordsBuffer.position(0);
        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
        GLES20.glVertexAttribPointer(mTextureCoordinateHandle, mTextureCoordinateDataSize,
                GLES20.GL_FLOAT, false, 0, mTexCoordsBuffer);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, mDrawListBuffer);

        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mColorHandle);
        GLES20.glDisableVertexAttribArray(mTextureCoordinateHandle);
    }
}
