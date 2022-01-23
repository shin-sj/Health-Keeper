package ddwu.mobile.final_project.ma02_20190973;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
//import android.widget.Toolbar;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.security.MessageDigest;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    public static final String TAG = "MainActivity";
    private SensorManager sensorManager;
    private View view;
    private long lastUpdate;

    Toolbar toolbar;
    Intent intent = null;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //바로 <map 에서 병원찾기> 페이지로??
        getAppKeyHash();

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lastUpdate = System.currentTimeMillis();

//        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle(""); //타이틀 없음

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);

//        drawerLayout = findViewById(R.id.drawer_layout);

    }

    private void getAppKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.e("Hash key : ", something);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("name not found", e.toString());
        }
    }


    public void onClick (View v) {
        Intent intent = null;

        switch (v.getId()) {
            //전체 리스트를 보여준다.
            case R.id.btn_all:
                intent = new Intent(this, AllActivity.class);
                break;
            //지도 검색
            case R.id.btn_search1:
                intent = new Intent(this, AroundActivity.class);
                break;
            //병원 검색
            case R.id.btn_search2:
                intent = new Intent(this, L_Activity.class);
                break;
            //스크랩 목록을 보여준다.
            case R.id.btn_scrap_list:
                intent = new Intent(this, ScrapActivity.class);
                break;
        }

        if (intent != null)
            startActivity(intent);
    }

//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.info_menu, menu);
//        return true;
//    }
//
//    //진료기록
//    public void onRecordClick (MenuItem item) {
//        intent = new Intent(this, AllActivity.class);
//        startActivity(intent);
//    }
//    //스크랩목록
//    public void onScrapClick (MenuItem item) {
//        intent = new Intent(this, ScrapActivity.class);
//        startActivity(intent);
//    }
//    //병원검색
//    public void onSearchClick (MenuItem item) {
//        intent = new Intent(this, L_Activity.class);
//        startActivity(intent);
//    }
//    //    개발자 소개
//    public void onMenuDevClick (MenuItem item) {
//        //메소드 체이닝(Method Chaining) 객체의 멤버 메소드가 수행 후 객체 자신을 반환할 경우 메소드를 연속적으로 호출
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        ConstraintLayout orderLayout = (ConstraintLayout) View.inflate(this, R.layout.order_layout, null);
//
//        builder.setTitle("개발자 소개")
//                .setView(orderLayout)
//                .setIcon(R.mipmap.ic_launcher)
//                .setPositiveButton("확인", null)
//
//                .setCancelable(false) //돌아가기 눌러도 안닫힘
//                .show();
//
//    }
//
//    //   앱 종료
//    public void onMenuExitClick (MenuItem item) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        ConstraintLayout orderLayout = (ConstraintLayout) View.inflate(this, R.layout.order_layout, null);
//
//        builder.setTitle("앱 종료")
//                .setMessage("앱을 종료하시겠습니까?")
//                .setPositiveButton("종료", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        finish(); //activity 닫는다.
//                    }
//                })
//                .setNegativeButton("취소", null)
//                .setCancelable(false) //돌아가기 눌러도 안닫힘
//                .show();
//    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(event);
        }

    }
    private void getAccelerometer(SensorEvent event) {
        float[] values = event.values;
        float x = values[0];
        float y = values[1];
        float z = values[2];
        double accelationSquareRoot =Math.sqrt( (x * x + y * y + z * z)
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH));

        if (accelationSquareRoot >= 5.0)
        {
            //call
            String tel = "tel:119";
            //         startActivity(new Intent("android.intent.action.CALL", Uri.parse(tel)));
            //         startActivity(new Intent("android.intent.action.CALL_PRIVILEGED"));
            startActivity(new Intent("android.intent.action.DIAL", Uri.parse(tel)));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {

        super.onPause();
        sensorManager.unregisterListener(this);
    }

}
