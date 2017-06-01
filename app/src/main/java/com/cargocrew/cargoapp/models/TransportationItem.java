package com.cargocrew.cargoapp.models;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Miki on 29.05.2017.
 */

public abstract class TransportationItem implements Serializable {

    @Setter
    @Getter
    private String name;
    @Setter
    @Getter
    private int value;
    @Setter
    @Getter
    private Point origin;
    @Setter
    @Getter
    private Point destination;
    @Setter
    @Getter
    private String key;
    @Setter
    @Getter
    private String owner;
    @Setter
    @Getter
    private String phoneNumber;
    @Setter
    @Getter
    private String destZipCode;
    @Getter
    @Setter
    private String destCountryCode;

}
