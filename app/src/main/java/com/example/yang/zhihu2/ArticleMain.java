package com.example.yang.zhihu2;

/*import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
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

public class ArticleMain extends AppCompatActivity {
    private List<News> newsList = new ArrayList<>();
    RecyclerView.LayoutManager recyclerViewlayoutManager;
    private String articleId_intent;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String username_intent;
    private Toolbar toolbar;
    private FloatingActionButton top;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_main);
        Intent intent=getIntent();
        articleId_intent=intent.getStringExtra("articleId_intent");
        final RecyclerView recyclerView = findViewById(R.id.recycler_news);
        Button news_back=findViewById(R.id.news_back);
        top=findViewById(R.id.top_news);
        NewsAdapter adapter = new NewsAdapter(newsList);
        recyclerView.setAdapter(adapter);

        recyclerViewlayoutManager = new LinearLayoutManager(this);
        GridLayoutManager layoutManager=new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(layoutManager);

        swipeRefreshLayout=findViewById(R.id.swipe_news);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,R.color.colorAccent,R.color.colorButton);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                ( new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        newsList.clear();
                        sendRequestWithHttpURLConnection();
                    }
                }, 3000);
            }
        });
        if (ContextCompat.checkSelfPermission(ArticleMain.this, Manifest.permission.INTERNET)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(ArticleMain.this,new String[]{Manifest.permission.INTERNET},1);
        } else{
            sendRequestWithHttpURLConnection();
        }
        top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.smoothScrollToPosition(0);
            }
        });
        news_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ArticleMain.this,Main.class);
                intent.putExtra("username_intent",username_intent);
                startActivity(intent);
                finish();
            }
        });
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        switch (requestCode){
            case 1:
                if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    sendRequestWithHttpURLConnection();
                }else {
                    Toast.makeText(ArticleMain.this,"你拒绝了权限",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    private void sendRequestWithHttpURLConnection() {
        // 开启线程来发起网络请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL("http://news-at.zhihu.com/api/3/section/"+articleId_intent);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();
                    // 下面对获取到的输入流进行读取
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
        String images = null;
        try {
            JSONObject jsonObject=new JSONObject(data);
            String timestamp=jsonObject.getString("timestamp");
            JSONArray jsonArray=jsonObject.getJSONArray("stories");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = (JSONObject)jsonArray.get(i);
                JSONArray jsonArray1=jsonObject1.getJSONArray("images");
                for (int j=0;j<jsonArray1.length();j++) {
                    images=jsonArray1.getString(j);
                }
                String date=jsonObject1.getString("date");
                String display_date = jsonObject1.getString("display_date");
                String id = jsonObject1.getString("id");
                String title = jsonObject1.getString("title");

                newsList.add(new News(title,id,display_date,images));

            }
            String name=jsonObject.getString("name");
            showResponse();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void showResponse() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 在这里进行UI操作，将结果显示到界面上
                RecyclerView recyclerView = findViewById(R.id.recycler_news);
                NewsAdapter adapter = new NewsAdapter(newsList);
                recyclerView.setAdapter(adapter);
            }
        });
    }
    public abstract class HidingScrollListener extends RecyclerView.OnScrollListener {
        private static final int HIDE_THRESHOLD = 20;
        private int scrolledDistance = 0;
        private boolean controlsVisible = true;
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (scrolledDistance > HIDE_THRESHOLD && controlsVisible) {
                onHide();
                controlsVisible = false;
                scrolledDistance = 0;
            } else if (scrolledDistance < -HIDE_THRESHOLD && !controlsVisible) {
                onShow();
                controlsVisible = true;
                scrolledDistance = 0;
            }
            if((controlsVisible && dy>0) || (!controlsVisible && dy<0)) {
                scrolledDistance += dy;
            }
        }
        public abstract void onHide();
        public abstract void onShow();

    }
    private void hideViews() {
        toolbar.animate().translationY(-toolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2));
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) top.getLayoutParams();
        int fabBottomMargin = lp.bottomMargin;
        top.animate().translationY(top.getHeight()+fabBottomMargin).setInterpolator(new AccelerateInterpolator(2)).start();
    }

    private void showViews() {
        toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
        top.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
    }
}*/
/*import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

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
import java.util.Objects;

public class ArticleMain extends AppCompatActivity {
    private List<News> newsList = new ArrayList<>();
    RecyclerView.LayoutManager recyclerViewlayoutManager;
    private String articleId_intent;
    private String like_articleId_intent=null;
    private String main;
    private String username_intent;
    private String articleName_intent;
    private String articleDescription_intent;
    private String articleThumbnail_intent;
    private MyDatabaseHelper dbHelper;
    private String Url;

    private boolean collect_article=false;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_main);

        dbHelper =new MyDatabaseHelper(this,"data.db",null,1) ;
        dbHelper.getWritableDatabase();
        FloatingActionButton love_fab=findViewById(R.id.love_fab);
        Toolbar toolbar = findViewById(R.id.toolbar_news);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setTitle("栏目详情");
        }

        Intent intent=getIntent();
        articleId_intent=intent.getStringExtra("articleId_intent");
        like_articleId_intent=intent.getStringExtra("like_articleId_intent");
        username_intent=intent.getStringExtra("username_intent");
        articleName_intent=intent.getStringExtra("articleName_intent");
        articleDescription_intent=intent.getStringExtra("articleDescription_intent");
        articleThumbnail_intent=intent.getStringExtra("articleThumbnail_intent");
        main=intent.getStringExtra("main");
        if(main.equals("main"))
            Url= "http://news-at.zhihu.com/api/3/section/"+articleId_intent;
        else
            Url = "http://news-at.zhihu.com/api/3/section/"+like_articleId_intent;
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        NewsAdapter adapter = new NewsAdapter(newsList);
        recyclerView.setAdapter(adapter);

        recyclerViewlayoutManager = new LinearLayoutManager(this);
        GridLayoutManager layoutManager=new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(layoutManager);



        swipeRefreshLayout=findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,R.color.colorAccent,R.color.colorButton);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                ( new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        newsList.clear();
                        sendRequestWithHttpURLConnection();
                    }
                }, 3000);
            }
        });
        if (ContextCompat.checkSelfPermission(ArticleMain.this, Manifest.permission.INTERNET)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(ArticleMain.this,new String[]{Manifest.permission.INTERNET},1);
        } else{
            sendRequestWithHttpURLConnection();
        }
        if (ContextCompat.checkSelfPermission(ArticleMain.this, Manifest.permission.INTERNET)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(ArticleMain.this,new String[]{Manifest.permission.INTERNET},1);
        } else{
            sendRequestWithHttpURLConnection();
        }
        love_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ArticleMain.this,"已成功收藏",Toast.LENGTH_SHORT).show();
            }
        });
    }
        /*SQLiteDatabase sdb = dbHelper.getReadableDatabase();
        Cursor cursor=sdb.query("like_column_table",null,null,null,null,null,null);
        if (cursor.moveToFirst()) {
            do {
                String username=cursor.getString(cursor.getColumnIndex("username"));
                String column=cursor.getString(cursor.getColumnIndex("column_id"));
                if(main.equals("main")){
                    if (username.equals(username_intent)&&column.equals(columnId_intent)){
                        collect_column=true;
                        break;
                    }
                }
                else {
                    if (username.equals(username_intent)&&column.equals(like_columnId_intent)){
                        collect_column=true;
                        break;
                    }
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        sdb.close();
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        switch (requestCode){
            case 1:
                if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    sendRequestWithHttpURLConnection();
                }else {
                    Toast.makeText(ArticleMain.this,"你拒绝了权限",Toast.LENGTH_SHORT).show();
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
        String images = null;
        try {
            JSONObject jsonObject=new JSONObject(data);
            String timestamp=jsonObject.getString("timestamp");
            JSONArray jsonArray=jsonObject.getJSONArray("stories");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = (JSONObject)jsonArray.get(i);
                JSONArray jsonArray1=jsonObject1.getJSONArray("images");
                for (int j=0;j<jsonArray1.length();j++) {
                    images=jsonArray1.getString(j);
                }
                String date=jsonObject1.getString("date");
                String display_date = jsonObject1.getString("display_date");
                String id = jsonObject1.getString("id");
                String title = jsonObject1.getString("title");

                newsList.add(new News(title,id,display_date,images));

            }
            String name=jsonObject.getString("name");
            showResponse();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void showResponse() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RecyclerView recyclerView = findViewById(R.id.recycler_view);
                NewsAdapter adapter = new NewsAdapter(newsList);
                recyclerView.setAdapter(adapter);
            }
        });
    }

    /*public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu,menu) ;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String id;
        if(main.equals("main"))
            id=columnId_intent;
        else
            id =like_columnId_intent;
        switch (item.getItemId()) {
            case R.id.Collect_Column:
                if (!collect_column){
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("column_id",id);
                    values.put("username",username_intent);
                    values.put("name",columnName_intent);
                    values.put("description",columnDescription_intent);
                    values.put("thumbnail",columnThumbnail_intent);
                    db.insert("like_column_table", null, values);
                    values.clear();
                    db.close();
                    collect_column=true;
                    Toast.makeText(MessageActivity.this,"已收藏",Toast.LENGTH_SHORT).show();
                }else Toast.makeText(MessageActivity.this,"收藏已存在",Toast.LENGTH_SHORT).show();
                break;
            case R.id.QuitCollect_Column:
                if (collect_column){
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    db.delete("like_column_table","column_id=?",new String[]{id});
                    db.close();
                    collect_column=false;
                    Toast.makeText(ArticleMain.this,"已取消收藏",Toast.LENGTH_SHORT).show();
                }else Toast.makeText(ArticleMain.this,"收藏不存在",Toast.LENGTH_SHORT).show();
            default:
        }
        return true;
    }
    public void onBackPressed() {
        Intent intent1 =new Intent(ArticleMain.this,Main.class);
        intent1.putExtra("username_intent",username_intent);
        /*Intent intent2=new Intent(MessageActivity.this,LikeColumnActivity.class);
        intent2.putExtra("username_intent",username_intent);
        if(main.equals("main"))
            startActivity(intent1);
        //else startActivity(intent2);
        finish();
    }

}*/
import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

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
import java.util.Objects;

