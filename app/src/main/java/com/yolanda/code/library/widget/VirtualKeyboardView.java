package com.yolanda.code.library.widget;

import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.yolanda.code.library.R;
import com.yolanda.code.library.util.KeyBoardAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Created by yolanda on 2018/11/06.
 * @description 虚拟键盘
 */
public class VirtualKeyboardView extends RelativeLayout implements AdapterView.OnItemClickListener{

    Context context;

    private GridView gridView;

    private ArrayList<Map<String, String>> valueList;
    private TextView mInputTextView;
//    private OnKeyboardListener mOnkeyboardListener;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mInputTextView == null) return;
        if (position < 11 && position != 9) {  //点击0~9按钮
            String amount = mInputTextView.getText().toString().trim();
            amount = amount + valueList.get(position).get("name");
            mInputTextView.setText(amount);
            setSelection();
        } else {
            if (position == 9) {      //点击退格键
                String amount = mInputTextView.getText().toString().trim();
                if (!amount.contains(".")) {
                    amount = amount + valueList.get(position).get("name");
                    mInputTextView.setText(amount);
                    setSelection();
                }
            }

            if (position == 11) {      //点击退格键
                String amount = mInputTextView.getText().toString().trim();
                if (amount.length() > 0) {
                    amount = amount.substring(0, amount.length() - 1);
                    mInputTextView.setText(amount);
                    setSelection();
                }
            }
        }
    }

    public interface OnKeyboardListener{
        void onKeyboard(String text);
    }

    public VirtualKeyboardView(Context context) {
        this(context, null);
    }

    public VirtualKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        View view = View.inflate(context, R.layout.login_view_virtual_keyboard, null);
        valueList = new ArrayList<>();
        gridView = (GridView) view.findViewById(R.id.gv_keybord);
        initValueList();
        setupView();
        addView(view);
    }

    public void bindInputView(TextView inputView){
        mInputTextView = inputView;
    }

    public ArrayList<Map<String, String>> getValueList() {
        return valueList;
    }

    private void setSelection(){
        if (mInputTextView instanceof EditText){
            final EditText editText = (EditText) mInputTextView;
            Editable ea = editText.getText();
            editText.setSelection(ea.length());
        }
    }

    private void initValueList() {

        // 初始化按钮上应该显示的数字
        for (int i = 1; i < 13; i++) {
            Map<String, String> map = new HashMap<>();
            if (i < 10) {
                map.put("name", String.valueOf(i));
            }
            else if (i == 10) {
                map.put("name", "");
            }
            else if (i == 11) {
                map.put("name", String.valueOf(0));
            } else if (i == 12) {
                map.put("name", "");
            }
            valueList.add(map);
        }
    }

    public GridView getGridView() {
        return gridView;
    }

    private void setupView() {
        KeyBoardAdapter keyBoardAdapter = new KeyBoardAdapter(context, valueList);
        gridView.setAdapter(keyBoardAdapter);
        gridView.setOnItemClickListener(this);
    }
}
