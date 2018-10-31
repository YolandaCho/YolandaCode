package com.yolanda.code.library;
import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.widget.Toast;
import com.yolanda.code.library.widget.DragImageView;

/**
 * @author Created by Yolanda on 2018/10/31.
 * @description 滑动拼图验证码
 */
public class DragImageCodeActivity extends Activity {
    DragImageView dragView;

    private Handler handler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_image_code);
        initView();
        initEvent();
    }


    public void initView() {
        dragView = (DragImageView) findViewById(R.id.dragView);
        dragView.setUp(BitmapFactory.decodeResource(getResources(), R.drawable.drag_cover),
                BitmapFactory.decodeResource(getResources(), R.drawable.drag_block),
                BitmapFactory.decodeResource(getResources(), R.drawable.drag_cover_c),
                0.377f);
    }

    protected void initEvent() {
        dragView.setDragListenner(new DragImageView.DragListenner() {
            @Override
            public void onDrag(float position) {
                Toast.makeText(DragImageCodeActivity.this, position + "", Toast.LENGTH_SHORT).show();
                if (Math.abs(position - 0.637) > 0.012)
                    dragView.fail();
                else {
                    dragView.ok();
                    runUIDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dragView.reset();
                        }
                    }, 2000);
                }
            }

        });
    }

    public void runUIDelayed(Runnable run, int de) {
        if (handler == null)
            handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(run, de);
    }

}
