package com.cargocrew.cargoapp.models;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Miki on 29.05.2017.
 */
@Setter
@Getter
public abstract class TransportationItem implements Serializable {


    private String name;
    private int value;
    private Point origin;
    private Point destination;
    private String key;
    private String owner;
    private String phoneNumber;
    private String destZipCode;
    private String destCountryCode;
    private String note;

}
