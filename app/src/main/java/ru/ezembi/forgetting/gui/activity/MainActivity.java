package ru.ezembi.forgetting.gui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import ru.ezembi.forgetting.R;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
}
