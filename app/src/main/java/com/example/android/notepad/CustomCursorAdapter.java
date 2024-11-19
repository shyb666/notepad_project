package com.example.android.notepad;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;

import com.example.asd.R;
//重回写适配器，使其能够显示图片
public class CustomCursorAdapter extends SimpleCursorAdapter {

    private Context mContext;
    private int mLayout;
    private Cursor mCursor;
    private LayoutInflater mInflater;

    // 构造函数，初始化必要的组件
    public CustomCursorAdapter(Context context, int layout, Cursor cursor, String[] from, int[] to) {
        super(context, layout, cursor, from, to);
        mContext = context;
        mLayout = layout;
        mCursor = cursor;
        mInflater = LayoutInflater.from(context);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // 创建并返回一个新的视图
        return mInflater.inflate(mLayout, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // 调用super.bindView()来绑定基本的数据
        super.bindView(view, context, cursor);

        // 获取ImageView
        ImageView imageView = view.findViewById(R.id.note_type);

        // 获取用于决定图片的条件列的值
        int columnIndex = cursor.getColumnIndexOrThrow(NotePad.Notes.COLUMN_TYPE);
        String value = cursor.getString(columnIndex); // 假设这一列是整型，根据实际情况调整
if(value==null){
    value="work";
}



        // 使用switch语句来设置不同的图片
        switch (value) {
            case "work":
                imageView.setImageResource(R.drawable.work);
                break;
            case "study":
                imageView.setImageResource(R.drawable.study);
                break;
            case "other":
                imageView.setImageResource(R.drawable.other);
                break;
            case "daylylife":
                imageView.setImageResource(R.drawable.life);
                break;
            case "privacy":
                imageView.setImageResource(R.drawable.privacy);
                break;
            // 添加更多的case来处理其他值
            default:
                imageView.setImageResource(R.drawable.item_background);
                break;
        }
    }
}
