package com.cargocrew.cargoapp.models;

import java.io.Serializable;

/**
 * Created by Miki on 29.05.2017.
 */

public abstract class TransportationItem implements Serializable {

    private String name;
    private int value;
    private Point origin;
    private Point destination;

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

    public Point getOrigin() {
        return origin;
    }

    public void setOrigin(Point origin) {
        this.origin = origin;
    }

    public Point getDestination() {
        return destination;
    }

    public void setDestination(Point destination) {
        this.destination = destination;
    }
}
