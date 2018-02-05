package com.amyzhongjie.hellonotes;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import base.ThemeActivity;
import utils.ColorPaletteUtils;

public class AddContent extends ThemeActivity{

    private String val;     //接受标识符，判断数据类型
    private Toolbar toolbar; //工具栏
    private EditText ettext;
    private ImageView c_img;
    private VideoView c_video;
    private NotesDB notesDB;
    private SQLiteDatabase dbWriter;

    private File phoneFile = null;     //存储拍照后的图片
    private Uri imageUri;       //存储图片的路径
    private String picname;     //图片的命名
    private static final int TAKE_PHOTO = 1;    //拍照的识别数
    private String chooseImgPath = null;
    private File videoFile = null;      //存储拍照后的视频
    private Uri videoUri;       //存储视频的路径
    private String videoname;     //视频的命名
    private static final int TAKE_VIDEO = 2;    //视频的识别数
    private static final int CHOOSE_PHOTO = 3;  //
    private int flag = 0;                   //判断是照片还是拍照的图片，保存时用
    private int judge;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addcontent);
        //设置工具栏
        findView();

        val = getIntent().getStringExtra("flag");

        //获取ID
        ettext = (EditText)findViewById(R.id.ettext);
        c_img =(ImageView)findViewById(R.id.c_img);
        c_video = (VideoView)findViewById(R.id.c_video);

        initView();


        //获得对数据库的操作
        notesDB = new NotesDB(this, "notes.db", null, 7);
        dbWriter = notesDB.getWritableDatabase();



        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    //工具栏
    private void findView() {
        toolbar = (Toolbar) findViewById(R.id.ccctoolbar);
        toolbar.setTitle("Add Notes");
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(onMenuItemClick);
    }


    public void initView(){

        switch (val){
            case "1":
                //文字类型
                createText();
                break;

                //拍照类型
            case "2.1":
                createPhotograph();
                break;

                //相册类型
            case "2.2":
                album();
                break;

                //视频类型
            case "3":
                createVideo();
                break;
        }
    }

    //当添加的是文字
    private void createText(){
        toolbar.setSubtitle("文字类");
        c_img.setVisibility(View.GONE);
        c_video.setVisibility(View.GONE);
    }

    //当添加的是拍照获取的图片
    private void createPhotograph(){
        toolbar.setSubtitle("图片类");
        c_img.setVisibility(View.VISIBLE);
        c_video.setVisibility(View.GONE);

        //跳转到系统相机
        Intent iimg = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //以具体时间命名防止重复
        picname = getJuTiTime().replace(" ", "") + ".jpg";
        phoneFile = new File(getExternalCacheDir(), picname);

        //Andriod7.0以上，file:// 不再被允许
        if(Build.VERSION.SDK_INT >= 24){
            imageUri = FileProvider.getUriForFile(this,
                    "com.amyzhongjie.hellonotes.fileprovider", phoneFile);
        }
        else {
            imageUri = Uri.fromFile(phoneFile);
        }

        //指定图片的输出地址
        iimg.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(iimg, TAKE_PHOTO);
    }

    //打开相册
    private void album(){
        toolbar.setSubtitle("图片类");
        flag = 1;
        c_img.setVisibility(View.VISIBLE);
        c_video.setVisibility(View.GONE);
        if(ContextCompat.checkSelfPermission(AddContent.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(AddContent.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }else{
            openAlbum();
        }


    }

    //当添加的是拍摄视频
    private void createVideo(){
        toolbar.setSubtitle("视频类");
        c_img.setVisibility(View.GONE);
        c_video.setVisibility(View.VISIBLE);

        //跳转到系统视频
        Intent video = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        //以具体时间命名防止重复
        videoname = getJuTiTime().replace(" ", "") + ".mp4";
        videoFile = new File(getExternalCacheDir(), videoname);

        //Andriod7.0以上，file:// 不再被允许
        if(Build.VERSION.SDK_INT >= 24){
            videoUri = FileProvider.getUriForFile(this,
                    "com.amyzhongjie.hellonotes.fileprovider", videoFile);
        }
        else {
            videoUri = Uri.fromFile(videoFile);
        }

        //指定视频的输出地址
        video.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
        startActivityForResult(video, TAKE_VIDEO);
    }


    private void openAlbum(){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);//打开相册
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else{
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateNowTheme();
        //updateTheme();
    }
    //向数据库中添加数据
    public void addDB(){
        judge = 0; //初始化0代表文字类型
        if(null != phoneFile || null != chooseImgPath){
            judge = 1;
        }else if(null != videoFile){
            judge = 2;
        }
        String t = getJuTiTime();
        ContentValues cv = new ContentValues();
        cv.put(NotesDB.JUDGE, judge);
        cv.put(NotesDB.CONTENT,ettext.getText().toString());
        cv.put(NotesDB.TIME,getTime());
        cv.put(NotesDB.HMS, t.substring(t.length() - 8));
        if(0 == flag){
            cv.put(NotesDB.PATH, phoneFile + "");
        }else {
            cv.put(NotesDB.PATH, chooseImgPath);
        }

        cv.put(NotesDB.VIDEO, videoFile + "");
        dbWriter.insert(NotesDB.TABLE_NAME, null, cv);
    }

    //获取具体时间
    public String getJuTiTime(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date();
        String s = format.format(curDate);
        return s;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_menu, menu);
        return true;
    }

    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_sure:
                    addDB();
                    finish();
                    break;
            }

            return true;
        }
    };
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return  true;

        }
        return super.onOptionsItemSelected(item);
    }

    //获取年月日 周几
    public String getTime(){
        SimpleDateFormat format = new SimpleDateFormat("dd yyyy.MM");
        Date curDate = new Date();
        String week = getWeek(curDate);
        String str = format.format(curDate);
        return week + str;
    }

    public static String getWeek(Date date){
        String[] weeks = {"周日","周一","周二","周三","周四","周五","周六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if(week_index<0){
            week_index = 0;
        }
        return weeks[week_index];
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){

        case TAKE_PHOTO:
            if(resultCode == RESULT_OK){   //传递的是图片
             try{
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                c_img.setImageBitmap(bitmap);
            }catch (FileNotFoundException e){
                e.printStackTrace();
                }
            }
            break;

        case TAKE_VIDEO:
            c_video.setVideoURI(videoUri);
            c_video.start();
           break;

           case CHOOSE_PHOTO:
               if(resultCode == RESULT_OK){
                   if(Build.VERSION.SDK_INT >= 19){
                        //4.4以上
                        handleImageOnKitKat(data);
                   }else{
                       //4.4以下
                       handleImageBeforeKitKat(data);
                   }
                }
                break;

        default:
            break;
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data){
        String imagePath = null;
        Uri uri = data.getData();
        if(DocumentsContract.isDocumentUri(this,uri)){
            //如果是document类型的uri，通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];//解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content:" +
                        "//downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        }else if("content".equalsIgnoreCase(uri.getScheme())){
            imagePath = getImagePath(uri, null);
        }else if("file".equalsIgnoreCase(uri.getScheme())){
            imagePath = uri.getPath();
        }
        display(imagePath);
    }

    private void handleImageBeforeKitKat(Intent data){
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        display(imagePath);
    }

    private void display(String imagepath){
        if(imagepath != null){
            chooseImgPath = imagepath;
            Bitmap bitmap = BitmapFactory.decodeFile(imagepath);
            c_img.setImageBitmap(bitmap);
        }else{
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }

    private String getImagePath(Uri uri, String selection){
        String path = null;
        Cursor cr = getContentResolver().query(uri, null, selection, null, null);
        if(cr != null){
            if(cr.moveToFirst()){
                path = cr.getString(cr.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cr.close();
        }
        return path;
    }

    private void updateNowTheme(){
        toolbar.setPopupTheme(getPopupToolbarStyle());
        toolbar.setBackgroundColor(getPrimaryColor());
        toolbar.setTitle("Add Notes");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (isTranslucentStatusBar()) {
                getWindow().setStatusBarColor(ColorPaletteUtils.getObscuredColor(getPrimaryColor()));
            } else {
                getWindow().setStatusBarColor(getPrimaryColor());
            }
            getWindow().setNavigationBarColor(isNavigationBarColored() ? getPrimaryColor() : ContextCompat.getColor(getApplicationContext(), R.color.md_black_1000));

        }
    }
}
