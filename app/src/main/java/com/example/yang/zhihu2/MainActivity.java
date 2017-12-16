package com.example.yang.zhihu2;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity  {
    private MyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        dbHelper =new MyDatabaseHelper(this,"data.db",null,1) ;
        dbHelper.getWritableDatabase();

        final Button login = findViewById(R.id.login);
        final Button register= findViewById(R.id.register);
        final EditText user=findViewById(R.id.username);
        final EditText pwd=findViewById(R.id.password);



       login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (check1()) {
                    if (check2()){
                        Intent intent = new Intent(MainActivity.this, Main.class);
                        intent.putExtra("username_intent",user.getText().toString().trim());
                        startActivity(intent);
                        Toast.makeText(MainActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                        finish();
                    }else Toast.makeText(MainActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                }
            }
            private boolean check1(){
                if((user.getText().toString().isEmpty())||(pwd.getText().toString().isEmpty())){
                    Toast.makeText(MainActivity.this, "不能为空，请重新输入", Toast.LENGTH_SHORT).show();
                }else{
                    return true;
                }
                return false;
            }
            private boolean check2() {
                SQLiteDatabase sdb = dbHelper.getReadableDatabase();
                Cursor cursor=sdb.query("userdata",null,null,null,null,null,null);
                if (cursor.moveToFirst()) {
                    do {
                        String username=cursor.getString(cursor.getColumnIndex("username"));
                        String password=cursor.getString(cursor.getColumnIndex("password"));
                        if ((username.equals(user.getText().toString().trim()))&&(password.equals(pwd.getText().toString().trim()))){
                                return true;
                            }
                    }while (cursor.moveToNext());
                }
                cursor.close();
                return false;
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("android.intent.action.REGISTER");
                startActivity(intent);
            }
        });

    }


}
