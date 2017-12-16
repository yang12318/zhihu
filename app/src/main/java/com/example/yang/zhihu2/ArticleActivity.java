package com.example.yang.zhihu2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
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

public class ArticleActivity extends AppCompatActivity {
    private String EssayId_intent,love_EssayId_intent,Title_intent,Thumbnail_intent,Url_intent;
    private String username_intent;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MyDatabaseHelper dbHelper;
    private String body,css,CSS;
    private WebView web_essay;
    private String hot;
    private boolean collect_essay=false;
    private String Url,Url2;
    private boolean love_essay=false;
    private Menu menu;
    private  String articleId_intent;
    private TextView essay_hot;

    @SuppressLint("SetJavaScriptEnabled")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        final Button essay_back=findViewById(R.id.essay_back);
        dbHelper =new MyDatabaseHelper(this,"data.db",null,1) ;
        dbHelper.getWritableDatabase();

        web_essay=findViewById(R.id.web_essay);
        web_essay.getSettings().setJavaScriptEnabled(true);
        web_essay.setWebViewClient(new WebViewClient());
        web_essay.getSettings().setSupportZoom(false);
        web_essay.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        web_essay.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        web_essay.getSettings().setLoadWithOverviewMode(true);
        web_essay.getSettings().setUseWideViewPort(true);
        web_essay.getSettings().setDomStorageEnabled(true);
        web_essay.getSettings().setAllowFileAccessFromFileURLs(true);
        web_essay.getSettings().setAllowUniversalAccessFromFileURLs(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            web_essay.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        }
        else
        {
            web_essay.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        }

