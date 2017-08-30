package com.blueeagle.helloopengl;

/*
 * Created by tuan.nv on 8/28/2017.
 */

import android.opengl.GLSurfaceView;
import android.util.Log;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

public class GLContextFactory implements GLSurfaceView.EGLContextFactory {

    private static double glVersion = 3.0;
    private static int EGL_CONTEXT_CLIENT_VERSION = 0x3098;

    @Override
    public EGLContext createContext(EGL10 egl10, EGLDisplay eglDisplay, EGLConfig eglConfig) {
        Log.w("EGLContextFactory", "Creating OpenGL ES " + glVersion + " context...");
        int[] attrList = {EGL_CONTEXT_CLIENT_VERSION, (int) glVersion, EGL10.EGL_NONE};
        EGLContext result = egl10.eglCreateContext(eglDisplay,
                eglConfig,
                EGL10.EGL_NO_CONTEXT,
                attrList);

        if (result == null) {
            Log.w("EGLContextFactory", "OpenGL version 3 is not available. Creating EGL context v2.0....");
            attrList[1] = 2;
            egl10.eglDestroyContext(eglDisplay, result);
            result = egl10.eglCreateContext(eglDisplay,
                    eglConfig,
                    EGL10.EGL_NO_CONTEXT,
                    attrList);
        } else {
            Log.w("EGLContextFactory", "EGLContext 3.0 is created.");
        }

        return result;
    }

    @Override
    public void destroyContext(EGL10 egl10, EGLDisplay eglDisplay, EGLContext eglContext) {
        cleanUp();
        egl10.eglDestroyContext(eglDisplay, eglContext);
    }

    private void cleanUp() {

    }
}
