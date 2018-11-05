package com.yolanda.code.library;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.yolanda.code.library.widget.NumberCodeView;

public class NumberCodeActivity extends Activity{
    NumberCodeView mNumberCodeView;
    private String mInputCode;

    private NumberCodeView.OnNumberInputListener mNumberInputListener =
                new NumberCodeView.OnNumberInputListener() {
                @Override
                public void onInputFinish() {
                    mInputCode = mNumberCodeView.getInputCode();
                }

                @Override
                public void onInputIng() {

                }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_code);
        initView();
    }

    public void initView() {
        mNumberCodeView = (NumberCodeView) findViewById(R.id.number_code_view);
        mNumberCodeView.setOnNumberInputListener(mNumberInputListener);
    }
}