        Toolbar toolbar = findViewById(R.id.toolbar_essay);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setTitle("");
        }
        Intent intent=getIntent();
        articleId_intent=intent.getStringExtra("articleId_intent");
        username_intent=intent.getStringExtra("username_intent");
        EssayId_intent=intent.getStringExtra("EssayId_intent");
        love_EssayId_intent=intent.getStringExtra("love_EssayId_intent");
        Title_intent=intent.getStringExtra("Title_intent");
        Thumbnail_intent=intent.getStringExtra("Thumbnail_intent");
        Url_intent=intent.getStringExtra("Url_intent");
        hot=intent.getStringExtra("hot");
        if(hot.equals("hot")||hot.equals("message"))
        Url= "http://news-at.zhihu.com/api/2/news/"+EssayId_intent;
        else
        Url = "http://news-at.zhihu.com/api/2/news/"+love_EssayId_intent;
        swipeRefreshLayout=findViewById(R.id.sre_essay);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,R.color.colorAccent,R.color.colorButton);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                ( new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        sendRequestWithHttpURLConnection();
                    }
                }, 3000);
            }
        });
        sendRequestWithHttpURLConnection();
        essay_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ArticleActivity.this,HotActivity.class);
                intent.putExtra("username_intent",username_intent);
                startActivity(intent);
                finish();
            }
        });
        /*SQLiteDatabase sdb = dbHelper.getReadableDatabase();
        Cursor cursor=sdb.query("loveessay_data",null,null,null,null,null,null);
        if (cursor.moveToFirst()) {
            do {
                String username=cursor.getString(cursor.getColumnIndex("username"));
                String essay=cursor.getString(cursor.getColumnIndex("essay_id"));
                if(hot.equals("hot")){
                    if (username.equals(username_intent)&&essay.equals(EssayId_intent)){
                        collect_essay=true;
                        break;
                    }
                }
                else {
                    if (username.equals(username_intent)&&essay.equals(love_EssayId_intent)){
                        collect_essay=true;
                        break;
                    }
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        sdb.close();*/

        /*SQLiteDatabase sdb1 = dbHelper.getReadableDatabase();
        Cursor cursor1=sdb1.query("love_article_table",null,null,null,null,null,null);
        if (cursor1.moveToFirst()) {
            do {
                String username=cursor1.getString(cursor1.getColumnIndex("username"));
                String article=cursor1.getString(cursor1.getColumnIndex("news_id"));
                if(hot.equals("hot")){
                    if (username.equals(username_intent)&&article.equals(NewsId_intent)){
                        love_article=true;
                        getMenuInflater().inflate(R.menu.article_menu,menu) ;
                        MenuItem menuItem=menu.findItem(R.id.LoveArticle);
                        menuItem.setIcon(R.drawable.like1);
                        break;
                    }
                }
                else {
                    if (username.equals(username_intent)&&article.equals(like_NewsId_intent)){
                        love_article=true;
                        getMenuInflater().inflate(R.menu.article_menu,menu) ;
                        MenuItem menuItem=menu.findItem(R.id.LoveArticle);
                        menuItem.setIcon(R.drawable.like1);
                        break;
                    }
                }
            }while (cursor1.moveToNext());
        }
        cursor1.close();
        sdb1.close();*/
    }
    private String getHtmlData(String bodyHTML) {
        String head = "<head><style>img{max-width: 100%; width:auto; height: auto;}</style></head>";
        return "<html>" + head + "<body>" + bodyHTML + "</body></html>";
    }
    private void sendRequestWithHttpURLConnection() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL(Url);
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
    /*private void getCSS(final String Css) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL(Css);
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
                    CSS=response.toString();
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
    }*/
    private void parseJSONWithJSONObject(String data) {
        try {
            JSONObject jsonObject=new JSONObject(data);
            body = jsonObject.getString("body");
            String image_source = jsonObject.getString("image_source");
            String title = jsonObject.getString("title");
            String image=jsonObject.getString("image");
            String share_url=jsonObject.getString("share_url");
            String thumbnail=jsonObject.getString("thumbnail");
            String ga_prefix=jsonObject.getString("ga_prefix");
            String id=jsonObject.getString("id");
            JSONArray js=jsonObject.getJSONArray("js");
            JSONArray jsonArray=jsonObject.getJSONArray("css");
            for (int j=0;j<jsonArray.length();j++) {
                css=jsonArray.getString(j);
            }
            showResponse();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
   /* private void sendRequestWithHttpURLConnection2() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection1 = null;
                BufferedReader reader1 = null;
                try {
                    URL url2 = new URL(Url2);
                    connection1 = (HttpURLConnection) url2.openConnection();
                    connection1.setRequestMethod("GET");
                    connection1.setConnectTimeout(8000);
                    connection1.setReadTimeout(8000);
                    InputStream in1 = connection1.getInputStream();
                    reader1 = new BufferedReader(new InputStreamReader(in1));
                    StringBuilder response1 = new StringBuilder();
                    String line1;
                    while ((line1 = reader1.readLine()) != null) {
                        response1.append(line1);
                    }
                    parseJSONWithJSONObject2(response1.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (reader1 != null) {
                        try {
                            reader1.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (connection1 != null) {
                        connection1.disconnect();
                    }
                }
            }
        }).start();
    }
    private void parseJSONWithJSONObject2(String s) {
        try {
            JSONObject jsonObject2 = new JSONObject(s);
            String longcomment = jsonObject2.getString("long_comments");
            String Popularity = jsonObject2.getString("popularity");
            String shortcomment = jsonObject2.getString("short_comments");
            String comment = jsonObject2.getString("comments");
            essay_hot.setText("长评"+longcomment);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/


    public boolean onCreateOptionsMenu(Menu menu) {
        /*String id;
        if(hot.equals("hot"))
            id=NewsId_intent;
        else
            id =like_NewsId_intent;
        getMenuInflater().inflate(R.menu.article_menu, menu);
        MenuItem item = menu.findItem(R.id.LoveArticle);
        if (love_article){
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.delete("love_article_table","news_id=?",new String[]{id});
            db.close();
            love_article=false;
            item.setIcon(R.drawable.like0);
        }else {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("news_id",id);
            values.put("username",username_intent);
            db.insert("love_article_table", null, values);
            values.clear();
            db.close();
            love_article=true;
            item.setIcon(R.drawable.like1);
        }
        return super.onCreateOptionsMenu(menu);*/
        getMenuInflater().inflate(R.menu.article_menu,menu) ;
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String id;
        if(hot.equals("hot")||hot.equals("message"))
            id=EssayId_intent;
        else
            id =love_EssayId_intent;
        switch (item.getItemId()) {
            /*case R.id.LoveArticle:
                if (love_article){
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    db.delete("love_article_table","news_id=?",new String[]{id});
                    db.close();
                    love_article=false;
                    getMenuInflater().inflate(R.menu.article_menu,menu) ;
                    MenuItem menuItem=menu.findItem(R.id.LoveArticle);
                    menuItem.setIcon(R.drawable.like0);
                }else {
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("news_id",id);
                    values.put("username",username_intent);
                    db.insert("love_article_table", null, values);
                    values.clear();
                    db.close();
                    love_article=true;
                    getMenuInflater().inflate(R.menu.article_menu,menu) ;
                    MenuItem menuItem=menu.findItem(R.id.LoveArticle);
                    menuItem.setIcon(R.drawable.like1);
                }
                break;*/
            case R.id.LongComment:
                Intent intent=new Intent(ArticleActivity.this,LongCommentActivity.class);
                intent.putExtra("EssayId_intent",EssayId_intent);
                intent.putExtra("love_EssayId_intent",love_EssayId_intent);
                intent.putExtra("hot",hot);
                startActivity(intent);
                break;
            case R.id.ShortComment:
                Intent intent1=new Intent(ArticleActivity.this,ShortCommentActivity.class);
                intent1.putExtra("EssayId_intent",EssayId_intent);
                intent1.putExtra("love_EssayId_intent",love_EssayId_intent);
                intent1.putExtra("hot",hot);
                startActivity(intent1);
                break;
            case R.id.Collect_Essay:
                if (!collect_essay){
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("essay_id",EssayId_intent);
                    values.put("username",username_intent);
                    values.put("title",Title_intent);
                    values.put("thumbnail",Thumbnail_intent);
                    values.put("url",Url_intent);
                    db.insert("loveessay_data", null, values);
                    values.clear();
                    db.close();
                    collect_essay=true;
                    Toast.makeText(ArticleActivity.this,"已收藏",Toast.LENGTH_SHORT).show();
                }else Toast.makeText(ArticleActivity.this,"收藏已存在",Toast.LENGTH_SHORT).show();
                break;
            case R.id.QuitCollect_Essay:
                if (collect_essay){
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    db.delete("loveessay_data","essay_id=?",new String[]{EssayId_intent});
                    db.close();
                    collect_essay=false;
                    Toast.makeText(ArticleActivity.this,"已取消收藏",Toast.LENGTH_SHORT).show();
                }else Toast.makeText(ArticleActivity.this,"收藏不存在",Toast.LENGTH_SHORT).show();
            default:
        }
        return true;
    }
    private void showResponse(){
        web_essay.post(new Runnable() {
            @Override
            public void run() {
                web_essay.loadDataWithBaseURL("", getHtmlData(body), "text/html", "UTF-8", "");
            }
        });
    }
    public void onBackPressed() {
        Intent intent1 =new Intent(ArticleActivity.this,HotActivity.class);
        intent1.putExtra("username_intent",username_intent);
        Intent intent2=new Intent(ArticleActivity.this,LoveEssayActivity.class);
        intent2.putExtra("username_intent",username_intent);
        Intent intent3=new Intent(ArticleActivity.this,ArticleMain.class);
        intent3.putExtra("username_intent",username_intent);
        intent3.putExtra("main","main");
        intent3.putExtra("articleId_intent",articleId_intent);
        if(hot.equals("hot"))
            startActivity(intent1);
        else if (hot.equals("message")){
            startActivity(intent3);
        }
        else startActivity(intent2);
        finish();
    }
}

