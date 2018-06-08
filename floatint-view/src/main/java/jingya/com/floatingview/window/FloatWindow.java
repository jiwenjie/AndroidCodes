package jingya.com.floatingview.window;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

import jingya.com.base_class_module.BaseUtils.ScreenUtils;
import jingya.com.floatingview.FloatingApplication;
import jingya.com.floatingview.R;


/**
 * @author kuky.
 */
public class FloatWindow {
    public static final int SLIDE_LEFT_TO_RIGHT = 2001;
    public static final int SLIDE_RIGHT_TO_LEFT = 2002;
    public static final int SLIDE_TOP_TO_BOTTOM = 2003;
    public static final int SLIDE_BOTTOM_TO_TOP = 2004;

    @SuppressLint("StaticFieldLeak")
    private static FloatWindow mWindow;
    private FloatView mFloatView;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mParams, mContainerParams;
    private int mScreenWidth, mScreenHeight;
    private Context mContext;
    private View mContentView;
    private RelativeLayout mContainer;
    private boolean isCenterShow;
    private int mSlideType = SLIDE_BOTTOM_TO_TOP;
    private OnLoadEndListener mOnLoadEndListener;
    private OnFingerDownListener mOnFingerDownListener;
    private OnContentScrollListener mOnContentScrollListener;
    private boolean needAlpha;
    private boolean setBackground;

    /**
     * 监听页面完全显示，完全显示再刷新数据，防止闪屏
     */
    public interface OnLoadEndListener {
        void onLoadEnd();
    }

    public interface OnFingerDownListener {
        void onFingerDown();
    }

    public interface OnContentScrollListener {
        void onContentScroll(View container);
    }

    public void setOnLoadEndListener(OnLoadEndListener listener) {
        this.mOnLoadEndListener = listener;
    }

    public void setOnFingerDownListener(OnFingerDownListener listener) {
        this.mOnFingerDownListener = listener;
    }

    public void setOnContentScrollListener(OnContentScrollListener listener) {
        this.mOnContentScrollListener = listener;
    }

    private FloatWindow() {
        this.mContext = FloatingApplication.getContext();
        mContainer = new RelativeLayout(mContext);
    }

    public static FloatWindow getInstance() {
        if (mWindow == null) {
            synchronized (FloatWindow.class) {
                if (mWindow == null) {
                    mWindow = new FloatWindow();
                }
            }
        }
        return mWindow;
    }

    /**
     * 设置滑动朝向
     *
     * @param slideType
     * @return
     */
    public FloatWindow setSlideType(int slideType) {
        this.mSlideType = slideType;
        return this;
    }

