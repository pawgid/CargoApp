package com.cargocrew.cargoapp.models;

import com.google.android.gms.maps.model.Polyline;

/**
 * Created by Miki on 25.05.2017.
 */

public class ValuesSingleton {

    private static final ValuesSingleton ourInstance = new ValuesSingleton();

    public static ValuesSingleton getInstance() {
        return ourInstance;
    }

    private ValuesSingleton() {
    }

    Polyline polyline;


    public Polyline getPolyline() {
        return polyline;
    }

    public void setPolyline(Polyline polyline) {
        this.polyline = polyline;
    }
}
