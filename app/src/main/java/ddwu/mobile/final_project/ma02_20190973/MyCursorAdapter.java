package ddwu.mobile.final_project.ma02_20190973;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

public class MyCursorAdapter extends CursorAdapter {
    LayoutInflater inflater;
    Cursor cursor;

    public MyCursorAdapter(Context context, int layout, Cursor c) {
        // 가능하면 생성자에서 layout도 전달받아서 사용할 수 있도록 정의하는게 좋다.
        super(context, c, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        cursor = c;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // inflater를 사용하여 ListView 내부에 표시할 view를 inflation
        View listItemLayout = inflater.inflate(R.layout.listview_layout, parent, false);
        return listItemLayout;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // ListView 내부에 표시할 뷰에 cursor의 데이터 연결
        // 이 부분은 ViewHolder를 사용하면 더 효율적인 코드가 된다.
        File imgFile = new File(cursor.getString(cursor.getColumnIndex(ReviewDBHelper.COL_IMG)));

        TextView list_title = (TextView)view.findViewById(R.id.tv_name);
        TextView list_hospital = (TextView)view.findViewById(R.id.tv_address);
        TextView list_date = (TextView)view.findViewById(R.id.tv_phone);
        ImageView list_img = (ImageView)view.findViewById(R.id.tv_img);
        list_title.setText(cursor.getString(cursor.getColumnIndex(ReviewDBHelper.COL_TITLE)));
        list_hospital.setText(cursor.getString(cursor.getColumnIndex(ReviewDBHelper.COL_HOSPITAL)));
        list_date.setText(cursor.getString(cursor.getColumnIndex(ReviewDBHelper.COL_DATE)));
        list_img.setImageBitmap(BitmapFactory.decodeFile(imgFile.getAbsolutePath()));
    }
}
