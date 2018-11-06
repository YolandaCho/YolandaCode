package com.yolanda.code.library;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.yolanda.code.library.util.ProgressIngManager;
import com.yolanda.code.library.widget.ProgressRunning;

/**
 * @author Created by yolanda on 2018/11/05.
 * @description 进度条
 */
public class ProgressActivity extends Activity implements ProgressRunning.OnLoadingListener{
    private ProgressIngManager progressIngManager;
    private ProgressRunning progressView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_running);
        init();
        initData();
    }

    public void init() {
        progressView = (ProgressRunning) findViewById(R.id.ing_piv_progress);
    }

    public void initData() {
        progressIngManager = new ProgressIngManager(this, progressView);
        progressView.setOnLoadingListener(this);
        progressIngManager.startTimer();
    }

    @Override
    public void onLoadingSuccess() {

    }

    @Override
    public void onLoadingFailed() {

    }
}
