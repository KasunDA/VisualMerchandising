package com.tophawks.vm.visualmerchandising.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.tophawks.vm.visualmerchandising.R;

import pl.droidsonroids.gif.GifImageView;

public class LauncherActivity extends AppCompatActivity {

    GifImageView VMIV;
    GifImageView SMIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        VMIV = (GifImageView) findViewById(R.id.launcher_vm_iv);
        VMIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LauncherActivity.this, VisualMerchandisingHomePage.class));
            }
        });
        SMIV = (GifImageView) findViewById(R.id.launcher_sm_iv);
        SMIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LauncherActivity.this, StockManagementHomePage.class));
            }
        });

    }
}
