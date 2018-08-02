package com.hajjhackathon.fifthpillar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.hajjhackathon.fifthpillar.broadcast.ui.MainActivity;

public class LandingPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        findViewById(R.id.btnBroadcast).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LandingPageActivity.this, MainActivity.class));
            }
        });

        findViewById(R.id.btnReceiver).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LandingPageActivity.this, com.hajjhackathon.fifthpillar.receiver.MainActivity.class));
            }
        });
    }

}
