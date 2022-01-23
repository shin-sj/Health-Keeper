package ddwu.mobile.final_project.ma02_20190973;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.content.ContentValues.TAG;

public class ReviewDBHelper extends SQLiteOpenHelper {
    private final static String DB_NAME = "review_db";
    public final static String TABLE_NAME = "review_table";
    public final static String COL_ID = "_id";
    public final static String COL_TITLE = "title";
    public final static String COL_HOSPITAL = "hospital";
    public final static String COL_DATE = "date";
    public final static String COL_SYMPTOM = "symptom";
    public final static String COL_CONTENTS = "contents";
    public final static String COL_IMG = "img";

    public ReviewDBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME + " (" + COL_ID + " integer primary key autoincrement, " +
                COL_TITLE + " TEXT, " + COL_HOSPITAL + " TEXT, " + COL_DATE + " TEXT, " + COL_SYMPTOM + " TEXT, "
                + COL_CONTENTS + " TEXT, " + COL_IMG + " integer)";

        Log.d(TAG, sql);
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
