package com.cargocrew.cargoapp.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;
import java.util.List;

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

    Polyline polyline = null;

    public Polyline getPolyline() {
        return polyline;
    }

    public void setPolyline(Polyline polyline) {
        this.polyline = polyline;
    }

    CargoItem cargoItem = new CargoItem();

    public CargoItem getCargoItem() {
        return cargoItem;
    }

    public void setCargoItem(CargoItem cargoItem) {
        this.cargoItem = cargoItem;
    }

    public void setCargoItemName(String name)
    {
        getCargoItem().setName(name);
    }

    public void setCargoItemValue(int value)
    {
        getCargoItem().setValue(value);
    }

    public void setCargoItemDestination(LatLng destination)
    {
        getCargoItem().setDestination(new Point(destination));
    }

    public void setCargoItemOrigin(LatLng origin)
    {
        getCargoItem().setOrigin(new Point(origin));
    }


    public void cleanCargoItem ()
    {
        cargoItem = new CargoItem();
    }


}
