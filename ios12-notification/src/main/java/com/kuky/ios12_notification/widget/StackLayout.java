package com.kuky.ios12_notification.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.kuky.ios12_notification.R;


/**
 * @author kuky.
 * @description
 */
public class StackLayout extends LinearLayout {
    private int mChildVerticalGap;
    private int mMaxVisibleChildCount;
    private int mLeft, mTop, mRight, mBottom;
    private float mScale;
    private BaseAdapter mAdapter;

    public StackLayout(Context context) {
        this(context, null);
    }

    public StackLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StackLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initStack(context, attrs);
    }

    private void initStack(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.StackLayout);
        mChildVerticalGap = ta.getDimensionPixelSize(R.styleable.StackLayout_child_vertical_gap,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 20, getResources().getDisplayMetrics()));
        mScale = ta.getFloat(R.styleable.StackLayout_child_horizontal_scale, 0.05f);
        mMaxVisibleChildCount = ta.getInteger(R.styleable.StackLayout_max_visible_child_count, 3);
        ta.recycle();
        setOrientation(VERTICAL);
    }

    public void setAdapter(BaseAdapter adapter) {
        this.mAdapter = adapter;
        int count = Math.min(mAdapter.getCount(), mMaxVisibleChildCount);
        this.removeAllViews();
        for (int i = 0; i < count; i++) {
            View v = mAdapter.getView(i, null, null);
            this.addView(v);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        measureChildren(widthMeasureSpec, heightMeasureSpec);

        mLeft = getPaddingLeft();
        mRight = getPaddingRight();
        mTop = getPaddingTop();
        mBottom = getPaddingBottom();

        if (getChildCount() > 0) {
            View top = getChildAt(getChildCount() - 1);
            int topHeight = top.getMeasuredHeight();
            setMeasuredDimension(mLeft + mRight + widthSize,
                    mTop + mBottom + topHeight + (getChildCount() > mMaxVisibleChildCount ?
                            (mMaxVisibleChildCount - 1) * mChildVerticalGap : (getChildCount() - 1) * mChildVerticalGap));
        } else {
            setMeasuredDimension(mLeft + mRight, mTop + mBottom);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int total = getChildCount();
        int showCount = Math.min(total, mMaxVisibleChildCount);
        int verticalOffset = mTop + mChildVerticalGap * (showCount - 1);
        float hoScale = mScale * (showCount - 1);

        /**
         * 序号从小到大开始堆叠
         */
        for (int i = total - showCount; i < total; i++) {
            View child = getChildAt(i);
            int height = child.getMeasuredHeight();
            child.layout(mLeft, verticalOffset, getWidth() - mRight, verticalOffset + height);
            child.setScaleX(1 - hoScale);

            hoScale -= mScale;
            verticalOffset -= mChildVerticalGap;
        }
    }
}
