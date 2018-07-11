package com.kuky.baselib.baseAdapter

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * @author kuky
 * @description 使用参考 java 版本
 */
abstract class BaseRvHeaderFooterAdapter<T : Any, VB : ViewDataBinding>(context: Context, data: MutableList<T>? = null) :
        RecyclerView.Adapter<BaseRvHeaderFooterAdapter.BaseHolder>() {
    private val HEADER = 0x00100000
    private val FOOTER = 0x00200000
    private val mHeaderBindings: SparseArray<ViewDataBinding> = SparseArray()
    private val mFooterBindings: SparseArray<ViewDataBinding> = SparseArray()
    protected lateinit var mViewBinding: VB
    protected var mContext: Context = context
    protected var mData: MutableList<T>? = data
    protected val mInflater: LayoutInflater
    protected var mSelectedPosition = -1
    private var mCurrentPosition = -1
    private var mOnItemClickListener: OnItemClickListener? = null
    private var mOnItemLongClickListener: OnItemLongClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(position: Int, view: View)
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(position: Int, view: View)
    }

    init {
        this.mInflater = LayoutInflater.from(mContext)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.mOnItemClickListener = listener
    }

    fun setOnItemLongClickListener(listener: OnItemLongClickListener) {
        this.mOnItemLongClickListener = listener
    }

    fun setSelectedItemPosition(position: Int) {
        this.mSelectedPosition = position
        notifyDataSetChanged()
    }

    fun getSelectedItemPosition(): Int {
        return mSelectedPosition
    }

    fun addHeaderBinding(headerBinding: ViewDataBinding) {
        mHeaderBindings.put(HEADER + getHeaderCount(), headerBinding)
        notifyItemInserted(getHeaderCount())
    }

    fun addFooterBinding(footerBinding: ViewDataBinding) {
        mFooterBindings.put(FOOTER + getFooterCount(), footerBinding)
        var insertPos: Int = if (mData == null) 0
        else mData!!.size - 1
        if (haveHeader()) insertPos += getHeaderCount()
        notifyItemInserted(insertPos)
    }

    fun getHeaderBindings(): MutableList<ViewDataBinding> {
        val headers = (HEADER until getHeaderCount() + HEADER).map { mHeaderBindings.get(it) }
        return headers as MutableList
    }

    fun getFooterBindings(): MutableList<ViewDataBinding> {
        val footers = (FOOTER until getFooterCount() + FOOTER).map { mFooterBindings.get(it) }
        return footers as MutableList
    }

    fun updateAdapterData(data: MutableList<T>?) {
        this.mData = data
        notifyDataSetChanged()
    }

    fun getAdapterData(): MutableList<T>? {
        return if (mData != null) mData!!
        else null
    }

    fun addData(data: T) {
        if (mData != null) {
            this.mData!!.add(data)
            notifyDataSetChanged()
        } else {
            throw IllegalStateException("mData is null and init first")
        }
    }

    fun addData(position: Int, data: T) {
        if (mData != null) {
            this.mData!!.add(position, data)
            notifyDataSetChanged()
        } else {
            throw IllegalStateException("mData is null and init first")
        }
    }

    fun addAllData(data: MutableList<T>) {
        if (mData != null) {
            this.mData!!.addAll(data)
            notifyDataSetChanged()
        } else {
            throw IllegalStateException("mData is null and init first")
        }
    }

    fun removeData(position: Int) {
        if (mData != null) {
            this.mData!!.removeAt(position)
            notifyDataSetChanged()
        } else {
            throw IllegalStateException("mData is null and init first")
        }
    }

    fun removeData(data: T) {
        if (mData != null && data in mData!!) {
            mData!!.remove(data)
            notifyDataSetChanged()
        } else {
            throw IllegalStateException("data not in mData and check it")
        }
    }

    fun clearData() {
        if (mData != null) {
            mData!!.clear()
            notifyDataSetChanged()
        } else {
            throw IllegalStateException("mData is null and init first")
        }
    }

    fun getCurrentPosition(): Int {
        return mCurrentPosition
    }

    protected fun getCurrentDataPisition(data: T): Int {
        if (mData != null && data in mData!!) {
            mData!!.indices
                    .filter { it == data }
                    .forEach { return it }
        }
        return -1
    }

    private fun getHeaderCount(): Int {
        return mHeaderBindings.size()
    }

    private fun getDataCount(): Int {
        return mData?.size ?: 0
    }

    private fun getFooterCount(): Int {
        return mFooterBindings.size()
    }

    private fun haveHeader(): Boolean {
        return mHeaderBindings.size() > 0
    }

    private fun haveFooter(): Boolean {
        return mFooterBindings.size() > 0
    }

    private fun isHeader(position: Int): Boolean {
        return haveHeader() && position < getHeaderCount()
    }

    private fun isFooter(position: Int): Boolean {
        return haveFooter() && position >= getHeaderCount() + getDataCount()
    }

    private fun getRealPosition(viewHolder: RecyclerView.ViewHolder): Int {
        return viewHolder.layoutPosition
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder {
        mViewBinding = DataBindingUtil.inflate(mInflater, getAdapterViewId(viewType), parent, false)
        return if (haveHeader() && mHeaderBindings.get(viewType) != null) BaseHolder(mHeaderBindings.get(viewType))
        else if (haveFooter() && mFooterBindings.get(viewType) != null) BaseHolder(mFooterBindings.get(viewType))
        else BaseHolder(mViewBinding)
    }

    abstract fun getAdapterViewId(viewType: Int): Int

    override fun onBindViewHolder(holder: BaseHolder, position: Int) {
        if (!isHeader(position) && !isFooter(position)) {
            val pos = position - getHeaderCount()
            this.mCurrentPosition = pos
            setVariable(holder.getBinding(), mData!![pos])
            holder.getBinding().executePendingBindings()

            if (mOnItemClickListener != null) {
                holder.itemView.setOnClickListener { view -> mOnItemClickListener!!.onItemClick(pos, view) }
            }

            if (mOnItemLongClickListener != null) {
                holder.itemView.setOnLongClickListener { view ->
                    mOnItemLongClickListener!!.onItemLongClick(pos, view)
                    false
                }
            }
        }
    }

    abstract fun setVariable(viewBinding: ViewDataBinding, t: T)

    override fun getItemViewType(position: Int): Int {
        return when {
            isHeader(position) -> mHeaderBindings.keyAt(position)
            isFooter(position) -> mFooterBindings.keyAt(position - getHeaderCount() - getDataCount())
            else -> getItemType(position - getHeaderCount())
        }
    }

    open fun getItemType(position: Int): Int = 0

    override fun getItemCount(): Int {
        return getHeaderCount() + getDataCount() + getFooterCount()
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        val lm = recyclerView.layoutManager
        if (lm is GridLayoutManager) {
            lm.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (isHeader(position) || isFooter(position)) lm.spanCount
                    else 1
                }
            }
        }
    }

    override fun onViewAttachedToWindow(holder: BaseHolder) {
        super.onViewAttachedToWindow(holder)
        val lp = holder.itemView.layoutParams
        if (lp is StaggeredGridLayoutManager.LayoutParams)
            lp.isFullSpan = isHeader(getRealPosition(holder)) || isFooter(getRealPosition(holder))
    }

    class BaseHolder(viewBinding: ViewDataBinding) : RecyclerView.ViewHolder(viewBinding.root) {
        private val mViewBinding = viewBinding

        fun getBinding(): ViewDataBinding {
            return mViewBinding
        }
    }
}