package com.blueeagle.helloopengl.views;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.blueeagle.helloopengl.R;
import com.blueeagle.helloopengl.gl.MyGLSurfaceView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.gl_surface)
    MyGLSurfaceView mGLSurfaceView;

    @BindView(R.id.image_previous)
    ImageView mImagePrevious;

    @BindView(R.id.image_next)
    ImageView mImageNext;

    @BindView(R.id.check_box_jni_java)
    CheckBox mCbUseJni;

    private int mCurrentCount = 0;
    private final int MODEL_COUNT = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        initViews();
    }

    private void initViews() {
        mImageNext.setEnabled(true);
        mImagePrevious.setEnabled(false);
    }

    @OnClick(R.id.image_next)
    public void renderTheNextModel() {
        mGLSurfaceView.setModelToDraw(++mCurrentCount % MODEL_COUNT);
        if (mCurrentCount == MODEL_COUNT - 1) mImageNext.setEnabled(false);
        else {
            mImageNext.setEnabled(true);
            mImagePrevious.setEnabled(true);
        }
    }

    @OnClick(R.id.image_previous)
    public void renderThePreviousModel() {
        if (--mCurrentCount < 0) {
            mCurrentCount = 0;
            mImagePrevious.setEnabled(false);
        } else {
            mImageNext.setEnabled(true);
            mImagePrevious.setEnabled(true);
        }

        mGLSurfaceView.setModelToDraw(mCurrentCount % MODEL_COUNT);
    }

    @OnClick(R.id.check_box_jni_java)
    public void changeDrawMode() {
        boolean isUseJni = mCbUseJni.isChecked();
        mGLSurfaceView.setUseJniFunc(isUseJni);
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
