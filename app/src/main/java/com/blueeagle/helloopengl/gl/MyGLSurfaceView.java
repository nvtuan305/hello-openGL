package com.blueeagle.helloopengl.gl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;

/*
 * Created by tuan.nv on 8/28/2017.
 */

public class MyGLSurfaceView extends GLSurfaceView {

    private MyGLRenderer mRenderer;
    private static final String TAG = "MyGLSurfaceView";

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
        if (GLHelper.isSupportOpenGLVersion(getContext(), 0x20000)) {
            Log.d(TAG, "The device is supported OpenGL v2.0");
            setEGLContextClientVersion(2);
        } else {
            Log.d(TAG, "The device is supported OpenGL v1.0");
            setEGLContextClientVersion(1);
        }

        // Set the renderer for MyGLSurfaceView
        mRenderer = new MyGLRenderer(getContext());
        setRenderer(mRenderer);

        // Render the view only when there is a change in the drawing data
        //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