    /**
     * 设置悬浮条属性
     */
    public FloatWindow initFloatView() {
        initFloatParams();

        mFloatView = new FloatView(FloatingApplication.getContext(), new OnFlingListener() {
            @Override
            public void onFingerDown() {
                needAlpha = true;
                setBackground = true;
                mFloatView.setVisibility(View.INVISIBLE);

                if (mOnFingerDownListener != null) {
                    mOnFingerDownListener.onFingerDown();
                }

                mScreenWidth = ScreenUtils.getScreenWidth(mContext);
                mScreenHeight = ScreenUtils.getScreenHeight(mContext);

                try {
                    mWindowManager.updateViewLayout(mContainer, mContainerParams);
                } catch (Exception e) {
                    e.printStackTrace();
                    mWindowManager.addView(mContainer, mContainerParams);
                    isCenterShow = true;
                }

                mContentView = LayoutInflater.from(mContext).inflate(R.layout.content_view, mContainer, false);
                RelativeLayout.LayoutParams contentLp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                mContentView.setLayoutParams(contentLp);
                mContentView.setVisibility(View.INVISIBLE);
                mContainer.addView(mContentView);

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                }


                switch (mSlideType) {
                    case SLIDE_LEFT_TO_RIGHT:
                        mContentView.layout(0, 0, 0, mScreenHeight);
                        break;

                    case SLIDE_RIGHT_TO_LEFT:
                        mContentView.layout(mScreenWidth, 0, mScreenWidth, mScreenHeight);
                        break;

                    case SLIDE_TOP_TO_BOTTOM:
                        mContentView.layout(0, 0, mScreenWidth, 0);
                        break;

                    case SLIDE_BOTTOM_TO_TOP:
                        mContentView.layout(0, mScreenHeight, mScreenWidth, mScreenHeight);
                        break;
                }


                mContentView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (mSlideType) {
                            case SLIDE_LEFT_TO_RIGHT:
                                leftInSmoothToLeft();
                                break;

                            case SLIDE_RIGHT_TO_LEFT:
                                rightInSmoothToRight();
                                break;

                            case SLIDE_TOP_TO_BOTTOM:
                                topInSmoothToTop();
                                break;

                            case SLIDE_BOTTOM_TO_TOP:
                                bottomInSmoothToBottom();
                                break;
                        }
                    }
                });
            }

            @Override
            public void onFingerUp(int direction) {
                needAlpha = false;
                switch (mSlideType) {
                    case SLIDE_LEFT_TO_RIGHT:
                        if (direction == FloatView.DIRECTION_RIGHT) {
                            leftInSmoothToRight();
                        } else {
                            leftInSmoothToLeft();
                        }
                        break;

                    case SLIDE_RIGHT_TO_LEFT:
                        if (direction == FloatView.DIRECTION_LEFT) {
                            rightInSmoothToLeft();
                        } else {
                            rightInSmoothToRight();
                        }
                        break;

                    case SLIDE_TOP_TO_BOTTOM:
                        if (direction == FloatView.DIRECTION_DOWN) {
                            topInSmoothToBottom();
                        } else {
                            topInSmoothToTop();
                        }
                        break;

                    case SLIDE_BOTTOM_TO_TOP:
                        if (direction == FloatView.DIRECTION_UP) {
                            bottomInSmoothToTop();
                        } else {
                            bottomInSmoothToBottom();
                        }
                        break;
                }
            }

            @Override
            public void onScrollDown(float scrollY) {
                if (mSlideType == SLIDE_TOP_TO_BOTTOM) {
                    mContentView.layout(0, 0, mScreenWidth, (int) scrollY);
                    makeContentViewVisible();
                    setContainerBackground();
                    setBackgroundAlpha(mContainer, (int) scrollY, mScreenHeight);
                }
            }

            @Override
            public void onScrollUp(float scrollY) {
                if (mSlideType == SLIDE_BOTTOM_TO_TOP) {
                    mContentView.layout(0, mScreenHeight - (int) scrollY, mScreenWidth, mScreenHeight);
                    makeContentViewVisible();
                    setContainerBackground();
                    setBackgroundAlpha(mContainer, (int) scrollY, mScreenHeight);
                }
            }

            @Override
            public void onScrollLeft(float scrollX) {
                if (mSlideType == SLIDE_RIGHT_TO_LEFT) {
                    mContentView.layout(mScreenWidth - (int) scrollX, 0, mScreenWidth, mScreenHeight);
                    makeContentViewVisible();
                    setContainerBackground();
                    setBackgroundAlpha(mContainer, (int) scrollX, mScreenWidth);
                }
            }

            @Override
            public void onScrollRight(float scrollX) {
                if (mSlideType == SLIDE_LEFT_TO_RIGHT) {
                    mContentView.layout(0, 0, (int) scrollX, mScreenHeight);
                    makeContentViewVisible();
                    setContainerBackground();
                    setBackgroundAlpha(mContainer, (int) scrollX, mScreenWidth);
                }
            }
        });

        switch (mSlideType) {
            case SLIDE_BOTTOM_TO_TOP:
            case SLIDE_TOP_TO_BOTTOM:
                mFloatView.setFloatValues(FloatView.TYPE_TOP_OR_BOTTOM);
                break;

            case SLIDE_LEFT_TO_RIGHT:
            case SLIDE_RIGHT_TO_LEFT:
                mFloatView.setFloatValues(FloatView.TYPE_LEFT_OR_RIGHT);
                break;
        }
        return this;
    }

    private void makeContentViewVisible() {
        /**
         * 只启动一次动画，解决滑动第一下闪屏问题
         */
        if (needAlpha) {
            AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
            alphaAnimation.setDuration(200);
            alphaAnimation.setFillAfter(true);
            mContentView.startAnimation(alphaAnimation);
            alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    mContentView.setVisibility(View.VISIBLE);
                    needAlpha = false;
                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
    }

    /**
     * 设置背景
     */
    private void setContainerBackground() {
        if (setBackground && mOnContentScrollListener != null) {
            mOnContentScrollListener.onContentScroll(mContainer);
            setBackground = false;
        }
    }

    /**
     * 设置基本悬浮参数
     */
    private void initFloatParams() {
        mWindowManager = (WindowManager) FloatingApplication.getContext().getSystemService(Context.WINDOW_SERVICE);
        /**
         * 悬浮条 params
         */
        mParams = new WindowManager.LayoutParams();
        mParams.packageName = FloatingApplication.getContext().getPackageName();
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            mParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        mParams.format = PixelFormat.RGBA_8888;

        // 根据悬浮窗最终的滑动方向，定义 indicatorView 的位置
        switch (mSlideType) {
            case SLIDE_LEFT_TO_RIGHT:
                mParams.gravity = Gravity.CENTER_VERTICAL | Gravity.START;
                break;
            case SLIDE_RIGHT_TO_LEFT:
                mParams.gravity = Gravity.CENTER_VERTICAL | Gravity.END;
                break;
            case SLIDE_TOP_TO_BOTTOM:
                mParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
                break;
            case SLIDE_BOTTOM_TO_TOP:
                mParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
                break;
        }

        /**
         * 悬浮窗 params
         */
        mContainerParams = new WindowManager.LayoutParams();
        mContainerParams.packageName = FloatingApplication.getContext().getPackageName();
        mContainerParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mContainerParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        mContainerParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mContainerParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            mContainerParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        mContainerParams.format = PixelFormat.TRANSPARENT;
    }

    /**
     * 显示悬浮条
     */
    public void show() {
        if (mFloatView == null)
            throw new IllegalStateException("FloatBar can not be null");

        if (mParams == null)
            throw new IllegalStateException("FloatBar LayoutParams can not be null");

        if (mContainer == null)
            throw new IllegalStateException("FloatViewContainer can not be null");

        if (mContainerParams == null)
            throw new IllegalStateException("FloatViewContainer LayoutParams can not be null");

        try {
            mWindowManager.updateViewLayout(mFloatView, mParams);
        } catch (IllegalArgumentException e) {
            mWindowManager.addView(mFloatView, mParams);
        }
    }

    /**
     * 右侧滑进，滑到页面左侧，进入动画
     */
    private void rightInSmoothToLeft() {
        int posX = mScreenWidth - mContentView.getWidth();
        ValueAnimator slideLeftAnim = ValueAnimator.ofInt(posX, 0).setDuration(300 * posX / mScreenWidth);
        slideLeftAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        slideLeftAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int pos = (int) animation.getAnimatedValue();
                mContentView.layout(pos, 0, mScreenWidth, mScreenHeight);
                setBackgroundAlpha(mContainer, mContentView.getWidth(), mScreenWidth);
            }
        });
        slideLeftAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (mOnLoadEndListener != null) {
                    mOnLoadEndListener.onLoadEnd();
                }
            }
        });

        slideLeftAnim.start();
    }

    /**
     * 右侧滑进，滑到页面右侧，退出动画
     */
    private void rightInSmoothToRight() {
        int posX = mScreenWidth - mContentView.getWidth();
        ValueAnimator slideRightAnim = ValueAnimator.ofInt(posX, mScreenWidth).setDuration(300 * (mScreenWidth - posX) / mScreenWidth);
        slideRightAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        slideRightAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int pos = (int) animation.getAnimatedValue();
                mContentView.layout(pos, 0, mScreenWidth, mScreenHeight);
                setBackgroundAlpha(mContainer, mContentView.getWidth(), mScreenWidth);
            }
        });

        slideRightAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                dismissControlCenter();
            }
        });
        slideRightAnim.start();
    }

    /**
     * 左侧滑进，滑到页面右侧，进入动画
     */
    private void leftInSmoothToRight() {
        int posX = mContentView.getWidth();
        ValueAnimator slideRightAnim = ValueAnimator.ofInt(posX, mScreenWidth).setDuration(300 * (mScreenWidth - posX) / mScreenWidth);
        slideRightAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        slideRightAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int pos = (int) animation.getAnimatedValue();
                mContentView.layout(0, 0, pos, mScreenHeight);
                setBackgroundAlpha(mContainer, mContentView.getWidth(), mScreenWidth);
            }
        });
        slideRightAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (mOnLoadEndListener != null) {
                    mOnLoadEndListener.onLoadEnd();
                }
            }
        });
        slideRightAnim.start();
    }

    /**
     * 左侧滑进，滑到页面左侧，退出动画
     */
    private void leftInSmoothToLeft() {
        int posX = mContentView.getWidth();
        ValueAnimator slideLeftAnim = ValueAnimator.ofInt(posX, 0).setDuration(300 * posX / mScreenWidth);
        slideLeftAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        slideLeftAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int pos = (int) animation.getAnimatedValue();
                mContentView.layout(0, 0, pos, mScreenHeight);
                setBackgroundAlpha(mContainer, mContentView.getWidth(), mScreenWidth);
            }
        });

        slideLeftAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                dismissControlCenter();
            }
        });
        slideLeftAnim.start();
    }

    /**
     * 顶部滑进，滑到页面底部，进入动画
     */
    private void topInSmoothToBottom() {
        int posY = mContentView.getHeight();
        ValueAnimator slideDownAnim = ValueAnimator.ofInt(posY, mScreenHeight).setDuration(300 * (mScreenHeight - posY) / mScreenHeight);
        slideDownAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        slideDownAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int pos = (int) animation.getAnimatedValue();
                mContentView.layout(0, 0, mScreenWidth, pos);
                setBackgroundAlpha(mContainer, mContentView.getHeight(), mScreenHeight);
            }
        });
        slideDownAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (mOnLoadEndListener != null) {
                    mOnLoadEndListener.onLoadEnd();
                }
            }
        });
        slideDownAnim.start();
    }

    /**
     * 顶部滑进，滑到顶部，退出动画
     */
    private void topInSmoothToTop() {
        int posY = mContentView.getHeight();
        ValueAnimator slideUpAnim = ValueAnimator.ofInt(posY, 0).setDuration(300 * posY / mScreenHeight);
        slideUpAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        slideUpAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int pos = (int) animation.getAnimatedValue();
                mContentView.layout(0, 0, mScreenWidth, pos);
                setBackgroundAlpha(mContainer, mContentView.getHeight(), mScreenHeight);
            }
        });
        slideUpAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                dismissControlCenter();
            }
        });
        slideUpAnim.start();
    }

    /**
     * 底部滑进，滑到顶部，进入动画
     */
    private void bottomInSmoothToTop() {
        int posY = mContentView.getHeight();
        ValueAnimator slideUpAnim = ValueAnimator.ofInt(posY, mScreenHeight).setDuration(300 * (mScreenHeight - posY) / mScreenHeight);
        slideUpAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        slideUpAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int pos = (int) animation.getAnimatedValue();
                mContentView.layout(0, mScreenHeight - pos, mScreenWidth, mScreenHeight);
                setBackgroundAlpha(mContainer, mContentView.getHeight(), mScreenHeight);
            }
        });
        slideUpAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (mOnLoadEndListener != null) {
                    mOnLoadEndListener.onLoadEnd();
                }
            }
        });
        slideUpAnim.start();
    }

    /**
     * 底部滑进，滑到底部，退出动画
     */
    private void bottomInSmoothToBottom() {
        int posY = mContentView.getHeight();
        ValueAnimator slideDownAnim = ValueAnimator.ofInt(posY, 0).setDuration(300 * posY / mScreenHeight);
        slideDownAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        slideDownAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int pos = (int) animation.getAnimatedValue();
                mContentView.layout(0, mScreenHeight - pos, mScreenWidth, mScreenHeight);
                setBackgroundAlpha(mContainer, mContentView.getHeight(), mScreenHeight);
            }
        });
        slideDownAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                dismissControlCenter();

            }
        });
        slideDownAnim.start();
    }

    /**
     * 设置背景透明度
     *
     * @param view
     * @param visibleAreaSize
     * @param totalSize
     */
    private void setBackgroundAlpha(View view, int visibleAreaSize, int totalSize) {
        try {
            view.getBackground().setAlpha(Math.min((int) (255 * 1.2 * visibleAreaSize / totalSize), 255));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭当前视图
     */
    private void dismissControlCenter() {
        if (isCenterShow) {
            mContentView = null;
            mContainer.removeAllViews();
            mWindowManager.removeView(mContainer);
            isCenterShow = false;
            mFloatView.setVisibility(View.VISIBLE);
        }
    }

    public void killManager() {
        dismissControlCenter();

        if (mWindowManager != null) {
            mWindowManager.removeView(mFloatView);
            mWindowManager = null;
            mWindow = null;
        }
    }
}
