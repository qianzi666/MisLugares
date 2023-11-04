package com.example.mislugares;

import android.app.Application;

public class Aplicacion extends Application {
    public GeoPunto posicionActual = new GeoPunto(0.0, 0.0);



    public RepositorioLugares lugares = new LugaresLista();
    @Override public void onCreate() {
        super.onCreate();
    }

    public AdaptadorLugares adaptador = new AdaptadorLugares(lugares);
}
