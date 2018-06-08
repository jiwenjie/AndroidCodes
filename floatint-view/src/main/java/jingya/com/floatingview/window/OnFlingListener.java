package jingya.com.floatingview.window;

/**
 * @author kuky.
 */

public abstract class OnFlingListener {
    // 手指按下
    public void onFingerDown() {
    }

    // 手指抬起
    public void onFingerUp(int slideDirection) {
    }

    // 手势上滑
    public void onScrollUp(float scrollY) {
    }

    // 手势下滑
    public void onScrollDown(float scrollY) {
    }

    // 手势左滑
    public void onScrollLeft(float scrollX) {
    }

    // 手势右滑
    public void onScrollRight(float scrollX) {
    }
}
