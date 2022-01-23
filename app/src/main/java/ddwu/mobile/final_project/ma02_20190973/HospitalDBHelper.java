package ddwu.mobile.final_project.ma02_20190973;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.content.ContentValues.TAG;

public class HospitalDBHelper  extends SQLiteOpenHelper {
    private final static String DB_NAME = "hospital_db";
    public final static String TABLE_NAME = "hospital_table";
    public final static String COL_ID = "_id";
    public final static String COL_NAME = "name";
    public final static String COL_PHONE = "phone";
    public final static String COL_ADDRESS = "address";


    public HospitalDBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "CREATE TABLE " + TABLE_NAME + " (" + COL_ID + " integer primary key autoincrement, " +
                COL_NAME + " TEXT, " + COL_PHONE + " TEXT, " + COL_ADDRESS + " TEXT)";
        Log.d(TAG, sql);
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
