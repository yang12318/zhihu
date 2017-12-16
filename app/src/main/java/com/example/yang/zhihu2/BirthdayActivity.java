package com.example.yang.zhihu2;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
public class BirthdayActivity extends AppCompatActivity {
    private SQLiteOpenHelper dbHelper;
    private String username_intent;
    CalendarView calendarView;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.birthday);
        dbHelper =new MyDatabaseHelper(this,"data.db",null,1) ;
        dbHelper.getWritableDatabase();
        Intent intent=getIntent();
        final String username_intent=intent.getStringExtra("username_intent");
        calendarView = (CalendarView) findViewById(R.id.calenderView);
        textView = (TextView) findViewById(R.id.textView);
        final Button birth_back=findViewById(R.id.birth_back);
        final Button birth_cancel=findViewById(R.id.birth_cancel);
        final Button birth_confirm=findViewById(R.id.birth_confirm);
        //为Calendar组件日期的改变
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                Toast.makeText(BirthdayActivity.this,"你的生日是："+ year + "年" + month + "月" + dayOfMonth + "日",Toast.LENGTH_SHORT ).show();
                textView.setText( year + "年" + month + "月" + dayOfMonth + "日");
            }
        });
        Toolbar toolbar = findViewById(R.id.toolbar_birth);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setTitle("");
        }
        SQLiteDatabase sdb = dbHelper.getReadableDatabase();
        Cursor cursor=sdb.query("userdata",null,null,null,null,null,null);
        if (cursor.getCount()!=0&&cursor.moveToFirst()) {
            do {
                String username=cursor.getString(cursor.getColumnIndex("username"));
                String birth=cursor.getString(cursor.getColumnIndex("birth"));
                if (username.equals(username_intent)){
                    textView.setText(birth);
                    break;
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        birth_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("birth", textView.getText().toString().trim());
                    //values.put("sex", sex);
                    db.update("userdata" ,values, "username=?",new String[]{username_intent});
                    values.clear();
                    Toast.makeText(BirthdayActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(BirthdayActivity.this,UserActivity.class);
                    intent.putExtra("username_intent",username_intent);
                    //intent.putExtra("username_intent",revise_name.getText().toString().trim());
                    startActivity(intent);
                    finish();

                    }
        });
        birth_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(BirthdayActivity.this,ReviseActivity.class);
                intent.putExtra("username_intent",username_intent);
                startActivity(intent);
                finish();
            }
        });
        birth_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(BirthdayActivity.this,ReviseActivity.class);
                intent.putExtra("username_intent",username_intent);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav_menu, menu);
        return true;
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/
}
