package jingya.com.iosnotificationapplication;

import android.content.Context;
import android.databinding.ViewDataBinding;

import java.util.List;

import jingya.com.base_class_module.BaseAdapter.BaseRvHeaderFooterAdapter;

/**
 * @author kuky.
 */
public class NotificationAdapter extends BaseRvHeaderFooterAdapter<Object> {
    public static final int NOTIFICATION_TYPE = 0;
    public static final int BLANK_TYPE = 1;
    public static final int OTHER_TYPE = 2;

    public NotificationAdapter(Context context) {
        super(context);
    }

    public NotificationAdapter(Context context, List<Object> data) {
        super(context, data);
    }


    @Override
    protected int getLayoutId(int viewType) {
        switch (viewType) {
            case NOTIFICATION_TYPE:
                return R.layout.item_notification;
            case BLANK_TYPE:
                return R.layout.item_blank;
            default:
                return 0;
        }
    }

    @Override
    protected void setVariable(ViewDataBinding binding, Object o) {
        if (o instanceof NotificationBean) {
            binding.setVariable(BR.notification, o);
        }
    }

    @Override
    protected int getItemType(int position) {
        if (mData.get(position) instanceof NotificationBean) {
            return NOTIFICATION_TYPE;
        } else if (mData.get(position) instanceof String) {
            return BLANK_TYPE;
        } else {
            return OTHER_TYPE;
        }
    }
}
