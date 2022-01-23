package ddwu.mobile.final_project.ma02_20190973;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddActivity extends Activity{
    final static String TAG = "AddActivity";
    final static int REQ_CODE = 100;

    EditText etTitle;
    EditText etHospital;
    EditText etDate;
    EditText etSymptom;
    EditText etContents;
    ImageView mImageView;
    String mCurrentPhotoPath;

    ReviewDBHelper helper;

    private static final int REQUEST_TAKE_PHOTO = 200;  // 원본 이미지 사용 요청
    private File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        etTitle = findViewById(R.id.etTitle);
        etHospital = findViewById(R.id.etHospital);
        etDate = findViewById(R.id.etDate);
        etSymptom = findViewById(R.id.etSymptom);
        etContents = findViewById(R.id.etContents);
        mImageView = findViewById(R.id.image_add);

        //인텐트에서 꺼낸 값이 널이 아닐 경우에만 병원명 자동으로 넣어주고 아니면 직접 입력
        String str = getIntent().getStringExtra("title");
        if (str != null) {
            etHospital.setText(str);
        }

        helper = new ReviewDBHelper(this);

    }

    protected  void onPause() {
        super.onPause();
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btn_save:
                dispatchTakePictureIntent();
                break;
            case R.id.btn_insert:
//			DB 데이터 삽입 작업 수행
                SQLiteDatabase db = helper.getWritableDatabase();

                ContentValues row = new ContentValues();
                row.put(ReviewDBHelper.COL_TITLE, etTitle.getText().toString());
                row.put(ReviewDBHelper.COL_HOSPITAL, etHospital.getText().toString());
                row.put(ReviewDBHelper.COL_DATE, etDate.getText().toString());
                row.put(ReviewDBHelper.COL_SYMPTOM, etSymptom.getText().toString());
                row.put(ReviewDBHelper.COL_CONTENTS, etContents.getText().toString());
                row.put(ReviewDBHelper.COL_IMG, mCurrentPhotoPath);

                long result = db.insert(ReviewDBHelper.TABLE_NAME, null, row);
                helper.close();

                String msg = result > 0 ? "기록 추가 성공" : "기록 추가 실패!";
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

                finish();
                break;
            case R.id.btn_cancel1:
//			DB 데이터 삽입 취소 수행
                finish();
                break;
        }
    }

    //    캘린더
    public void onCalClick(View v) {
        Intent intent = new Intent(AddActivity.this, CalendarActivity.class);
        startActivityForResult(intent, REQ_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {  // 고화질 이미지 요청 결과 처리
            setPic();
        }
        //캘린더 처리
        if(requestCode == REQ_CODE) {
         switch (resultCode) {
            case RESULT_OK:
               String result = data.getStringExtra("result_data");
               etDate.setText(result);
               break;
            case RESULT_CANCELED:
               break;
         }
      }

    }

    /*원본 사진 요청*/
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 요청을 처리할 수 있는 카메라 앱이 있을 경우
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // 사진을 저장할 파일 생성
            photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            // 파일을 정상 생성하였을 경우
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "ddwu.mobile.final_project.ma02_20190973.fileprovider",    // 다른 앱에서 내 앱의 파일을 접근하기 위한 권한명 지정
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    /*이미지뷰에 지정할 수 있는 크기로 이미지를 변경하여 표시*/
    private void setPic() {
        // 이미지뷰의 가로/세로 크기 확인
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        // 원본 사진의 이미지 가로/세로 크기 확인
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;    // 메모리에 로딩하진 않고 실제 크기만 확인 시 true
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // 원본사진과 이미지뷰의 크기 비율 계산
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        // 지정한 옵션에 따라 비트맵을 메모리에 로딩
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mImageView.setImageBitmap(bitmap);
    }



    /*현재 시간을 사용한 파일명으로 앱전용의 외부저장소에 파일 정보 생성*/
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.i(TAG, "Created file path: " + mCurrentPhotoPath);
        return image;
    }

    //    캘린더
//    public void onCalendarClick(View v) {
//        Intent intent = new Intent(this, CalendarActivity.class);
//        startActivityForResult(intent, REQ_CODE);
//    }



}
