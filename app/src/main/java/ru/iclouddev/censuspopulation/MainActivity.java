package ru.iclouddev.censuspopulation;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import ru.iclouddev.censuspopulation.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        progressBar = findViewById(R.id.progressBar);

        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
            }, 1);
        }

        setupClickListeners();
        setupVersionText();
    }

    private void setupClickListeners() {
        binding.censusTakerButton.setOnClickListener(v -> {
            // TODO: Implement census login in the future
        });

        binding.publicInfoButton.setOnClickListener(v -> {
            showLoading();
            Intent intent = new Intent(this, ContainerIRSActivity.class);
            startActivity(intent);
        });
    }

    private void setupVersionText() {
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            binding.versionTextView.setText(getString(R.string.version, version));
        } catch (PackageManager.NameNotFoundException e) {
            binding.versionTextView.setText(getString(R.string.version, "unknown"));
        }
    }

    private void showLoading() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        if (progressBar != null) progressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideLoading();
    }
}