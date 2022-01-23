package ddwu.mobile.final_project.ma02_20190973;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MyAlarm extends AppCompatActivity {
    //startActivityForResult
    final static int REQ_CODE = 100;
    private static final String TAG = "AlarmTest";

    EditText etTest;
    String date2;
    EditText etMessage;
    String message2;

    //  private TimePicker timePicker;
    private AlarmManager alarmManager;
    private GregorianCalendar mCalender;

    private NotificationManager notificationManager;
    NotificationCompat.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        mCalender = new GregorianCalendar();

        //알람 날짜
        etTest = findViewById(R.id.etTest);

        alarmManager= (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);

        Log.v("HelloAlarmActivity", mCalender.getTime().toString());

        //알람설정 버튼
        Button button = (Button) findViewById(R.id.btnNoti);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date2 = etTest.getText().toString();
                Log.v(TAG, "===>setAlarm] date:" + date2 );

                setAlarm();
            }
        });
    }

    //    캘린더
    public void onCalClick(View v) {
        Intent intent = new Intent(MyAlarm.this, CalendarActivity.class);
        startActivityForResult(intent, REQ_CODE);
    }

    public void onAlarmClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.btnNoti:
                date2 = etTest.getText().toString();
                Log.v(TAG, "date:" + date2 );
                Log.v(TAG, "message:" + message2 );
                intent = new Intent(this, AlarmReceiver.class);
                intent.putExtra("message", message2);
                sendBroadcast(intent);

        }
    }

    public void onCancelClick(View v) {
        finish();
    }

    private void setAlarm() {
        //AlarmReceiver에 값 전달
        Intent receiverIntent = new Intent(MyAlarm.this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MyAlarm.this, 0, receiverIntent, 0);

       //  String from = "2021-12-23 06:36:00"; //임의로 날짜와 시간을 지정
        String from = etTest.getText().toString() + " 09:00:00"; //예약 당일 오전 9시에 알람
        //   Log.v(TAG, "test:" + from + "  date:" + etTest.getText().toString() );

        //날짜 포맷을 바꿔주는 소스코드
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date datetime = null;
        try {
            datetime = dateFormat.parse(from);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(datetime);

        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(),pendingIntent);

        //////////////////////
        Toast.makeText(this, "알람을 등록했습니다!", Toast.LENGTH_SHORT).show();
        finish();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQ_CODE) {
            switch (resultCode) {
                case RESULT_OK:
                    String result = data.getStringExtra("result_data");
                    etTest.setText(result);
                    break;
                case RESULT_CANCELED:
                    break;
            }
        }
    }

}