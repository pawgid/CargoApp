package com.cargocrew.cargoapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cargocrew.cargoapp.forDrawingRoute.DownloadTask;
import com.cargocrew.cargoapp.forDrawingRoute.Services;
import com.cargocrew.cargoapp.models.CargoItem;
import com.cargocrew.cargoapp.models.ValuesSingleton;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.cargocrew.cargoapp.R.id.map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int REQUEST_ACCESS_FINE_LOCATION = 10001;
    public static GoogleMap mMap;
    private List<Marker> currentRouteMarkerList = new ArrayList<>();

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference cargoRef = database.getInstance().getReference("Cargo");
    ValuesSingleton VS = ValuesSingleton.getInstance();

    HashMap<String, CargoItem> cargoItemMap = new HashMap<String, CargoItem>();
    List<CargoItem> cargoItemList = new ArrayList();

    @BindView(R.id.searchEditText)
    EditText searchEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);

        if (!haveLocationPermission()) return;

        setUpMap();
        dataBaseSetup();
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng poznan = new LatLng(52.4004458, 16.7615836);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(poznan));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(6.0f));


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        } else {
            mMap.setMyLocationEnabled(true);
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(com.google.android.gms.maps.model.LatLng latLng) {
                getRoute(latLng);
            }
        });

    }

    @OnClick(R.id.searchButton)
    public void search() {
        String location = searchEditText.getText().toString();
        if (location != null && !location.equals("")) {
            LatLng coordinationAsLatLng = getCoordinationFromName(location);
            getRoute(coordinationAsLatLng);
        }
    }


    public void dataBaseSetup() {

        cargoRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Toast.makeText(MapsActivity.this, "onChildAdded", Toast.LENGTH_SHORT).show();

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    CargoItem value = data.getValue(CargoItem.class);
                    cargoItemList.add(value);
                }

                mMap.clear();

                Services services = new Services();

                for (CargoItem cargoItem: cargoItemList){
//                CargoItem cargoItem = cargoItemList.get(0);

                currentRouteMarkerList.add(mMap.addMarker(new MarkerOptions().position(cargoItem.getOrigin().toLatLong())));
                currentRouteMarkerList.add(mMap.addMarker(new MarkerOptions().position(cargoItem.getDestination().toLatLong())));

                String url = services.getDirectionsUrl(cargoItem.getOrigin().toLatLong(), cargoItem.getDestination().toLatLong());
                DownloadTask downloadTask = new DownloadTask();
                downloadTask.execute(url);
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Toast.makeText(MapsActivity.this, "onChildChanged", Toast.LENGTH_SHORT).show();

                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    CargoItem value = data.getValue(CargoItem.class);
                    cargoItemList.add(value);
                }

                mMap.clear();

                Services services = new Services();

                for (CargoItem cargoItem: cargoItemList){
//                CargoItem cargoItem = cargoItemList.get(0);

                    currentRouteMarkerList.add(mMap.addMarker(new MarkerOptions().position(cargoItem.getOrigin().toLatLong())));
                    currentRouteMarkerList.add(mMap.addMarker(new MarkerOptions().position(cargoItem.getDestination().toLatLong())));

                    String url = services.getDirectionsUrl(cargoItem.getOrigin().toLatLong(), cargoItem.getDestination().toLatLong());
                    DownloadTask downloadTask = new DownloadTask();
                    downloadTask.execute(url);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Toast.makeText(MapsActivity.this, "onChildRemoved", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Toast.makeText(MapsActivity.this, "onChildMoved", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MapsActivity.this, "onCancelled", Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void addToFireBase() {

        String key = cargoRef.push().getKey();
        cargoRef.child("Bananowy Magazyn").child(key).setValue(new CargoItem("Banany", 1));

    }

    private LatLng getCoordinationFromName(String location) {
        Geocoder geocoder = new Geocoder(this);
        Address address = null;
        try {
            address = geocoder.getFromLocationName(location, 1).get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
        return latLng;
    }

    private void getRoute(LatLng coordination) {


        if (currentRouteMarkerList.size() == 0) {

//            mMap.clear();

            currentRouteMarkerList.add(mMap.addMarker(new MarkerOptions().position(coordination)));
            searchEditText.setText("");
            VS.setCargoItemOrigin(currentRouteMarkerList.get(0).getPosition());

        } else if (currentRouteMarkerList.size() == 1) {

            currentRouteMarkerList.add(mMap.addMarker(new MarkerOptions().position(coordination)));
            searchEditText.setText("");
            VS.setCargoItemDestination(currentRouteMarkerList.get(1).getPosition());


            LatLng origin = currentRouteMarkerList.get(0).getPosition();
            LatLng dest = currentRouteMarkerList.get(1).getPosition();
            Services s = new Services();
            String url = s.getDirectionsUrl(origin, dest);
            DownloadTask downloadTask = new DownloadTask();
            downloadTask.execute(url);

            settingZoom(currentRouteMarkerList);

            currentRouteMarkerList.clear();

        } else {
            Log.i("M", "getRoute error");
            currentRouteMarkerList.clear();
        }

    }

    private void settingZoom(List<Marker> markerList) {
        if (markerList.size() >= 2) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Marker marker : markerList) {
                builder.include(marker.getPosition());
            }
            LatLngBounds bounds = builder.build();
            int padding = 100;
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            mMap.animateCamera(cu);
        } else {
            LatLngBounds bounds = this.mMap.getProjection().getVisibleRegion().latLngBounds;
            if (!bounds.contains(markerList.get(0).getPosition())) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerList.get(0).getPosition(), 8));
            }
        }
    }

    @BindView(R.id.nameEditText)
    TextView nameEditText;


    @OnClick(R.id.sendButton)
    public void sendButtonClick() {
        VS.setCargoItemName(nameEditText.getText().toString());

        String key = cargoRef.push().getKey();
        cargoRef.child("Storage").child(key).setValue(VS.getCargoItem());
        mMap.clear();

    }

    @OnClick(R.id.cancelButton)
    public void cancelButtonClick() {
        VS.cleanCargoItem();
        mMap.clear();

    }


}



















