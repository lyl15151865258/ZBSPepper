package com.zhongbenshuo.zbspepper.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

/**
 * Fragment切换Adapter
 * Created at 2020/7/6 0006 11:15
 *
 * @author : LiYuliang
 * @version : 2020/7/6 0006 11:15
 */

public class FragmentAdapter extends FragmentStateAdapter {

    private List<Fragment> fragments;

    public FragmentAdapter(@NonNull FragmentActivity fragmentActivity, List<Fragment> fragments) {
        super(fragmentActivity);
        this.fragments = fragments;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position);
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }

}