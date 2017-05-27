package com.cargocrew.cargoapp.models;

import com.google.android.gms.maps.model.LatLng;
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

    CargoItem cargoItem = null;

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
        getCargoItem().setDestination(destination);
    }

    public void setCargoItemOrigin(LatLng origin)
    {
        getCargoItem().setOrigin(origin);
    }

    public void setCargoItemRoute(Polyline route)
    {
        getCargoItem().setRoute(route);
    }
}
