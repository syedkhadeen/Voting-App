package com.example.onlinevotingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class FirstScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_screen);

        Button startButton = findViewById(R.id.button);
        startButton.setOnClickListener(v -> {
            // Navigate to SecondPage when the button is clicked
            startActivity(new Intent(FirstScreen.this, SecondPage.class));
        });
    }
}
