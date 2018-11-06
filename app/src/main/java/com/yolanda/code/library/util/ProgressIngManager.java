package com.yolanda.code.library.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import com.yolanda.code.library.widget.ProgressRunning;

import java.util.TimerTask;

/**
 * @author Created by yolanda on 2017/7/28.
 * @describe 生成订单，还车进度条
 */

public class ProgressIngManager {

    private ProgressRunning mProgressView;
    private int mProgress = 0;
    private Context mContext;

    private ProgressData progressData1, progressData2, progressData3, progressData4;
    private TimerTask timerTask1 = null, timerTask2 = null, timerTask3 = null, timerTask4 = null;

    public ProgressIngManager(Context context, ProgressRunning progressView) {
        this.mContext = context;
        this.mProgressView = progressView;
        init();
    }

    private void init() {
        progressData1 = new ProgressData(55, 20);
        progressData2 = new ProgressData(75, 20);
        progressData3 = new ProgressData(100, 20);
        progressData4 = new ProgressData(0, 20);
    }

    private void startTimerTask1() {
        timerTask1 = new TimerTask() {
            @Override
            public void run() {
                if (mProgress < progressData1.getProgress()) {
                    mProgress++;
                    mHandler.sendEmptyMessage(1);
                }
            }
        };
        progressData1.getTimer().schedule(timerTask1, 0, progressData1.getTime());
    }

    private void cancelTimerTask1() {
        timerTask1.cancel();
        progressData1.getTimer().cancel();
    }

    private void startTimerTask2() {
        timerTask2 = new TimerTask() {
            @Override
            public void run() {
                if (mProgress < progressData2.getProgress()) {
                    mProgress++;
                    mHandler.sendEmptyMessage(2);
                }
            }
        };
        progressData2.getTimer().schedule(timerTask2, 0, progressData2.getTime());
    }

    private void cancelTimerTask2() {
        timerTask2.cancel();
        progressData2.getTimer().cancel();
    }

    private void startTimerTask3() {
        timerTask3 = new TimerTask() {
            @Override
            public void run() {
                if (mProgress < progressData3.getProgress()) {
                    mProgress++;
                    mHandler.sendEmptyMessage(3);
                }
            }
        };
        progressData3.getTimer().schedule(timerTask3, 0, progressData3.getTime());
    }

    private void cancelTimerTask3() {
        timerTask3.cancel();
        progressData3.getTimer().cancel();
    }

    private void startTimerTask4() {
        timerTask4 = new TimerTask() {
            @Override
            public void run() {
                if (mProgress > progressData4.getProgress()) {
                    mProgress--;
                    mHandler.sendEmptyMessage(4);
                }
            }
        };
        progressData4.getTimer().schedule(timerTask4, 0, progressData4.getTime());
    }

    private void cancelTimerTask4() {
        timerTask4.cancel();
        progressData4.getTimer().cancel();
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mProgressView.setProgress(mProgress);
            if (msg.what == 1) {
                if (mProgress == progressData1.getProgress()) {
                    cancelTimerTask1();
                    startTimerTask2();
                }
            }
            if (msg.what == 2) {
                if (mProgress == progressData2.getProgress()) {
                    cancelTimerTask2();
                    startTimerTask3();
                }
            }
            if (msg.what == 3) {
                if (mProgress == progressData3.getProgress()) {
                    cancelTimerTask3();
                    mProgressView.setStatus(ProgressRunning.STATUS_SUCCESS);
                }
            }
            if (msg.what == 4) {
                if (mProgress == progressData4.getProgress()) {
                    cancelTimerTask4();
                    mProgressView.setStatus(ProgressRunning.STATUS_FAILED);
                }
            }
        }
    };


    /**
     * @describe 加载成功的显示
     */
    public void setProgressSuccess() {
        if (timerTask2 != null && progressData2.getTimer() != null) {
            cancelTimerTask2();
        }
        startTimerTask3();
    }

    /**
     * @describe 加载失败的显示
     */
    public void setProgressFailed() {
        if (timerTask1 != null && progressData1.getTimer() != null) {
            cancelTimerTask1();
        }
        if (timerTask2 != null && progressData2.getTimer() != null) {
            cancelTimerTask2();
        }
        if (timerTask3 != null && progressData3.getTimer() != null) {
            cancelTimerTask3();
        }
        startTimerTask4();
    }

    public void startTimer() {
        startTimerTask1();
    }

    public void stopTimer() {
        if (timerTask1 != null && progressData1.getTimer() != null) {
            cancelTimerTask1();
        }
        if (timerTask2 != null && progressData2.getTimer() != null) {
            cancelTimerTask2();
        }
        if (timerTask3 != null && progressData3.getTimer() != null) {
            cancelTimerTask3();
        }
        if (timerTask4 != null && progressData4.getTimer() != null) {
            cancelTimerTask4();
        }
    }

}