public class ArticleMain extends AppCompatActivity {
    private List<News> newsList = new ArrayList<>();
    RecyclerView.LayoutManager recyclerViewlayoutManager;
    private String articleId_intent;
    private String love_newsId_intent=null;
    private String main;
    private String username_intent;
    private String articleName_intent;
    private String articleDescription_intent;
    private String articleThumbnail_intent;
    private MyDatabaseHelper dbHelper;
    private String Url;

    private boolean collect_news=false;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_main);
        final Button news_back=findViewById(R.id.news_back);
        FloatingActionButton top=findViewById(R.id.top_news);
        dbHelper =new MyDatabaseHelper(this,"data.db",null,1) ;
        dbHelper.getWritableDatabase();

        Toolbar toolbar = findViewById(R.id.toolbar_news);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setTitle("");
        }

        Intent intent=getIntent();
        articleId_intent=intent.getStringExtra("articleId_intent");
        love_newsId_intent=intent.getStringExtra("love_newsId_intent");
        username_intent=intent.getStringExtra("username_intent");
        articleName_intent=intent.getStringExtra("articleName_intent");
        articleDescription_intent=intent.getStringExtra("articleDescription_intent");
        articleThumbnail_intent=intent.getStringExtra("articleThumbnail_intent");
        main=intent.getStringExtra("main");
        if(main.equals("main"))
            Url= "http://news-at.zhihu.com/api/3/section/"+articleId_intent;
        else
            Url = "http://news-at.zhihu.com/api/3/section/"+love_newsId_intent;
        final RecyclerView recyclerView = findViewById(R.id.recycler_news);
        NewsAdapter adapter = new NewsAdapter(newsList);
        recyclerView.setAdapter(adapter);

        recyclerViewlayoutManager = new LinearLayoutManager(this);
        GridLayoutManager layoutManager=new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(layoutManager);



        swipeRefreshLayout=findViewById(R.id.swipe_news);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,R.color.colorAccent,R.color.colorButton);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                ( new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        newsList.clear();
                        sendRequestWithHttpURLConnection();
                    }
                }, 3000);
            }
        });
        if (ContextCompat.checkSelfPermission(ArticleMain.this, Manifest.permission.INTERNET)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(ArticleMain.this,new String[]{Manifest.permission.INTERNET},1);
        } else{
            sendRequestWithHttpURLConnection();
        }
        SQLiteDatabase sdb = dbHelper.getReadableDatabase();
        Cursor cursor=sdb.query("lovenews_data",null,null,null,null,null,null);
        if (cursor.moveToFirst()) {
            do {
                String username=cursor.getString(cursor.getColumnIndex("username"));
                String news=cursor.getString(cursor.getColumnIndex("news_id"));
                if(main.equals("main")){
                    if (username.equals(username_intent)&&news.equals(articleId_intent)){
                        collect_news=true;
                        break;
                    }
                }
                else {
                    if (username.equals(username_intent)&&news.equals(love_newsId_intent)){
                        collect_news=true;
                        break;
                    }
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        sdb.close();
        news_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ArticleMain.this,Main.class);
                intent.putExtra("username_intent",username_intent);
                startActivity(intent);
                finish();
            }
        });
        top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.smoothScrollToPosition(0);
            }
        });
        /*ArticleMain.ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ArticleMain.ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                final News news =newsList.get(position);
                Intent intent=new Intent(ArticleMain.this,ArticleActivity.class);
                intent.putExtra("NewsId_intent",news.getId());
                intent.putExtra("hot","message");
                intent.putExtra("articleId_intent",articleId_intent);
                intent.putExtra("username_intent",username_intent);
                intent.putExtra("title",news.getTitle());
                intent.putExtra("thumbnail",news.getImages());
                startActivity(intent);
            }
        });*/

    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        switch (requestCode){
            case 1:
                if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    sendRequestWithHttpURLConnection();
                }else {
                    Toast.makeText(ArticleMain.this,"你拒绝了权限",Toast.LENGTH_SHORT).show();
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
        String images = null;
        try {
            JSONObject jsonObject=new JSONObject(data);
            String timestamp=jsonObject.getString("timestamp");
            JSONArray jsonArray=jsonObject.getJSONArray("stories");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = (JSONObject)jsonArray.get(i);
                JSONArray jsonArray1=jsonObject1.getJSONArray("images");
                for (int j=0;j<jsonArray1.length();j++) {
                    images=jsonArray1.getString(j);
                }
                String date=jsonObject1.getString("date");
                String display_date = jsonObject1.getString("display_date");
                String id = jsonObject1.getString("id");
                String title = jsonObject1.getString("title");

                newsList.add(new News(title,id,display_date,images));

            }
            String name=jsonObject.getString("name");
            showResponse();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void showResponse() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RecyclerView recyclerView = findViewById(R.id.recycler_news);
                NewsAdapter adapter = new NewsAdapter(newsList);
                recyclerView.setAdapter(adapter);
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar,menu) ;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String id;
        if(main.equals("main"))
            id=articleId_intent;
        else
            id =love_newsId_intent;
        switch (item.getItemId()) {
            case R.id.Collect_News:
                if (!collect_news){
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("news_id",id);
                    values.put("username",username_intent);
                    values.put("name",articleName_intent);
                    values.put("description",articleDescription_intent);
                    values.put("thumbnail",articleThumbnail_intent);
                    db.insert("lovenews_data", null, values);
                    values.clear();
                    db.close();
                    collect_news=true;
                    Toast.makeText(ArticleMain.this,"已收藏",Toast.LENGTH_SHORT).show();
                }else Toast.makeText(ArticleMain.this,"收藏已存在",Toast.LENGTH_SHORT).show();
                break;
            case R.id.QuitCollect_News:
                if (collect_news){
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    db.delete("lovenews_data","news_id=?",new String[]{id});
                    db.close();
                    collect_news=false;
                    Toast.makeText(ArticleMain.this,"已取消收藏",Toast.LENGTH_SHORT).show();
                }else Toast.makeText(ArticleMain.this,"收藏不存在",Toast.LENGTH_SHORT).show();
            default:
        }
        return true;
    }
    public void onBackPressed() {
        Intent intent1 =new Intent(ArticleMain.this,Main.class);
        intent1.putExtra("username_intent",username_intent);
        Intent intent2=new Intent(ArticleMain.this,LoveNewsActivity.class);
        intent2.putExtra("username_intent",username_intent);
        if(main.equals("main"))
            startActivity(intent1);
        else startActivity(intent2);
        finish();
    }
    public static class ItemClickSupport {
        private final RecyclerView mRecyclerView;
        private ArticleMain.ItemClickSupport.OnItemClickListener mOnItemClickListener;
        private ArticleMain.ItemClickSupport.OnItemLongClickListener mOnItemLongClickListener;
        private View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    RecyclerView.ViewHolder holder = mRecyclerView.getChildViewHolder(v);
                    mOnItemClickListener.onItemClicked(mRecyclerView, holder.getAdapterPosition(), v);
                }
            }
        };
        private View.OnLongClickListener mOnLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnItemLongClickListener != null) {
                    RecyclerView.ViewHolder holder = mRecyclerView.getChildViewHolder(v);
                    return mOnItemLongClickListener.onItemLongClicked(mRecyclerView, holder.getAdapterPosition(), v);
                }
                return false;
            }
        };
        private RecyclerView.OnChildAttachStateChangeListener mAttachListener
                = new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {
                if (mOnItemClickListener != null) {
                    view.setOnClickListener(mOnClickListener);
                }
                if (mOnItemLongClickListener != null) {
                    view.setOnLongClickListener(mOnLongClickListener);
                }
            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {

            }
        };

        private ItemClickSupport(RecyclerView recyclerView) {
            mRecyclerView = recyclerView;
            mRecyclerView.setTag(R.id.item_click_support, this);
            mRecyclerView.addOnChildAttachStateChangeListener(mAttachListener);
        }

        public static ArticleMain.ItemClickSupport addTo(RecyclerView view) {
            ArticleMain.ItemClickSupport support = (ArticleMain.ItemClickSupport) view.getTag(R.id.item_click_support);
            if (support == null) {
                support = new ArticleMain.ItemClickSupport(view);
            }
            return support;
        }

        public static ArticleMain.ItemClickSupport removeFrom(RecyclerView view) {
            ArticleMain.ItemClickSupport support = (ArticleMain.ItemClickSupport) view.getTag(R.id.item_click_support);
            if (support != null) {
                support.detach(view);
            }
            return support;
        }

        public ArticleMain.ItemClickSupport setOnItemClickListener(ArticleMain.ItemClickSupport.OnItemClickListener listener) {
            mOnItemClickListener = listener;
            return this;
        }

        public ArticleMain.ItemClickSupport setOnItemLongClickListener(ArticleMain.ItemClickSupport.OnItemLongClickListener listener) {
            mOnItemLongClickListener = listener;
            return this;
        }

        private void detach(RecyclerView view) {
            view.removeOnChildAttachStateChangeListener(mAttachListener);
            view.setTag(R.id.item_click_support, null);
        }

        public interface OnItemClickListener {

            void onItemClicked(RecyclerView recyclerView, int position, View v);
        }

        public interface OnItemLongClickListener {

            boolean onItemLongClicked(RecyclerView recyclerView, int position, View v);
        }
    }

}

