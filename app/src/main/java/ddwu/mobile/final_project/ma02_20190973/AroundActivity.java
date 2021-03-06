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

    private EditText editText; //?????? ??????
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

//      IntentService??? ???????????? ?????? ????????? ResultReceiver
        latLngResultReceiver = new LatLngResultReceiver(new Handler());

        // ?????? ?????? ??? ????????? ?????? ?????? ??? ?????? ??????
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

        // Places ????????? ??? ??????????????? ??????
        Places.initialize(getApplicationContext(), getString(R.string.api_key));
        placesClient = Places.createClient(this); //placesClient ????????? Place API ????????? ?????? ???


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(""); //????????? ??????
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        Log.d(TAG, "Map ready");

        // TODO: ??? ?????? ??? ????????? ?????? ??? ?????? ??????
        markerOptions = new MarkerOptions();
//        mGeoDataClient = Places.getGeoDataClient(MainActivity.this);

        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(final Marker marker) {
                String placeId = marker.getTag().toString();    // ????????? setTag() ??? ????????? Place ID ??????

                List<Place.Field> placeFields       // ??????????????? ????????? ????????? ?????? ??????
                        = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.PHONE_NUMBER, Place.Field.ADDRESS);

                FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields).build();    // ?????? ??????

                // ?????? ?????? ??? ?????? ??????/?????? ????????? ??????
                placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                    @Override                    // ?????? ?????? ??? ?????? ????????? ??????
                    public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {  // ?????? ?????? ???
                        Place place = fetchPlaceResponse.getPlace();
                        Log.i(TAG, "Place found: " + place.getName());  // ?????? ??? ?????? ???
                        Log.i(TAG, "Phone: " + place.getPhoneNumber());
                        Log.i(TAG, "Address: " + place.getAddress());

                        Intent intent = new Intent(AroundActivity.this, DetailActivity.class);
                        intent.putExtra("name", place.getName());
                        intent.putExtra("phone", place.getPhoneNumber());
                        intent.putExtra("address", place.getAddress());
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {   // ?????? ?????? ??? ?????? ????????? ??????
                    @Override
                    public void onFailure(@NonNull Exception exception) {   // ?????? ?????? ???
                        if (exception instanceof ApiException) {
                            ApiException apiException = (ApiException) exception;
                            int statusCode = apiException.getStatusCode();  // ?????? ??? ??????
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


    /* ?????? ??? ??????/?????? ?????? IntentService ?????? */
    private void startLatLngService() {
        Intent intent = new Intent(this, FetchLatLngIntentService.class);
        String address = editText.getText().toString();

        intent.putExtra(Constants.RECEIVER, latLngResultReceiver);
        intent.putExtra(Constants.ADDRESS_DATA_EXTRA, address);

        startService(intent);
    }

    /* ?????? ??? ??????/?????? ?????? ResultReceiver */
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

                    mGoogleMap.clear();     // ????????? ?????? ??????

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



    /*???????????? ??????????????? ??????*/
    private void mapLoad() {
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /* ?????? permission ?????? */
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
                // ???????????? ??????????????? ?????? ??? ?????? ??????
                mapLoad();
            } else {
                // ????????? ????????? ??? ???????????? ??????
                Toast.makeText(this, "??? ????????? ?????? ?????? ????????? ?????????", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.info_menu, menu);
        return true;
    }

//    //????????????
//    public void onRecordClick (MenuItem item) {
//        intent = new Intent(this, AllActivity.class);
//        startActivity(intent);
//    }
//    //???????????????
//    public void onScrapClick (MenuItem item) {
//        intent = new Intent(this, ScrapActivity.class);
//        startActivity(intent);
//    }
//    //????????????
//    public void onSearchClick (MenuItem item) {
//        intent = new Intent(this, L_Activity.class);
//        startActivity(intent);
//    }
    //    ????????? ??????
    public void onMenuDevClick (MenuItem item) {
        //????????? ?????????(Method Chaining) ????????? ?????? ???????????? ?????? ??? ?????? ????????? ????????? ?????? ???????????? ??????????????? ??????
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        ConstraintLayout orderLayout = (ConstraintLayout) View.inflate(this, R.layout.order_layout, null);

        builder.setTitle("????????? ??????")
                .setView(orderLayout)
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("??????", null)

                .setCancelable(false) //???????????? ????????? ?????????
                .show();

    }

    //   ??? ??????
    public void onMenuExitClick (MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        ConstraintLayout orderLayout = (ConstraintLayout) View.inflate(this, R.layout.order_layout, null);
        finish();
//        builder.setTitle("??? ??????")
//                .setMessage("?????? ?????????????????????????")
//                .setPositiveButton("??????", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        finish(); //activity ?????????.
//                    }
//                })
//                .setNegativeButton("??????", null)
//                .setCancelable(false) //???????????? ????????? ?????????
//                .show();
    }
}
