package com.cargocrew.cargoapp.forDrawingRoute;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Miki on 24.05.2017.
 */

public class Services {

    public String getDirectionsUrl(LatLng origin, LatLng dest) {

        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String sensor = "sensor=false";
        String parameters = str_origin + "&" + str_dest + "&" + sensor;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }



}
