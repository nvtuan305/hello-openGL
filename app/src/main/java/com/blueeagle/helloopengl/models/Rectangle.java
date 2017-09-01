package com.blueeagle.helloopengl.models;

/*
 * Created by tuan.nv on 8/30/2017.
 */

import android.content.Context;
import android.opengl.GLES20;

import com.blueeagle.helloopengl.R;
import com.blueeagle.helloopengl.utils.ProgramHelper;
import com.blueeagle.helloopengl.utils.RawResourceReader;
import com.blueeagle.helloopengl.utils.ShaderHelper;
import com.blueeagle.helloopengl.utils.TextureHelper;

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
            -1.0f, 1.0f, 0.0f, 0.95f, 0.83f, 0.01f, 1.0f,
            -1.0f, -1.0f, 0.0f, 0.95f, 0.83f, 0.01f, 1.0f,
            1.0f, -1.0f, 0.0f, 0.95f, 0.83f, 0.01f, 1.0f,
            1.0f, 1.0f, 0.0f, 0.95f, 0.83f, 0.01f, 1.0f
    };
    short[] drawOrder = {0, 1, 2, 0, 2, 3};
    private String mVertexShaderCode = "";
    private String mFragmentShaderCode = "";
    static final String TAG = "MMRectangle";
    private int mMvpMatrixHandle;
    private int mPositionHandle;
    private int mColorHandle;
    private int mTexSampler2DHandle;
    private int mTextureCoordinateHandle;
    private int mTextureDataHandle;
    private final int mStrideBytes = 7 * BYTE_PER_FLOAT;
    private final int mPositionOffset = 0;
    private final int mPositionDataSize = 3;
    private final int mColorOffset = 3;
    private final int mColorDataSize = 4;
    private final int mTextureCoordinateDataSize = 2;

    public Rectangle(Context context) {
        loadShaderCode(context);
        init(mVertexShaderCode, mFragmentShaderCode);
    }

    public Rectangle(Context context, int textureId) {
        float[] textureCoordinateData = {
                0.1f, 0f,
                0.1f, 1f,
                0.9f, 1f,
                0.9f, 0f
        };

        mTexCoordsBuffer = ByteBuffer.allocateDirect(textureCoordinateData.length * BYTE_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mTexCoordsBuffer.put(textureCoordinateData).position(0);

        // Load shader code
        loadShaderCode(context);
        init(mVertexShaderCode, mFragmentShaderCode);

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
        int vertexShader = ShaderHelper.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragShader = ShaderHelper.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        // Link shader to a program
        mProgram = ProgramHelper.createAndLinkProgram(vertexShader, fragShader);
    }

    private void loadShaderCode(Context context) {
        mVertexShaderCode = RawResourceReader.readTextFromRawFileResource(context,
                R.raw.rect_vertex_shader_code);
        mFragmentShaderCode = RawResourceReader.readTextFromRawFileResource(context,
                R.raw.rect_fragment_shader_code);
    }

    public void draw() {
        passDataToOpenGL();

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, mDrawListBuffer);

        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mColorHandle);
    }

    private void passDataToOpenGL() {
        GLES20.glUseProgram(mProgram);

        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "a_Position");
        mColorHandle = GLES20.glGetAttribLocation(mProgram, "a_Color");

        // Pass in the position information
        mVertexBuffer.position(mPositionOffset);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT,
                false, mStrideBytes, mVertexBuffer);

        // Pass in the mDefaultColor information
        mVertexBuffer.position(mColorOffset);
        GLES20.glEnableVertexAttribArray(mColorHandle);
        GLES20.glVertexAttribPointer(mColorHandle, mColorDataSize, GLES20.GL_FLOAT,
                false, mStrideBytes, mVertexBuffer);
    }

    public void drawWithTexture(float[] mvpMatrix) {
        passDataToOpenGL();

        mMvpMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_MVPMatrix");
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

        // Pass projection an view matrix
        GLES20.glUniformMatrix4fv(mMvpMatrixHandle, 1, false, mvpMatrix, 0);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, mDrawListBuffer);

        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mColorHandle);
        GLES20.glDisableVertexAttribArray(mTextureCoordinateHandle);
    }
}
