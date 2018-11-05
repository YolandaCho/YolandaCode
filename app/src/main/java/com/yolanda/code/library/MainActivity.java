package com.yolanda.code.library;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * @author Created by yolanda on 2018/11/05.
 */
public class MainActivity extends Activity implements View.OnClickListener{
    private Button btnDragImage;
    private Button btnNumberCode;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    public void init() {
        btnDragImage = (Button) findViewById(R.id.btn_drag_image);
        btnNumberCode = (Button) findViewById(R.id.btn_number_code);
        btnDragImage.setOnClickListener(this);
        btnNumberCode.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_drag_image:
                Intent intentDragImage = new Intent(MainActivity.this, DragImageCodeActivity.class);
                startActivity(intentDragImage);
                break;
            case R.id.btn_number_code:
                Intent intentNumberCode = new Intent(MainActivity.this, NumberCodeActivity.class);
                startActivity(intentNumberCode);
                break;
        }
    }
}
