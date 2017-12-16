package com.example.yang.zhihu2;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.ContentValues;
import android.widget.EditText;
import android.widget.Toast;

public class Register extends AppCompatActivity {

    private MyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        dbHelper = new MyDatabaseHelper(this, "data.db", null, 1);
        dbHelper.getWritableDatabase();

        final Button register = findViewById(R.id.re_register);
        final Button cancel = findViewById(R.id.cancel);
        final EditText user = findViewById(R.id.re_username);
        final EditText pwd = findViewById(R.id.re_password);


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Register.this, MainActivity.class);
                startActivity(intent);
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check1() && !check2()) {
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("username", user.getText().toString().trim());
                    values.put("password", pwd.getText().toString().trim());
                    db.insert("userdata", null, values);
                    values.clear();
                    Toast.makeText(Register.this, "注册成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Register.this, MainActivity.class);
                    intent.putExtra("username_intent", user.getText().toString().trim());
                    startActivity(intent);
                }
            }

            private boolean check1() {
                if (user.getText().toString().contains(" ") || pwd.getText().toString().contains(" ")) {
                    Toast.makeText(Register.this, "不能包含空格", Toast.LENGTH_SHORT).show();
                } else if (user.getText().toString().trim().equals("")) {
                    Toast.makeText(Register.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                } else if (pwd.getText().toString().trim().equals("")) {
                    Toast.makeText(Register.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                } else if (pwd.getText().toString().trim().length()<8) {
                    Toast.makeText(Register.this, "密码不能小于八位", Toast.LENGTH_SHORT).show();
                } else {
                    return true;
                }
                return false;
            }

            private boolean check2() {
                SQLiteDatabase sdb = dbHelper.getReadableDatabase();
                Cursor cursor = sdb.query("userdata", null, null, null, null, null, null);
                if (cursor.moveToFirst()) {
                    do {
                        String username = cursor.getString(cursor.getColumnIndex("username"));
                        if (username.equals(user.getText().toString().trim()))
                        {
                            Toast.makeText(Register.this, "已存在此用户，请重新注册", Toast.LENGTH_SHORT).show();
                            return true;
                        }
                        } while (cursor.moveToNext());
                }
                cursor.close();
                sdb.close();
                return false;
            }
        });
    }

    protected void onDestroy() {
        super.onDestroy();
    }
}

