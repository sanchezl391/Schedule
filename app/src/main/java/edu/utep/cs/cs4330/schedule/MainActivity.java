package edu.utep.cs.cs4330.schedule;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent i = new Intent(MainActivity.this, noteList.class);
        startActivity(i);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
