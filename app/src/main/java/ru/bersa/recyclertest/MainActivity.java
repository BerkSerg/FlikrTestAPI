package ru.bersa.recyclertest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.SearchView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, RecyclerAdapter.OnItemClickListener {
    RecyclerAdapter recyclerAdapter;
    RadioButton rb0;
    RadioButton rb1;
    int viewType = 0;
    static final String APP_SETTINGS="myFlikSettings";
    static final String APP_SETTINGS_VIEWTYPE="viewtype";
    private SharedPreferences mSettings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mSettings = getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE);
        if (mSettings.contains(APP_SETTINGS_VIEWTYPE)){
            viewType=mSettings.getInt(APP_SETTINGS_VIEWTYPE,0);
        }


        setContentView(R.layout.activity_main);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));


        recyclerAdapter = new RecyclerAdapter(this, recyclerView);

        if (!recyclerAdapter.fillContent()){
            Toast toast = Toast.makeText(getApplicationContext(), R.string.error_net,Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            return;
        }
        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.setOnItemClickListener(MainActivity.this);
        selectViewType(viewType);

        rb0 = findViewById(R.id.radioButton0);
        rb1 = findViewById(R.id.radioButton1);
        if (viewType==0)
            rb0.setChecked(true);
        else
            rb1.setChecked(true);

        rb0.setOnClickListener(this);
        rb1.setOnClickListener(this);

        recyclerAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!recyclerAdapter.addPhotoToArray(0)){
                            Toast toast = Toast.makeText(getApplicationContext(), R.string.error_net,Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.TOP,0,0);
                            toast.show();
                            return;
                        }
                        recyclerAdapter.notifyDataSetChanged();
                        recyclerAdapter.setLoaded();
                    }
                },100);

            }
        });
    }

    public void selectViewType(int type){
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        switch(type){
            case 0:
                recyclerAdapter.visibleThreshold=5;
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                break;
            case 1:
                int cpanCount;
                recyclerAdapter.visibleThreshold=12;
                if (getResources().getConfiguration().orientation==Configuration.ORIENTATION_PORTRAIT)
                    cpanCount=3;
                else
                    cpanCount=4;
                recyclerView.setLayoutManager(new GridLayoutManager(this,cpanCount));
                break;
        }
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.radioButton1:
                viewType=1;
                break;
            case R.id.radioButton0:
                viewType=0;
                break;
            default: break;
        }
        selectViewType(viewType);
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putInt(APP_SETTINGS_VIEWTYPE,viewType);
        editor.apply();

    }


    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (viewType==0) return;
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        if (newConfig.orientation==Configuration.ORIENTATION_PORTRAIT){
            recyclerView.setLayoutManager(new GridLayoutManager(this,3));
        } else if (newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE){
            recyclerView.setLayoutManager(new GridLayoutManager(this,4));
        }
    }

    @Override
    public void onItemClick(int pos) {
        Intent fullIntent = new Intent(this, FullActivity.class);
        ImgContainer ic = recyclerAdapter.getItem(pos);
        fullIntent.putExtra("URL",ic.getMainPhoto());
        startActivity(fullIntent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        MenuItem menuItem = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (recyclerAdapter!=null){
                    recyclerAdapter.getFilter().filter(s);
                }
                return true;
            }
        });

        return true;
    }
}
