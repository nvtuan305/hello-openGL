package com.blueeagle.helloopengl;

/*
 * Created by tuan.nv on 8/30/2017.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

public class TextureHelper {

    public static int loadTexture(Context context, int resourceId) {
        final int[] textureHandle = new int[1];
        // numberHandle, array, offset
        GLES20.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] != 0) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;

            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);

            // Bind to the texture in OpenGL
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

            // Set filtering
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

            // Load the bitmap to the bound texture
            // target | level | bitmap | border
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

            // FREE UP MEMORY
            // Bitmap objects contain data that resides in native memory
            // and they take a few cycles to be garbage collected
            bitmap.recycle();
        }

        if (textureHandle[0] == 0)
            throw new RuntimeException("Error loading texture");

        return textureHandle[0];
    }

}
