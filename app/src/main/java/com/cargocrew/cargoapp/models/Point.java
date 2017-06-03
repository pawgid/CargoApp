package com.cargocrew.cargoapp.models;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Miki on 27.05.2017.
 */
@Setter
@Getter
public class Point implements Serializable {

    public double latitude;
    public double longitude;

    public Point(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Point() {
    }

    public Point(LatLng origin) {
        this.latitude = origin.latitude;
        this.longitude = origin.longitude;
    }

    public LatLng toLatLong(){
        return new LatLng(latitude, longitude);
    }


}
