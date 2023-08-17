package com.texnar13.rollbotserverhost;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.texnar13.rollbotserverhost.ui.main.MainFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {

            // выставляем главный фрагмент видимым
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_activity_fragments_container, MainFragment.newInstance())
                    .commitNow();
        }
    }
}