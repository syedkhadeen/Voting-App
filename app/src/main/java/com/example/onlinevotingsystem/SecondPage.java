package com.example.onlinevotingsystem;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;


public class SecondPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secondpage);

        // Voter button click listener
        findViewById(R.id.button3).setOnClickListener(view -> {
            // Navigate to the Voter activity
            startActivity(new Intent(SecondPage.this, Voter.class));
        });

        // Admin button click listener
        findViewById(R.id.button2).setOnClickListener(view -> {
            // Navigate to the Admin activity
            startActivity(new Intent(SecondPage.this, Admin.class));
        });
    }
}

