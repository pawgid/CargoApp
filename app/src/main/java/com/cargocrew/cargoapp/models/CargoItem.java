package com.cargocrew.cargoapp.models;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Miki on 24.05.2017.
 */
@Setter
@Getter
public class CargoItem extends TransportationItem {

    private double width;
    private double length;
    private double height;
    private double weight;
    private double offer;
}