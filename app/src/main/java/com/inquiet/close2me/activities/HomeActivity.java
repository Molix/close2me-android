package com.inquiet.close2me.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.inquiet.close2me.R;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        findViewById(R.id.button_map).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
/*
                checkLocationPermissionAndContinue(new OnPermissionRequested() {
                    @Override
                    public void onPermissionGranted() {
                        startActivity(new Intent(HomeActivity.this, MapsActivity.class));
                    }

                    @Override
                    public void onPermissionDenied() {
                    }
                });
*/
                startActivity(new Intent(HomeActivity.this, MapActivity.class));
            }
        });
    }
}
