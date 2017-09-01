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

public class MagicRectangle {
    // Vertex buffer
    private FloatBuffer mVertexBuffer;

    // Texture coordinate buffer
    private FloatBuffer mTexCoordsBuffer;

    // Vertex order buffer
    private ShortBuffer mDrawListBuffer;

    // Program handle
    private int mProgram;

    static final int BYTE_PER_FLOAT = 4;
    static final int BYTE_PER_SHORT = 2;
    private final int mStrideBytes = 3 * BYTE_PER_FLOAT;
    private final int mPositionOffset = 0;
    private final int mPositionDataSize = 3;
    private final int mTextureCoordinateDataSize = 2;

    // Vertices data
    float[] mVerticesData;

    // Texture coordinate data
    float[] mTexCoordsData;

    // Draw element list
    short[] drawOrder = {0, 1, 2, 0, 2, 3};

    private int mMvpMatrixHandle;
    private int mPositionHandle;
    private int mTexSampler2DHandle;
    private int mTextureCoordinateHandle;
    private int mTextureDataHandle;

    public MagicRectangle(Context context,
                          int textureId,
                          float[] rectVerticesData,
                          float[] textureCoordinateData) {

        this.mVerticesData = rectVerticesData;
        this.mTexCoordsData = textureCoordinateData;

        // Build vertex buffer
        mVertexBuffer = ByteBuffer.allocateDirect(mVerticesData.length * BYTE_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mVertexBuffer.put(mVerticesData)
                .position(0);

        // Build draw list buffer
        mDrawListBuffer = ByteBuffer.allocateDirect(drawOrder.length * BYTE_PER_SHORT)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer();
        mDrawListBuffer.put(drawOrder).position(0);

        // Build texture buffer
        mTexCoordsBuffer = ByteBuffer.allocateDirect(mTexCoordsData.length * BYTE_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mTexCoordsBuffer.put(mTexCoordsData).position(0);

        // Load shader
        String vertexShaderCode = getVertexShaderCode(context);
        String fragmentShaderCode = getFragmentShaderCode(context);
        int vertexShader = ShaderHelper.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragShader = ShaderHelper.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        // Link shader to a program
        mProgram = ProgramHelper.createAndLinkProgram(vertexShader, fragShader);

        // Load texture
        mTextureDataHandle = TextureHelper.loadTexture(context, textureId);
    }

    public void setVerticesAndTexCoords(float[] rectVerticesData, float[] textureCoordinateData) {
        this.mVerticesData = rectVerticesData;
        this.mTexCoordsData = textureCoordinateData;

        // Build vertex buffer
        mVertexBuffer.clear();
        mVertexBuffer = null;
        mVertexBuffer = ByteBuffer.allocateDirect(mVerticesData.length * BYTE_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mVertexBuffer.put(mVerticesData)
                .position(0);

        // Build texture buffer
        mTexCoordsBuffer.clear();
        mTexCoordsBuffer = null;
        mTexCoordsBuffer = ByteBuffer.allocateDirect(mTexCoordsData.length * BYTE_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mTexCoordsBuffer.put(mTexCoordsData).position(0);
    }

    private String getVertexShaderCode(Context context) {
        return RawResourceReader.readTextFromRawFileResource(context,
                R.raw.rect_vertex_shader_code);
    }

    private String getFragmentShaderCode(Context context) {
        return RawResourceReader.readTextFromRawFileResource(context,
                R.raw.rect_fragment_shader_code);
    }

    public void draw(float[] mvpMatrix) {
        // Set the current program use to draw
        GLES20.glUseProgram(mProgram);

        // Pass vertices, texture, MVPMatrix data to OpenGL
        passDataToOpenGL(mvpMatrix);

        // Draw rectangle
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, mDrawListBuffer);

        // Disable vertex attribute array
        disableAllVertexAttribArray();
    }

    private void disableAllVertexAttribArray() {
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTextureCoordinateHandle);
    }

    private void passDataToOpenGL(float[] mvpMatrix) {
        // Pass in the position information
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "a_Position");
        mVertexBuffer.position(0);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT,
                false, mStrideBytes, mVertexBuffer);

        // Set the active texture unit to texture unit 0
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        // Bind the texture to this unit
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle);

        // Tell the texture uniform sampler (u_Texture) to use this texture in
        // the shader by binding to texture unit 0
        mTexSampler2DHandle = GLES20.glGetUniformLocation(mProgram, "u_Texture");
        GLES20.glUniform1i(mTexSampler2DHandle, 0);

        // Pass texture coordinate information
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgram, "a_TexCoordinate");
        mTexCoordsBuffer.position(0);
        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
        GLES20.glVertexAttribPointer(mTextureCoordinateHandle, mTextureCoordinateDataSize,
                GLES20.GL_FLOAT, false, 0, mTexCoordsBuffer);

        // Pass in MVP matrix
        mMvpMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_MVPMatrix");
        GLES20.glUniformMatrix4fv(mMvpMatrixHandle, 1, false, mvpMatrix, 0);
    }
}
