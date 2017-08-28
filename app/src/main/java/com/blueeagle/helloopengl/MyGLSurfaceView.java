package com.blueeagle.helloopengl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import javax.microedition.khronos.opengles.GL10;

/*
 * Created by tuan.nv on 8/28/2017.
 */

public class MyGLSurfaceView extends GLSurfaceView {

    private MyGLRenderer mRenderer;

    public MyGLSurfaceView(Context context) {
        super(context);
        init();
    }

    public MyGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // Create a OpenGL 2.0 context
        setEGLContextClientVersion(2);

        // Set the renderer for MyGLSurfaceView
        mRenderer = new MyGLRenderer();
        setRenderer(mRenderer);

        // Render the view only when there is a change in the drawing data
        //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
