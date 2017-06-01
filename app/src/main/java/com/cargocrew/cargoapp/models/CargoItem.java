package com.cargocrew.cargoapp.models;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Miki on 24.05.2017.
 */

public class CargoItem extends TransportationItem {


    @Setter
    @Getter
    private double width;
    @Setter
    @Getter
    private double length;
    @Setter
    @Getter
    private double height;
    @Setter
    @Getter
    private double weight;
    @Setter
    @Getter
    private String note;
    @Setter
    @Getter
    private double offer;
}