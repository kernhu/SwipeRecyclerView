package org.xcion.srv;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;


import org.xcion.srv.adapter.PagerAdapter;
import org.xcion.srv.fragment.GridFragment;
import org.xcion.srv.fragment.LinearFragment;
import org.xcion.srv.fragment.StaggeredGridFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.sliding_tab_layout)
    TabLayout mSlidingTabLayout;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    Unbinder mUnbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUnbinder = ButterKnife.bind(this);

        bindData();
    }

    private void bindData() {

        List<String> mTitles = new ArrayList<>();
        mTitles.add(getString(R.string.string_linear));
        mTitles.add(getString(R.string.string_grid));
        mTitles.add(getString(R.string.string_staggered_grid));
        mTitles.add(getString(R.string.string_empty));
        mTitles.add(getString(R.string.string_error));

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(LinearFragment.getInstance(0, mTitles.get(0)));
        fragments.add(GridFragment.getInstance(1, mTitles.get(1)));
        fragments.add(StaggeredGridFragment.getInstance(2, mTitles.get(2)));
        fragments.add(LinearFragment.getInstance(3, mTitles.get(3)));
        fragments.add(LinearFragment.getInstance(4, mTitles.get(4)));

        PagerAdapter mPagerAdapter = new PagerAdapter(getSupportFragmentManager(), 0);
        mViewPager.setAdapter(mPagerAdapter);
        mPagerAdapter.setUpdate(fragments);
        mSlidingTabLayout.setupWithViewPager(mViewPager);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
    }
}