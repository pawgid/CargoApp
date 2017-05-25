package com.cargocrew.cargoapp.models;

import io.realm.RealmObject;

/**
 * Created by Miki on 24.05.2017.
 */

public class CargoItem extends RealmObject {

    private int a;

    public CargoItem() {
    }

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }
}
