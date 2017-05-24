package com.cargocrew.cargoapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.widget.EditText;

import com.cargocrew.cargoapp.forDrawingRoute.DownloadTask;
import com.cargocrew.cargoapp.forDrawingRoute.Services;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.cargocrew.cargoapp.R.id.map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int REQUEST_ACCESS_FINE_LOCATION = 10001;
    public static GoogleMap mMap;
    private List<Address> addressList = new ArrayList<>();
    private List<Marker> markerList = new ArrayList<>();

    @BindView(R.id.searchEditText)
    EditText searchEditText;

    @OnClick(R.id.searchButton)
    public void search() {
        String location = searchEditText.getText().toString();
        if (location != null && !location.equals("")) {

            Geocoder geocoder = new Geocoder(this);
            if (addressList.size() >= 2) {
                mMap.clear();
                addressList.clear();
                markerList.clear();
            }
            try {
                addressList.add(geocoder.getFromLocationName(location, 1).get(0));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address = addressList.get(addressList.size() - 1);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

            markerList.add(mMap.addMarker(new MarkerOptions().position(latLng)));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));


            if (addressList.size() > 1) {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (Marker marker : markerList) {
                    builder.include(marker.getPosition());
                }
                LatLngBounds bounds = builder.build();
//            Then obtain a movement description object by using the factory: CameraUpdateFactory:
                int padding = 100; // offset from edges of the map in pixels
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
//            Or if you want an animation:
                mMap.animateCamera(cu);
            } else {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
            }

            if (addressList.size() >= 2) {
                LatLng origin = new LatLng(addressList.get(0).getLatitude(), addressList.get(0).getLongitude());
                LatLng dest = new LatLng(addressList.get(1).getLatitude(), addressList.get(1).getLongitude());

                // Getting URL to the Google Directions API
                Services s = new Services();
                String url = s.getDirectionsUrl(origin, dest);

                DownloadTask downloadTask = new DownloadTask();

                // Start downloading json data from Google Directions API
                downloadTask.execute(url);
            }
            searchEditText.setText(""); 
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);

        if (!haveLocationPermission()) return;

        setUpMap();

    }

    private boolean haveLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_ACCESS_FINE_LOCATION && grantResults[0] != -1) {
            {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng poznan = new LatLng(52.4004458, 16.7615836);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(poznan));


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        } else {
            mMap.setMyLocationEnabled(true);
        }
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                mMap.addMarker(new MarkerOptions().position(point).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            }
        });
    }


}
