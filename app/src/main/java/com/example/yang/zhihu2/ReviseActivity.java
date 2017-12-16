package com.example.yang.zhihu2;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class ReviseActivity extends AppCompatActivity {
    private String sex;
    private SQLiteOpenHelper dbHelper;
    private String imagePath;
    public static final int CHOOSE_PHOTO=2;
    private boolean set_user_image=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.revise_user);
        dbHelper =new MyDatabaseHelper(this,"data.db",null,1) ;
        dbHelper.getWritableDatabase();
        Intent intent=getIntent();
        final String username_intent=intent.getStringExtra("username_intent");
        Button revise_confirm=findViewById(R.id.revise_confirm);
        Button revise_cancel=findViewById(R.id.revise_cancel);
        final EditText revise_nickname=findViewById(R.id.revise_nickname);
        final EditText revise_call=findViewById(R.id.revise_call);
        Button revise_birth=findViewById(R.id.revise_birth);
        final TextView revise_name=findViewById(R.id.revise_name);
        ImageView revise_head=findViewById(R.id.head_revise);
        final RadioGroup radioGroup = findViewById(R.id.radioGroup);
        Button revise_image=findViewById(R.id.revise_image);
        RadioButton btnWoman=findViewById(R.id.revise_btnWoman);
        RadioButton btnMan=findViewById(R.id.revise_btnMan);
        Button re_back=findViewById(R.id.re_back);
        SQLiteDatabase sdb = dbHelper.getReadableDatabase();
        Cursor cursor=sdb.query("userdata",null,null,null,null,null,null);
        if (cursor.getCount()!=0&&cursor.moveToFirst()) {
            do {
                String username=cursor.getString(cursor.getColumnIndex("username"));
                String nickname=cursor.getString(cursor.getColumnIndex("nickname"));
                String birth=cursor.getString(cursor.getColumnIndex("birth"));
                String header=cursor.getString(cursor.getColumnIndex("user_image"));
                String call=cursor.getString(cursor.getColumnIndex("call"));
                String sex=cursor.getString(cursor.getColumnIndex("sex"));
                if (username.equals(username_intent)){
                    revise_name.setText(username);
                    revise_nickname.setText(nickname);
                    revise_call.setText(call);
                    /*if(sex.equals("m"))btnMan.setChecked(true);
                    else btnWoman.setChecked(true);*/
                    Glide.with(this).load(header).asBitmap().placeholder(R.drawable.zhihu).error(R.drawable.zhihu).into(revise_head);
                    break;
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        revise_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(ReviseActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.
                        PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(ReviseActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }else {
                    openAlbum();
                }
            }
        });
        re_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ReviseActivity.this,UserActivity.class);
                intent.putExtra("username_intent",username_intent);
                startActivity(intent);
                finish();
            }
        });
        revise_birth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ReviseActivity.this,BirthdayActivity.class);
                intent.putExtra("username_intent",username_intent);
                startActivity(intent);
                //finish();
            }
        });
        revise_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < radioGroup.getChildCount(); i++) {
                    RadioButton rd = (RadioButton) radioGroup.getChildAt(i);
                    if (rd.isChecked()) {
                        if (rd.getText().equals("男"))sex="m";
                        else sex="f";
                        break;
                    }
                }
                if (check1()&&check2()){
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("nickname", revise_nickname.getText().toString().trim());
                    values.put("call", revise_call.getText().toString().trim());
                    values.put("sex", sex);
                    if(set_user_image)
                        values.put("user_image", imagePath);
                    db.update("userdata" ,values, "username=?",new String[]{revise_name.getText().toString()});
                    values.clear();
                    Toast.makeText(ReviseActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ReviseActivity.this,UserActivity.class);
                    intent.putExtra("username_intent",revise_name.getText().toString().trim());
                    startActivity(intent);
                    finish();
                }
            }
            private boolean check1(){
                if (revise_nickname.getText().toString().equals("")){
                    Toast.makeText(ReviseActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                }else if (revise_nickname.getText().toString().contains(" ")){
                    Toast.makeText(ReviseActivity.this,"不能包含空格",Toast.LENGTH_SHORT).show();
                }else{
                    return true;
                }
                return false;
            }
            private boolean check2(){
                if (revise_call.getText().toString().length()!=11){
                    Toast.makeText(ReviseActivity.this, "请输入正确手机号", Toast.LENGTH_SHORT).show();
                }else if (revise_call.getText().toString().contains(" ")){
                    Toast.makeText(ReviseActivity.this,"不能包含空格",Toast.LENGTH_SHORT).show();
                }else{
                    return true;
                }
                return false;

            }
            /*private boolean check2() {
                SQLiteDatabase sdb = dbHelper.getReadableDatabase();
                Cursor cursor=sdb.query("userdata",null,null,null,null,null,null);
                if (cursor.moveToFirst()) {
                    do {
                        String username=cursor.getString(cursor.getColumnIndex("username"));
                        String nickname=cursor.getString(cursor.getColumnIndex("nickname"));
                        if (!username.equals(username_intent)&&nickname.equals(revise_nickname.getText().toString().trim())){
                            Toast.makeText(ReviseActivity.this,"昵称已存在",Toast.LENGTH_SHORT).show();
                            return true;
                        }
                    }while (cursor.moveToNext());
                }
                cursor.close();
                return false;
            }*/
        });
        revise_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(ReviseActivity.this,UserActivity.class);
                intent.putExtra("username_intent",username_intent);
                startActivity(intent);
                finish();
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "你拒绝了权限", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }
    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO); // 打开相册
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CHOOSE_PHOTO:
                if (resultCode != ReviseActivity.RESULT_CANCELED&&data!=null) {
                    if (resultCode == RESULT_OK) {
                        // 判断手机系统版本号
                        if (Build.VERSION.SDK_INT>=19)
                            // 4.4及以上系统使用这个方法处理图片
                            handleImageOnKitKat(data);
                    }else {
                        //4.4以下系统使用这个方法处理图片
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                break;
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        imagePath = null;
        Uri uri = data.getData();
        Log.d("TAG", "handleImageOnKitKat: uri is " + uri);
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            assert uri != null;
            if("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else {
            assert uri != null;
            if ("content".equalsIgnoreCase(uri.getScheme())) {
                // 如果是content类型的Uri，则使用普通方式处理
                imagePath = getImagePath(uri, null);
            } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                // 如果是file类型的Uri，直接获取图片路径即可
                imagePath = uri.getPath();
            }
        }
        displayImage(imagePath); // 根据图片路径显示图片
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }


    private void displayImage(String imagePath) {
        ImageView revise_head=findViewById(R.id.head_revise);
        if (imagePath != null) {
            //Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            //revise_head.setImageBitmap(bitmap);
            Glide.with(this).load(imagePath).asBitmap().placeholder(R.drawable.zhihu).error(R.drawable.zhihu).into(revise_head);
            set_user_image=true;
        } else {
            Toast.makeText(this, "获取图片失败", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onBackPressed() {
        TextView username=findViewById(R.id.revise_name);
        Intent intent =new Intent(ReviseActivity.this,UserActivity.class);
        intent.putExtra("username_intent",username.getText().toString());
        startActivity(intent);
        finish();
    }

}
