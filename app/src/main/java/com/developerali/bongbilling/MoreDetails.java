package com.developerali.bongbilling;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.developerali.bongbilling.Adapters.TopicsPagerAdapter;
import com.developerali.bongbilling.databinding.ActivityMoreDetailsBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MoreDetails extends AppCompatActivity {

    ActivityMoreDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMoreDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.viewPager.setAdapter(new TopicsPagerAdapter(this));

        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {
            View customView = LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
            TextView tabText = customView.findViewById(R.id.tabText);

            switch (position) {
                case 0:
                    tabText.setText("Today's Check-In");
                    break;
                case 1:
                    tabText.setText("Tomorrow's Check-In");
                    break;
                case 2:
                    tabText.setText("Select Date Range");
                    break;
            }

            tab.setCustomView(customView);
        }).attach();

        binding.tabLayout.getTabAt(0).select();
        updateTabAppearance(binding.tabLayout.getTabAt(0), true);

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                updateTabAppearance(tab, true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                updateTabAppearance(tab, false);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        binding.back.setOnClickListener(v->{
            onBackPressed();
        });







    }

    @Override
    public void onBackPressed() {
        int currentTab = binding.tabLayout.getSelectedTabPosition(); // Get the current tab position
        if (currentTab > 0) {
            TabLayout.Tab previousTab = binding.tabLayout.getTabAt(currentTab - 1);
            previousTab.select();
            updateTabAppearance(previousTab, true);
            TabLayout.Tab currentTabObj = binding.tabLayout.getTabAt(currentTab);
            updateTabAppearance(currentTabObj, false);
        } else {
            super.onBackPressed();
        }
    }

    private void updateTabAppearance(TabLayout.Tab tab, boolean isSelected) {
        View customView = tab.getCustomView();
        if (customView != null) {
            TextView tabText = customView.findViewById(R.id.tabText);
            if (isSelected) {
                tabText.setTextColor(getResources().getColor(R.color.red));
            } else {
                tabText.setTextColor(getResources().getColor(R.color.black));
            }
        }
    }
}