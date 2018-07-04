package jingya.com.iosnotificationapplication;

import android.animation.ValueAnimator;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import jingya.com.base_class_module.BaseUtils.LogUtils;
import jingya.com.base_class_module.BaseUtils.ScreenUtils;
import jingya.com.base_class_module.BaseViews.BaseActivity;
import jingya.com.iosnotificationapplication.databinding.ActivityMainBinding;
import jingya.com.iosnotificationapplication.databinding.HeaderLayoutBinding;

public class MainActivity extends BaseActivity<ActivityMainBinding> {
    private static final int OLD_NOTIFICATION_VIEW = 1001;
    private static final int NEW_NOTIFICATION_VIEW = 1002;
    private static final int BLANK_VIEW = 1003;
    private static final int ERROR_VIEW = 1004;

    private List<NotificationBean> oldNotifications = new ArrayList<>();
    private List<NotificationBean> newNotifications = new ArrayList<>();
    private List<Object> notificationData = new ArrayList<>();
    private NotificationAdapter notificationAdapter;
    private HeaderLayoutBinding headerLayoutBinding;
    private int mHeadHeight, mScreenHeight;
    /**
     * 占位 View 的属性
     */
    private FrameLayout.LayoutParams blankLp;
    /**
     * 占位 View 的初始高度
     */
    private int blankViewHeight;

    private LinearLayoutManager mListManager;
    /**
     * 刚进入时能见的最后一个 itemPosition
     */
    private int initLastItemPosition;
    /**
     * 用于记录 RecyclerView 滑动
     */
    private int lastVerticalOffset, currentVerticalOffset, verticalDelta;
    /**
     * 判断是否往上滑
     */
    private boolean isDragUp;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initActivity(Bundle savedInstanceState) {
        mListManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
        mScreenHeight = ScreenUtils.getScreenHeight(MainActivity.this);

        for (int i = 0; i < 3; i++) {
            NotificationBean nb = new NotificationBean();
            nb.setNotificationContent("NEW NOTIFICATION " + i);
            nb.setNewNotification(true);
            newNotifications.add(nb);
        }

        for (int i = 0; i < 15; i++) {
            NotificationBean nb = new NotificationBean();
            nb.setNotificationContent("OLD NOTIFICATION " + i);
            nb.setNewNotification(false);
            oldNotifications.add(nb);
        }

        blankLp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);

        notificationData.addAll(newNotifications);
        notificationData.add("BLANK ITEM");
        notificationData.addAll(oldNotifications);

        notificationAdapter = new NotificationAdapter(MainActivity.this, notificationData);
        mViewBinding.notificationList.setLayoutManager(mListManager);
        mViewBinding.notificationList.setAdapter(notificationAdapter);

        headerLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(MainActivity.this), R.layout.header_layout,
                mViewBinding.notificationList, false);
        notificationAdapter.addHeaderBinding(headerLayoutBinding);
        headerLayoutBinding.musicPlayer.setVisibility(View.GONE);

        /**
         * 必要延时，正确获取 RecyclerView 当前屏的最后一个 item
         */
        Observable.timer(100, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        initLastItemPosition = mListManager.findLastVisibleItemPosition();
                        int lastViewType = lastPositionItemType();
                        LogUtils.e("initLastPosition: " + initLastItemPosition);
                        LogUtils.e("LastType: " + lastPositionItemType());

                        switch (lastViewType) {
                            /**
                             * 最后一个可见为新通知，取消占位 View
                             */
                            case NEW_NOTIFICATION_VIEW:
                                blankLp.height = 0;
                                break;
                            /**
                             * 最后一个可见为旧通知，(高度为 5/6 屏幕高度 - 新通知区域高度 - 头部高度) 和 100 之间的最大值
                             */
                            case OLD_NOTIFICATION_VIEW:
                                blankLp.height = Math.max(mScreenHeight * 5 / 6 - mHeadHeight - getNewNotificationAreaHeight(), 100);
                                LogUtils.e("newNotificationHeight: " + getNewNotificationAreaHeight() + ", blankHeight: " + blankLp.height);
                                break;
                        }

                        blankViewHeight = blankLp.height;

                        View blankView = getBlankView();

                        if (blankView != null) {
                            blankView.findViewById(R.id.blank_content).setLayoutParams(blankLp);
                            /**
                             * 延时设置透明度，需要先把 占位 view 的高度设置完成以后再设置
                             * 不做延时会出现数量多于实际数量
                             */
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    initOldNotificationArea();
                                }
                            }, 50);
                        }
                    }
                });
    }

    @Override
    protected void setListener() {
        super.setListener();
        /**
         * 计算头部的高度
         */
        headerLayoutBinding.headerContainer.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                headerLayoutBinding.headerContainer.getViewTreeObserver().removeOnPreDrawListener(this);
                mHeadHeight = headerLayoutBinding.headerContainer.getHeight();
                return false;
            }
        });

        mViewBinding.notificationList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                isDragUp = dy > 0;

                currentVerticalOffset = recyclerView.computeVerticalScrollOffset();
                /**
                 * 滑动距离
                 */
                verticalDelta = currentVerticalOffset - lastVerticalOffset;

                final View blankView = getBlankView();

                /**
                 * 动态设置 BlankView 高度
                 */
                if (blankView != null && blankView.getHeight() > blankViewHeight / 5 && blankView.getHeight() <= blankViewHeight) {
                    blankLp.height -= 4 * verticalDelta;

                    /**
                     * 当前屏的 OldNotification
                     */
                    final List<View> oldNotifications = getOldNotificationItemsCurrentPage();
                    final int range = oldNotifications.size();

                    for (int i = 0; i < range; i++) {
                        oldNotifications.get(i).setAlpha(1 - (blankLp.height * 1.0f * (i + 1)) / (3 * blankViewHeight));
                    }

                    /**
                     * 最小范围
                     */
                    if (blankLp.height <= blankViewHeight / 5) {
                        ValueAnimator scrollAnim = ValueAnimator.ofInt(blankLp.height, 0);
                        scrollAnim.setDuration(100);
                        scrollAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                int animValue = (int) animation.getAnimatedValue();
                                blankLp.height = animValue;
                                blankView.findViewById(R.id.blank_content).setLayoutParams(blankLp);

                                for (int i = 0; i < range; i++) {
                                    oldNotifications.get(i).setAlpha(1 - (animValue * 1.0f * ((i + 1))) / (3 * blankViewHeight));
                                }
                            }
                        });
                        scrollAnim.start();
                    }

                    /**
                     * 最大范围
                     */
                    if (blankLp.height >= blankViewHeight) {
                        blankLp.height = blankViewHeight;
                        for (int i = 0; i < range; i++) {
                            oldNotifications.get(i).setAlpha(0);
                        }
                    }

                    blankView.findViewById(R.id.blank_content).setLayoutParams(blankLp);
                }

                /**
                 * 前一个值赋给上一个值
                 */
                lastVerticalOffset = currentVerticalOffset;
            }
        });

        mViewBinding.notificationList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    final View blankView = getBlankView();
                    if (blankView != null && blankLp.height > 0 && blankLp.height < blankViewHeight) {
                        blankLp.height = blankViewHeight;
                        blankView.findViewById(R.id.blank_content).setLayoutParams(blankLp);

                        /**
                         * 获取头部距离顶部的高度
                         */
                        int topOffset = mViewBinding.notificationList.getChildAt(0).getTop();
                        /**
                         * 头部回弹
                         */
                        mViewBinding.notificationList.scrollBy(0, topOffset);
                    } else if (blankView != null && !isBottom()) {
                        /**
                         * 占位 view 高度为 0，释放手指回弹头部
                         */
                        int topOffset = mViewBinding.notificationList.getChildAt(0).getTop();
                        int bottomOffset = mViewBinding.notificationList.getChildAt(0).getBottom();
                        mViewBinding.notificationList.scrollBy(0, Math.abs(topOffset) > Math.abs(bottomOffset) ? bottomOffset : topOffset);
                    }
                }
                return true;
            }
        });

        mViewBinding.notificationList.setOnActionListener(new ParallaxRecyclerView.OnActionListener() {
            @Override
            public void onActionUp() {
                final View blankView = getBlankView();
                if (blankView != null && blankLp.height > 0 && blankLp.height < blankViewHeight) {
                    blankLp.height = blankViewHeight;
                    blankView.findViewById(R.id.blank_content).setLayoutParams(blankLp);

                    /**
                     * 获取头部距离顶部的高度
                     */
                    int topOffset = mViewBinding.notificationList.getChildAt(0).getTop();
                    /**
                     * 头部回弹
                     */
                    mViewBinding.notificationList.scrollBy(0, topOffset);
                } else if (blankView != null && !isBottom()) {
                    /**
                     * 占位 view 高度为 0，释放手指回弹头部
                     */
                    int topOffset = mViewBinding.notificationList.getChildAt(0).getTop();
                    int bottomOffset = mViewBinding.notificationList.getChildAt(0).getBottom();
                    mViewBinding.notificationList.scrollBy(0, Math.abs(topOffset) > Math.abs(bottomOffset) ? bottomOffset : topOffset);
                }
            }
        });
    }

    private boolean isBottom() {
        return isDragUp && mViewBinding.notificationList.computeVerticalScrollExtent()
                + mViewBinding.notificationList.computeVerticalScrollOffset()
                >= mViewBinding.notificationList.computeVerticalScrollRange();

    }

    /**
     * 获取第一次最后可见的 item 类型，用来设置 BlankView 的初始高度
     *
     * @return
     */
    private int lastPositionItemType() {
        /**
         * -1 需要将头部去除
         */
        if (initLastItemPosition > 0) {
            Object data = notificationData.get(initLastItemPosition - 1);
            if (data instanceof NotificationBean) {
                NotificationBean nb = (NotificationBean) data;
                if (nb.isNewNotification()) {
                    return NEW_NOTIFICATION_VIEW;
                } else {
                    return OLD_NOTIFICATION_VIEW;
                }
            } else {
                return BLANK_VIEW;
            }
        }
        return ERROR_VIEW;
    }

    /**
     * 获取占位的 item，从第二个开始获取，第一个为头部
     *
     * @return
     */
    private View getBlankView() {
        for (int i = 1; i < mListManager.getChildCount(); i++) {
            if (mListManager.getChildAt(i) instanceof FrameLayout) {
                return mListManager.getChildAt(i);
            }
        }
        return null;
    }

    /**
     * 获取新消息区域的高度（当前屏幕最后一个可见为 OldNotification 时候调用）
     *
     * @return
     */
    private int getNewNotificationAreaHeight() {
        int height = 0;
        int range = mListManager.findLastVisibleItemPosition();

        for (int i = 1; i < range; i++) {
            if (notificationData.get(i - 1) instanceof NotificationBean &&
                    ((NotificationBean) notificationData.get(i - 1)).isNewNotification()) {
                height += mListManager.getChildAt(i).getHeight();
            }
        }
        return height;
    }

    private void initOldNotificationArea() {
        for (View v : getOldNotificationItemsCurrentPage()) {
            LogUtils.e("v: " + v);
            v.setAlpha(0);
        }
    }

    /**
     * 获取当前屏上的所有旧通知 item
     *
     * @return
     */
    private List<View> getOldNotificationItemsCurrentPage() {
        List<View> views = new ArrayList<>();
        int blankPosition = getBlankViewPosition();
        int range = mListManager.findLastVisibleItemPosition();

        if (blankPosition != -1 && blankPosition < range) {
            for (int i = blankPosition + 1; i <= range; i++) {
                views.add(mListManager.getChildAt(i));
            }
        }
        return views;
    }

    /**
     * 获取占位 view 在当前屏幕的位置
     *
     * @return
     */
    private int getBlankViewPosition() {
        View blankView = getBlankView();
        if (blankView != null) {
            int range = mListManager.findLastVisibleItemPosition();
            for (int i = 0; i < range; i++) {
                if (mListManager.getChildAt(i) == blankView) {
                    return i;
                }
            }
        }
        return -1;
    }
}
