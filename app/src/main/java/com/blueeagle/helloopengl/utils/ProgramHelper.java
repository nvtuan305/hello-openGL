package com.blueeagle.helloopengl.utils;

/*
 * Created by tuan.nv on 8/31/2017.
 */

import android.opengl.GLES20;

public class ProgramHelper {

    public static int createAndLinkProgram(int vertexShaderHandle,
                                           int fragmentShaderHandle) throws RuntimeException {
        int program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexShaderHandle);
        GLES20.glAttachShader(program, fragmentShaderHandle);
        GLES20.glLinkProgram(program);
        checkLinkingProgramError(program);
        return program;
    }

    private static void checkLinkingProgramError(int program) {
        int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] == 0) {
            GLES20.glDeleteProgram(program);
            throw new RuntimeException("Error linking program: " + GLES20.glGetProgramInfoLog(program));
        }
    }
}
