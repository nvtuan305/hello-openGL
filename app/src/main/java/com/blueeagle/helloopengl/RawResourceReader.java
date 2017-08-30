package com.blueeagle.helloopengl;

/*
 * Created by tuan.nv on 8/30/2017.
 */

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class RawResourceReader {

    static final String TAG = "RawResourceReader";

    public static String readTextFromRawFileResource(Context context, int resourceId) {
        InputStream inputStream = context.getResources().openRawResource(resourceId);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        String nextLine;
        StringBuilder body = new StringBuilder("");

        try {
            while ((nextLine = bufferedReader.readLine()) != null) {
                body.append(nextLine);
                body.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Can not read text from this resource");
        }

        return body.toString();
    }
}
