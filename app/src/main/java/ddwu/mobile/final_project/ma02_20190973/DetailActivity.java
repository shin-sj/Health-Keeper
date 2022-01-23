package ddwu.mobile.final_project.ma02_20190973;


import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
//import android.widget.Toolbar;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class DetailActivity extends AppCompatActivity {

    EditText etName;
    EditText etPhone;
    EditText etAddress;
    String name;
    String phone;
    String address;
    HospitalDBHelper helper;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.around_detail);

        Intent intent = getIntent();

        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);

        etName.setText(intent.getStringExtra("name"));
        etPhone.setText(intent.getStringExtra("phone"));
        etAddress.setText(intent.getStringExtra("address"));

        name = etName.getText().toString();
        phone = etPhone.getText().toString();
        address = etAddress.getText().toString();

        helper = new HospitalDBHelper(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(""); //타이틀 없음
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.call_menu, menu);
        return true;
    }

    //  메뉴-전화걸기
    public void onCallClick(MenuItem item) {
        //메소드 체이닝(Method Chaining) 객체의 멤버 메소드가 수행 후 객체 자신을 반환할 경우 메소드를 연속적으로 호출
        String tel = "tel:"+phone;
        startActivity(new Intent("android.intent.action.DIAL", Uri.parse(tel)));

    }
    
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.btn_review:
                intent = new Intent(this, AddActivity.class);
                intent.putExtra("title", name); //병원명 전달
                break;
            case R.id.btn_alarm:
                intent = new Intent(this, MyAlarm.class);
                break;
            case R.id.btn_close:
                finish();
                break;
            case R.id.btn_scrap:
                SQLiteDatabase db = helper.getWritableDatabase();

                ContentValues row = new ContentValues();
                row.put(HospitalDBHelper.COL_NAME, etName.getText().toString());
                row.put(HospitalDBHelper.COL_PHONE, etPhone.getText().toString());
                row.put(HospitalDBHelper.COL_ADDRESS, etAddress.getText().toString());

                long result = db.insert(HospitalDBHelper.TABLE_NAME, null, row);
                helper.close();

                String msg = result > 0 ? "스크랩 성공" : "스크랩 실패!";
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                finish();
                break;

        }
        if (intent != null) startActivity(intent);
    }
}

