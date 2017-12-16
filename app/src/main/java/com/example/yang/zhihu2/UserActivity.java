package com.example.yang.zhihu2;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class UserActivity extends AppCompatActivity {
    private SQLiteOpenHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user);

        FloatingActionButton user_fab=findViewById(R.id.user_fab);

        dbHelper =new MyDatabaseHelper(this,"data.db",null,1) ;
        dbHelper.getWritableDatabase();

        Intent intent=getIntent();
        final String username_intent=intent.getStringExtra("username_intent");

        final TextView user_nickname=findViewById(R.id.user_nickname);
        final TextView user_call=findViewById(R.id.user_call);
        final TextView user_name=findViewById(R.id.user_name);
        final TextView user_birth=findViewById(R.id.user_birth);
        final ImageView user_head=findViewById(R.id.head_user);
        final TextView user_sex=findViewById(R.id.user_sex);
        final Button user_back=findViewById(R.id.user_back);
        final Button essay_love=findViewById(R.id.essay_love);
        final Button news_love=findViewById(R.id.news_love);
        SQLiteDatabase sdb = dbHelper.getReadableDatabase();
        Cursor cursor=sdb.query("userdata",null,null,null,null,null,null);
        if (cursor.getCount()!=0&&cursor.moveToFirst()) {
            do {
                String call=cursor.getString(cursor.getColumnIndex("call"));
                String username=cursor.getString(cursor.getColumnIndex("username"));
                String nickname=cursor.getString(cursor.getColumnIndex("nickname"));
                String header=cursor.getString(cursor.getColumnIndex("user_image"));
                String birth=cursor.getString(cursor.getColumnIndex("birth"));
                String sex=cursor.getString(cursor.getColumnIndex("sex"));
                if (username.equals(username_intent)){
                    user_name.setText(username);
                    user_call.setText(call);
                    user_nickname.setText(nickname);
                    user_birth.setText(birth);
                   /* if(sex.equals("m"))user_sex.setText("男");
                    else user_sex.setText("女");*/
                    Glide.with(this).load(header).asBitmap().placeholder(R.drawable.zhihu).error(R.drawable.zhihu).into(user_head);
                    break;
                }
            }while (cursor.moveToNext());
        }
        cursor.close();

        user_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(UserActivity.this,ReviseActivity.class);
                intent.putExtra("username_intent",username_intent);
                startActivity(intent);
                finish();
            }
        });
        user_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(UserActivity.this,Main.class);
                intent.putExtra("username_intent",username_intent);
                startActivity(intent);
                finish();
            }
        });
        news_love.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(UserActivity.this,LoveNewsActivity.class);
                intent.putExtra("username_intent",username_intent);
                startActivity(intent);
            }
        });

        essay_love.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(UserActivity.this,LoveEssayActivity.class);
                intent.putExtra("username_intent",username_intent);
                startActivity(intent);
            }
        });
    }
    @Override
    public void onBackPressed() {
        TextView username=findViewById(R.id.user_name);
        Intent intent =new Intent(UserActivity.this,Main.class);
        intent.putExtra("username_intent",username.getText().toString());
        startActivity(intent);
        finish();
    }
}
