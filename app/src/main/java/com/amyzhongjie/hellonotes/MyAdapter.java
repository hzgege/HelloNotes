package com.amyzhongjie.hellonotes;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter{

    private Context context;
    private Cursor cursor;
    //private RelativeLayout layout;
    private LayoutInflater inflater;

    public MyAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return cursor.getCount();
    }

    @Override
    public Object getItem(int position) {
        return cursor.getPosition();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        ViewHolder viewHolder;
        if(view == null){
            viewHolder = new ViewHolder();
            view = (RelativeLayout) inflater.inflate(R.layout.cell, null);
            viewHolder.contenttv = (TextView)view.findViewById(R.id.list_content);
            viewHolder.timetv = (TextView)view.findViewById(R.id.list_time);
            viewHolder.imgiv = (ImageView)view.findViewById(R.id.list_img);
            viewHolder.videoiv = (ImageView)view.findViewById(R.id.list_video);
            viewHolder.contentTime = (TextView) view.findViewById(R.id.content_time);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }

        //获取内容
        //在Android 查询数据是通过Cursor 类来实现的。
        // 当我们使用 SQLiteDatabase.query()方法时，就会得到Cursor对象， Cursor所指向的就是每一条数据
        cursor.moveToPosition(position);
        String content = cursor.getString(cursor.getColumnIndex("content"));
        String time = cursor.getString(cursor.getColumnIndex("time"));
        String url = cursor.getString(cursor.getColumnIndex("path"));
        String urlvideo = cursor.getString(cursor.getColumnIndex("video"));
        String cTime = cursor.getString(cursor.getColumnIndex("hms"));

        viewHolder.contenttv.setText(content);     //显示文字内容
        viewHolder.timetv.setText(time);           //显示时间
        viewHolder.contentTime.setText(cTime);
        viewHolder.imgiv.setImageBitmap(getImageThumbnail(url, 200,200));          //显示图片缩略图
        viewHolder.videoiv.setImageBitmap(getVideoThumbnail(urlvideo, 200,200,     //显示视频缩略图
                MediaStore.Images.Thumbnails.MICRO_KIND));
        return view;
    }

    //避免重复的findid
    class ViewHolder{
        TextView contenttv;
        TextView timetv;
        ImageView imgiv;
        ImageView videoiv;
        TextView contentTime;
    }


    //获取图片缩略图
    public Bitmap getImageThumbnail(String uri, int width, int height){
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmap = BitmapFactory.decodeFile(uri, options);
        options.inJustDecodeBounds = false;

        int beWidth = options.outWidth/width;
        int beHeight = options.outHeight/height;
        int be = 1;
        if (beWidth < beHeight){
            be = beWidth;
        }
        else {
            be = beHeight;
        }

        if(be <= 0){
            be = 1;
        }
        options.inSampleSize = be;
        bitmap = BitmapFactory.decodeFile(uri, options);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    //获取视频缩略图
    private Bitmap getVideoThumbnail(String uri, int width, int height, int kind){
        Bitmap bitmap = null;
        bitmap = ThumbnailUtils.createVideoThumbnail(uri, kind);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

}
