package com.cargocrew.cargoapp.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;

import java.io.Serializable;

import io.realm.RealmObject;

/**
 * Created by Miki on 24.05.2017.
 */

public class CargoItem implements Serializable {

    private String name;
    private int value;
    private LatLng origin;
    private LatLng destination;
    private Polyline route;


    public CargoItem() {
    }

    public CargoItem(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public LatLng getOrigin() {
        return origin;
    }

    public void setOrigin(LatLng origin) {
        this.origin = origin;
    }

    public LatLng getDestination() {
        return destination;
    }

    public void setDestination(LatLng destination) {
        this.destination = destination;
    }

    public Polyline getRoute() {
        return route;
    }

    public void setRoute(Polyline route) {
        this.route = route;
    }
}
