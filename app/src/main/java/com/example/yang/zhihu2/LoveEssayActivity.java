package com.example.yang.zhihu2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;


import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LoveEssayActivity extends AppCompatActivity {
    private List<LoveEssay> love_EssayList = new ArrayList<>();
    RecyclerView.LayoutManager recyclerViewlayoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String username_intent;
    private MyDatabaseHelper dbHelper;
    private Timer mTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.love_essay);
        // init timer
        mTimer = new Timer();
        // start timer task
        setTimerTask();
        Button love_essay_back=findViewById(R.id.love_essay_back);
        Intent intent=getIntent();
        username_intent=intent.getStringExtra("username_intent");

        dbHelper =new MyDatabaseHelper(this,"data.db",null,1) ;
        dbHelper.getWritableDatabase();
        SQLiteDatabase sdb = dbHelper.getReadableDatabase();
        Cursor cursor=sdb.query("loveessay_data",null,null,null,null,null,null);
        if (cursor.moveToFirst()) {
            do {
                String username=cursor.getString(cursor.getColumnIndex("username"));
                String essay_id=cursor.getString(cursor.getColumnIndex("essay_id"));
                String title=cursor.getString(cursor.getColumnIndex("title"));
                String thumbnail=cursor.getString(cursor.getColumnIndex("thumbnail"));
                String url=cursor.getString(cursor.getColumnIndex("url"));
                //if (username.equals(username_intent)){
                    love_EssayList.add(new LoveEssay(username_intent,title,essay_id,thumbnail,url));
                //}
            }while (cursor.moveToNext());
        }
        cursor.close();
        sdb.close();


        Toolbar toolbar = findViewById(R.id.toolbar_love_essay);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setTitle("");
        }

        RecyclerView recyclerView = findViewById(R.id.rev_love_essay);
        LoveEssayAdapter adapter = new LoveEssayAdapter(love_EssayList);
        recyclerView.setAdapter(adapter);

        recyclerViewlayoutManager = new LinearLayoutManager(this);
        GridLayoutManager layoutManager=new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(layoutManager);

        swipeRefreshLayout=findViewById(R.id.sre_love_essay);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,R.color.colorAccent,R.color.colorButton);
        swipeRefreshLayout.setProgressViewEndTarget (false,300);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                ( new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        love_EssayList.clear();
                        SQLiteDatabase sdb = dbHelper.getReadableDatabase();
                        Cursor cursor=sdb.query("loveessay_data",null,null,null,null,null,null);
                        if (cursor.moveToFirst()) {
                            do {
                                String username=cursor.getString(cursor.getColumnIndex("username"));
                                String essay_id=cursor.getString(cursor.getColumnIndex("essay_id"));
                                String title=cursor.getString(cursor.getColumnIndex("title"));
                                String thumbnail=cursor.getString(cursor.getColumnIndex("thumbnail"));
                                String url=cursor.getString(cursor.getColumnIndex("url"));
                                if (username.equals(username_intent)){
                                    love_EssayList.add(new LoveEssay(username_intent,title,essay_id,thumbnail,url));
                                }
                            }while (cursor.moveToNext());
                        }
                        cursor.close();
                        sdb.close();
                        showResponse();
                    }
                }, 1000);
            }
        });
        love_essay_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoveEssayActivity.this,UserActivity.class);
                intent.putExtra("username_intent",username_intent);
                startActivity(intent);
                finish();
            }
        });
        if (ContextCompat.checkSelfPermission(LoveEssayActivity.this, Manifest.permission.INTERNET)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(LoveEssayActivity.this,new String[]{Manifest.permission.INTERNET},1);
        } else{
            showResponse();
        }

    }

    private void showResponse() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RecyclerView recyclerView = findViewById(R.id.rev_love_essay);
                LoveEssayAdapter adapter = new LoveEssayAdapter(love_EssayList);
                recyclerView.setAdapter(adapter);
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // cancel timer
        mTimer.cancel();
    }

    private void setTimerTask() {
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 1;
                doActionHandler.sendMessage(message);
            }
        }, 1000, 1000/* 表示1000毫秒之後，每隔1000毫秒執行一次 */);
    }

    /**
     * do some action
     */
    @SuppressLint("HandlerLeak")
    private Handler doActionHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int msgId = msg.what;
            switch (msgId) {
                case 1:
                    // do some action
                    love_EssayList.clear();
                    SQLiteDatabase sdb = dbHelper.getReadableDatabase();
                    Cursor cursor=sdb.query("loveessay_data",null,null,null,null,null,null);
                    if (cursor.moveToFirst()) {
                        do {
                            String username=cursor.getString(cursor.getColumnIndex("username"));
                            String essay_id=cursor.getString(cursor.getColumnIndex("essay_id"));
                            String title=cursor.getString(cursor.getColumnIndex("title"));
                            String thumbnail=cursor.getString(cursor.getColumnIndex("thumbnail"));
                            String url=cursor.getString(cursor.getColumnIndex("url"));
                           // if (username.equals(username_intent)){
                                love_EssayList.add(new LoveEssay(username_intent,title,essay_id,thumbnail,url));
                           // }
                        }while (cursor.moveToNext());
                    }
                    cursor.close();
                    sdb.close();
                    showResponse();
                    break;
                default:
                    break;
            }
        }
    };

}
