package com.kuky.base_kt_module.BaseAdapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

/**
 * @author Kuky
 */
class BaseFragmentPagerAdapter(fm: FragmentManager, fragments: MutableList<Fragment>, titles: Array<String>)
    : FragmentPagerAdapter(fm) {

    private var mFragments: MutableList<Fragment>? = null
    private var mTitles: Array<String>? = null

    init {
        this.mFragments = fragments
        this.mTitles = titles
    }

    override fun getItem(position: Int): Fragment {
        return mFragments!![position]
    }

    override fun getCount(): Int {
        return mTitles!!.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return if (mTitles!!.isEmpty()) super.getPageTitle(position)
        else mTitles!![position]
    }
}