package com.sgb.floattabscrollview;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;

import com.sgb.mylibrary.FloatTabScrollView;

public class MainActivity extends AppCompatActivity {

    private FloatTabScrollView mFloatTabScrollView;
    private AppBarLayout mAppBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFloatTabScrollView = (FloatTabScrollView) findViewById(R.id.floattabscrollview);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        mFloatTabScrollView.setToolbar(mAppBarLayout, true);
    }
}
