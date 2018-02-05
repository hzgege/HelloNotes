package com.amyzhongjie.hellonotes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class NotesDB extends SQLiteOpenHelper{

    public static final String TABLE_NAME = "notes";    //表名
    public static final String CONTENT = "content";     //内容
    public static final String PATH = "path";           //图片
    public static final String VIDEO = "video";         //视频
    public static final String ID = "_id";              //数据库ID
    public static final String JUDGE = "judge";         //判断是哪种内容
    public static final String TIME = "time";           //当前创建内容的时间
    public static final String HMS = "hms";
    public static final String CREATE_T = "CREATE TABLE " + TABLE_NAME + " ("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + JUDGE + " INTEGER NOT NULL,"
            + CONTENT + " TEXT NOT NULL,"
            + PATH + " TEXT,"
            + VIDEO + " TEXT,"
            + HMS + " TEXT Not NULL,"
            + TIME + " TEXT NOT NULL)";

    public static final String Path = "path";    //图片背景那个的路径
    public static final String CreateImgPath = "CREATE TABLE imgpath ("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + Path + " TEXT NOT NULL)";

    /*
     * 1.name是数据库名
     * 2.factory是允许查询时返回一个自定义的cursor,一般null
     * 3.version是版本
     */
    public NotesDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_T);
        sqLiteDatabase.execSQL(CreateImgPath);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists notes");
        sqLiteDatabase.execSQL("drop table if exists imgpath");
        onCreate(sqLiteDatabase);
    }
}
