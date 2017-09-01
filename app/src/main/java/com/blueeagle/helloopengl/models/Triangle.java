package com.blueeagle.helloopengl.models;

/*
 * Created by tuan.nv on 8/28/2017.
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

public class Triangle {

    private FloatBuffer mVertexBuffer;
    private FloatBuffer mTexCoordsBuffer;
    private String vertexShaderCode;
    private String fragmentShaderCode;
    private int mProgram;
    static final int COORDS_PER_VERTEX = 3;
    private int mPositionHandle;
    private int mColorHandle;
    private int mTexDataHandle;
    private int mTexCoordsHandle;
    private int mTexSampler2DHandle;
    private int mMVPMatrixHandle;
    private float[] mTexCoords;
    float mDefaultColor[] = {1.0f, 1.0f, 1.0f, 1.0f};
    float mCoords[] = {
            0.0f, 0.5f, 0.0f, // top
            -0.5f, -0.3f, 0.0f, // bottom left
            0.5f, -0.3f, 0.0f // bottom right
    };
    private final int vertexCount = mCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4;

    public Triangle(Context context) {
        init(context);
    }

    public Triangle(Context context, float[] mTexCoords,
                    float[] mDefaultCoords, int textureId) {
        this.mTexCoords = mTexCoords;
        this.mCoords = mDefaultCoords;
        initWithTexture(context, textureId);
    }

    public Triangle(Context context, float[] triangleCoords, float[] color) {
        this.mCoords = triangleCoords;
        this.mDefaultColor = color;
        init(context);
    }

    private String getVertexShaderCode(Context context) {
        return RawResourceReader.readTextFromRawFileResource(context,
                R.raw.simple_triangle_vertex_shader);
    }

    private String getFragmentShaderCode(Context context) {
        return RawResourceReader.readTextFromRawFileResource(context,
                R.raw.simple_triangle_fragment_shader);
    }

    private String getVertexShaderWithTextureCode(Context context) {
        return RawResourceReader.readTextFromRawFileResource(context,
                R.raw.triangle_vertex_shader);
    }

    private String getFragmentShaderWithTextureCode(Context context) {
        return RawResourceReader.readTextFromRawFileResource(context,
                R.raw.triangle_fragment_shader);
    }

    private void init(Context context) {
        // Build vertex buffer
        mVertexBuffer = ByteBuffer.allocateDirect(mCoords.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mVertexBuffer.put(mCoords).position(0);

        // Compile shader code (Shaders contain OpenGL Shading Language (GLSL) code that
        // must be compiled prior to using it in the OpenGL ES environment)
        // -------------------------
        // Compiling OpenGL shader and linking programs is expensive in term of CPU cycles
        // and processing time ====> AVOID DOING THIS MORE THAN ONCE
        vertexShaderCode = getVertexShaderCode(context);
        fragmentShaderCode = getFragmentShaderCode(context);
        int vertexShader = ShaderHelper.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = ShaderHelper.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        mProgram = ProgramHelper.createAndLinkProgram(vertexShader, fragmentShader);
    }

    private void initWithTexture(Context context, int textureId) {
        // Build vertex buffer
        mVertexBuffer = ByteBuffer.allocateDirect(mCoords.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mVertexBuffer.put(mCoords).position(0);

        // Build texture coordinate buffer
        mTexCoordsBuffer = ByteBuffer.allocateDirect(mTexCoords.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mTexCoordsBuffer.put(mTexCoords).position(0);

        vertexShaderCode = getVertexShaderWithTextureCode(context);
        fragmentShaderCode = getFragmentShaderWithTextureCode(context);
        int vertexShader = ShaderHelper.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = ShaderHelper.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        mProgram = ProgramHelper.createAndLinkProgram(vertexShader, fragmentShader);

        // Load texture
        mTexDataHandle = TextureHelper.loadTexture(context, textureId);
    }

    public void drawWithSolidBackground(float[] mvpMatrix) {
        // Add program to the OpenGL ES environment
        GLES20.glUseProgram(mProgram);

        // Pass in position information
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "v_Position");
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, mVertexBuffer);

        // Pass in MVP matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "v_MVPMatrix");
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

        // Pass in color information
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "v_Color");
        GLES20.glUniform4fv(mColorHandle, 1, mDefaultColor, 0);

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }

    public void drawWithTexture(float[] mvpMatrix) {
        // Add program to the OpenGL ES environment
        GLES20.glUseProgram(mProgram);

        // Pass in position information
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "v_Position");
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, mVertexBuffer);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexDataHandle);
        mTexSampler2DHandle = GLES20.glGetUniformLocation(mProgram, "u_Texture");
        GLES20.glUniform1f(mTexSampler2DHandle, 0);

        // Pass in texture coordinate information
        mTexCoordsHandle = GLES20.glGetAttribLocation(mProgram, "a_TextureCoordinate");
        GLES20.glEnableVertexAttribArray(mTexCoordsHandle);
        GLES20.glVertexAttribPointer(mTexCoordsHandle, 2, GLES20.GL_FLOAT,
                false, 0, mTexCoordsBuffer);

        // Pass in MVP matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "v_MVPMatrix");
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTexCoordsHandle);
    }
}
