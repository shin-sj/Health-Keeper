package ddwu.mobile.final_project.ma02_20190973;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

public class AllActivity extends Activity{
    ListView list_Info = null;
    ReviewDBHelper helper;
    Cursor cursor;
    MyCursorAdapter myCursorAdapter;
    EditText editText;
    Intent intent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all);
        list_Info = (ListView)findViewById(R.id.lvReview);
        editText = (EditText)findViewById(R.id.et_name_search);

        helper = new ReviewDBHelper(this);

        myCursorAdapter = new MyCursorAdapter(this, R.layout.listview_layout, null);
        list_Info.setAdapter(myCursorAdapter);

        //수정
        list_Info.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                Intent intent = new Intent(AllActivity.this, UpdateActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });

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
                    AlertDialog.Builder builder = new AlertDialog.Builder(AllActivity.this);
                    builder.setTitle("기록 삭제");
                    builder.setMessage(cursor.getString(cursor.getColumnIndex(helper.COL_TITLE)) + "의 기록을 삭제하시겠습니까?");
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

                            Toast.makeText(AllActivity.this, "기록이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setNegativeButton("취소", null);
                    builder.show();
                }
                return true;
            }
        });

    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_name_search:
                if (editText.getText().toString().equals("")) {
                    Toast.makeText(this, "검색어를 입력해주세요", Toast.LENGTH_SHORT).show();
                    break;
                }
                else {
                    String result = "";

                    SQLiteDatabase db = helper.getWritableDatabase();
                    Cursor cursor = db.rawQuery("SELECT * FROM "
                            + ReviewDBHelper.TABLE_NAME + " WHERE title LIKE '%" + editText.getText().toString() + "%';", null);

                    while (cursor.moveToNext()) {
                        String title = cursor.getString(cursor.getColumnIndex(ReviewDBHelper.COL_TITLE));
                        String location = cursor.getString(cursor.getColumnIndex(ReviewDBHelper.COL_HOSPITAL));
                        String date = cursor.getString(cursor.getColumnIndex(ReviewDBHelper.COL_DATE));
                        String symptom = cursor.getString(cursor.getColumnIndex(ReviewDBHelper.COL_SYMPTOM));
                        String contents = cursor.getString(cursor.getColumnIndex(ReviewDBHelper.COL_CONTENTS));
                        result += "제목 : " + title + "\n위치 : " + location + "\n날짜 : " + date + "\n증상 : " + symptom + "\n진단 : " + contents + "\n\n";
                    }

                    if(result.equals("")) {
                        Toast.makeText(this, "해당 결과를 찾을 수 없습니다!", Toast.LENGTH_SHORT).show();
                        helper.close();
                        cursor.close();
                        break;
                    }
                    else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(AllActivity.this);
                        builder.setTitle("검색 결과");
                        builder.setMessage(result);

                        builder.show();
                        helper.close();
                        cursor.close();
                        break;
                    }
                }
        }
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
        cursor = db.rawQuery("select * from " + ReviewDBHelper.TABLE_NAME, null);

        myCursorAdapter.changeCursor(cursor);
        helper.close();
    }


//    menu
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.info_menu, menu);
        return true;
    }

    //진료기록
    public void onRecordClick (MenuItem item) {
        intent = new Intent(this, AllActivity.class);
        startActivity(intent);
    }
    //스크랩목록
    public void onScrapClick (MenuItem item) {
        intent = new Intent(this, ScrapActivity.class);
        startActivity(intent);
    }
    //병원검색
    public void onSearchClick (MenuItem item) {
        intent = new Intent(this, L_Activity.class);
        startActivity(intent);
    }
    //    개발자 소개
    public void onMenuDevClick (MenuItem item) {
        //메소드 체이닝(Method Chaining) 객체의 멤버 메소드가 수행 후 객체 자신을 반환할 경우 메소드를 연속적으로 호출
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        ConstraintLayout orderLayout = (ConstraintLayout) View.inflate(this, R.layout.order_layout, null);

        builder.setTitle("개발자 소개")
                .setView(orderLayout)
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("확인", null)

                .setCancelable(false) //돌아가기 눌러도 안닫힘
                .show();

    }

    //   앱 종료
    public void onMenuExitClick (MenuItem item) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        ConstraintLayout orderLayout = (ConstraintLayout) View.inflate(this, R.layout.order_layout, null);

        builder.setTitle("앱 종료")
                .setMessage("앱을 종료하시겠습니까?")
                .setPositiveButton("종료", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish(); //activity 닫는다.
                    }
                })
                .setNegativeButton("취소", null)
                .setCancelable(false) //돌아가기 눌러도 안닫힘
                .show();
    }


}
