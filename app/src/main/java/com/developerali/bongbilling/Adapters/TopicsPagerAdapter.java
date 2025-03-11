package com.developerali.bongbilling.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.developerali.bongbilling.Fragments.DateRangeFragment;
import com.developerali.bongbilling.Fragments.TodayFragment;
import com.developerali.bongbilling.Fragments.TomorrowFragment;


public class TopicsPagerAdapter extends FragmentStateAdapter {

    public TopicsPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new TodayFragment();
            case 1:
                return new TomorrowFragment();
            case 2:
                return new DateRangeFragment();
            default:
                return new TodayFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3; // Number of tabs
    }
}
