package ddwu.mobile.final_project.ma02_20190973;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity {

    Date currentTime = Calendar.getInstance().getTime();
    SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());
    SimpleDateFormat monthFormat = new SimpleDateFormat("MM", Locale.getDefault());
    SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
    String year = yearFormat.format(currentTime);
    String month = monthFormat.format(currentTime);
    String day = dayFormat.format(currentTime);

    String calDate = year + "-" + month + "-" + day;    //선택 안하면 현재 날짜

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        CalendarView calendar = (CalendarView) findViewById(R.id.calendar);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
//                선택된 날짜를 알려줌
                calDate = "";
                calDate += String.valueOf(year) + "-";

                if(String.valueOf(month+1).length() == 1)
                    calDate += "0" + String.valueOf(month+1);
                else
                    calDate += String.valueOf(month+1);

                calDate += "-";

                if(String.valueOf(dayOfMonth).length() == 1)
                    calDate += "0" + String.valueOf(dayOfMonth);
                else
                    calDate += String.valueOf(dayOfMonth);
            }
        });

    }

    public void onCalClick(View v) {
        switch (v.getId()) {
            case R.id.btnCalOk:
                Intent resultIntent = new Intent();
                resultIntent.putExtra("result_data", calDate);
                setResult(RESULT_OK, resultIntent);
                break;
            case R.id.btnCalCancel:
                setResult(RESULT_CANCELED);
                break;
        }
        finish();

    }
}