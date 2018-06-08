package jingya.com.base_class_module;

/**
 * @author Kuky
 *         <p>
 *         网络数据请求回调
 */
public class ResultCallBack {

    public interface OnHttpRequestCallBack<T> {
        void onSuccess(T t);

        void onFail(String msg);
    }

    public interface onListCallBack<T> {
        void onSucceed(T t);

        void onEmpty();

        void onFailed();
    }

    public interface OnResultBack<T> {
        void onResult(T t);
    }
}
