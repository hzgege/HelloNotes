package com.amyzhongjie.hellonotes;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.yancy.imageselector.ImageConfig;
import com.yancy.imageselector.ImageSelector;
import com.yancy.imageselector.ImageSelectorActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import base.ThemeActivity;

import static com.amyzhongjie.hellonotes.R.color.white;

public class SelectOne extends ThemeActivity implements View.OnClickListener{

    private Button s_back, s_delete;
    private ImageView s_img;
    private VideoView s_video;
    private TextView s_time;
    private EditText s_tv;
    private Toolbar toolbar;            //工具栏
    private NotesDB notesDB;
    private SQLiteDatabase dbWriter;    //获取写权限
    private SQLiteDatabase getDbWriter;    //获取写权限
    private SQLiteDatabase dbReader;       //读
    private File videoFile = null;      //加载视频
    private Uri videoUri = null;
    private FloatingActionButton fb;
    private ImageView imageView;
    private ArrayList<String> path = new ArrayList<>();
    public static final int REQUEST_CODE = 1000;
    private Intent intent;
    private CollapsingToolbarLayout collapsingToolbar;
    private int imgFlag = 0;        //判断图片是否被删除
    private int videoFlag = 0;      //判断视频是否被删除

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_content);
        findView();

        intent = getIntent();
        //添加监听
        fb = (FloatingActionButton)findViewById(R.id.FTBar);
        fb.setOnClickListener(this);

        s_time = (TextView)findViewById(R.id.s_time);
        s_img = (ImageView)findViewById(R.id.s_img);
        s_video = (VideoView)findViewById(R.id.s_video);
        s_tv = (EditText) findViewById(R.id.s_tv);

        notesDB = new NotesDB(this, "notes.db", null, 7);
        dbWriter = notesDB.getWritableDatabase();


        //没有接受到图片，隐藏ImageView,否则显示
        if(intent.getStringExtra(NotesDB.PATH).equals("null")){
            s_img.setVisibility(View.GONE);
        }else {
            s_img.setVisibility(View.VISIBLE);
        }

        //没有接受到视频，隐藏VideoView,否则显示
        if(intent.getStringExtra(NotesDB.VIDEO).equals("null")){
            s_video.setVisibility(View.GONE);
        }else {
            s_video.setVisibility(View.VISIBLE);
        }

        //加载时间
        s_time.setText(intent.getStringExtra("time"));

        //加载文字
        s_tv.setText(intent.getStringExtra(NotesDB.CONTENT));
        //设置不可编辑
        s_tv.setFocusable(false);
        s_tv.setFocusableInTouchMode(false);
        //加载图片
        Bitmap bitmap = BitmapFactory.decodeFile(intent.getStringExtra(NotesDB.PATH));
        s_img.setImageBitmap(bitmap);

        //加载视频
        videoFile = new File(intent.getStringExtra(NotesDB.VIDEO));
        videoUri = FileProvider.getUriForFile(this,
                "com.amyzhongjie.hellonotes.fileprovider", videoFile);
        s_video.setVideoURI(videoUri);
        s_video.start();
    }

    //工具栏
    private void findView() {
        toolbar = (Toolbar) findViewById(R.id.Ctoolbar);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        imageView = (ImageView)findViewById(R.id.backImgView);
        //从数据库读取默认路径
        NotesDB nb = new NotesDB(this, "imgpath.db", null, 7);
        //获取对象对数据库进行操作
        dbReader = nb.getReadableDatabase();
        String sql = " select * from imgpath where " + NotesDB.ID + " = " + 1 ;
        Cursor cursor = dbReader.rawQuery( sql, null);
        if(cursor.moveToFirst()){
            String p = cursor.getString(cursor.getColumnIndex("path"));
            Bitmap bitmap = BitmapFactory.decodeFile(p);
            imageView.setImageBitmap(bitmap);
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SelectOne.this);
                // icon
                builder.setIcon(R.mipmap.choosep);
                builder.setTitle("更换背景图");
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ImageConfig imageConfig
                                = new ImageConfig.Builder(new GlideLoader())
                                .steepToolBarColor(getPrimaryColor())
                                .titleBgColor(getPrimaryColor())
                                .titleSubmitTextColor(getResources().getColor(white))
                                .titleTextColor(getResources().getColor(white))
                                .pathList(path)
                                // 开启单选   （默认为多选）
                                .singleSelect()
                                .build();

                        ImageSelector.open(SelectOne.this, imageConfig);   // 开启图片选择器
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.show();
            }
        });


        //imageView.setImageResource(R.mipmap.cat);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        collapsingToolbar.setTitle("HelloNotes");

        //修改状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

    }

    /*
       2.onOptionsItemSelected 选择菜单
    */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return  true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImageSelector.IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {

            // Get Image Path List
            List<String> pathList = data.getStringArrayListExtra(ImageSelectorActivity.EXTRA_RESULT);

            for (String path : pathList) {
                if(path != null){
                    Log.i("ImagePathList", path);
                    //将path的设置入数据库
                    NotesDB nb = new NotesDB(this, "imgpath.db", null, 7);
                    //获取对象对数据库进行操作,背景图的路径存入数据库。以后就都是这个了
                    getDbWriter = nb.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("path", path);
                    getDbWriter.update("imgpath", values, "_id = 1", null);
                    Bitmap bitmap = BitmapFactory.decodeFile(path);
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    public void setEdit(){
        s_tv.setFocusableInTouchMode(true);
        s_tv.setFocusable(true);
        s_tv.requestFocus();
    }

    public void setCanNotEdit(){
        s_tv.setFocusable(false);
        s_tv.setFocusableInTouchMode(false);
    }
    @Override
    public void onResume() {
        super.onResume();
        updateNowTheme();
        updateTheme();
    }

    private class s_OnLongClick implements View.OnLongClickListener {

        @Override
        public boolean onLongClick(View view) {
            switch (view.getId()){
                case R.id.s_img:
                    AlertDialog.Builder builder = new AlertDialog.Builder(SelectOne.this);
                    // icon
                    builder.setIcon(R.mipmap.choosep);
                    builder.setTitle("删除此图片");
                    builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            s_img.setVisibility(View.GONE);
                            imgFlag = 1;
                        }
                    });
                    builder.setNegativeButton("取消", null);
                    builder.show();
                    return false;

                case R.id.s_video:
                    AlertDialog.Builder bbuilder = new AlertDialog.Builder(SelectOne.this);
                    // icon
                    bbuilder.setIcon(R.mipmap.choosep);
                    bbuilder.setTitle("删除此视频");
                    bbuilder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            s_video.setVisibility(View.GONE);
                            videoFlag = 1;
                        }
                    });
                    bbuilder.setNegativeButton("取消", null);
                    bbuilder.show();
                    return false;
                    default:
                        return false;
            }
        }
    }

        @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.FTBar:
                //文本变为可编辑
                setEdit();
                //图片的监听
                s_img.setOnLongClickListener(new s_OnLongClick());
                s_video.setOnLongClickListener(new s_OnLongClick());

                Snackbar.make(view,"", Snackbar.LENGTH_INDEFINITE).setAction("保存", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //数据库操作
                        updateData(s_tv.getText().toString());
                        //文本框不可编辑
                        setCanNotEdit();
                        s_img.setOnLongClickListener(null);
                        s_video.setOnClickListener(null);
                    }
                }).show();

                break;
        }
    }
    private void updateNowTheme(){

        collapsingToolbar.setContentScrimColor(getPrimaryColor());
        collapsingToolbar.setStatusBarScrimColor(getPrimaryColor());
        toolbar.setPopupTheme(getPopupToolbarStyle());
        toolbar.setBackgroundColor(0);
        toolbar.setTitle("");
        fb.setBackgroundTintList(ColorStateList.valueOf(getPrimaryColor()));

    }

    //更新数据
    public void updateData(String update_values){

        String id = ""+intent.getIntExtra(NotesDB.ID, 0);
        String sql = "update " + NotesDB.TABLE_NAME + " set " + NotesDB.CONTENT
                + " = ? where _id = ?";
        dbWriter.execSQL(sql, new String[]{update_values, id});
        if( 1 == imgFlag ){

            sql = "update " + NotesDB.TABLE_NAME + " set " + NotesDB.PATH
                    + " = ? where _id = ?";
            dbWriter.execSQL(sql, new String[]{"null", id});
        }
        if(1 == videoFlag){
            sql = "update " + NotesDB.TABLE_NAME + " set " + NotesDB.VIDEO
                    + " = ? where _id = ?";
            dbWriter.execSQL(sql, new String[]{"null", id});
        }
    }
}
