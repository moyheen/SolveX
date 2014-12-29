package com.moyheen.user.solvex.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moyheen.user.solvex.R;
import com.moyheen.user.solvex.common.view.ContentFragment;
import com.moyheen.user.solvex.common.view.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by moyheen on 28-Dec-14.
 */
public class LeaderboardFragment extends Fragment {

    // This class represents a tab to be displayed
    static class PagerItems {
        private final CharSequence mTitle;
        private final int mIndicatorColor;
        private final int mDividerColor;

        PagerItems(CharSequence title, int indicatorColor, int dividerColor) {
            mTitle = title;
            mIndicatorColor = indicatorColor;
            mDividerColor = dividerColor;
        }

        // Return a fragment to be displayed by viewpager
        Fragment createFragment() {
            return ContentFragment.newInstance(mTitle, mIndicatorColor, mDividerColor);
        }

        CharSequence getTitle() {
            return mTitle;
        }

        int getIndicatorColor() {
            return mIndicatorColor;
        }

        int getDividerColor() {
            return mDividerColor;
        }

    }

    private SlidingTabLayout mSlidingTabLayout;

    private List<PagerItems> mTabs = new ArrayList<PagerItems>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTabs.add(new PagerItems(
                getString(R.string.general), // Title
                Color.RED, // Indicator color
                Color.BLUE // Divider color
        ));

        mTabs.add(new PagerItems(
                getString(R.string.personal_best), // Title
                Color.GREEN, // Indicator color
                Color.BLACK // Divider color
        ));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_leaderboard, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        TabsPagerAdapter mTabsPagerAdapter = new TabsPagerAdapter(getChildFragmentManager());

        ViewPager mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mViewPager.setAdapter(mTabsPagerAdapter);

        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);

        mSlidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getDividerColor(int position) {
                return mTabs.get(position).getDividerColor();
            }

            @Override
            public int getIndicatorColor(int position) {
                return mTabs.get(position).getIndicatorColor();
            }
        });
    }

    class TabsPagerAdapter extends FragmentPagerAdapter {

        TabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    return new GeneralFragment();
                case 1:
                    return new PersonalBestFragment();
            }
            return null;
        }

        // Return the title of the pages
        @Override
        public CharSequence getPageTitle(int position) {

            return mTabs.get(position).getTitle();
        }

        // The number of pages to display
        @Override
        public int getCount() {
            return mTabs.size();
        }


        @Override
        public boolean isViewFromObject(View view, Object o) {
            return o == view;
        }

    }
}
