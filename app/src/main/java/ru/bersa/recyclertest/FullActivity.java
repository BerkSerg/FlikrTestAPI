package ru.bersa.recyclertest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

public class FullActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full);
        Intent intent = getIntent();
        String imgUrl = intent.getStringExtra("URL");
        ImageView iv = findViewById(R.id.image_view_full);
        iv.setImageBitmap(GalleryReceiver.getBmp(imgUrl));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);

    }
}
