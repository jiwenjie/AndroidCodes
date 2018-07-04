package com.kuky.baselib.baseAdapter

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

/**
 * @author Kuky
 */
abstract class BaseListViewAdapter<T : Any, VB : ViewDataBinding>(context: Context, data: MutableList<T>?) : BaseAdapter() {
    protected var mContext: Context = context
    protected var mData: MutableList<T>? = data
    protected lateinit var mViewBinding: VB
    protected val mInflater: LayoutInflater
    protected var mSelectedPosition: Int = -1

    init {
        this.mInflater = LayoutInflater.from(mContext)
    }

    fun updateAdapterData(data: MutableList<T>) {
        this.mData = data
        notifyDataSetChanged()
    }

    fun getAdapterData(): MutableList<T>? {
        return mData
    }

    fun setSelectedItemPosition(position: Int) {
        this.mSelectedPosition = position
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return if (mData == null) 0 else mData!!.size
    }

    override fun getItem(position: Int): Any {
        return mData!![position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        mViewBinding =
                if (convertView == null) {
                    DataBindingUtil.inflate(mInflater, getItemLayoutId(), parent, false)
                } else {
                    DataBindingUtil.getBinding(convertView)!!
                }
        setVariable(mViewBinding, position)
        return mViewBinding.root
    }

    abstract fun setVariable(viewBinding: VB, position: Int)

    abstract fun getItemLayoutId(): Int
}