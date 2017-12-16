package com.example.yang.zhihu2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
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

public class LoveEssayActivity extends AppCompatActivity {
    private List<LoveEssay> love_essayList = new ArrayList<>();
    RecyclerView.LayoutManager recyclerViewlayoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String username_intent;
    private MyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.love_essay);
        final Button love_essay_back=findViewById(R.id.love_essay_back);
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
                if (username.equals(username_intent)){
                    love_essayList.add(new LoveEssay(username,title,essay_id,thumbnail,url));
               }
            }while (cursor.moveToNext());
        }
        cursor.close();
        sdb.close();

        love_essay_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoveEssayActivity.this,UserActivity.class);
                intent.putExtra("username_intent",username_intent);
                startActivity(intent);
                finish();
            }
        });
        Toolbar toolbar = findViewById(R.id.toolbar_love_essay);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setTitle("");
        }

        RecyclerView recyclerView = findViewById(R.id.rev_love_essay);
        LoveEssayAdapter adapter = new LoveEssayAdapter(love_essayList);
        recyclerView.setAdapter(adapter);

        recyclerViewlayoutManager = new LinearLayoutManager(this);
        GridLayoutManager layoutManager=new GridLayoutManager(this,1);
        recyclerView.setLayoutManager(layoutManager);

        swipeRefreshLayout=findViewById(R.id.sre_love_essay);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,R.color.colorAccent,R.color.colorButton);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                ( new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        love_essayList.clear();
                        showResponse();
                    }
                }, 3000);
            }
        });
        if (ContextCompat.checkSelfPermission(LoveEssayActivity.this, Manifest.permission.INTERNET)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(LoveEssayActivity.this,new String[]{Manifest.permission.INTERNET},1);
        } else{
            showResponse();
        }

        LoveEssayActivity.ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new LoveEssayActivity.ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                final LoveEssay love_essay = love_essayList.get(position);
                Intent intent=new Intent(LoveEssayActivity.this,ArticleActivity.class);
                intent.putExtra("Title_intent",love_essay.getTitle());
                intent.putExtra("username_intent",username_intent);
                intent.putExtra("love_EssayId_intent",love_essay.getEssay_id());
                intent.putExtra("Url_intent",love_essay.getUrl());
                intent.putExtra("Thumbnail_intent",love_essay.getThumbnail());
                intent.putExtra("hot","like");
                startActivity(intent);
                finish();
            }
        });
    }

    private void showResponse() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RecyclerView recyclerView = findViewById(R.id.rev_love_essay);
                LoveEssayAdapter adapter = new LoveEssayAdapter(love_essayList);
                recyclerView.setAdapter(adapter);
            }
        });
    }

    public static class ItemClickSupport {
        private final RecyclerView mRecyclerView;
        private LoveEssayActivity.ItemClickSupport.OnItemClickListener mOnItemClickListener;
        private LoveEssayActivity.ItemClickSupport.OnItemLongClickListener mOnItemLongClickListener;
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

        public static LoveEssayActivity.ItemClickSupport addTo(RecyclerView view) {
            LoveEssayActivity.ItemClickSupport support = (LoveEssayActivity.ItemClickSupport) view.getTag(R.id.item_click_support);
            if (support == null) {
                support = new LoveEssayActivity.ItemClickSupport(view);
            }
            return support;
        }

        public static LoveEssayActivity.ItemClickSupport removeFrom(RecyclerView view) {
            LoveEssayActivity.ItemClickSupport support = (LoveEssayActivity.ItemClickSupport) view.getTag(R.id.item_click_support);
            if (support != null) {
                support.detach(view);
            }
            return support;
        }

        public LoveEssayActivity.ItemClickSupport setOnItemClickListener(LoveEssayActivity.ItemClickSupport.OnItemClickListener listener) {
            mOnItemClickListener = listener;
            return this;
        }

        public LoveEssayActivity.ItemClickSupport setOnItemLongClickListener(LoveEssayActivity.ItemClickSupport.OnItemLongClickListener listener) {
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
