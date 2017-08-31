package com.blueeagle.helloopengl.views;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.blueeagle.helloopengl.R;
import com.blueeagle.helloopengl.gl.MyGLSurfaceView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.gl_surface)
    MyGLSurfaceView mGLSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        initViews();
    }

    private void initViews() {

    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();
    }
}
