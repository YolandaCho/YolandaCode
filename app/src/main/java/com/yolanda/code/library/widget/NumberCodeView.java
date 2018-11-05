package com.yolanda.code.library.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.ViewCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import com.yolanda.code.library.R;
import com.yolanda.code.library.util.DimenUtil;
import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.text.InputType.TYPE_NULL;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
/**
 * @author Created by yolanda on 2017/6/17.
 * @description 自定义输入框
 */
public class NumberCodeView extends FrameLayout {
    private static final int[] STATE_NORMAL = {-android.R.attr.state_selected};
    private static final int[] STATE_SELECTED = {android.R.attr.state_selected};
    private static final int DEFAULT_TEXT_COLOR = 0xFFffffff;
    private static final int DEFAULT_TEXT_SIZE = 60;   //px
    private static final int DEFAULT_FRAME_SIZE = 80;  //px
    private static final int DEFAULT_FRAME_PADDING = 14;
    private static final int DEFAULT_CODE_LENGTH = 4;
    /**
     * 输入View
     */
    private EditText mEditText;

    private int mLastIndex = 0;
    private int mCurIndex = 0;
    private int mCodeLength = 0;
    private Paint mCodeTextPaint;
    private Rect mTextRect;
    private String mCodeText = "";
    private int mFrameSize = -1;
    private int mFramePadding = -1;
    private int mCodeTextColor = -1;
    private int mCodeTextSize = -1;
    private int mNormalId = R.mipmap.verificate_code_normal;
    private int mSelectId = R.mipmap.verificate_code_selected;
    private boolean mShowSystemKeyboard = true;

    private @DrawableRes
    int mFrameDrawableId = -1;
    private SparseArrayCompat<Drawable> mInputDrawable = new SparseArrayCompat<>();
    private InputMethodManager mInputMethodManager;
    private OnNumberInputListener mOnNumberInputListener;
    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            boolean isBack = mCodeText.length() > s.length();
            mLastIndex = mCurIndex;
            if (!TextUtils.isEmpty(s)) {
                mCodeText = s.toString();
                mCurIndex = isBack ? mCodeText.length() - 1 : mCodeText.length();
                mCurIndex = mCurIndex == mCodeLength ? mCurIndex - 1 : mCurIndex;
            } else {
                mCurIndex = 0;
                mCodeText = "";
            }
            setDrawableState(mLastIndex, STATE_NORMAL);
            if (mCodeText.length() == mCodeLength) {
                if (mOnNumberInputListener != null) {
                    mOnNumberInputListener.onInputFinish();
                }
            } else {
                setDrawableState(mCurIndex, STATE_SELECTED);
                if (mOnNumberInputListener != null) {
                    mOnNumberInputListener.onInputIng();
                }
            }
            invalidate();
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    public NumberCodeView(Context context) {
        this(context, null);
    }

