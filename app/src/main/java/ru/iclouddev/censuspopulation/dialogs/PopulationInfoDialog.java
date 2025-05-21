package ru.iclouddev.censuspopulation.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import ru.iclouddev.censuspopulation.R;
import ru.iclouddev.censuspopulation.data.model.PopulationInfo;

public class PopulationInfoDialog extends Dialog {
    private static final String TAG = "PopulationInfoDialog";
    private final String title;
    private final PopulationInfo info;

    public PopulationInfoDialog(@NonNull Context context, String title, PopulationInfo info) {
        super(context);
        this.title = title;
        this.info = info;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_population_info);

        // Настройка размера и прозрачности диалога
        Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(params);
        }

        TextView titleView = findViewById(R.id.titleTextView);
        TextView populationView = findViewById(R.id.populationTextView);
        TextView maleCountView = findViewById(R.id.maleCountTextView);
        TextView femaleCountView = findViewById(R.id.femaleCountTextView);
        ImageButton closeButton = findViewById(R.id.closeButton);

        titleView.setText(title);
        populationView.setText(String.format("Общая численность: %d чел.", info.getPopulation()));
        maleCountView.setText(String.format("Мужчин: %d чел.", info.getMaleCount()));
        femaleCountView.setText(String.format("Женщин: %d чел.", info.getFemaleCount()));

        closeButton.setOnClickListener(v -> dismiss());
    }
} 