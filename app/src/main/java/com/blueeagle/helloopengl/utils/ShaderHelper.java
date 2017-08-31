package com.blueeagle.helloopengl.utils;

import android.opengl.GLES20;

/*
 * Created by tuan.nv on 8/31/2017.
 */

public class ShaderHelper {

    public static int loadShader(int type, String shaderCode) throws RuntimeException {
        // create a vertex shader (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader (GLES20.GL_FRAGMENT_SHADER)
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
