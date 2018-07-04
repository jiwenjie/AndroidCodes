package jingya.com.base_class_module.BaseAdapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kuky
 * @description recyclerView adapter 封装类
 * <p>
 * 支持添加多个头部 {@link #addHeaderBinding(ViewDataBinding)}
 * 支持添加多个尾部 {@link #addFooterBinding(ViewDataBinding)}
 * 支持点击事件 {@link #setOnItemClickListener(OnItemClickListener)}
 * 支持长按事件 {@link #setOnItemLongClickListener(OnItemLongClickListener)}
 * 支持多布局，重写 {@link #getItemType(int)} 方法即可
 * 支持单项选定 {@link #setSelectedItemPosition(int)}
 */
public abstract class BaseRvHeaderFooterAdapter<T> extends RecyclerView.Adapter<BaseRvHeaderFooterAdapter.BaseHeaderFooterHolder> {
    private static final int HEADER = 0x00100000;
    private static final int FOOTER = 0x00200000;
    private SparseArray<ViewDataBinding> mHeaderBindings = new SparseArray<>();
    private SparseArray<ViewDataBinding> mFooterBindings = new SparseArray<>();
    protected Context mContext;
    protected List<T> mData;
    private LayoutInflater mInflater;
    protected int mSelectedPosition = -1;
    private int mCurrentPosition = -1;
    private boolean mCloseRecycle = false;
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    public BaseRvHeaderFooterAdapter(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(mContext);
    }

    @Deprecated
    public BaseRvHeaderFooterAdapter(Context context, boolean closeRecycle) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(mContext);
        this.mCloseRecycle = closeRecycle;
    }

    public BaseRvHeaderFooterAdapter(Context context, List<T> data) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(mContext);
        this.mData = data;
    }

    @Deprecated
    public BaseRvHeaderFooterAdapter(Context context, List<T> data, boolean closeRecycle) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(mContext);
        this.mData = data;
        this.mCloseRecycle = closeRecycle;
    }

    /**
     * 设置监听
     */
    public void setOnItemClickListener(OnItemClickListener clickListener) {
        this.mOnItemClickListener = clickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener longClickListener) {
        this.mOnItemLongClickListener = longClickListener;
    }

    /**
     * 当前选中项, 默认为 -1
     */
    public void setSelectedItemPosition(int position) {
        mSelectedPosition = position;
        notifyDataSetChanged();
    }

    /**
     * 添加头部 binding
     */
    public void addHeaderBinding(ViewDataBinding headBinding) {
        mHeaderBindings.put(HEADER + getHeaderCount(), headBinding);
        notifyItemInserted(getHeaderCount());
    }

    /**
     * 添加底部 binding
     */
    public void addFooterBinding(ViewDataBinding footerBinding) {
        mFooterBindings.put(FOOTER + getFooterCount(), footerBinding);
        int insertPos = (mData == null ? 0 : mData.size() - 1);
        if (haveHeaderBinding()) insertPos += mHeaderBindings.size();
        notifyItemInserted(insertPos);
    }

    /**
     * 返回头部视图的 ViewDataBinding 列表
     */
    public List<ViewDataBinding> getHeaderBindings() {
        List<ViewDataBinding> headers = new ArrayList<>();
        for (int i = HEADER; i < getHeaderCount() + HEADER; i++) {
            headers.add(mHeaderBindings.get(i));
        }
        return headers;
    }

    /**
     * 返回尾部视图的 ViewDataBinding 列表
     */
    public List<ViewDataBinding> getFooterBindings() {
        List<ViewDataBinding> footers = new ArrayList<>();
        for (int i = FOOTER; i < getFooterCount() + FOOTER; i++) {
            footers.add(mFooterBindings.get(i));
        }
        return footers;
    }

    /**
     * 更新数据
     *
     * @param data
     */
    public void updateAdapterData(List<T> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    public List<T> getAdapterData() {
        return mData;
    }

    /**
     * 添加数据
     *
     * @param t
     */
    public void addData(T t) {
        if (mData != null) {
            this.mData.add(t);
            notifyDataSetChanged();
        } else {
            throw new IllegalStateException("mData is null and init first");
        }
    }

    public void addData(int position, T t) {
        if (mData != null) {
            this.mData.add(position, t);
            notifyDataSetChanged();
        } else {
            throw new IllegalStateException("mData is null and init first");
        }
    }

    /**
     * 添加数据
     *
     * @param data
     */
    public void addAllData(List<T> data) {
        if (mData != null) {
            this.mData.addAll(data);
            notifyDataSetChanged();
        } else {
            throw new IllegalStateException("mData is null and init first");
        }
    }

    /**
     * 去除数据
     *
     * @param t
     */
    public void removeData(T t) {
        if (mData != null && mData.contains(t)) {
            mData.remove(t);
            notifyDataSetChanged();
        } else {
            throw new IllegalStateException("data not in mData and check it");
        }
    }

    /**
     * 去除数据
     *
     * @param position
     */
    public void removeData(int position) {
        if (mData != null) {
            mData.remove(position);
            notifyDataSetChanged();
        } else {
            throw new IllegalStateException("mData is null and init first");
        }
    }

    /**
     * 去除全部数据
     */
    public void cleanData() {
        mData.clear();
        notifyDataSetChanged();
    }

    protected int getCurrentPosition() {
        return mCurrentPosition;
    }

    /**
     * 获取当前的 data 位置
     */
    protected int getCurrentDataPosition(T t) {
        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i) == t) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 头部长度
     */
    private int getHeaderCount() {
        return mHeaderBindings.size();
    }

    /**
     * 数据长度
     */
    private int getDataCount() {
        return mData == null ? 0 : mData.size();
    }

    /**
     * 底部长度
     */
    private int getFooterCount() {
        return mFooterBindings.size();
    }

    /**
     * 是否有头部
     */
    private boolean haveHeaderBinding() {
        return mHeaderBindings.size() > 0;
    }

    /**
     * 是否有底部
     */
    private boolean haveFooterBinding() {
        return mFooterBindings.size() > 0;
    }

    /**
     * 是否是头部
     */
    private boolean isHeaderBinding(int position) {
        return haveHeaderBinding() && position < getHeaderCount();
    }

    /**
     * 是否是底部
     */
    private boolean isFooterBinding(int position) {
        return haveFooterBinding() && position >= getHeaderCount() + getDataCount();
    }

    /**
     * 获取实际 position
     */
    private int getRealPosition(BaseHeaderFooterHolder holder) {
        return holder.getLayoutPosition();
    }

    @NonNull
    @Override
    public BaseHeaderFooterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BaseHeaderFooterHolder holder;
        if (haveHeaderBinding() && mHeaderBindings.get(viewType) != null) {
            holder = new BaseHeaderFooterHolder(mHeaderBindings.get(viewType));
        } else if (haveFooterBinding() && mFooterBindings.get(viewType) != null) {
            holder = new BaseHeaderFooterHolder(mFooterBindings.get(viewType));
        } else {
            holder = new BaseHeaderFooterHolder(DataBindingUtil.inflate(mInflater, getLayoutId(viewType), parent, false));
        }
        return holder;
    }

    protected abstract int getLayoutId(int viewType);

    @Override
    public void onBindViewHolder(@NonNull BaseHeaderFooterHolder holder, int position) {
        if (!isHeaderBinding(position) && !isFooterBinding(position)) {
            position = position - getHeaderCount();
            this.mCurrentPosition = position;
            setVariable(holder.getBinding(), mData.get(position));
            holder.getBinding().executePendingBindings();

            if (mCloseRecycle) {
                holder.setIsRecyclable(false);
            }

            if (mOnItemClickListener != null) {
                final int finalPosition = position;
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onItemClick(v, finalPosition);
                    }
                });
            }

            if (mOnItemLongClickListener != null) {
                final int finalPosition = position;
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        mOnItemLongClickListener.onItemLongClick(v, finalPosition);
                        return false;
                    }
                });
            }
        }
    }

    protected abstract void setVariable(ViewDataBinding binding, T t);

    @Override
    public int getItemViewType(int position) {
        if (isHeaderBinding(position)) return mHeaderBindings.keyAt(position);
        else if (isFooterBinding(position))
            return mFooterBindings.keyAt(position - getHeaderCount() - getDataCount());
        else return getItemType(position - getHeaderCount());
    }

    protected int getItemType(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return getHeaderCount() + getDataCount() + getFooterCount();
    }

    /**
     * 适配 GridLayoutManager 添加头部底部
     */
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();
        if (lm instanceof GridLayoutManager) {
            final GridLayoutManager gm = (GridLayoutManager) lm;
            gm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (isHeaderBinding(position) || isFooterBinding(position)) {
                        return gm.getSpanCount();
                    } else {
                        return 1;
                    }
                }
            });
        }
    }

    /**
     * 适配 StaggeredGridLayoutManager 添加头部底部
     */
    @Override
    public void onViewAttachedToWindow(BaseHeaderFooterHolder holder) {
        super.onViewAttachedToWindow(holder);
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (lp instanceof StaggeredGridLayoutManager.LayoutParams) {
            ((StaggeredGridLayoutManager.LayoutParams) lp)
                    .setFullSpan(isHeaderBinding(getRealPosition(holder)) || isFooterBinding(getRealPosition(holder)));
        }
    }


    public class BaseHeaderFooterHolder<VB extends ViewDataBinding> extends RecyclerView.ViewHolder {
        private VB mBinding;

        public BaseHeaderFooterHolder(VB binding) {
            super(binding.getRoot());
            this.mBinding = binding;
        }

        public VB getBinding() {
            return mBinding;
        }
    }
}
