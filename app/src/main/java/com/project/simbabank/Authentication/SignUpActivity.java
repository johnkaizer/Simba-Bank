package com.project.simbabank.Authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.project.simbabank.MainActivity;
import com.project.simbabank.R;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
    }

    public void sign_In(View view) {
        startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
        finish();

    }

    public void home(View view) {
        startActivity(new Intent(SignUpActivity.this, MainActivity.class));
        finish();
    }
}