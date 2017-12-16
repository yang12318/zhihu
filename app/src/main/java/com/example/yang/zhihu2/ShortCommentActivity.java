package com.example.yang.zhihu2;
/*import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ShortCommentActivity extends AppCompatActivity {
    private List<ShortComment> ShortCommentList = new ArrayList<>();
    RecyclerView.LayoutManager recyclerViewlayoutManager;
    private String Url;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String EssayId_intent,love_EssayId_intent;
    private String LCN;//短评论数
    private String JsonLength="6";
    private String hot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shortcomments);

        Toolbar toolbar = findViewById(R.id.toolbar_shortcomment);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setTitle("");
        }
        Intent intent=getIntent();
        EssayId_intent=intent.getStringExtra("EssayId_intent");
        love_EssayId_intent=intent.getStringExtra("love_EssayId_intent");
        hot=intent.getStringExtra("hot");
        if(hot.equals("hot"))
            Url="https://news-at.zhihu.com/api/4/story/"+EssayId_intent+"/short-comments";
        else
            Url="https://news-at.zhihu.com/api/4/story/"+love_EssayId_intent+"/short-comments";

        RecyclerView recyclerView = findViewById(R.id.rev_shortcomment);
        ShortCommentAdapter adapter = new ShortCommentAdapter(ShortCommentList);
        recyclerView.setAdapter(adapter);

        recyclerViewlayoutManager = new LinearLayoutManager(this);
        GridLayoutManager layoutManager=new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(layoutManager);

        swipeRefreshLayout=findViewById(R.id.sre_shortcomment);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,R.color.colorAccent,R.color.colorButton);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                ( new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        ShortCommentList.clear();
                        sendRequestWithHttpURLConnection();
                    }
                }, 3000);
            }
        });

        if (ContextCompat.checkSelfPermission(ShortCommentActivity.this, Manifest.permission.INTERNET)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(ShortCommentActivity.this,new String[]{Manifest.permission.INTERNET},1);
        } else{
            sendRequestWithHttpURLConnection();
        }

    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        switch (requestCode){
            case 1:
                if (grantResults.length>0&&grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    sendRequestWithHttpURLConnection();
                }else {
                    Toast.makeText(ShortCommentActivity.this,"你拒绝了权限",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    private void sendRequestWithHttpURLConnection() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url= new URL(Url);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    parseJSONWithJSONObject(response.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    private void parseJSONWithJSONObject(String data) {
        String reply_content = null;
        String reply_status = null;
        String reply_id = null;
        String reply_author = null;
        String err_msg = null;
        try {
            JSONObject jsonObject=new JSONObject(data);
            JSONArray jsonArray=jsonObject.getJSONArray("comments");
            LCN=String.valueOf(jsonArray.length());

            if(!LCN.equals("0")){
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = (JSONObject)jsonArray.get(i);
                    JsonLength=String.valueOf(jsonObject1.length());
                    String author=jsonObject1.getString("author");
                    String content = jsonObject1.getString("content");
                    String id = jsonObject1.getString("id");
                    String avatar = jsonObject1.getString("avatar");
                    String time = jsonObject1.getString("time");
                    String likes = jsonObject1.getString("likes");
                    if(JsonLength.equals("7")){
                        JSONObject reply_to=jsonObject1.getJSONObject("reply_to");
                        for (int j=0;j<reply_to.length();j++){
                            reply_content=reply_to.getString("content");
                            reply_status=reply_to.getString("status");
                            reply_id=reply_to.getString("id");
                            reply_author=reply_to.getString("author");
                            if(!reply_status.equals("0"))
                                err_msg=reply_to.getString("err_msg");
                        }
                    }
                    ShortCommentList.add(new ShortComment(author,id,content,avatar,likes,time,reply_content,reply_status,reply_id,reply_author,err_msg,JsonLength));
                    reply_content = null;
                    reply_status = null;
                    reply_id = null;
                    reply_author = null;
                    err_msg = null;
                    JsonLength="6";
                }
            }
            showResponse();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void showResponse() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView number=findViewById(R.id.short_number);
                number.setText(LCN);
                RecyclerView recyclerView = findViewById(R.id.rev_shortcomment);
                ShortCommentAdapter adapter = new ShortCommentAdapter(ShortCommentList);
                recyclerView.setAdapter(adapter);
            }
        });
    }
}*/
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ShortCommentActivity extends AppCompatActivity {
    private List<ShortComment> ShortCommentList = new ArrayList<>();
    RecyclerView.LayoutManager recyclerViewlayoutManager;
    private String Url;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String SCN;//short commits number

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shortcomments);

        Toolbar toolbar = findViewById(R.id.toolbar_shortcomment);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setTitle("");
        }
        Intent intent=getIntent();
        String essayId_intent = intent.getStringExtra("EssayId_intent");
        Url="https://news-at.zhihu.com/api/4/story/"+ essayId_intent +"/short-comments";
        RecyclerView recyclerView = findViewById(R.id.rev_shortcomment);
        ShortCommentAdapter adapter = new ShortCommentAdapter(ShortCommentList);
        recyclerView.setAdapter(adapter);

        recyclerViewlayoutManager = new LinearLayoutManager(this);
        GridLayoutManager layoutManager=new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(layoutManager);

        swipeRefreshLayout=findViewById(R.id.sre_shortcomment);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,R.color.colorAccent,R.color.colorButton);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                ( new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        ShortCommentList.clear();
                        sendRequestWithHttpURLConnection();
                    }
                }, 3000);
            }
        });

        if (ContextCompat.checkSelfPermission(ShortCommentActivity.this, Manifest.permission.INTERNET)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(ShortCommentActivity.this,new String[]{Manifest.permission.INTERNET},1);
        } else{
            sendRequestWithHttpURLConnection();
        }

    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        switch (requestCode){
            case 1:
                if (grantResults.length>0&&grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    sendRequestWithHttpURLConnection();
                }else {
                    Toast.makeText(ShortCommentActivity.this,"你拒绝了权限",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    private void sendRequestWithHttpURLConnection() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url= new URL(Url);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    parseJSONWithJSONObject(response.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    private void parseJSONWithJSONObject(String data) {
        String reply_content = null;
        String reply_status = null;
        String reply_id = null;
        String reply_author = null;
        String err_msg = null;
        try {
            JSONObject jsonObject=new JSONObject(data);
            JSONArray jsonArray=jsonObject.getJSONArray("comments");
            SCN=String.valueOf(jsonArray.length());

            if(!SCN.equals("0")){
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = (JSONObject)jsonArray.get(i);
                    String jsonLength = String.valueOf(jsonObject1.length());
                    String author=jsonObject1.getString("author");
                    String content = jsonObject1.getString("content");
                    String id = jsonObject1.getString("id");
                    String avatar = jsonObject1.getString("avatar");
                    String time = jsonObject1.getString("time");
                    String likes = jsonObject1.getString("likes");
                    if(jsonLength.equals("7")){
                        JSONObject reply_to=jsonObject1.getJSONObject("reply_to");
                        for (int j=0;j<reply_to.length();j++){
                            reply_content=reply_to.getString("content");
                            reply_status=reply_to.getString("status");
                            reply_id=reply_to.getString("id");
                            reply_author=reply_to.getString("author");
                            if(!reply_status.equals("0"))
                                err_msg=reply_to.getString("err_msg");
                        }
                    }
                    ShortCommentList.add(new ShortComment(author,id,content,avatar,likes,time,reply_content,reply_status,reply_id,reply_author,err_msg, jsonLength));
                    reply_content = null;
                    reply_status = null;
                    reply_id = null;
                    reply_author = null;
                    err_msg = null;
                }
            }
            showResponse();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void showResponse() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView number=findViewById(R.id.short_number);
                number.setText(SCN);
                RecyclerView recyclerView = findViewById(R.id.rev_shortcomment);
                ShortCommentAdapter adapter = new ShortCommentAdapter(ShortCommentList);
                recyclerView.setAdapter(adapter);
            }
        });
    }
}
