package com.cargocrew.cargoapp.activities;

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
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cargocrew.cargoapp.R;
import com.cargocrew.cargoapp.forDrawingRoute.DownloadTask;
import com.cargocrew.cargoapp.forDrawingRoute.Services;
import com.cargocrew.cargoapp.models.CargoItem;
import com.cargocrew.cargoapp.models.Point;
import com.cargocrew.cargoapp.models.TransportationItem;
import com.cargocrew.cargoapp.models.TruckItem;
import com.cargocrew.cargoapp.models.ValuesSingleton;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private final int MARKER_ONCLICK_NULL = 200;
    private final int MARKER_ONCLICK_ZOOM_FORM_DETAIL = 201;

    private int mapOnClickState = MAP_ONCLICK_NULL;
    private int markerOnClickState = MARKER_ONCLICK_ZOOM_FORM_DETAIL;


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
//    private ValuesSingleton VS = ValuesSingleton.getInstance();
    private FirebaseAuth auth;

    @BindView(R.id.searchEditTextWrapper)
    LinearLayout searchEditTextWrapper;
    @BindView(R.id.searchEditText)
    EditText searchEditText;
    @BindView(R.id.ButtonSearch)
    Button ButtonSearch;
    @BindView(R.id.cancelButtonOnSearch)
    Button cancelButtonOnSearch;

    @BindView(R.id.floatingActionButtonOpenSearch)
    FloatingActionButton floatingActionButtonOpenSearch;
    @BindView(R.id.floatingActionButtonSwitch)
    FloatingActionButton floatingActionButtonSwitch;
    @BindView(R.id.floatingLoginOptionsButton)
    FloatingActionButton floatingLoginOptionsButton;

    @BindView(R.id.addTruckBar)
    LinearLayout addTruckBar;
    @BindView(R.id.truckDetailDateEditText)
    EditText truckDetailDateEditText;
    @BindView(R.id.truckDetailTelEditText)
    EditText truckDetailTelEditText;
    @BindView(R.id.truckDetailTypeEditText)
    EditText truckDetailTypeEditText;
    @BindView(R.id.truckDetaiAddButton)
    Button truckDetailAddButton;
    @BindView(R.id.truckDetailCancelButton)
    Button truckDetailCancelButton;

    @BindView(R.id.truckDetailDateTextViewWrapper)
    LinearLayout truckDetailDateTextViewWrapper;
    @BindView(R.id.truckDetailTelTextViewWrapper)
    LinearLayout truckDetailTelTextViewWrapper;
    @BindView(R.id.truckDetailTypeTextViewWrapper)
    LinearLayout truckDetailTypeTextViewWrapper;

    @BindView(R.id.truckDetailBar)
    LinearLayout truckDetailBar;
    @BindView(R.id.truckDetailDateTextView)
    TextView truckDetailDateTextView;
    @BindView(R.id.truckDetailTelTextView)
    TextView truckDetailTelTextView;
    @BindView(R.id.truckDetailTypeTextView)
    TextView truckDetailTypeTextView;
    @BindView(R.id.truckDetaildeleteTruck)
    Button truckDetailDeleteTruck;

    @BindView(R.id.addCargoBar)
    LinearLayout addCargoBar;
    @BindView(R.id.cargoDetailCountryEditText)
    EditText cargoDetailCountryEditText;
    @BindView(R.id.cargoDetailDestEditText)
    EditText cargoDetailDestEditText;
    @BindView(R.id.cargoDetailEstimEditText)
    EditText cargoDetailEstimateEditText;
    @BindView(R.id.cargoDetailHeightEditText)
    EditText cargoDetailHeightEditText;
    @BindView(R.id.cargoDetailLengthEditText)
    EditText cargoDetailLengthEditText;
    @BindView(R.id.cargoDetailPhoneNumberEditText)
    EditText cargoDetailPhoneNumberEditText;
    @BindView(R.id.cargoDetailWeightEditText)
    EditText cargoDetailWeightEditText;
    @BindView(R.id.cargoDetailWidthEditText)
    EditText cargoDetailWidthEditText;
    @BindView(R.id.cargoDetailZipCodeEditText)
    EditText cargoDetailZipCodeEditText;
    @BindView(R.id.additionalDetailsEditText)
    EditText additionalDetailsEditText;
    @BindView(R.id.cargoDetailCancelButtonn)
    Button cargoDetailCancelButton;
    @BindView(R.id.cargoDetailOrder)
    Button cargoDetailOrder;

    @BindView(R.id.cargoDetailBar)
    LinearLayout cargoDetailBar;
    @BindView(R.id.cargoDetailDeliveryAddressTextView)
    TextView cargoDetailDeliveryAddressTextView;
    @BindView(R.id.cargoDetailLenghtTextView)
    TextView cargoDetailLenghtTextView;
    @BindView(R.id.cargoDetailHeightTextView)
    TextView cargoDetailHeightTextView;
    @BindView(R.id.cargoDetailWidthTextView)
    TextView cargoDetailWidthTextView;
    @BindView(R.id.cargoDetailWeightTextView)
    TextView cargoDetailWeightTextView;
    @BindView(R.id.cargoDetailNoteTextView)
    TextView cargoDetailNoteTextView;
    @BindView(R.id.cargoDetailPhoneNumberTextView)
    TextView cargoDetailPhoneNumberTextView;
    @BindView(R.id.cargoDetailBudgetTextView)
    TextView cargoDetailBudgetTextView;
    @BindView(R.id.cargoDetailDeleteCargo)
    Button cargoDetailDeleteCargo;
    @BindView(R.id.cargoDetailRadioButton)
    RadioButton cargoDetailRadioButton;


    @OnClick(R.id.truckDetaiAddButton)
    public void truckDetailAddButtonClick() {

        TruckItem truckItem = new TruckItem();
        truckItem.setOrigin(new Point(currentRouteMarkerList.get(0).getPosition()));
        truckItem.setDestination(new Point(currentRouteMarkerList.get(1).getPosition()));
        truckItem = bindTruckFromForm(truckItem);
        String key = cargoRef.push().getKey();
        truckRef.child(key).setValue(truckItem);

        backToMainMapView();
    }

    @OnClick(R.id.truckDetailCancelButton)
    public void truckDetailCancelButtonClick() {

        mMap.animateCamera(CameraUpdateFactory.zoomTo(5f));
        backToMainMapView();
    }

    @OnClick(R.id.truckDetaildeleteTruck)
    public void deleteTruckClick() {
        truckRef.child(selectedMarker).removeValue();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        backToMainMapView();
    }

    public TruckItem bindTruckFromForm(TruckItem truckItem) {

        truckItem.setDestStreet(truckDetailDateEditText.getText().toString());
        truckItem.setPhoneNumber(truckDetailTelEditText.getText().toString());
        truckItem.setType(truckDetailTypeEditText.getText().toString());

        return truckItem;
    }

    public void bindViewFromTruck(TruckItem truck) {
        if (truck.getDate() != null && !truck.getDate().isEmpty()) {
            truckDetailDateTextView.setText(truck.getDate());
        } else {
            truckDetailDateTextViewWrapper.setVisibility(View.GONE);
        }
        if (truck.getPhoneNumber() != null && !truck.getPhoneNumber().isEmpty()) {
            truckDetailTelTextView.setText(truck.getPhoneNumber());
        } else {
            truckDetailTelTextViewWrapper.setVisibility(View.GONE);
        }
        if (truck.getType() != null && !truck.getType().isEmpty()) {
            truckDetailTypeTextView.setText(truck.getType());
        } else {
            truckDetailTypeTextViewWrapper.setVisibility(View.GONE);
        }
    }


    @OnClick(R.id.cargoDetailOrder)
    public void cargoDetailOrderClick() {
        CargoItem cargoItem = new CargoItem();
        cargoItem.setOrigin(new Point(currentRouteMarkerList.get(0).getPosition()));
        cargoItem.setDestination(new Point(currentRouteMarkerList.get(1).getPosition()));
        cargoItem = bindCargoFromForm(cargoItem);
        String key = truckRef.push().getKey();
        cargoRef.child(key).setValue(cargoItem);

        backToMainMapView();
    }

    @OnClick(R.id.cargoDetailCancelButtonn)
    public void cargoDetailCancelButtonClick() {

        mMap.animateCamera(CameraUpdateFactory.zoomTo(5f));
        backToMainMapView();
    }

    @OnClick(R.id.cargoDetailDeleteCargo)
    public void deleteCargoClick() {
        cargoRef.child(selectedMarker).removeValue();
        backToMainMapView();
    }


    public CargoItem bindCargoFromForm(CargoItem cargo) {

        cargo.setDestCountryCode(cargoDetailCountryEditText.getText().toString());
        cargo.setDestStreet(cargoDetailDestEditText.getText().toString());
        cargo.setDestZipCodeAndCity(cargoDetailZipCodeEditText.getText().toString());
        cargo.setPhoneNumber(cargoDetailPhoneNumberEditText.getText().toString());
        cargo.setAddidtionalInfo(additionalDetailsEditText.getText().toString());

        cargo.setOffer(zeroIfEditTextIsEmpty(cargoDetailEstimateEditText));
        cargo.setHeight(zeroIfEditTextIsEmpty(cargoDetailHeightEditText));
        cargo.setLength(zeroIfEditTextIsEmpty(cargoDetailLengthEditText));
        cargo.setWeight(zeroIfEditTextIsEmpty(cargoDetailWeightEditText));
        cargo.setWidth(zeroIfEditTextIsEmpty(cargoDetailWidthEditText));

        return cargo;
    }

    private Double zeroIfEditTextIsEmpty(EditText field) {
        if (field.getText().toString().isEmpty()) {
            return 0.0;
        }
        return Double.valueOf(field.getText().toString());
    }

    public void bindViewFromCargo(CargoItem cargo) {


        cargoDetailDeliveryAddressTextView.setText(cargo.getDestStreet() + "\n" + cargo.getDestZipCodeAndCity() + "; " + cargo.getDestCountryCode());
        cargoDetailDeliveryAddressTextView.setText(cargo.getDestStreet() + "\n" + cargo.getDestZipCodeAndCity() + "; " + cargo.getDestCountryCode());
        cargoDetailLenghtTextView.setText("L: " + String.valueOf(cargo.getLength()) + " cm");
        cargoDetailHeightTextView.setText("H: " + String.valueOf(cargo.getHeight()) + " cm");
        cargoDetailWidthTextView.setText("L: " + String.valueOf(cargo.getWidth()) + " cm");
        cargoDetailWeightTextView.setText("Weight: " + String.valueOf(cargo.getWeight()) + " kg");
        cargoDetailNoteTextView.setText(cargo.getAddidtionalInfo());
        cargoDetailPhoneNumberTextView.setText("Contact number: " + cargo.getPhoneNumber());
        cargoDetailBudgetTextView.setText("Offer: " + String.valueOf(cargo.getOffer()));
    }


    @OnClick(R.id.floatingActionButtonOpenSearch)
    public void floatingActionButtonOpenSearchClick() {
        mMap.clear();
        hideFloatingButtons();
        searchEditTextWrapper.setVisibility(View.VISIBLE);
        mapOnClickState = MAP_ONCLICK_ADD_MARKER;
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
        mMap.clear();
        drawTransportationMarkers(currentSelect);
    }

    @OnClick(R.id.floatingLoginOptionsButton)
    public void floatingLoginOptionsButtonClick() {
        startActivity(new Intent(MapsActivity.this, LoginOptionsActivity.class));
    }

    @OnClick(R.id.ButtonSearch)
    public void search() {
        String location = searchEditText.getText().toString();
        if (location != null && !location.equals("")) {
            LatLng coordinationAsLatLng = getCoordinationFromName(location);
            getRoute(coordinationAsLatLng);
        }
    }

    @OnClick(R.id.cancelButtonOnSearch)
    public void cancelSearch() {
        mMap.animateCamera(CameraUpdateFactory.zoomTo(5f));
        backToMainMapView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);

