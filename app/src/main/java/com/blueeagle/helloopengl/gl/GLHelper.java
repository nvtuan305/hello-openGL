package com.blueeagle.helloopengl.gl;

/*
 * Created by tuan.nv on 8/30/2017.
 */

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;

public class GLHelper {

    /**
     * Check device support openGL version
     */
    public static boolean isSupportOpenGLVersion(Context context, int version) {
        ActivityManager activityManager = (ActivityManager)
                context.getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo ci = activityManager.getDeviceConfigurationInfo();
        return ci.reqGlEsVersion >= version;
    }
}
