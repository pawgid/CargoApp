package com.cargocrew.cargoapp.models;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Miki on 29.05.2017.
 */

public class TruckItem extends TransportationItem {


    @Setter
    @Getter
    private String date;
    @Setter
    @Getter
    private String type;



}
