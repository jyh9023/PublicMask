package com.example.mypubllicmask2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import net.daum.mf.map.api.CalloutBalloonAdapter;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MapView.MapViewEventListener, MapView.POIItemEventListener{
    HashMap<String, teststores> stores_info = new HashMap<String, teststores>();
    ArrayList<storesInfo> store_arr = new ArrayList<storesInfo>();
    ViewGroup mapViewContainer;
    MapView mapView;
    Animation translateUp;
    Animation translateDown;
    LinearLayout menuContainer;
    TextView open_stat;
    TextView name;
    TextView remain_stat;
    TextView stock_at;
    TextView created_at;
    LocationManager lm;
    double x,y;
    Geocoder geocoder;

    @Override
    public void onMapViewInitialized(MapView mapView) {

    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {
        //menuContainer.startAnimation(translateUp);
        menuContainer.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {
        teststores detail_fragment = stores_info.get(mapPOIItem.getItemName());
        Log.d("아화나네", detail_fragment.stock_at + detail_fragment.remain_stat + detail_fragment.name);

        name.setText(detail_fragment.name);
        if(detail_fragment.remain_stat != null && detail_fragment.remain_stat.equals("plenty")) {
        remain_stat.setText("충분");
        remain_stat.setTextColor(Color.parseColor("#ABF200"));
        }
        else if(detail_fragment.remain_stat != null && detail_fragment.remain_stat.equals("some")) {
            remain_stat.setText("보통");
            remain_stat.setTextColor(Color.parseColor("#FFBB00"));
        }
        else if(detail_fragment.remain_stat != null && detail_fragment.remain_stat.equals("few")) {
            remain_stat.setText("부족");
            remain_stat.setTextColor(Color.parseColor("#FF0000"));
        }
        else if(detail_fragment.remain_stat != null && detail_fragment.remain_stat.equals("break")){
            remain_stat.setText("없음");
            remain_stat.setTextColor(Color.parseColor("#A6A6A6"));
        }
        else {
            remain_stat.setText("관련정보 없음");
            remain_stat.setTextColor(Color.parseColor("#A6A6A6"));
        }

        if(detail_fragment.remain_stat == null) {
            stock_at.setText("관련정보없음");
        }
        else {
            stock_at.setText("입고 시간 : " + detail_fragment.stock_at.substring(5,16));
        }
        if(detail_fragment.created_at == null) {
            created_at.setText("관련정보없음");
        }
        else {
            created_at.setText("업데이트 시간 : " + detail_fragment.created_at.substring(5,16));
        }

        menuContainer.setVisibility(View.VISIBLE);
        //menuContainer.startAnimation(translateDown);
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }

    class CustomCalloutBalloonAdapter implements CalloutBalloonAdapter {
        private final View mCalloutBalloon = null;

        public CustomCalloutBalloonAdapter() {
        }

        @Override
        public View getCalloutBalloon(MapPOIItem poiItem) {
            return mCalloutBalloon;
        }

        @Override
        public View getPressedCalloutBalloon(MapPOIItem poiItem) {
            return null;
        }
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mapView = new MapView(this);
        mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);
        translateUp = AnimationUtils.loadAnimation(this, R.anim.translate_up);
        translateDown = AnimationUtils.loadAnimation(this, R.anim.translate_down);
        menuContainer = findViewById(R.id.menuContainer);
        open_stat = findViewById(R.id.open_stat);
        name = findViewById(R.id.name);
        remain_stat = findViewById(R.id.remain_stat);
        stock_at = findViewById(R.id.stock_at);
        created_at = findViewById(R.id.created_at);
        geocoder = new Geocoder(this);
        if(AppHelper.requestQueue == null)
        {
            AppHelper.requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        Permissioncheck();

       // mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(37.4062280, 126.9170542), true);

        Button refresh  = findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stock_update_map();
            }
        });
        Button gps = findViewById(R.id.gps);
        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stock_update_gps();
            }
        });
        Button search = findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Search.class);
                startActivityForResult(intent, 101);
            }
        });


        if(AppHelper.requestQueue == null)
        {
            AppHelper.requestQueue = Volley.newRequestQueue(getApplicationContext());
        }



       // requestpage();
        //requestStockpage();



        translateUp.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                menuContainer.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mapView.setPOIItemEventListener(this);
        mapView.setMapViewEventListener(this);
        CustomCalloutBalloonAdapter customCalloutBalloonAdapter = new CustomCalloutBalloonAdapter();
        mapView.setCalloutBalloonAdapter(customCalloutBalloonAdapter);




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                String s = data.getStringExtra("content");
                searchLocation(s);
            }
        }
    }


    public void Permissioncheck() {
        /*
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "GPS 권한 있음.", Toast.LENGTH_LONG).show();
            lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            stock_update_gps();
        } else {
            Toast.makeText(this, "GPS 권한 있음.", Toast.LENGTH_LONG).show();
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(this, "GPS 권한 있음.", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
        */


        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(MainActivity.this, "권한 허가", Toast.LENGTH_SHORT).show();
                stock_update_gps();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(MainActivity.this, "권한 거부\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }

        };


        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage("위치 권한이 필요해요")
                .setDeniedMessage("왜 거부하셨어요...\n하지만 [설정] > [권한] 에서 권한을 허용할 수 있어요.")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .check();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int grantResults[]){
        switch(requestCode){
            case 1:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(getApplicationContext(), "GPS 권한 승인", Toast.LENGTH_SHORT).show();
                    lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                    stock_update_gps();
                }else{
                    Toast.makeText(getApplicationContext(), "GPS 권한 거부", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }
    public void searchLocation(String location_name) {
        List<Address> list = null;
        try {
            list = geocoder.getFromLocationName(location_name,1);
            x = list.get(0).getLatitude();
            y = list.get(0).getLongitude();
            init_mapView(x,y);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void stock_update_gps() {
        lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        String locationprovider = LocationManager.NETWORK_PROVIDER;

        if(lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true) {
            locationprovider = LocationManager.NETWORK_PROVIDER;
        }
        else if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER) == true) {
            locationprovider = LocationManager.GPS_PROVIDER;
        }
        //String provider = location.getProvider();
        Location location = lm.getLastKnownLocation(locationprovider);
        y = location.getLongitude();
        x = location.getLatitude();
        init_mapView(x,y);
        //double altitude = location.getAltitude();
    }

    public void stock_update_map() {
        //double altitude = location.getAltitude();
        x = mapView.getMapCenterPoint().getMapPointGeoCoord().latitude;
        y = mapView.getMapCenterPoint().getMapPointGeoCoord().longitude;
        init_mapView(x,y);
    }

    public void init_mapView(double x, double y) {
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(x, y), true);
        mapView.removeAllPOIItems();
        MapPOIItem marker = new MapPOIItem();
        Log.d("아커지우기", "마커지웠져영");
        marker.setItemName("user");
        marker.setMapPoint(MapPoint.mapPointWithGeoCoord(x, y));
        marker.setMarkerType(MapPOIItem.MarkerType.RedPin);
        mapView.addPOIItem(marker);
        testrequset(Double.toString(x),Double.toString(y));
    }

    //keytool -exportcert -alias MyKey -keystore MyKey.jks | C:\OpenSSL-Win64\bin\openssl sha1 -binary | C:\OpenSSL-Win64\bin\openssl base64
    //Q7QzKdipzIb6rT8wBWzmbqY3mdU=
    //o27LT6F6cDoVOGYJ93Vmpf/gvFg=
    public void testrequset(String lati, String lngt) {
        String url = "https://8oi9s0nnth.apigw.ntruss.com/corona19-masks/v1/storesByGeo/json?lat=" + lati + "&lng=" + lngt + "&m=1000";
        Utf8StringRequest request = new Utf8StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("114488","응답 받음 ->" + response);
                        testprocess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("119977","에러 발생 ->" + error.getMessage());

                    }
                }

        );
        request.setShouldCache(false);
        AppHelper.requestQueue.add(request);
        Log.d("114488", "큐에넣음 ");
    }

    public void testprocess (String response) {
        Gson gson = new Gson();
        test info = gson.fromJson(response, test.class);
        stores_info.clear();
        for(int i=0; i<info.stores.size(); i++) {
            MapPOIItem marker = new MapPOIItem();
            stores_info.put(info.stores.get(i).name, info.stores.get(i));
            marker.setItemName(info.stores.get(i).name);
            marker.setMapPoint(MapPoint.mapPointWithGeoCoord(info.stores.get(i).lat, info.stores.get(i).lng));
            marker.setMarkerType(MapPOIItem.MarkerType.CustomImage);

            if(info.stores.get(i).remain_stat != null && info.stores.get(i).remain_stat.equals("plenty")) {
                marker.setCustomImageResourceId(R.drawable.green_marker);
            }
            else if(info.stores.get(i).remain_stat != null && info.stores.get(i).remain_stat.equals("some")) {
                marker.setCustomImageResourceId(R.drawable.orange_marker);
            }
            else if(info.stores.get(i).remain_stat != null && info.stores.get(i).remain_stat.equals("few")) {
                marker.setCustomImageResourceId(R.drawable.red_marker);
            }
            else if(info.stores.get(i).remain_stat != null && info.stores.get(i).remain_stat.equals("break")){
                marker.setCustomImageResourceId(R.drawable.grey_marker);
            }
            else {
                marker.setCustomImageResourceId(R.drawable.grey_marker);
            }
            marker.setCustomImageAutoscale(false); // hdpi, xhdpi 등 안드로이드 플랫폼의 스케일을 사용할 경우 지도 라이브러리의 스케일 기능을 꺼줌.
            marker.setCustomImageAnchor(0.5f, 1.0f); // 마커 이미지중 기준이 되는 위치(앵커포인트) 지정 - 마커 이미지 좌측 상단 기준 x(0.0f ~ 1.0f), y(0.0f ~ 1.0f) 값.
            mapView.addPOIItem(marker);
        }
    }

    /*
    public void requestpage() {
        String url = "https://8oi9s0nnth.apigw.ntruss.com/corona19-masks/v1/stores/json";

        Utf8StringRequest request = new Utf8StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("114488","응답 받음 ->" + response);
                        getpages(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("119977","에러 발생 ->" + error.getMessage());

                    }
                }

        );
        request.setShouldCache(false);
        AppHelper.requestQueue.add(request);
        Log.d("114488", "큐에넣음 ");
    }

    public void getpages(String response) {
        Gson gson = new Gson();

        Store info = gson.fromJson(response, Store.class);
        int totalpages = info.totalPages;
        Log.d("114488", "페이지숫자받음 ");
        for(int i=1; i<=totalpages; i++) {
            requestStores(i);
        }
    }

    public void requestStores(int resId) {
        String url = "https://8oi9s0nnth.apigw.ntruss.com/corona19-masks/v1/stores/json?page=" + resId;

        Utf8StringRequest request = new Utf8StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("requestStores","응답 받음 ->" + response);
                        processStores(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("119977","에러 발생 ->" + error.getMessage());

                    }
                }

        );
        request.setShouldCache(false);
        AppHelper.requestQueue.add(request);

    }

    public void processStores (String response) {
        Gson gson = new Gson();

        MapPOIItem marker = new MapPOIItem();
        marker.setItemName("아이구야");
        //marker.setTag(Integer.parseInt(info.code));
        marker.setMapPoint(MapPoint.mapPointWithGeoCoord(37.405383, 126.916431));
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin);
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
        Log.d("제발뜨라고", "put_marker: ");
        mapView.addPOIItem(marker);



        Store info = gson.fromJson(response, Store.class);
        Log.d("제발뜨라고", info.count + "");
        Log.d("제발뜨라고", info.page + "");
        for(int i=0; i<info.storeInfos.size(); i++) {
            //map.put(info.storesInfos.get(i).code, info.storesInfos.get(i));
            store_arr.add(info.storeInfos.get(i));
            put_marker(info.storeInfos.get(i));
        }
        store_check = false;

    }


    public void put_marker(storesInfo info) {
        MapPOIItem marker = new MapPOIItem();
        Log.d("제발뜨라고", info.lat + "");
        Log.d("제발뜨라고", info.lng + "");
        marker.setItemName(info.name);
        marker.setTag(Integer.parseInt(info.code));
        marker.setMapPoint(MapPoint.mapPointWithGeoCoord(info.lat, info.lng));
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin);
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
        Log.d("제발뜨라고", "put_marker: ");
        mapView.addPOIItem(marker);

    }



    public void requestStockpage() {
        String url = "https://8oi9s0nnth.apigw.ntruss.com/corona19-masks/v1/sales/json";

        Utf8StringRequest request = new Utf8StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("114488","응답 받음 ->" + response);
                        getStockpages(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("119977","에러 발생 ->" + error.getMessage());

                    }
                }

        );
        request.setShouldCache(false);
        AppHelper.requestQueue.add(request);
        Log.d("114488", "큐에넣음 ");
    }

    public void getStockpages(String response) {
        Gson gson = new Gson();

        Stock info = gson.fromJson(response, Stock.class);
        int totalpages = info.totalPages;
        Log.d("114488", "페이지숫자받음 ");
        for(int i=1; i<=totalpages; i++) {
            requestStockStores(i);
        }
    }



    public void requestStockStores(int resId) {
        String url = "https://8oi9s0nnth.apigw.ntruss.com/corona19-masks/v1/sales/json?page=" + resId;

        Utf8StringRequest request = new Utf8StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("requestStockStores","응답 받음 ->" + response);
                        processStockStores(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("119977","에러 발생 ->" + error.getMessage());

                    }
                }

        );
        request.setShouldCache(false);
        AppHelper.requestQueue.add(request);

    }



    public void processStockStores (String response) {
        Gson gson = new Gson();

        Stock info = gson.fromJson(response, Stock.class);
        Log.d("제발뜨라고", info.page + "");
        for(int i=0; i<info.sales.size(); i++) {
            stock_map.put(info.sales.get(i).code, info.sales.get(i));
        }
    }


*/
    public class Utf8StringRequest extends StringRequest {
        public Utf8StringRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
            super(method, url, listener, errorListener);
        }

        public Utf8StringRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
            super(url, listener, errorListener);
        }

        @Override
        protected Response<String> parseNetworkResponse(NetworkResponse response) {
            try {
                String utf8String = new String(response.data, "UTF-8");
                return Response.success(utf8String, HttpHeaderParser.parseCacheHeaders(response));
            } catch (UnsupportedEncodingException e) {
                // log error
                return Response.error(new ParseError(e));
            } catch (Exception e) {
                // log error
                return Response.error(new ParseError(e));
            }
        }
    }


}
