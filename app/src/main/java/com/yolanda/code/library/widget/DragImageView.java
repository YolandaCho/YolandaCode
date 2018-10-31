package com.yolanda.code.library.widget;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.yolanda.code.library.R;
import com.yolanda.code.library.view.DiyStyleTextView;

/**
 * @author Created by Yolanda on 2018/10/31.
 * @description 滑动拼图验证码
 */
public final class DragImageView extends FrameLayout implements SeekBar.OnSeekBarChangeListener {

    private final int showTipsTime = 1500;
    private final int animeTime = 333;
    private final int flashTime = 800;

    private ImageView ivCover;
    private ImageView ivBlock;
    private SeekBar sb;
    private TextView tvTips2;
    private DiyStyleTextView tvTips;
    private View vFlash, flContent;

    private Handler handler = new Handler();
    private Bitmap cover, block, completeCover;
    private boolean isNormal;

    public DragImageView(Context context) {
        super(context);
        init();
    }

    public DragImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DragImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        View.inflate(getContext(), R.layout.drag_view, this);
        flContent = findViewById(R.id.drag_fl_content);
        ivCover = findViewById(R.id.drag_iv_cover);
        ivBlock = findViewById(R.id.drag_iv_block);
        tvTips = findViewById(R.id.drag_tv_tips);
        tvTips2 = findViewById(R.id.drag_tv_tips2);
        vFlash = findViewById(R.id.drag_v_flash);

