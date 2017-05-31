package com.cargocrew.cargoapp.models;

import com.google.android.gms.maps.model.LatLng;

import lombok.Getter;
import lombok.Setter;

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


//    @Setter
//    @Getter
//    private boolean searchClickable = false;

    @Setter
    @Getter
    CargoItem cargoItem = new CargoItem();

    public void setCargoItemName(String name) {
        getCargoItem().setName(name);
    }

    public void setCargoItemValue(int value) {
        getCargoItem().setValue(value);
    }

    public void setCargoItemDestination(LatLng destination) {
        getCargoItem().setDestination(new Point(destination));
    }

    public void setCargoItemOrigin(LatLng origin) {
        getCargoItem().setOrigin(new Point(origin));
    }


    public void cleanCargoItem() {
        cargoItem = new CargoItem();
    }


}
