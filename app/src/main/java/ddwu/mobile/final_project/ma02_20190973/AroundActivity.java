package ddwu.mobile.final_project.ma02_20190973;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import noman.googleplaces.NRPlaces;
import noman.googleplaces.PlaceType;
import noman.googleplaces.PlacesException;
import noman.googleplaces.PlacesListener;
import androidx.appcompat.widget.Toolbar;

public class AroundActivity extends AppCompatActivity implements OnMapReadyCallback {

    final static String TAG = "AroundActivity";
    final static int PERMISSION_REQ_CODE = 100;

    private EditText editText; //실제 주소
    private LatLng latLng;
    private String lat;
    private String lng;
    private LocationManager locManager;
    private String bestProvider;
    Toolbar toolbar;
    Intent intent = null;

    private LatLngResultReceiver latLngResultReceiver;

    /*UI*/
    private GoogleMap mGoogleMap;
    private MarkerOptions markerOptions;


    /*DATA*/
    private PlacesClient placesClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.around_search);

        editText = (EditText)findViewById(R.id.et_name_search);
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        bestProvider = LocationManager.PASSIVE_PROVIDER;

//      IntentService가 생성하는 결과 수신용 ResultReceiver
        latLngResultReceiver = new LatLngResultReceiver(new Handler());

        // 권한 확인 후 권한이 있을 경우 맵 로딩 실행
        if (checkPermission()) {
            mapLoad();
        };

        Location lastLocation = locManager.getLastKnownLocation(bestProvider);

        if (lastLocation == null) {
            latLng = new LatLng(37.601822402047915, 127.04156039013381 );
        }
        else {
            latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());

        }

        // Places 초기화 및 클라이언트 생성
        Places.initialize(getApplicationContext(), getString(R.string.api_key));
        placesClient = Places.createClient(this); //placesClient 객체는 Place API 요청을 위한 것


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(""); //타이틀 없음
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        Log.d(TAG, "Map ready");

        // TODO: 맵 로딩 후 초기에 해야 할 작업 구현
        markerOptions = new MarkerOptions();
//        mGeoDataClient = Places.getGeoDataClient(MainActivity.this);

        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(final Marker marker) {
                String placeId = marker.getTag().toString();    // 마커의 setTag() 로 저장한 Place ID 확인

                List<Place.Field> placeFields       // 상세정보로 요청할 정보의 유형 지정
                        = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.PHONE_NUMBER, Place.Field.ADDRESS);

                FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields).build();    // 요청 생성

                // 요청 처리 및 요청 성공/실패 리스너 지정
                placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                    @Override                    // 요청 성공 시 처리 리스너 연결
                    public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {  // 요청 성공 시
                        Place place = fetchPlaceResponse.getPlace();
                        Log.i(TAG, "Place found: " + place.getName());  // 장소 명 확인 등
                        Log.i(TAG, "Phone: " + place.getPhoneNumber());
                        Log.i(TAG, "Address: " + place.getAddress());

                        Intent intent = new Intent(AroundActivity.this, DetailActivity.class);
                        intent.putExtra("name", place.getName());
                        intent.putExtra("phone", place.getPhoneNumber());
                        intent.putExtra("address", place.getAddress());
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {   // 요청 실패 시 처리 리스너 연결
                    @Override
                    public void onFailure(@NonNull Exception exception) {   // 요청 실패 시
                        if (exception instanceof ApiException) {
                            ApiException apiException = (ApiException) exception;
                            int statusCode = apiException.getStatusCode();  // 필요 시 확인
                            Log.e(TAG, "Place not found: " + exception.getMessage());
                        }
                    }
                });
            }
        });
    }



    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btn_now:
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
                searchStart(PlaceType.HOSPITAL, latLng);
                break;
            case R.id.btn_name_search:
                startLatLngService();
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
                searchStart(PlaceType.HOSPITAL, latLng);
                break;
        }
    }


    private void searchStart(String type, LatLng latLng) {
        new NRPlaces.Builder().listener(placesListener)
                .key(getString(R.string.api_key))
                .latlng(latLng.latitude, latLng.longitude)
                .radius(5000)
                .type(type)
                .build()
                .execute();
    }


    /* 주소 → 위도/경도 변환 IntentService 실행 */
    private void startLatLngService() {
        Intent intent = new Intent(this, FetchLatLngIntentService.class);
        String address = editText.getText().toString();

        intent.putExtra(Constants.RECEIVER, latLngResultReceiver);
        intent.putExtra(Constants.ADDRESS_DATA_EXTRA, address);

        startService(intent);
    }

    /* 주소 → 위도/경도 변환 ResultReceiver */
    class LatLngResultReceiver extends ResultReceiver {
        public LatLngResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            ArrayList<LatLng> latLngList = null;

            if (resultCode == Constants.SUCCESS_RESULT) {
                if (resultData == null)
                    return;

                latLngList = (ArrayList<LatLng>) resultData.getSerializable(Constants.RESULT_DATA_KEY);

                if (latLngList == null) {
                    lat = null;
                    lng = null;
                } else {
                    LatLng mlatlng = latLngList.get(0);
                    latLng = new LatLng(mlatlng.latitude, mlatlng.longitude);
                }
            } else {
                editText.setText(getString(R.string.no_address_found));
            }
        }
    }


    PlacesListener placesListener = new PlacesListener() {
        @Override
        public void onPlacesSuccess(final List<noman.googleplaces.Place> places) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    mGoogleMap.clear();     // 기존의 마커 삭제

                    for (noman.googleplaces.Place place : places) {
                        markerOptions.title(place.getName());
                        markerOptions.position(new LatLng(place.getLatitude(), place.getLongitude()));
                        Marker newMarker = mGoogleMap.addMarker(markerOptions);
                        newMarker.setTag(place.getPlaceId());
                        Log.d(TAG, "ID: " + place.getPlaceId());
                    }
                }
            });
        }
        @Override
        public void onPlacesFailure(PlacesException e) {}
        @Override
        public void onPlacesStart() {}
        @Override
        public void onPlacesFinished() {}
    };



    /*구글맵을 멤버변수로 로딩*/
    private void mapLoad() {
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /* 필요 permission 요청 */
    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION },
                        PERMISSION_REQ_CODE);
                return false;
            }
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSION_REQ_CODE) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // 퍼미션을 획득하였을 경우 맵 로딩 실행
                mapLoad();
            } else {
                // 퍼미션 미획득 시 액티비티 종료
                Toast.makeText(this, "앱 실행을 위해 권한 허용이 필요함", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.info_menu, menu);
        return true;
    }

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
    //    개발자 소개
    public void onMenuDevClick (MenuItem item) {
        //메소드 체이닝(Method Chaining) 객체의 멤버 메소드가 수행 후 객체 자신을 반환할 경우 메소드를 연속적으로 호출
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        ConstraintLayout orderLayout = (ConstraintLayout) View.inflate(this, R.layout.order_layout, null);
        finish();
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
    }
}
