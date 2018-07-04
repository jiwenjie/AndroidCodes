package jingya.com.base_class_module.BaseAdapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * @author kuky
 * @description
 */
public class BaseFragmentPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> mList;
    private String[] mTitles;

    public BaseFragmentPagerAdapter(FragmentManager fm, List<Fragment> list, String[] titles) {
        super(fm);
        this.mList = list;
        this.mTitles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        return mList.get(position);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    /**
     * 绑定标题
     *
     * @param position
     * @return
     */
    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles == null ? super.getPageTitle(position) : mTitles[position];
    }

    public void setTitles(String[] titles) {
        this.mTitles = titles;
        notifyDataSetChanged();
    }
}
