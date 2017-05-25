package com.cargocrew.cargoapp.models;

import io.realm.RealmObject;

/**
 * Created by Miki on 24.05.2017.
 */

public class CargoItem extends RealmObject {

    private String name;
    private int value;


    public CargoItem() {
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
}
