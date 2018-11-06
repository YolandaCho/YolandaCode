package com.yolanda.code.library.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import com.yolanda.code.library.util.PixelUtil;

/**
 * Created by yolanda on 2017/8/27.
 * describe:生成订单、结束订单进度条
 */

public class ProgressRunning extends View {

    private int startColor = Color.parseColor("#FFF7AA");
    private int endColor = Color.parseColor("#F8E71C");
    private int themeColor = Color.parseColor("#F8E71C");
    private int bottomColor = Color.parseColor("#F0F0F0");
    private int textColor = Color.parseColor("#979797");
    private int bottomWidth = 8;
    private int topWidth = 16;
    private int margin = 26;
    private int resultWidth = 20;
    private int leftTextSize = 100;
    private int rightTextSize = 32;
    private int textMargin = 10;

    private float yesWidth;
    private float yesHeight;
    private float viewWidth;
    private float viewHeight;

    private Paint bottomPaint;//背景画笔
    private Paint topPaint;//渐变画笔
    private Paint circlePaint;//圆球画笔
    private Paint resultPaint;//结果画笔
    private Paint textPaint;//文字画笔
    private Path successPath, dstSuccessPath;//成功路径
    private PathMeasure mPathMeasure;
    private Rect rectLeft;

    private RectF mBottomRectF;
    private RectF mTopRectF;
    private int currentProgress = 0;//当前进度
    private float unitAngle;//每进度转过的角度

    private PixelUtil pixelUtil;

    public ProgressRunning(Context context) {
        this(context, null);
    }

