package jingya.com.floatingview.window;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import jingya.com.base_class_module.BaseUtils.ScreenUtils;
import jingya.com.floatingview.R;


/**
 * @author kuky.
 */
public class FloatView extends LinearLayout {
    // 判断长条的布局方向
    public static final int TYPE_LEFT_OR_RIGHT = 1;
    public static final int TYPE_TOP_OR_BOTTOM = 2;

    // 判断抬手的手势方向
    public static final int DIRECTION_LEFT = 1001;
    public static final int DIRECTION_RIGHT = 1002;
    public static final int DIRECTION_UP = 1003;
    public static final int DIRECTION_DOWN = 1004;
    public static final int NO_ACTION = 1005;

    // 手指滑动的阈值
    private static final int X_SLOP = 10;
    private static final int Y_SLOP = 10;

    private Context mContext;
    private TextView mTouchView;
    // 手势监听
    private GestureDetector mGestureDetector;
    private OnFlingListener mOnFlingListener;
    private int mScreenWidth, mScreenHeight;
    private int mDirection = NO_ACTION, mBarType = TYPE_LEFT_OR_RIGHT;
    private float scrollX, scrollY, lastScrollX, lastScrollY;
    private Vibrator mVibrator;

    public FloatView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressLint("ClickableViewAccessibility")
    public FloatView(Context context, OnFlingListener flingListener) {
        super(context);
        this.mContext = context;
        this.mScreenHeight = ScreenUtils.getScreenHeight(mContext);
        this.mScreenWidth = ScreenUtils.getScreenWidth(mContext);
        LayoutInflater.from(mContext).inflate(R.layout.layout_float_view, this);
        mTouchView = findViewById(R.id.touch_view);
        mOnFlingListener = flingListener;
        mGestureDetector = new GestureDetector(mContext, new FloatViewOnGestureListener(mOnFlingListener));
        mVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);

        mTouchView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 按下时候震动提醒
                        mVibrator.vibrate(50);
                        mGestureDetector.onTouchEvent(event);
                        if (mOnFlingListener != null)
                            mOnFlingListener.onFingerDown();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        mGestureDetector.onTouchEvent(event);
                        break;

                    case MotionEvent.ACTION_UP:
                        if (mOnFlingListener != null)
                            mOnFlingListener.onFingerUp(mDirection);
                        // 抬手的时候注意把 direction 回归初始状态
                        mDirection = NO_ACTION;
                }
                return true;
            }
        });
    }

    public void setFloatValues(int barType) {
        this.mBarType = barType;
        setFloatSize();
    }

    /**
     * 设置悬浮条属性
     */
    private void setFloatSize() {
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int margin_1 = ScreenUtils.dip2px(mContext, 1);
        int margin_2 = ScreenUtils.dip2px(mContext, 2);
        float density = ScreenUtils.getScreenDensity(mContext);

        if (mBarType == TYPE_LEFT_OR_RIGHT) {
            lp.width = (int) (density * 5);
            lp.height = Math.min(mScreenWidth, mScreenHeight) / 2;
            lp.setMargins(margin_1, margin_2, margin_1, margin_2);
        } else if (mBarType == TYPE_TOP_OR_BOTTOM) {
            lp.width = Math.min(mScreenWidth, mScreenHeight) / 2;
            lp.height = (int) (density * 5);
            lp.setMargins(margin_2, margin_1, margin_2, margin_1);
        }

        mTouchView.setLayoutParams(lp);
        mTouchView.setBackgroundColor(0xFFFF4081);
        mTouchView.setAlpha(1);
    }

    class FloatViewOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        private OnFlingListener mFlingListener;

        public FloatViewOnGestureListener(OnFlingListener listener) {
            this.mFlingListener = listener;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            float x = e2.getX() - e1.getX();
            float y = e2.getY() - e1.getY();
            float x_abs = Math.abs(x);
            float y_abs = Math.abs(y);

            scrollX = e2.getX();
            scrollY = e2.getY();

            // x > y 且 x 滑动距离大于阈值，则是水平滑动，否则是垂直滑动
            if (mBarType == TYPE_LEFT_OR_RIGHT && x_abs > X_SLOP) {
                // 如果 x 的滑动距离大于 0 则是右滑，否则为左滑
                if (x > 0 && mFlingListener != null) {
                    mOnFlingListener.onScrollRight(x_abs);
                } else if (x < 0 && mFlingListener != null) {
                    mOnFlingListener.onScrollLeft(x_abs);
                }
                // 用来记录抬手前的最后一下是左滑还是右滑，最后通过回调函数传回
                mDirection = (scrollX - lastScrollX) >= 0 ? DIRECTION_RIGHT : DIRECTION_LEFT;
            } else if (mBarType == TYPE_TOP_OR_BOTTOM && y_abs > Y_SLOP) {
                // 如果 y 的滑动距离大于 0 则是下滑， 否则上滑
                if (y > 0 && mFlingListener != null) {
                    mOnFlingListener.onScrollDown(y_abs);
                } else if (y < 0 && mFlingListener != null) {
                    mOnFlingListener.onScrollUp(y_abs);
                }
                // 用来记录抬手前最后一下是上滑还是下滑
                mDirection = (scrollY - lastScrollY >= 0) ? DIRECTION_DOWN : DIRECTION_UP;
            }

            lastScrollX = scrollX;
            lastScrollY = scrollY;

            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }
}