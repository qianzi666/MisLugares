package com.example.mislugares;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    private static final int SOLICITUD_PERMISO_LOCALIZACION = 1;
    private CasosUsoLocalizacion usoLocalizacion;

    private RecyclerView recyclerView;
    public AdaptadorLugares adaptador;



    private static MediaPlayer mp;
    private static int currentPlaybackPosition = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (mp == null) {
            mp = MediaPlayer.create(this, R.raw.audio);
        }

        // 检查是否有之前保存的播放位置
        if (currentPlaybackPosition > 0) {
            mp.seekTo(currentPlaybackPosition);
        }
        mp.start();



        adaptador = ((Aplicacion) getApplication()).adaptador;
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adaptador);


        adaptador.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos= recyclerView.getChildAdapterPosition(v);
                mostrarLugar(pos);
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        usoLocalizacion = new CasosUsoLocalizacion(this,
                SOLICITUD_PERMISO_LOCALIZACION);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentPosition", currentPlaybackPosition);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentPlaybackPosition = savedInstanceState.getInt("currentPosition");
    }




    public void lanzarAcercaDe(View view){
        Intent i = new Intent(this, AcercaDeActivity.class);
        startActivity(i);

    }

    public void lanzarPreferencias(View view){
        Intent i = new Intent(this, PreferenciasActivity.class);
        startActivity(i);

    }

    void mostrarLugar(int pos) {
        Intent i = new Intent(this, VistaLugarActivity.class);
        i.putExtra("pos", pos);
        startActivity(i);
        /*Toast.makeText(MainActivity.this, "Selección: " +
                        ((Aplicacion) getApplication()).lugares.elemento(pos).getNombre(),
                Toast.LENGTH_SHORT).show();*/
    }


    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true; /** true -> el menú ya está visible */
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            lanzarPreferencias(null);
            return true;
        }

        if (id == R.id.acercaDe) {
            // 处理"Acerca de..."菜单项的点击事件
            // 在这里执行相应的操作
            lanzarAcercaDe(null); // 调用你的 lanzarAcercaDe 方法
            mp.pause(); // 暂停播放
            return true;
        } else if (id == R.id.menu_buscar) {
            // 处理"Buscar"菜单项的点击事件
            // 在这里执行相应的操作
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override public void onRequestPermissionsResult(int requestCode,
                                                     String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if (requestCode == SOLICITUD_PERMISO_LOCALIZACION
                && grantResults.length == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            usoLocalizacion.permisoConcedido();
    }


    @Override
    protected void onPause() {
        super.onPause();

        if (mp != null && mp.isPlaying()) {
            currentPlaybackPosition = mp.getCurrentPosition();
            mp.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mp != null && !mp.isPlaying()) {
            mp.start();
        }
    }

    public void compartir(Lugar lugar) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT, lugar.getNombre() + " - " + lugar.getUrl());
        startActivity(i);
    }
    public void llamarTelefono(Lugar lugar) {
        startActivity(new Intent(Intent.ACTION_DIAL,
                Uri.parse("tel:" + lugar.getTelefono())));
    }
    public void verPgWeb(Lugar lugar) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(lugar.getUrl())));
    }
    public final void verMapa(Lugar lugar) {
        double lat = lugar.getPosicion().getLatitud();
        double lon = lugar.getPosicion().getLongitud();
        Uri uri = lugar.getPosicion() != GeoPunto.SIN_POSICION
                ? Uri.parse("geo:" + lat + ',' + lon)
                : Uri.parse("geo:0,0?q=" + lugar.getDireccion());
        startActivity(new Intent("android.intent.action.VIEW", uri));
    }







}