    public ProgressRunning(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressRunning(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        pixelUtil = new PixelUtil(context);
        init();
    }

    private void init() {
        bottomWidth = pixelUtil.dp2px(bottomWidth);
        topWidth = pixelUtil.dp2px(topWidth);
        margin = pixelUtil.dp2px(margin);
        resultWidth = pixelUtil.dp2px(resultWidth);
        leftTextSize = pixelUtil.dp2px(leftTextSize);
        rightTextSize = pixelUtil.dp2px(rightTextSize);
        textMargin = pixelUtil.dp2px(textMargin);

        bottomPaint = new Paint();
        bottomPaint.setStyle(Paint.Style.STROKE);
        bottomPaint.setStrokeWidth(bottomWidth);
        bottomPaint.setColor(bottomColor);
        bottomPaint.setAntiAlias(true);

        topPaint = new Paint();
        topPaint.setStyle(Paint.Style.STROKE);
        topPaint.setStrokeCap(Paint.Cap.ROUND);
        topPaint.setStrokeWidth(topWidth);
        topPaint.setAntiAlias(true);

        resultPaint = new Paint();
        resultPaint.setStyle(Paint.Style.STROKE);
        resultPaint.setStrokeCap(Paint.Cap.ROUND);
        resultPaint.setStrokeWidth(resultWidth);
        resultPaint.setColor(themeColor);
        resultPaint.setAntiAlias(true);

        circlePaint = new Paint();
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setColor(themeColor);
        circlePaint.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setAntiAlias(true);
        textPaint.setColor(textColor);

        successPath = new Path();
        dstSuccessPath = new Path();
        mPathMeasure = new PathMeasure();

        mBottomRectF = new RectF();
        mTopRectF = new RectF();
        unitAngle = (float) (360 / 100.0);
        rectLeft = new Rect();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewWidth = this.getMeasuredWidth();
        viewHeight = this.getMeasuredHeight();

        mBottomRectF.left = bottomWidth; // 左上角x
        mBottomRectF.top = bottomWidth; // 左上角y
        mBottomRectF.right = viewWidth - bottomWidth; // 左下角x
        mBottomRectF.bottom = viewHeight - bottomWidth; // 右下角y

        mTopRectF.left = topWidth / 2; // 左上角x
        mTopRectF.top = topWidth / 2; // 左上角y
        mTopRectF.right = viewWidth - topWidth / 2; // 左下角x
        mTopRectF.bottom = viewHeight - topWidth / 2; // 右下角y

        yesWidth = pixelUtil.dp2px(120);
        yesHeight = pixelUtil.dp2px(86);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (mStatus) {
            case STATUS_LOADING:
                onDrawLoading(canvas);
                break;
            case STATUS_SUCCESS:
                onDrawSuccess(canvas);
                break;
            case STATUS_FAILED:
                onDrawLoading(canvas);
                if (mOnLoadingListener != null) {
                    mOnLoadingListener.onLoadingFailed();
                }
                break;
        }
    }

    private void onDrawLoading(Canvas canvas) {
        if (currentProgress <= 0) {
            drawBg(canvas);
            drawCircle(canvas, viewWidth / 2f, topWidth / 2f);
            drawCircle(canvas, viewWidth / 2f, topWidth / 2f + margin);
            drawText(canvas);
        } else if (currentProgress < 100) {
            drawBg(canvas);
            drawProgress(canvas);
            drawText(canvas);
            canvas.save();
            canvas.rotate(unitAngle * currentProgress, viewWidth / 2f, viewHeight / 2f);
            drawCircle(canvas, viewWidth / 2f, topWidth / 2f + margin);
            canvas.restore();
        } else if (currentProgress == 100) {
            drawProgressAll(canvas);
            drawText(canvas);
        }
    }

    private void onDrawSuccess(Canvas canvas) {
        drawProgressAll(canvas);
        dstSuccessPath.reset();
//        dstSuccessPath.lineTo(0, 0);
        drawYes(canvas);
    }

    private void drawBg(Canvas canvas) {
        canvas.drawArc(mBottomRectF, -90, 360, false, bottomPaint);
    }

    private void drawProgress(Canvas canvas) {
        for (int i = 0, end = (int) (currentProgress * unitAngle); i <= end; i++) {
            topPaint.setColor(getGradient(i / (float) end, startColor, endColor));
            canvas.drawArc(mTopRectF, -90 + i, 1, false, topPaint);
        }
    }

    private void drawCircle(Canvas canvas, float x, float y) {
        canvas.drawCircle(x, y, topWidth / 2f, circlePaint);
    }

    private void drawProgressAll(Canvas canvas) {
        canvas.drawArc(mTopRectF, -90, 360, false, topPaint);
    }

    private void drawYes(Canvas canvas) {
        successPath.moveTo(viewWidth / 2f - yesWidth / 2f, viewHeight / 2f);
        successPath.lineTo(viewWidth / 2f - yesWidth / 6f, viewHeight / 2f + yesHeight / 2f);
        successPath.lineTo(viewWidth / 2f + yesWidth / 2f, viewHeight / 2f - yesHeight / 2f);
        mPathMeasure.nextContour();
        mPathMeasure.setPath(successPath, false);
        mPathMeasure.getSegment(0, successValue * mPathMeasure.getLength(), dstSuccessPath, true);
        resultPaint.setPathEffect(new CornerPathEffect(resultWidth / 2));
        canvas.drawPath(dstSuccessPath, resultPaint);
    }

    private void drawText(Canvas canvas) {
        textPaint.setTextSize(leftTextSize);
        textPaint.getTextBounds(progressNumber, 0, progressNumber.length(), rectLeft);
        canvas.drawText(progressNumber, viewWidth / 2f - rectLeft.width() / 2f, viewHeight / 2f + rectLeft.height() / 2f, textPaint);
        textPaint.setTextSize(rightTextSize);
        canvas.drawText(percent, viewWidth / 2f + rectLeft.width() / 2f + textMargin, viewHeight / 2f + rectLeft.height() / 2f, textPaint);
    }

    private int getGradient(float fraction, int startColor, int endColor) {
        if (fraction > 1) fraction = 1;
        int alphaStart = Color.alpha(startColor);
        int redStart = Color.red(startColor);
        int blueStart = Color.blue(startColor);
        int greenStart = Color.green(startColor);
        int alphaEnd = Color.alpha(endColor);
        int redEnd = Color.red(endColor);
        int blueEnd = Color.blue(endColor);
        int greenEnd = Color.green(endColor);
        int alphaDifference = alphaEnd - alphaStart;
        int redDifference = redEnd - redStart;
        int blueDifference = blueEnd - blueStart;
        int greenDifference = greenEnd - greenStart;
        int alphaCurrent = (int) (alphaStart + fraction * alphaDifference);
        int redCurrent = (int) (redStart + fraction * redDifference);
        int blueCurrent = (int) (blueStart + fraction * blueDifference);
        int greenCurrent = (int) (greenStart + fraction * greenDifference);
        return Color.argb(alphaCurrent, redCurrent, greenCurrent, blueCurrent);
    }

    private float successValue;//动画值
    private String progressNumber = "0";//绘制的进度值
    private String percent = "%";

    private void startSuccessAnim() {
        ValueAnimator success = ValueAnimator.ofFloat(0f, 1.0f);
        success.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                successValue = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        success.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mOnLoadingListener != null) {
                    mOnLoadingListener.onLoadingSuccess();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        success.setDuration(500);
        success.start();
    }

    public void setProgress(int progress) {
        this.currentProgress = progress;
        this.progressNumber = progress + "";
        invalidate();
    }

    public void setStatus(int status) {
        this.mStatus = status;
        switch (status) {
            case STATUS_LOADING:
                break;
            case STATUS_SUCCESS:
                startSuccessAnim();
                break;
            case STATUS_FAILED:
                break;
        }
        invalidate();
    }

    public void setOnLoadingListener(OnLoadingListener listener) {
        this.mOnLoadingListener = listener;
    }

    public static final int STATUS_LOADING = 1;
    public static final int STATUS_SUCCESS = 2;
    public static final int STATUS_FAILED = 3;
    public int mStatus = STATUS_LOADING;

    private OnLoadingListener mOnLoadingListener;

    public interface OnLoadingListener {

        void onLoadingSuccess();

        void onLoadingFailed();
    }

}
