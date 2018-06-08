package jingya.com.base_class_module.BaseAdapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * @author Kuky
 */
public abstract class BaseListViewAdapter<T, VB extends ViewDataBinding> extends BaseAdapter {
    protected VB mViewBinding;
    protected List<T> mData;
    protected Context mContext;
    protected int selectedPosition = -1;
    protected LayoutInflater mInflater;

    public void updateAdapterData(List<T> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    public List<T> getAdapterData() {
        return mData;
    }

    public void setSelectedItemPosition(int position) {
        this.selectedPosition = position;
    }

    public BaseListViewAdapter(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            mViewBinding = DataBindingUtil.inflate(mInflater, getLayoutId(), parent, false);
        } else {
            mViewBinding = DataBindingUtil.getBinding(convertView);
        }
        setVariable(mViewBinding, mData.get(position));
        return mViewBinding.getRoot();
    }

    protected abstract void setVariable(VB viewBinding, T t);

    protected abstract int getLayoutId();
}