    public NumberCodeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumberCodeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NumberCodeView);
        int size = typedArray.getIndexCount();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                int attr = typedArray.getIndex(i);
                switch (attr) {
                    case R.styleable.NumberCodeView_codeTextColor:
                        mCodeTextColor = typedArray.getColor(attr, -1);
                        break;
                    case R.styleable.NumberCodeView_codeTextSize:
                        mCodeTextSize = typedArray.getDimensionPixelSize(attr, -1);
                        if (DimenUtil.isPxVal(typedArray.peekValue(attr))) {
                            mCodeTextSize = DimenUtil.getPercentHeightSizeBigger(mCodeTextSize);
                        }
                        break;
                    case R.styleable.NumberCodeView_frameSize:
                        mFrameSize = typedArray.getDimensionPixelSize(attr, -1);
                        if (DimenUtil.isPxVal(typedArray.peekValue(attr))) {
                            mFrameSize = DimenUtil.getPercentHeightSizeBigger(mFrameSize);
                        }
                        break;
                    case R.styleable.NumberCodeView_framePadding:
                        mFramePadding = typedArray.getDimensionPixelOffset(attr, -1);
                        if (DimenUtil.isPxVal(typedArray.peekValue(attr))) {
                            mFramePadding = DimenUtil.getPercentWidthSizeBigger(mFramePadding);
                        }
                        break;
                    case R.styleable.NumberCodeView_codeLength:
                        mCodeLength = typedArray.getInt(attr, -1);
                        break;
                    case R.styleable.NumberCodeView_frameDrawableId:
                        mFrameDrawableId = typedArray.getResourceId(attr, -1);
                        break;
                    case R.styleable.NumberCodeView_normal:
                        mNormalId = typedArray.getResourceId(attr, R.mipmap.verificate_code_normal);
                        break;
                    case R.styleable.NumberCodeView_select:
                        mSelectId = typedArray.getResourceId(attr, R.mipmap.verificate_code_selected);
                        break;


                }
            }
        }
        typedArray.recycle();
        if (mCodeTextColor == -1) {
            mCodeTextColor = DEFAULT_TEXT_COLOR;
        }
        if (mCodeTextSize == -1) {
            mCodeTextSize = DimenUtil.getPercentHeightSizeBigger(DEFAULT_TEXT_SIZE);
        }
        if (mFrameSize == -1) {
            mFrameSize = DimenUtil.getPercentHeightSizeBigger(DEFAULT_FRAME_SIZE);
        }
        if (mFramePadding == -1) {
            mFramePadding = DimenUtil.getPercentWidthSizeBigger(DEFAULT_FRAME_PADDING);
        }
        if (mCodeLength <= 0) {
            mCodeLength = DEFAULT_CODE_LENGTH;
        }
        mTextRect = new Rect();
        mInputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        initEditText();
        initTextPaint();
        initStateListDrawable();
        setWillNotDraw(false);
    }

    private void initEditText() {
        mEditText = new EditText(getContext());
        mEditText.addTextChangedListener(mTextWatcher);
        mEditText.setCursorVisible(false);
        ViewCompat.setBackground(mEditText, new ColorDrawable(Color.TRANSPARENT));
        mEditText.setTextColor(Color.TRANSPARENT);
        mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mCodeLength)});
        mEditText.setFocusable(true);
        mEditText.requestFocus();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mEditText.setShowSoftInputOnFocus(true);
        }
        mEditText.setInputType(TYPE_CLASS_NUMBER);
        mEditText.setSingleLine();
        addView(mEditText, new LayoutParams(MATCH_PARENT, MATCH_PARENT));
    }

    /**
     * 是否显示键盘
     * @param showSystemKeyboard true为显示,false为不显示
     */
    public void setShowKeyboard(boolean showSystemKeyboard){
        if (mShowSystemKeyboard == showSystemKeyboard) return;
        mShowSystemKeyboard = showSystemKeyboard;
        if (mShowSystemKeyboard){
            mEditText.setInputType(TYPE_CLASS_NUMBER);
        } else {
            mEditText.setInputType(TYPE_NULL);
        }
    }

    public EditText getInputView(){
        return mEditText;
    }

    public void setText(CharSequence text){
        if (mEditText != null){
            mEditText.setText(text);
        }
    }

    private void initTextPaint() {
        mCodeTextPaint = new TextPaint();
        mCodeTextPaint.setColor(mCodeTextColor);
        mCodeTextPaint.setAntiAlias(true);
        mCodeTextPaint.setTextSize(mCodeTextSize);
        mCodeTextPaint.setFakeBoldText(true);
        mCodeTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    private Drawable getFrameDrawable() {
        if (mFrameDrawableId == -1) {
            StateListDrawable drawable = new StateListDrawable();
            drawable.addState(STATE_NORMAL, ContextCompat.getDrawable(getContext(), mNormalId));
            drawable.addState(STATE_SELECTED, ContextCompat.getDrawable(getContext(), mSelectId));
            return drawable;
        } else {
            return ContextCompat.getDrawable(getContext(), mFrameDrawableId);
        }
    }

    private void initStateListDrawable() {
        for (int i = 0; i < mCodeLength; i++) {
            mInputDrawable.put(i, getFrameDrawable());
        }
        mCurIndex = mLastIndex = 0;
        setDrawableState(mCurIndex, STATE_SELECTED);
    }

//    private static boolean isAttrPxType(TypedArray typeArray, int index) {
//        return typeArray.peekValue(index).type == TypedValue.COMPLEX_UNIT_PX;
//    }

    public void setOnNumberInputListener(OnNumberInputListener listener) {
        this.mOnNumberInputListener = listener;
    }

    /**
     * 设置drawable state
     */
    private void setDrawableState(int index, int[] state) {
        if (index < 0 || index > mInputDrawable.size() - 1) return;
        mInputDrawable.get(index).setState(state);
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (getVisibility() != VISIBLE) {
            mInputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        if (heightSpecMode == MeasureSpec.AT_MOST) {
            height = mFrameSize;
        }
        if (widthSpecMode == MeasureSpec.AT_MOST) {
            width = (mCodeLength * mFrameSize) + (mFramePadding * (mCodeLength - 1));
        }

        int childWidthSpec = getChildMeasureSpec(widthMeasureSpec, 0, width);
        int childHeightSpec = getChildMeasureSpec(heightMeasureSpec, 0, height);
        mEditText.measure(childWidthSpec, childHeightSpec);
        setMeasuredDimension(width, height);
    }

    public String getInputCode() {
        return mCodeText;
    }

    private String indexOfCode(int index) {
        if (TextUtils.isEmpty(mCodeText)) {
            return "";
        }
        if (index < 0 || index > mCodeText.length() - 1) {
            return "";
        }
        return String.valueOf(mCodeText.charAt(index));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int left = 0;
        int right = mFrameSize;

        int size = mInputDrawable.size();
        for (int i = 0; i < size; i++) {
            Drawable drawable = mInputDrawable.get(i);
            drawable.setBounds(left, 0, right, getMeasuredHeight());
            drawable.draw(canvas);

            //绘制文本
            drawCodeText(canvas, drawable.getBounds(), indexOfCode(i));
            left = right + mFramePadding;
            right = left + mFrameSize;
        }
    }

    private void drawCodeText(Canvas canvas, Rect bound, String text) {
        if (!TextUtils.isEmpty(text)) {
            mCodeTextPaint.getTextBounds(text, 0, text.length(), mTextRect);
            canvas.drawText(text, bound.centerX(), bound.height() / 2 + mTextRect.height() / 2, mCodeTextPaint);
        }
    }

    public interface OnNumberInputListener {
        void onInputFinish();
        void onInputIng();
    }
}