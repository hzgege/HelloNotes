package com.amyzhongjie.hellonotes;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import UI.SettingActivity;
import base.ThemeActivity;
import utils.ColorPaletteUtils;

public class MainActivity extends ThemeActivity {

    private SwipeMenuListView listView;                //SwipeMenuListView对象,左滑删除

    private Toolbar toolbar;            //工具栏
    private DrawerLayout drawerLayout;
    private Intent i;   //标识符
    private ActionBarDrawerToggle mToggle;  //抽屉开关
    private MyAdapter adapter;          //创建适配器对象
    private NotesDB notesDB;            //创建数据库对象
    private SQLiteDatabase dbReader;    //获取对象对数据库进行操作
    private Cursor cursor;              //查询数据库的操作对象
    private FloatingActionButton rightLowerButton;
    private FloatingActionMenu rightLowerMenu;
    private SubActionButton buttonText, buttonImg, buttonVideo;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_layout);
        findView();//设置工具栏
        initDrawlayout();
        initView();
        rightLowerButton();
    }

    //工具栏
    private void findView() {
        toolbar = (Toolbar) findViewById(R.id.mytoolbar);
        toolbar.setTitle("HelloNotes");
        setSupportActionBar(toolbar);
    }

    public void initDrawlayout(){

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navView = (NavigationView)findViewById(R.id.nav_view);
        mToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        mToggle.syncState();
        drawerLayout.addDrawerListener(mToggle);//添加监听

        //设置默认的
        navView.setCheckedItem(R.id.nav_all);
        //添加监听
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawers();
                switch (item.getItemId()){
                    case R.id.nav_all:
                        selectDB();
                        break;
                    case R.id.nav_text:
                        selectDBFenLei(0);
                        break;
                    case R.id.nav_img:
                        selectDBFenLei(1);
                        break;
                    case R.id.nav_video:
                        selectDBFenLei(2);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    /*
        1.onCreateOptionsMenu 创建菜单
        2.onOptionsItemSelected 选择菜单
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.option_1:
                //换肤
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
                break;
        }
        return true;
       }

    private void updateNowTheme(){
        toolbar.setPopupTheme(getPopupToolbarStyle());
        toolbar.setBackgroundColor(getPrimaryColor());
        toolbar.setTitle(R.string.appTitle);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (isTranslucentStatusBar()) {
                getWindow().setStatusBarColor(ColorPaletteUtils.getObscuredColor(getPrimaryColor()));
            } else {
                getWindow().setStatusBarColor(getPrimaryColor());
            }
            getWindow().setNavigationBarColor(isNavigationBarColored() ? getPrimaryColor() : ContextCompat.getColor(getApplicationContext(), R.color.md_black_1000));
        }
        buttonText.setBackgroundTintList(ColorStateList.valueOf(getPrimaryColor()));
        buttonImg.setBackgroundTintList(ColorStateList.valueOf(getPrimaryColor()));
        buttonVideo.setBackgroundTintList(ColorStateList.valueOf(getPrimaryColor()));
        rightLowerButton.setBackgroundTintList(ColorStateList.valueOf(getPrimaryColor()));
    }

    public void initView(){
        listView = (SwipeMenuListView)findViewById(R.id.listView);
        // set creator
        listView.setMenuCreator(creator);
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        // delete
                        SQLiteDatabase dbWriter;
                        dbWriter = notesDB.getWritableDatabase();
                        cursor.moveToPosition(position);
                        int id = cursor.getInt(cursor.getColumnIndex(NotesDB.ID));
                        dbWriter.delete(NotesDB.TABLE_NAME, "_id=" + id,null);
                        selectDB();
                        adapter.notifyDataSetChanged();
                        break;
                }
                return false;
            }
        });
        /*
         * version指比1大就可以让onUpgrade()执行,删掉表再创建
         */
        notesDB = new NotesDB(this, "notes.db", null, 7);
        //获取对象对数据库进行操作
        dbReader = notesDB.getReadableDatabase();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                cursor.moveToPosition(position);
                Intent i  = new Intent(MainActivity.this, SelectOne.class);
                i.putExtra(NotesDB.ID, cursor.getInt(cursor.getColumnIndex(NotesDB.ID)));
                i.putExtra(NotesDB.CONTENT, cursor.getString(cursor.getColumnIndex(NotesDB.CONTENT)));
                i.putExtra(NotesDB.TIME, cursor.getString(cursor.getColumnIndex(NotesDB.TIME)));
                i.putExtra(NotesDB.PATH, cursor.getString(cursor.getColumnIndex(NotesDB.PATH)));
                i.putExtra(NotesDB.VIDEO, cursor.getString(cursor.getColumnIndex(NotesDB.VIDEO)));
                String time = cursor.getString(cursor.getColumnIndex(NotesDB.TIME)) +"  "+
                        cursor.getString(cursor.getColumnIndex(NotesDB.HMS));
                i.putExtra("time", time);
                startActivity(i);
            }
        });
    }

    SwipeMenuCreator creator = new SwipeMenuCreator() {

        @Override
        public void create(SwipeMenu menu) {

            SwipeMenuItem deleteItem = new SwipeMenuItem(
                    getApplicationContext());
            //背景
            deleteItem.setBackground(R.color.deleteColor);
            //宽
            deleteItem.setWidth(250);
            //图标
            deleteItem.setIcon(R.mipmap.delete);
            menu.addMenuItem(deleteItem);
        }
    };


    //查询数据
    private void selectDB(){
        /*
         * 1.只传表名，即查所有的
         * 2.+ " order by id desc"从大到小排列
         */
        String sql = " select * from " + NotesDB.TABLE_NAME + " order by " + NotesDB.ID + " desc";
        cursor = dbReader.rawQuery( sql, null);
        adapter = new MyAdapter(this, cursor);
        listView.setAdapter(adapter);
    }

    //分类查询
    private void selectDBFenLei(int style){
        String sql = " select * from " + NotesDB.TABLE_NAME + " where " + NotesDB.JUDGE + " = " + style + " order by " + NotesDB.ID + " desc";
        cursor = dbReader.rawQuery( sql, null);
        adapter = new MyAdapter(this, cursor);
        listView.setAdapter(adapter);
    }


    @Override
    public void onResume() {
        super.onResume();
        updateNowTheme();
        rightLowerMenu.close(true);
        selectDB();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            Intent i= new Intent(Intent.ACTION_MAIN);  //主启动，不期望接收数据

            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);       //新的activity栈中开启，或者已经存在就调到栈前

            i.addCategory(Intent.CATEGORY_HOME);            //添加种类，为设备首次启动显示的页面

            startActivity(i);
        }
        return super.onKeyDown(keyCode, event);
    }

    // 右下角的菜单
    private void rightLowerButton() {
        final ImageView fabIconNew = new ImageView(this);
        // 设置菜单按钮Button的图标
        fabIconNew.setImageResource(R.mipmap.add);
        rightLowerButton = new FloatingActionButton.Builder(
                this)
                .setContentView(fabIconNew)
                .build();

        SubActionButton.Builder rLSubBuilder = new SubActionButton.Builder(this);
        ImageView rlIcon1 = new ImageView(this);
        ImageView rlIcon2 = new ImageView(this);
        ImageView rlIcon3 = new ImageView(this);

        rlIcon1.setImageResource(R.mipmap.float_text);
        rlIcon2.setImageResource(R.mipmap.float_img);
        rlIcon3.setImageResource(R.mipmap.float_video);

        buttonText = rLSubBuilder.setContentView(rlIcon1).build();
        buttonImg = rLSubBuilder.setContentView(rlIcon2).build();
        buttonVideo = rLSubBuilder.setContentView(rlIcon3).build();

        rightLowerMenu = new FloatingActionMenu.Builder(
                this)
                .addSubActionView(buttonText)
                .addSubActionView(buttonImg)
                .addSubActionView(buttonVideo)
                .attachTo(rightLowerButton).build();

        //状态改变
        rightLowerMenu.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {

            @Override
            public void onMenuOpened(FloatingActionMenu menu) {
                // 逆时针旋转90°
                fabIconNew.setRotation(0);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(
                        View.ROTATION, -90);

                ObjectAnimator animation = ObjectAnimator
                        .ofPropertyValuesHolder(fabIconNew, pvhR);
                animation.start();
            }

            @Override
            public void onMenuClosed(FloatingActionMenu menu) {
                // 顺时针旋转90°
                fabIconNew.setRotation(-90);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(
                        View.ROTATION, 0);
                ObjectAnimator animation = ObjectAnimator
                        .ofPropertyValuesHolder(fabIconNew, pvhR);
                animation.start();

            }
        });


        /*
         * 为3个button添加监听
         */
        buttonText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i = new Intent(MainActivity.this, AddContent.class);     //传递标识符告知是图片、文字还是视频
                i.putExtra("flag" , "1");
                startActivity(i);
            }
        });

        buttonImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //传递标识符告知是图片、文字还是视频
                i = new Intent(MainActivity.this, AddContent.class);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                // icon
                builder.setIcon(R.mipmap.choosep);
                builder.setTitle("请选择图片来源");
                //指定列表
                final String[] pic = {"拍照", "相册"};
                //设置列表选择项
                builder.setSingleChoiceItems(pic, -1, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        switch (which){
                            case 0://相机
                                i.putExtra("flag" , "2.1");
                                startActivity(i);
                                dialog.dismiss();
                                break;
                            case 1://相册
                                i.putExtra("flag" , "2.2");
                                startActivity(i);
                                dialog.dismiss();
                                break;
                        }
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.show();
            }
        });

        buttonVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i = new Intent(MainActivity.this, AddContent.class);     //传递标识符告知是图片、文字还是视频
                i.putExtra("flag" , "3");
                startActivity(i);
            }
        });
    }
}
