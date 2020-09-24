package org.xcion.srv.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Kern Hu
 * @E-mail:
 * @CreateDate: 2020/9/22 15:25
 * @UpdateUser: Kern Hu
 * @UpdateDate: 2020/9/22 15:25
 * @Version: 1.0
 * @Description:
 * @UpdateRemark:
 */
public class PagerAdapter extends FragmentPagerAdapter {

    List<Fragment> fragments;

    public PagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        fragments = new ArrayList<>();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return fragments.get(position).getArguments().getString("title");
    }

    public void setUpdate(List<Fragment> fragments) {
        this.fragments.addAll(fragments);
        notifyDataSetChanged();
    }
}