        tvTips.setColorRegex("拼图|成功|失败|正确|[\\d\\.%]+", 0xfff75151);
        sb = findViewById(R.id.drag_sb);
        sb.setMax(getContext().getResources().getDisplayMetrics().widthPixels);
        sb.setOnSeekBarChangeListener(this);
        reset();
    }


    /**
     * 设置资源
     *
     * @param cover         拼图
     * @param block         滑块
     * @param completeCover 完成的拼图
     * @param block_y       滑块Y值比例
     */
    public void setUp(Bitmap cover, Bitmap block, Bitmap completeCover, float block_y) {
        this.cover = cover;
        this.block = block;
        this.completeCover = completeCover;
        ivCover.setImageBitmap(completeCover);
        ivBlock.setImageBitmap(block);
        setLocation(1f * cover.getWidth() / cover.getHeight(), 1f * block.getHeight() / cover.getHeight(), block_y);
    }

    /**
     * 设置比例大小
     *
     * @param cover_wph  图片bili
     * @param block_size 滑块大小占高比
     * @param block_y    滑块位置占高比
     */
    private void setLocation(final float cover_wph, final float block_size, final float block_y) {
        post(new Runnable() {
            @Override
            public void run() {
                final int w = flContent.getMeasuredWidth();
                int h = (int) (w / cover_wph);
                ViewGroup.LayoutParams l = flContent.getLayoutParams();
                l.width = w;
                l.height = h;
                flContent.setLayoutParams(l);

                ViewGroup.MarginLayoutParams l2 = (MarginLayoutParams) ivBlock.getLayoutParams();
                l2.height = (int) (h * block_size);
                l2.width = l2.height * block.getWidth() / block.getHeight();
                l2.topMargin = (int) (h * block_y);
                ivBlock.setLayoutParams(l2);
            }
        });

    }

    public void ok() {
        ivCover.setImageBitmap(completeCover);
        blockHideAnime();
        int penset = (int) (99 - (timeUse > 1 ? timeUse - 1 : 0) / 0.1f);
        if (penset < 1) penset = 1;
        tvTips.setText(String.format("拼图成功: 耗时%.1f秒,打败了%d%%的用户!", timeUse, penset));
        tipsShowAnime(true);
        flashShowAnime();
        sb.setEnabled(false);
    }

    public void fail() {
        twinkleImage(ivBlock);
        tvTips.setText("拼图失败: 请重新拖曳滑块到正确的位置!");
        tipsShowAnime(true);
        handler.postDelayed(resetRun, showTipsTime);
        sb.setEnabled(false);
    }

    public void reset() {
        final int position = sb.getProgress();
        if (position != 0) {
            ValueAnimator animator = ValueAnimator.ofFloat(1f, 0);
            animator.setDuration(animeTime).start();
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float f = (Float) animation.getAnimatedValue();
                    sb.setProgress((int) (position * f));
                }
            });
        }
        tipsShowAnime(false);
        tips2ShowAnime(true);
        sb.setEnabled(true);
        ivBlock.setVisibility(GONE);
        vFlash.setVisibility(GONE);
        ivCover.setImageBitmap(completeCover);
        isNormal = true;
    }

    //===================seekbar监听===================
    private long timeTemp;
    private float timeUse;

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int cw = ivCover.getMeasuredWidth();
        int bw = ivBlock.getMeasuredWidth();
        ViewGroup.MarginLayoutParams l = (MarginLayoutParams) ivBlock.getLayoutParams();
        l.leftMargin = (cw - bw) * progress / seekBar.getMax();
        ivBlock.setLayoutParams(l);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        ivBlock.setVisibility(VISIBLE);
        ivCover.setImageBitmap(cover);
        tips2ShowAnime(false);
        timeTemp = System.currentTimeMillis();
        isNormal = false;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        timeUse = (System.currentTimeMillis() - timeTemp) / 1000.f;
        if (dragListenner != null)
            dragListenner.onDrag(seekBar.getProgress() * 1f / seekBar.getMax());
    }
    //===================seekbar监听===================


    //闪烁滑块
    private void twinkleImage(final View view) {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1.0F);
        animator.setTarget(view);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(showTipsTime).start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = (Float) animation.getAnimatedValue();
                int time = (int) (showTipsTime * f);

                if (time < 125)
                    view.setVisibility(INVISIBLE);
                else if (time < 250)
                    view.setVisibility(VISIBLE);
                else if (time < 375)
                    view.setVisibility(INVISIBLE);
                else
                    view.setVisibility(VISIBLE);
            }
        });
    }

    //提示文本显示隐藏
    private void tipsShowAnime(boolean isShow) {
        if ((tvTips.getVisibility() == VISIBLE) == isShow)
            return;
        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, isShow ? 1f : 0f,
                Animation.RELATIVE_TO_SELF, isShow ? 0f : 1f);
        translateAnimation.setDuration(animeTime);
        //translateAnimation.setInterpolator(new LinearInterpolator());
        tvTips.setAnimation(translateAnimation);
        tvTips.setVisibility(isShow ? VISIBLE : GONE);
    }

    //提示文本显示隐藏
    private void tips2ShowAnime(boolean isShow) {
        if ((tvTips2.getVisibility() == VISIBLE) == isShow)
            return;
        AlphaAnimation translateAnimation = new AlphaAnimation(isShow ? 0 : 1, isShow ? 1 : 0);
        translateAnimation.setDuration(animeTime);
        //translateAnimation.setInterpolator(new LinearInterpolator());
        tvTips2.setAnimation(translateAnimation);
        tvTips2.setVisibility(isShow ? VISIBLE : GONE);
    }

    //成功完成拼图滑块消失
    private void blockHideAnime() {
        AlphaAnimation translateAnimation = new AlphaAnimation(1, 0);
        translateAnimation.setDuration(animeTime);
        //translateAnimation.setInterpolator(new LinearInterpolator());
        ivBlock.setAnimation(translateAnimation);
        ivBlock.setVisibility(GONE);
    }

    //失败震动动画
    private void failAnime() {

    }

    //成功高亮动画
    private void flashShowAnime() {
        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 1f,
                Animation.RELATIVE_TO_SELF, -1f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f);
        translateAnimation.setDuration(flashTime);
        //translateAnimation.setInterpolator(new LinearInterpolator());
        vFlash.setAnimation(translateAnimation);
        vFlash.setVisibility(VISIBLE);
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                vFlash.setVisibility(GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    //失败延时重置控件
    private Runnable resetRun = new Runnable() {

        @Override
        public void run() {
            tipsShowAnime(false);
            tips2ShowAnime(true);
            sb.setEnabled(true);

            final int position = sb.getProgress();
            ValueAnimator animator = ValueAnimator.ofFloat(1f, 0);
            animator.setDuration(animeTime).start();
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float f = (Float) animation.getAnimatedValue();
                    sb.setProgress((int) (position * f));
                }
            });

            isNormal = true;
        }
    };


    //监听
    private DragListenner dragListenner;

    public interface DragListenner {

        void onDrag(float position);

    }

    public void setDragListenner(DragListenner dragListenner) {
        this.dragListenner = dragListenner;
    }
}