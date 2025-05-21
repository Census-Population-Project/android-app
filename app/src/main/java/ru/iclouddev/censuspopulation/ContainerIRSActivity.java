package ru.iclouddev.censuspopulation;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import ru.iclouddev.censuspopulation.databinding.ActivityIrsContainerBinding;
import ru.iclouddev.censuspopulation.fragments.MapFragment;
import ru.iclouddev.censuspopulation.fragments.RegionInfoFragment;
import ru.iclouddev.censuspopulation.fragments.StatsFragment;

public class ContainerIRSActivity extends AppCompatActivity {
    private ActivityIrsContainerBinding binding;
    private MapFragment mapFragment;
    private RegionInfoFragment regionInfoFragment;
    private StatsFragment statsFragment;
    private Fragment activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIrsContainerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        BottomNavigationView bottomNavigationView = binding.bottomNavigation;
        bottomNavigationView.setItemIconTintList(null);

        mapFragment = new MapFragment();
        regionInfoFragment = new RegionInfoFragment();
        statsFragment = new StatsFragment();
        activeFragment = mapFragment;

        setupBottomNavigation();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentContainer, mapFragment, "map")
                    .add(R.id.fragmentContainer, regionInfoFragment, "region")
                    .hide(regionInfoFragment)
                    .add(R.id.fragmentContainer, statsFragment, "stats")
                    .hide(statsFragment)
                    .commit();
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = binding.bottomNavigation;
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_map) {
                selectedFragment = mapFragment;
            } else if (itemId == R.id.navigation_region_info) {
                selectedFragment = regionInfoFragment;
            } else if (itemId == R.id.navigation_statistics) {
                selectedFragment = statsFragment;
            }

            if (selectedFragment != null && selectedFragment != activeFragment) {
                getSupportFragmentManager().beginTransaction()
                        .hide(activeFragment)
                        .show(selectedFragment)
                        .commit();
                activeFragment = selectedFragment;
                return true;
            }
            return false;
        });
    }
} 