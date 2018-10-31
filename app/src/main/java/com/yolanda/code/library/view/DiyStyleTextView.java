package com.yolanda.code.library.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.AppCompatTextView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Created by Yolanda on 2017/03/25.
 * edit on 2018/10/31 增加图片
 * @description 自定义部分颜色,图片+点击监听的textview
 */
public class DiyStyleTextView extends AppCompatTextView {

    private String colorRegex;
    private int color;
    private boolean underlineText = false;

    private String imageRegex;
    private Bitmap bitmap;

    public DiyStyleTextView(Context context) {
        super(context);
    }

    public DiyStyleTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public DiyStyleTextView setUnderlineText(boolean underlineText) {
        this.underlineText = underlineText;
        return this;
    }

    public DiyStyleTextView setColorRegex(String colorRegex, int color) {
        setMovementMethod(LinkMovementMethod.getInstance());
        this.colorRegex = colorRegex;
        this.color = color;
        return this;
    }


    public DiyStyleTextView setImageRegex(String imageRegex, Bitmap bitmap) {
        setMovementMethod(LinkMovementMethod.getInstance());
        this.imageRegex = imageRegex;
        this.bitmap = bitmap;
        return this;
    }

    public void setText(CharSequence text, BufferType type) {
        super.setText(setTextStyle(text, false), type);
    }


    public void setDiyTextColor(CharSequence text, String regularExpression, int color, DiyTextClick mDiyTextClick) {
        setColorRegex(regularExpression, color).setDiyTextClickListenner(mDiyTextClick).setTextStyle(text, true);
    }

    public void setDiyTextColor(CharSequence text, String regularExpression, int color) {
        setDiyTextColor(text, regularExpression, color, null);
    }

    public void setDiyTextImage(CharSequence text, String regularExpression, Bitmap bitmap, DiyTextClick mDiyTextClick) {
        setImageRegex(regularExpression, bitmap).setDiyTextClickListenner(mDiyTextClick).setTextStyle(text, true);
    }

    public void setDiyTextImage(CharSequence text, String regularExpression, Bitmap bitmap) {
        setDiyTextImage(text, regularExpression, bitmap, null);
    }


    private List<Integer> indexArr = new ArrayList<>();
    private List<String> strArr = new ArrayList<>();

    public CharSequence setTextStyle(CharSequence text, boolean flag) {
        if (TextUtils.isEmpty(text)) {
            if (flag) super.setText(text);
            return text;
        }

        SpannableStringBuilder styledText = new SpannableStringBuilder(text);

        if (!TextUtils.isEmpty(colorRegex)) {

            indexArr.clear();
            strArr.clear();

            Pattern p = Pattern.compile(colorRegex);
            Matcher m = p.matcher(text);
            while (m.find()) {
                strArr.add(m.group());
                indexArr.add(m.start());
            }
            for (int i = 0; i < indexArr.size(); i++) {
                int index = indexArr.get(i);
                String clickText = strArr.get(i);

                styledText.setSpan(
                        new TextViewClickSpan(clickText),
                        index,
                        index + clickText.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

        }

        if (!TextUtils.isEmpty(imageRegex)) {

            indexArr.clear();
            strArr.clear();

            Pattern p = Pattern.compile(imageRegex);
            Matcher m = p.matcher(text);
            while (m.find()) {
                strArr.add(m.group());
                indexArr.add(m.start());
            }
            for (int i = 0; i < indexArr.size(); i++) {
                int index = indexArr.get(i);
                String clickText = strArr.get(i);
                styledText.setSpan(
                        new ImageSpan(bitmap),
                        index,
                        index + clickText.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                styledText.setSpan(
                        new TextViewClickSpan(clickText),
                        index,
                        index + clickText.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        if (flag) super.setText(styledText);
        return styledText;
    }


    private class TextViewClickSpan extends ClickableSpan {

        private String clickText;

        TextViewClickSpan(String clickText) {
            this.clickText = clickText;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(color);
            ds.setUnderlineText(underlineText); //下划线
        }

        @Override
        public void onClick(View widget) {//点击事件
            if (diyTextClickListenner != null)
                diyTextClickListenner.diyTextClick(clickText);
        }
    }

    private DiyTextClick diyTextClickListenner;

    public interface DiyTextClick {
        void diyTextClick(String s);
    }

    public DiyStyleTextView setDiyTextClickListenner(DiyTextClick mDiyTextClick) {
        this.diyTextClickListenner = mDiyTextClick;
        setClickable(true);
        return this;
    }

}
