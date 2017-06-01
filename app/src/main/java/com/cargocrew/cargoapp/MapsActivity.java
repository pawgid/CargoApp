package com.cargocrew.cargoapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cargocrew.cargoapp.forDrawingRoute.DownloadTask;
import com.cargocrew.cargoapp.forDrawingRoute.Services;
import com.cargocrew.cargoapp.models.CargoItem;
import com.cargocrew.cargoapp.models.TransportationItem;
import com.cargocrew.cargoapp.models.TruckItem;
import com.cargocrew.cargoapp.models.ValuesSingleton;
import com.cargocrew.cargoapp.models.registrationAndLogin.MainActivity;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
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

    public static final int REQUEST_ACCESS_FINE_LOCATION = 1001;

    public static Context mContext;

    public static GoogleMap mMap;

    private final int MAP_ONCLICK_NULL = 100;
    private final int MAP_ONCLICK_ZOOM_OUT_FROM_DETAIL = 101;
    private final int MAP_ONCLICK_ADD_MARKER = 102;

    private int mapOnClickState = MAP_ONCLICK_NULL;

    private List<Marker> currentRouteMarkerList = new ArrayList<>();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference cargoRef = database.getInstance().getReference("Cargo");
    private DatabaseReference truckRef = database.getInstance().getReference("Truck");

    private HashMap<String, TransportationItem> cargoHashMap = new HashMap<>();
    private HashMap<String, TransportationItem> truckHashMap = new HashMap<>();
    private HashMap<String, TransportationItem> currentSelect = new HashMap<>();

    private String selectedMarker = null;
    private CameraPosition cameraPosition = null;
    public boolean mapRefreshable = true;
    private ValuesSingleton VS = ValuesSingleton.getInstance();

    private FloatingActionButton floatingLoginOptionsButton;

    @BindView(R.id.searchEditText)
    EditText searchEditText;

    @BindView(R.id.floatingActionButtonSearch)
    FloatingActionButton floatingActionButtonSearch;

    @BindView(R.id.floatingActionButtonOpenSearch)
    FloatingActionButton floatingActionButtonOpenSearch;

    @BindView(R.id.floatingActionButtonSwitch)
    FloatingActionButton floatingActionButtonSwitch;

    @BindView(R.id.bottomBar)
    LinearLayout bottomBar;

    @BindView(R.id.nameEditText)
    TextView nameEditText;

    @BindView(R.id.sendButton)
    Button sendButton;

    @BindView(R.id.cargoDetailBar)
    LinearLayout cargoDetailBar;

    @BindView(R.id.truckDetailBar)
    LinearLayout truckDetailBar;

    @BindView(R.id.addCargoBar)
    LinearLayout addCargoBar;

    @BindView(R.id.addTruckBar)
    LinearLayout addTruckBar;





    @OnClick(R.id.floatingActionButtonOpenSearch)
    public void floatingActionButtonOpenSearchClick() {
        mMap.clear();
        showSearchEventItems();
        mapOnClickState = MAP_ONCLICK_ADD_MARKER;

        if (currentSelect == cargoHashMap)
            addCargoBar.setVisibility(View.VISIBLE);
        if (currentSelect == truckHashMap)
            addTruckBar.setVisibility(View.VISIBLE);


    }

    @OnClick(R.id.floatingActionButtonSwitch)
    public void floatingActionButtonSwitchClick() {
        if (currentSelect == cargoHashMap) {
            currentSelect = truckHashMap;
            floatingActionButtonSwitch.setImageDrawable(getResources().getDrawable(R.drawable.truck));
        } else {
            currentSelect = cargoHashMap;
            floatingActionButtonSwitch.setImageDrawable(getResources().getDrawable(R.drawable.cargo_icon));
        }

        hideSearchEventItems();
        mMap.clear();
        drawTransportationMarkers(currentSelect);
    }





    @OnClick(R.id.sendButton)
    public void sendButtonClick() {
        VS.setCargoItemName(nameEditText.getText().toString());

        String key = cargoRef.push().getKey();
        if (currentSelect == cargoHashMap) {
            cargoRef.child(key).setValue(VS.getCargoItem());
        } else {
            truckRef.child(key).setValue(VS.getCargoItem());
        }
        mMap.clear();
        hideSearchEventItems();

        drawTransportationMarkers(currentSelect);
        mapRefreshable = true;
        addCargoBar.setVisibility(View.GONE);
        addTruckBar.setVisibility(View.GONE);
    }

    @OnClick(R.id.cancelButton)
    public void cancelButtonClick() {
        VS.cleanCargoItem();
        currentRouteMarkerList.clear();
        mMap.clear();
        hideSearchEventItems();
        mMap.animateCamera(CameraUpdateFactory.zoomTo(5f));
        drawTransportationMarkers(currentSelect);
        mapRefreshable = true;
        mapOnClickState = MAP_ONCLICK_NULL;

        addCargoBar.setVisibility(View.GONE);
        addTruckBar.setVisibility(View.GONE);
    }

    @OnClick(R.id.floatingActionButtonSearch)
    public void search() {
        String location = searchEditText.getText().toString();
        if (location != null && !location.equals("")) {
            LatLng coordinationAsLatLng = getCoordinationFromName(location);
            getRoute(coordinationAsLatLng);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);
        floatingLoginOptionsButton = (FloatingActionButton) findViewById(R.id.floatingLoginOptionsButton);
        floatingLoginOptionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapsActivity.this, MainActivity.class));
            }
        });


        currentSelect = cargoHashMap;

        if (!haveLocationPermission()) return;

        setUpMap();
        dataBaseSetup();
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

    private boolean haveLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);
            return false;
        }
        return true;
    }

    private void setUpMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mContext = getBaseContext();
        mMap = googleMap;
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        LatLng polandCenter = new LatLng(52.069381, 19.480334);
        cameraPosition = CameraPosition.builder().zoom(5.4f).target(polandCenter).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map));


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        } else {
            mMap.setMyLocationEnabled(true);
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(com.google.android.gms.maps.model.LatLng latLng) {

                switch (mapOnClickState) {
                    case MAP_ONCLICK_ADD_MARKER:
                        showSearchEventItems();
                        getRoute(latLng);

                        break;
                    case MAP_ONCLICK_ZOOM_OUT_FROM_DETAIL:
                        mMap.clear();
                        mapRefreshable = true;
                        drawTransportationMarkers(currentSelect);
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        mapOnClickState = MAP_ONCLICK_NULL;
                        cargoDetailBar.setVisibility(View.GONE);
                        truckDetailBar.setVisibility(View.GONE);
                        floatingActionButtonSwitch.setVisibility(View.VISIBLE);
                        floatingActionButtonOpenSearch.setVisibility(View.VISIBLE);
                        break;
                    default:
                        break;
                }

            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker arg0) {
                Toast.makeText(MapsActivity.this, arg0.getTitle(), Toast.LENGTH_SHORT).show();// display toast

                floatingActionButtonSwitch.setVisibility(View.GONE);
                floatingActionButtonOpenSearch.setVisibility(View.GONE);

                TransportationItem tag = (TransportationItem) arg0.getTag();

//               TODO Przekazać informację jaki obiekt ma klucz - zmienić tag na parę <ID, TransportItem>

                LatLng origin = tag.getOrigin().toLatLong();
                LatLng dest = tag.getDestination().toLatLong();
                Services s = new Services();
                String url = s.getDirectionsUrl(origin, dest);
                DownloadTask downloadTask = new DownloadTask();

                mapRefreshable = false;

                mMap.clear();
                Marker startMarker = mMap.addMarker(new MarkerOptions().position(origin).title("Start"));
                Marker destMarker = mMap.addMarker(new MarkerOptions().position(dest).title("Dest"));
                downloadTask.execute(url);

                ArrayList<Marker> markers = new ArrayList<>();
                markers.add(startMarker);
                markers.add(destMarker);

                if (tag.getClass() == CargoItem.class)
                    cargoDetailBar.setVisibility(View.VISIBLE);
                if (tag.getClass() == TruckItem.class)
                    truckDetailBar.setVisibility(View.VISIBLE);


                cameraPosition = mMap.getCameraPosition();

                settingZoom(markers);
                mapOnClickState = MAP_ONCLICK_ZOOM_OUT_FROM_DETAIL;


                return true;
            }
        });


    }

    public void dataBaseSetup() {

        cargoRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Toast.makeText(MapsActivity.this, "onChildAdded", Toast.LENGTH_SHORT).show();

                String key = dataSnapshot.getKey();
                CargoItem cargoItem = dataSnapshot.getValue(CargoItem.class);
                cargoHashMap.put(key, cargoItem);

                if (mapRefreshable && currentSelect == cargoHashMap) {
                    mMap.clear();
                    drawTransportationMarkers(currentSelect);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Toast.makeText(MapsActivity.this, "onChildChanged", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Toast.makeText(MapsActivity.this, "onChildRemoved", Toast.LENGTH_SHORT).show();

                String key = dataSnapshot.getKey();
                if (cargoHashMap.containsKey(key)) {
                    cargoHashMap.remove(key);
                }

                if (mapRefreshable && currentSelect == cargoHashMap) {
                    mMap.clear();
                    drawTransportationMarkers(currentSelect);
                }
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


        truckRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Toast.makeText(MapsActivity.this, "onChildAdded", Toast.LENGTH_SHORT).show();

                String key = dataSnapshot.getKey();
                TruckItem truckItem = dataSnapshot.getValue(TruckItem.class);
                truckHashMap.put(key, truckItem);

                if (mapRefreshable && currentSelect == truckHashMap) {
                    mMap.clear();
                    drawTransportationMarkers(currentSelect);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Toast.makeText(MapsActivity.this, "onChildChanged", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Toast.makeText(MapsActivity.this, "onChildRemoved", Toast.LENGTH_SHORT).show();

                String key = dataSnapshot.getKey();
                if (truckHashMap.containsKey(key)) {
                    truckHashMap.remove(key);
                }

                if (mapRefreshable && currentSelect == truckHashMap) {
                    mMap.clear();
                    drawTransportationMarkers(currentSelect);
                }
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

        mapRefreshable = false;

        if (currentRouteMarkerList.size() == 0) {

            mMap.clear();

            currentRouteMarkerList.add(mMap.addMarker(new MarkerOptions().position(coordination)));
            searchEditText.setText("");
            VS.setCargoItemOrigin(currentRouteMarkerList.get(0).getPosition());
            settingZoom(currentRouteMarkerList);

            Geocoder geocoder = new Geocoder(this);
            Address address = null;
            try {
                address = geocoder.getFromLocation(currentRouteMarkerList.get(0).getPosition().latitude, currentRouteMarkerList.get(0).getPosition().longitude, 1).get(0);
                Toast.makeText(this, address.getPostalCode().toString(), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
            searchEditText.setHint("Enter destination");


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
            searchEditText.setHint("Enter start location");
            searchEditText.setVisibility(View.GONE);
            mapOnClickState = MAP_ONCLICK_NULL;

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

    public void drawTransportationMarkers(HashMap<String, TransportationItem> transportationItemHashMap) {

//         @TODO Ustawianie różnych ikonek na makrery
//        Drawable drawable = getResources().getDrawable(R.drawable.truck);
//        if (currentSelect == cargoHashMap)
//        drawable = getResources().getDrawable(R.drawable.cargo_icon);
//
//        Canvas canvas = new Canvas();
//        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
//        canvas.setBitmap(bitmap);
//        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
//        drawable.draw(canvas);
//        BitmapDescriptor markerIcon = BitmapDescriptorFactory.fromBitmap(bitmap);
//        @TODO Pamiętać dodać .icon do rysowania markera


        for (TransportationItem transportationItem : transportationItemHashMap.values()) {
            Marker marker = mMap.addMarker(new MarkerOptions().position(transportationItem.getOrigin().toLatLong()));
            marker.setTitle(transportationItem.getName());
            marker.setTag(transportationItem);
        }
    }

    public void showSearchEventItems() {
        floatingActionButtonOpenSearch.setVisibility(View.GONE);
        floatingActionButtonSearch.setVisibility(View.VISIBLE);
        searchEditText.setVisibility(View.VISIBLE);
        bottomBar.setVisibility(View.VISIBLE);
    }

    public void hideSearchEventItems() {
        floatingActionButtonOpenSearch.setVisibility(View.VISIBLE);
        floatingActionButtonSearch.setVisibility(View.GONE);
        searchEditText.setVisibility(View.GONE);
        bottomBar.setVisibility(View.GONE);
    }
}



















