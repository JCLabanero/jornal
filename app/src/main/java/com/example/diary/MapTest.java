package com.example.diary;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.TextView;

//import com.mapbox.mapboxsdk.Mapbox;
//import com.mapbox.mapboxsdk.maps.*;

public class MapTest extends AppCompatActivity {
    int REQUEST_CODE = 5678;
    TextView selectedLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_test);

//        Mapbox.getInstance(this,getString(R.layout.activity_map_test));
    }
}