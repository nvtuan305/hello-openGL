package com.blueeagle.helloopengl;

/*
 * Created by tuan.nv on 8/28/2017.
 */

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Triangle {

    private FloatBuffer vertexBuffer;

    static final int COORDS_PER_VERTEX = 3;

    static float triangleCoords[] = {
            0.0f, 0.5f, 0.0f, // top
            -0.5f, -0.3f, 0.0f, // bottom left
            0.5f, -0.3f, 0.0f // bottom right
    };

    float color[] = {1.0f, 1.0f, 1.0f, 1.0f};

    private final String vertexShaderCode =
            "attribute vec4 vPosition;"
            + "void main() {"
            + " gl_Position = vPosition;"
            + "}";

    private final String fragmentShaderCode =
            "precision mediump float;"
            + "uniform vec4 vColor;"
            + "void main() {"
            + " gl_FragColor = vColor;"
            + "}";

    private final int mProgram;

    public Triangle() {
        // Build vertex buffer
        ByteBuffer bb = ByteBuffer.allocateDirect(triangleCoords.length * 4);
        // Use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(triangleCoords);
        vertexBuffer.position(0);

        // Compile shader code (Shaders contain OpenGL Shading Language (GLSL) code that
        // must be compiled prior to using it in the OpenGL ES environment)
        // -------------------------
        // Compiling OpenGL shader and linking programs is expensive in term of CPU cycles
        // and processing time ====> AVOID DOING THIS MORE THAN ONCE
        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();
        // Add vertex shader to program
        GLES20.glAttachShader(mProgram, vertexShader);
        // Add fragment shader to program
        GLES20.glAttachShader(mProgram, fragmentShader);
        // Create OpenGL ES program executables
        GLES20.glLinkProgram(mProgram);
    }

    private int mPositionHandle;
    private int mColorHandle;

    private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4;

    public void draw() {
        // Add program to the OpenGL ES environment
        GLES20.glUseProgram(mProgram);

        // Get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // Get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the triangle
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
}
