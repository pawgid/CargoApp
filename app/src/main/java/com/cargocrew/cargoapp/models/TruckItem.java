package com.cargocrew.cargoapp.models;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Miki on 29.05.2017.
 */
@Setter
@Getter
public class TruckItem extends TransportationItem {

    private String date;
    private String type;

}
