package com.example.mislugares;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.Context.LOCATION_SERVICE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.ActivityCompat;

public class CasosUsoLocalizacion implements LocationListener {
    private static final String TAG = "MisLugares";
    private Activity actividad;
    private int codigoPermiso;
    private LocationManager manejadorLoc;
    private Location mejorLoc;
    private GeoPunto posicionActual;
    private AdaptadorLugares adaptador;
    public CasosUsoLocalizacion(Activity actividad, int codigoPermiso) {
        this.actividad = actividad;
        this.codigoPermiso = codigoPermiso;
        manejadorLoc = (LocationManager) actividad.getSystemService(
                LOCATION_SERVICE);
        posicionActual = ((Aplicacion) actividad.getApplication())
                .posicionActual;
        adaptador = ((Aplicacion) actividad.getApplication()).adaptador;
        ultimaLocalizazion();
    }

    public boolean hayPermisoLocalizacion() {
        return (ActivityCompat.checkSelfPermission(
                actividad, ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED);
    }

    void ultimaLocalizazion() {
        if (hayPermisoLocalizacion()) {
            if (manejadorLoc.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                actualizaMejorLocaliz(manejadorLoc.getLastKnownLocation(
                        LocationManager.GPS_PROVIDER));
            }
            if (manejadorLoc.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                actualizaMejorLocaliz(manejadorLoc.getLastKnownLocation(
                        LocationManager.NETWORK_PROVIDER));
            }
        } else {
            solicitarPermiso(ACCESS_FINE_LOCATION,
                    "Sin el permiso localización no puedo mostrar la distancia"+
                            " a los lugares.", codigoPermiso, actividad);
        }
    }

    private void solicitarPermiso(String accessFineLocation, String s, int codigoPermiso, Activity actividad) {
    }

    public void permisoConcedido() {
        ultimaLocalizazion();
        activarProveedores();
        adaptador.notifyDataSetChanged();
    }
    @SuppressLint("MissingPermission")
    private void activarProveedores() {
        if (hayPermisoLocalizacion()) {
            if (manejadorLoc.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                manejadorLoc.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        20 * 1000, 5,(LocationListener) this);
            }
            if (manejadorLoc.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                manejadorLoc.requestLocationUpdates(LocationManager
                        .NETWORK_PROVIDER, 10 * 1000, 10,(LocationListener) this);
            }
        } else {
            solicitarPermiso(ACCESS_FINE_LOCATION,
                    "Sin el permiso localización no puedo mostrar la distancia"+
                            " a los lugares.", codigoPermiso, actividad);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Nueva localización: " + location);
        actualizaMejorLocaliz(location);
        adaptador.notifyDataSetChanged();
    }

    @Override
    public void onProviderDisabled(String proveedor) {
        Log.d(TAG, "Se deshabilita: " + proveedor);
        activarProveedores();
    }

    @Override
    public void onProviderEnabled(String proveedor) {
        Log.d(TAG, "Se habilita: " + proveedor);
        activarProveedores();
    }

    @Override
    public void onStatusChanged(String proveedor, int estado, Bundle extras) {
        Log.d(TAG, "Cambia estado: " + proveedor);
        activarProveedores();
    }

    private static final long DOS_MINUTOS = 2 * 60 * 1000;
    private void actualizaMejorLocaliz(Location localiz) {
        if (localiz != null && (mejorLoc == null
                || localiz.getAccuracy() < 2*mejorLoc.getAccuracy()
                || localiz.getTime() - mejorLoc.getTime() > DOS_MINUTOS)) {
            Log.d(TAG, "Nueva mejor localización");
            mejorLoc = localiz;
            posicionActual.setLatitud(localiz.getLatitude());
            posicionActual.setLongitud(localiz.getLongitude());
        }
    }



}