//        auth = FirebaseAuth.getInstance();
//        String uid = auth.getCurrentUser().getUid();

        currentSelect = cargoHashMap;

        haveLocationPermission();

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
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map));
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        LatLng polandCenter = new LatLng(52.069381, 19.480334);
        cameraPosition = CameraPosition.builder().zoom(5.4f).target(polandCenter).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


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
                        getRoute(latLng);

                        break;
                    case MAP_ONCLICK_ZOOM_OUT_FROM_DETAIL:
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        backToMainMapView();
                        break;
                    default:
                        break;
                }

            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker arg0) {

                switch (markerOnClickState) {
                    case MARKER_ONCLICK_ZOOM_FORM_DETAIL:

                        markerOnClickState = MARKER_ONCLICK_NULL;
                        hideFloatingButtons();

                        selectedMarker = (String) arg0.getTag();

                        TransportationItem selectedTransportItem = null;
                        if (cargoHashMap.containsKey(selectedMarker)) {
                            selectedTransportItem = cargoHashMap.get(selectedMarker);
                        }
                        if (truckHashMap.containsKey(selectedMarker)) {
                            selectedTransportItem = truckHashMap.get(selectedMarker);
                        }

                        LatLng origin = selectedTransportItem.getOrigin().toLatLong();
                        LatLng dest = selectedTransportItem.getDestination().toLatLong();
                        Services services = new Services();
                        String url = services.getDirectionsUrl(origin, dest);
                        DownloadTask downloadTask = new DownloadTask();

                        mapRefreshable = false;
                        mMap.clear();

                        Marker startMarker = mMap.addMarker(new MarkerOptions()
                                .position(origin));
                        Marker destMarker = mMap.addMarker(new MarkerOptions()
                                .position(dest));
                        downloadTask.execute(url);

                        ArrayList<Marker> markers = new ArrayList<>();
                        markers.add(startMarker);
                        markers.add(destMarker);

                        if (selectedTransportItem.getClass() == CargoItem.class) {
                            cargoDetailBar.setVisibility(View.VISIBLE);
                            bindViewFromCargo((CargoItem) cargoHashMap.get(selectedMarker));
                        }
                        if (selectedTransportItem.getClass() == TruckItem.class) {
                            truckDetailBar.setVisibility(View.VISIBLE);
                            bindViewFromTruck((TruckItem) truckHashMap.get(selectedMarker));
                        }
                        cameraPosition = mMap.getCameraPosition();

                        settingZoomAroundMarkerList(markers);
                        mapOnClickState = MAP_ONCLICK_ZOOM_OUT_FROM_DETAIL;

                        break;
                    default:
                        break;
                }

                return true;
            }
        });


    }

    public void dataBaseSetup() {

        cargoRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

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
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        truckRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

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
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
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

    private void bindViewFromGeocoderData(LatLng location) {
        Geocoder geocoder = new Geocoder(this);
        Address address = null;
        try {
            address = geocoder.getFromLocation(location.latitude, location.longitude, 1).get(0);
            cargoDetailDestEditText.setText(address.getAddressLine(0));
            cargoDetailZipCodeEditText.setText(address.getAddressLine(1));
            cargoDetailCountryEditText.setText(address.getCountryCode());


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getRoute(LatLng coordination) {

        mapRefreshable = false;

        if (currentRouteMarkerList.size() == 0) {

            mMap.clear();

            currentRouteMarkerList.add(mMap.addMarker(new MarkerOptions()
                    .position(coordination)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))));
            searchEditText.setText("");
            settingZoomAroundMarkerList(currentRouteMarkerList);

            searchEditText.setHint("Enter destination");


        } else if (currentRouteMarkerList.size() == 1) {

            currentRouteMarkerList.add(mMap.addMarker(new MarkerOptions()
                    .position(coordination)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))));

            if (currentSelect == cargoHashMap)
                addCargoBar.setVisibility(View.VISIBLE);
            bindViewFromGeocoderData(currentRouteMarkerList.get(1).getPosition());
            if (currentSelect == truckHashMap)
                addTruckBar.setVisibility(View.VISIBLE);

            LatLng origin = currentRouteMarkerList.get(0).getPosition();
            LatLng dest = currentRouteMarkerList.get(1).getPosition();
            Services s = new Services();
            String url = s.getDirectionsUrl(origin, dest);
            DownloadTask downloadTask = new DownloadTask();
            downloadTask.execute(url);

            settingZoomAroundMarkerList(currentRouteMarkerList);

            currentRouteMarkerList.clear();
            searchEditTextWrapper.setVisibility(View.GONE);
            mapOnClickState = MAP_ONCLICK_NULL;

        } else {
            Log.i("M", "getRoute error");
            currentRouteMarkerList.clear();
        }

    }

    private void settingZoomAroundMarkerList(List<Marker> markerList) {
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


        for (Map.Entry<String, TransportationItem> entry : transportationItemHashMap.entrySet()) {
            Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(entry.getValue().getOrigin().toLatLong())
//                    .icon(BitmapDescriptorFactory.defaultMarker(getResources().getColor(R.color.colorAccent)))
            );
            marker.setTag(entry.getKey());
        }

    }

    private void backToMainMapView() {

        searchEditTextWrapper.setVisibility(View.GONE);
        searchEditText.setText("");
        searchEditText.setHint("Enter start location");
        showFloatingButtons();
        cargoDetailBar.setVisibility(View.GONE);
        cleanCargoDetailBar();
        truckDetailBar.setVisibility(View.GONE);
        cleanTruckDetailBar();
        addTruckBar.setVisibility(View.GONE);
        cleanAddTruckBar();
        addCargoBar.setVisibility(View.GONE);
        cleanAddCargoBar();

        mapRefreshable=true;

        mapOnClickState=MAP_ONCLICK_NULL;
        markerOnClickState=MARKER_ONCLICK_ZOOM_FORM_DETAIL;

        mMap.clear();
        drawTransportationMarkers(currentSelect);

        currentRouteMarkerList.clear();

    }

    private void hideFloatingButtons() {
        floatingActionButtonOpenSearch.setVisibility(View.GONE);
        floatingLoginOptionsButton.setVisibility(View.GONE);
        floatingActionButtonSwitch.setVisibility(View.GONE);
    }

    private void showFloatingButtons() {
        floatingActionButtonOpenSearch.setVisibility(View.VISIBLE);
        floatingLoginOptionsButton.setVisibility(View.VISIBLE);
        floatingActionButtonSwitch.setVisibility(View.VISIBLE);
    }

    private void cleanCargoDetailBar() {
        cargoDetailDeliveryAddressTextView.setVisibility(View.VISIBLE);
        cargoDetailDeliveryAddressTextView.setText("");
        cargoDetailLenghtTextView.setVisibility(View.VISIBLE);
        cargoDetailLenghtTextView.setText("");
        cargoDetailHeightTextView.setVisibility(View.VISIBLE);
        cargoDetailHeightTextView.setText("");
        cargoDetailWidthTextView.setVisibility(View.VISIBLE);
        cargoDetailWidthTextView.setText("");
        cargoDetailWeightTextView.setVisibility(View.VISIBLE);
        cargoDetailWeightTextView.setText("");
        cargoDetailNoteTextView.setVisibility(View.VISIBLE);
        cargoDetailNoteTextView.setText("");
        cargoDetailPhoneNumberTextView.setVisibility(View.VISIBLE);
        cargoDetailPhoneNumberTextView.setText("");
        cargoDetailBudgetTextView.setVisibility(View.VISIBLE);
        cargoDetailBudgetTextView.setText("");
    }

    private void cleanTruckDetailBar() {
        truckDetailDateTextView.setText("");
        truckDetailDateTextViewWrapper.setVisibility(View.VISIBLE);
        truckDetailTelTextView.setText("");
        truckDetailTelTextViewWrapper.setVisibility(View.VISIBLE);
        truckDetailTypeTextView.setText("");
        truckDetailTypeTextViewWrapper.setVisibility(View.VISIBLE);
    }

    private void cleanAddTruckBar() {
        truckDetailDateEditText.setText("");
        truckDetailTelEditText.setText("");
        truckDetailTypeEditText.setText("");
    }

    private void cleanAddCargoBar() {
        cargoDetailCountryEditText.setText("");
        cargoDetailDestEditText.setText("");
        cargoDetailEstimateEditText.setText("");
        cargoDetailHeightEditText.setText("");
        cargoDetailLengthEditText.setText("");
        cargoDetailPhoneNumberEditText.setText("");
        cargoDetailWeightEditText.setText("");
        cargoDetailWidthEditText.setText("");
        cargoDetailZipCodeEditText.setText("");
        additionalDetailsEditText.setText("");

    }
}