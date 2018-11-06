package com.yolanda.code.library.util;

import java.util.Timer;

/**
 * @author Created by zhangweitao on 2017/8/25.
 * @describe 进度体实体
 */

public class ProgressData {

    private int progress;
    private long time;
    private Timer timer = new Timer();

    public ProgressData(int progress, long time) {
        this.progress = progress;
        this.time = time;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }
}
