package ddwu.mobile.final_project.ma02_20190973;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class ScrapActivity extends Activity {
    ListView list_Info = null;
    HospitalDBHelper helper;
    Cursor cursor;
    MyCursorAdapter2 MyCursorAdapter2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrap);
        list_Info = (ListView)findViewById(R.id.lvScrap);

        helper = new HospitalDBHelper(this);

        MyCursorAdapter2 = new MyCursorAdapter2(this, R.layout.listview_layout2, null);
        list_Info.setAdapter(MyCursorAdapter2);

        //삭제
        list_Info.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int pos, final long id) {
//                삭제 기능 작성 - 삭제 확인 대화상자 출력
                final int position = pos;

                SQLiteDatabase db = helper.getWritableDatabase();
                cursor = db.rawQuery("SELECT * FROM "
                        + helper.TABLE_NAME + " WHERE _id='" + id + "';", null);

                while (cursor.moveToNext()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ScrapActivity.this);
                    builder.setTitle("스크랩취소");
                    builder.setMessage(cursor.getString(cursor.getColumnIndex(helper.COL_NAME)) + "스크랩을 취소하겠습니까?");
                    builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
//                        DB 삭제 수행
                            SQLiteDatabase db = helper.getWritableDatabase();
                            String whereClause = helper.COL_ID + "=?";
                            String[] whereArgs = new String[]{String.valueOf(id)};
                            db.delete(helper.TABLE_NAME, whereClause, whereArgs);
                            helper.close();

//                        새로운 DB 내용으로 리스트뷰 갱신
                            readAllContacts();

                            Toast.makeText(ScrapActivity.this, "스크랩이 취소되었습니다", Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setNegativeButton("취소", null);
                    builder.show();
                }
                return true;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        readAllContacts();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        cursor 사용 종료
        if (cursor != null) cursor.close();
    }

    private void readAllContacts() {
//        DB에서 데이터를 읽어와 Adapter에 설정
        SQLiteDatabase db = helper.getReadableDatabase();
        cursor = db.rawQuery("select * from " + HospitalDBHelper.TABLE_NAME, null);

        MyCursorAdapter2.changeCursor(cursor);
        helper.close();
    }
}
