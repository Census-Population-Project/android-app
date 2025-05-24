package ru.iclouddev.censuspopulation;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import ru.iclouddev.censuspopulation.databinding.ActivityIrsContainerBinding;
import ru.iclouddev.censuspopulation.fragments.MapFragment;
import ru.iclouddev.censuspopulation.fragments.InfoFragment;
import ru.iclouddev.censuspopulation.fragments.StatisticsFragment;
import ru.iclouddev.censuspopulation.viewmodels.CensusViewModel;

public class ContainerIRSActivity extends AppCompatActivity {
    private ActivityIrsContainerBinding binding;
    private MapFragment mapFragment;
    private InfoFragment infoFragment;
    private StatisticsFragment statisticsFragment;
    private Fragment activeFragment;
    private CensusViewModel censusViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIrsContainerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        BottomNavigationView bottomNavigationView = binding.bottomNavigation;
        bottomNavigationView.setItemIconTintList(null);

        // Initialize ViewModel
        censusViewModel = new ViewModelProvider(this).get(CensusViewModel.class);

        mapFragment = new MapFragment();
        infoFragment = new InfoFragment();
        statisticsFragment = new StatisticsFragment();
        activeFragment = mapFragment;

        setupBottomNavigation();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentContainer, mapFragment, "map")
                    .add(R.id.fragmentContainer, infoFragment, "region")
                    .hide(infoFragment)
                    .add(R.id.fragmentContainer, statisticsFragment, "stats")
                    .hide(statisticsFragment)
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
                selectedFragment = infoFragment;
            } else if (itemId == R.id.navigation_statistics) {
                selectedFragment = statisticsFragment;
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

    public CensusViewModel getCensusViewModel() {
        return censusViewModel;
    }
} 