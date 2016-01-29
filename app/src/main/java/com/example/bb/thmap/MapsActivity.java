package com.example.bb.thmap;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {


    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private Geocoder geocoder;
    private Button btnSearch, btnchangetypemaps, btnPlace;
    private EditText edtSearch;
    private TextView tv_Distance, tvInfo;
    private List<LatLng> markerPoints = null;
    private GoogleApiClient client;
    private ArrayList<Place> places = null;
    private Location mLocation = null;
    private ImageView imgIcon;
    private RelativeLayout rlInfo;
    private ListView lvPlaces;
    private MyArrayAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        lvPlaces = (ListView) findViewById(R.id.lvPlaces);

        mMap = mapFragment.getMap();
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);  // Hiển thị nút phóng to thu nhỏ trong Google map
        geocoder = new Geocoder(this, Locale.getDefault());
        if (!geocoder.isPresent()) {
            Toast t = Toast.makeText(this, "khong ho tro", Toast.LENGTH_LONG);
            t.show();
        } else {
            Toast t = Toast.makeText(this, "co ho tro", Toast.LENGTH_LONG);
            t.show();
        }
        myLocation(mMap);   //Goi Phuong  thuc  myLocation hien thị vị trí hiện tại.     }


        //thay doi kieu ban do
        btnchangetypemaps = (Button) findViewById(R.id.btnChangeType);
        btnchangetypemaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = mMap.getMapType();
                switch (i) {
                    case GoogleMap.MAP_TYPE_HYBRID:
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        Toast.makeText(getBaseContext(), "TYPE NORMAL", Toast.LENGTH_SHORT).show();
                        break;
                    case GoogleMap.MAP_TYPE_NORMAL:
                        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        Toast.makeText(getBaseContext(), "TYPE SATELLITE", Toast.LENGTH_SHORT).show();
                        break;
                    case GoogleMap.MAP_TYPE_SATELLITE:
                        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                        Toast.makeText(getBaseContext(), "TYPE TERAIN", Toast.LENGTH_SHORT).show();
                        break;
                    case GoogleMap.MAP_TYPE_TERRAIN:
                        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        Toast.makeText(getBaseContext(), "TYPE HYBRID", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        //bat su kien cho tim kiem
        edtSearch = (EditText) findViewById(R.id.edtSearch);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newlocation = edtSearch.getText().toString();
                if (!newlocation.equals("")) {
                    searchAddress(mMap, newlocation);
                    edtSearch.setText("");
                } else {
                    Toast.makeText(getBaseContext(), "Vui long nhap dia chi can tim", Toast.LENGTH_SHORT).show();
                }
            }
        });


        //bat su kien chon 1 marker
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 17.0f));
                return true;
            }
        });

        //su kien them Marker khi click vào map

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                lvPlaces.setVisibility(View.INVISIBLE);
            }
        });

        //su kien button Diadiemlancan
        btnPlace = (Button) findViewById(R.id.btnLanCan);
        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
                mLocation = myLocation(mMap);
                DiaDiemLanCan();
                lvPlaces.setBackgroundColor(Color.WHITE);
                lvPlaces.setVisibility(View.VISIBLE);

            }
        });

        lvPlaces.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Place chosePlace = places.get(position);
                LatLng mLatLng = new LatLng(chosePlace.getLatitude(), chosePlace.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 17.0f));
            }
        });


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        DiaDiemLanCan();
    }

    private void DiaDiemLanCan() {
        PlacesService service = new PlacesService("AIzaSyBfQPm46M3j2joTFlHachk4RCXfeR7ZFWE");
        if (mLocation != null) {
            places = service.findPlaces(mLocation.getLatitude(), mLocation.getLongitude(), "");
            for (Place pl : places) {
                //System.out.println(pl.getName());
                //LatLng mLatLng = new LatLng(pl.getLatitude(), pl.getLongitude());
                addmarker(pl);


                //System.out.println(pl.getId());
            }


            mAdapter = new MyArrayAdapter(this, R.layout.myplaces_layout, places);
            lvPlaces.setAdapter(mAdapter);

            lvPlaces.setVisibility(View.INVISIBLE);
        }
    }

    private Location myLocation(GoogleMap map) {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //            int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        if (location != null) {
            mLocation = location;
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            try {
                List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                Address ad = addresses.get(0);
                CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(16).bearing(0).tilt(0).build();
                String address = ad.getAddressLine(0);
                String country = ad.getCountryName();
                String city = ad.getLocality();
                String subadminarea = ad.getSubAdminArea();
                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.title("My Address: " + address + "," + subadminarea + "," + city + "," + country);

                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                markerOptions.position(latLng);
                Marker marker = map.addMarker(markerOptions);
                marker.showInfoWindow();
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 7));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return location;
    }

    //phuong thuc danh dau
    private void addmarker(Place place) {
        Address address = null;
        List<Address> addressList = null;
        LatLng latLng = new LatLng(place.getLatitude(), place.getLongitude());

        try {
            addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

            if (addressList.size() > 0)
                address = addressList.get(0);
        } catch (IOException e) {
            e.printStackTrace();
        } /*
        if (address != null) {
            MarkerOptions mkoption = new MarkerOptions();
            mkoption.position(latLng).title(address.getAddressLine(0) + "," + address.getSubLocality() + "," + address.getSubAdminArea() + "," + address.getAdminArea());
            Marker marker = mMap.addMarker(mkoption);
            marker.showInfoWindow();
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }
    */
        if ((place != null) && (address != null)) {
            MarkerOptions mkoption = new MarkerOptions();
            String info = address.getAddressLine(0) + "," + address.getSubLocality() + "," + address.getSubAdminArea() + "," + address.getAdminArea();
            mkoption.position(latLng).title(place.getName());
            place.setInfoDetail(info);
            Marker marker = mMap.addMarker(mkoption);


            //System.out.println(marker.getId());
           /* Bitmap bm = null;
            DownloadImage dlImage = new DownloadImage();
            dlImage.execute(place.getIcon());
            try {
                bm = dlImage.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }*/
            //marker.setIcon(BitmapDescriptorFactory.fromBitmap(bm));
            marker.showInfoWindow();


            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }
    }

    //phuong thuc tim dia diem tren map
    private void searchAddress(GoogleMap map, String location) {
        if (location != null || !location.equals("")) {
            List<Address> addressList = null;
            try {
                addressList = geocoder.getFromLocationName(location, 5);


            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addressList.size() > 0) {
                Address address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.title(address.getAddressLine(0) + "," + address.getSubLocality() + "," + address.getSubAdminArea() + "," + address.getAdminArea()).position(latLng);
                Marker marker = map.addMarker(markerOptions);
                marker.showInfoWindow();
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 7));
            } else {
                Toast.makeText(getBaseContext(), "Khong tiem thay dia diem", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

