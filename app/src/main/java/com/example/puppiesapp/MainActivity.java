package com.example.puppiesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
    public void proximaTela(View view){

        Intent intent = new Intent(this, Cadastro.class);
        startActivity(intent);
    }
    public void proximaTela2(View view){

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }


}