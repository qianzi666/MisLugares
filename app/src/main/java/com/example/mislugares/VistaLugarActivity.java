package com.example.mislugares;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.DateFormat;
import java.util.Date;

public class VistaLugarActivity extends AppCompatActivity {

    private Activity actividad;

    private RepositorioLugares lugares;
    private int pos;
    private Lugar lugar;
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_lugar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lugares = ((Aplicacion) getApplication()).lugares;
        Bundle extras = getIntent().getExtras();
        pos = extras.getInt("pos", 0);
        lugar = lugares.elemento(pos);

        actividad = this;


    }

    public void actualizaVistas() {
        TextView nombre = findViewById(R.id.nombre);
        ImageView logoTipo = findViewById(R.id.logo_tipo);
        TextView tipo = findViewById(R.id.tipo);
        TextView direccion = findViewById(R.id.direccion);
        TextView telefono = findViewById(R.id.telefono);
        TextView url = findViewById(R.id.url);
        TextView comentario = findViewById(R.id.comentario);
        TextView fecha = findViewById(R.id.fecha);
        TextView hora = findViewById(R.id.hora);
        RatingBar valoracion = findViewById(R.id.valoracion);
        nombre.setText(lugar.getNombre());
        logoTipo.setImageResource(lugar.getTipo().getRecurso());
        tipo.setText(lugar.getTipo().getTexto());
        direccion.setText(lugar.getDireccion());
        telefono.setText(Integer.toString(lugar.getTelefono()));
        url.setText(lugar.getUrl());
        comentario.setText(lugar.getComentario());
        fecha.setText(DateFormat.getDateInstance().format(
                new Date(lugar.getFecha())));
        hora.setText(DateFormat.getTimeInstance().format(
                new Date(lugar.getFecha())));
        valoracion.setRating(lugar.getValoracion());
        valoracion.setOnRatingBarChangeListener(
                new RatingBar.OnRatingBarChangeListener() {
                    @Override public void onRatingChanged(RatingBar ratingBar,
                                                          float valor, boolean fromUser) {
                        lugar.setValoracion(valor);
                    }
                });
    }

    // INTENCIONES
    public void compartir(Lugar lugar) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT,
                lugar.getNombre() + " - " + lugar.getUrl());
        actividad.startActivity(i);
    }
    public void llamarTelefono(Lugar lugar) {
        actividad.startActivity(new Intent(Intent.ACTION_CALL,
                Uri.parse("tel:" + lugar.getTelefono())));
    }
    public void verPgWeb(Lugar lugar) {
        actividad.startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse(lugar.getUrl())));
    }
    public final void verMapa(Lugar lugar) {
        double lat = lugar.getPosicion().getLatitud();
        double lon = lugar.getPosicion().getLongitud();
        Uri uri = lugar.getPosicion() != GeoPunto.SIN_POSICION
                ? Uri.parse("geo:" + lat + ',' + lon)
                : Uri.parse("geo:0,0?q=" + lugar.getDireccion());
        actividad.startActivity(new Intent("android.intent.action.VIEW", uri));
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vista_lugar, menu);
        return true;
    }
    @Override public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.accion_compartir) {
            compartir(lugar);
            return true;
        }
        if (id == R.id.accion_llegar) {
            verMapa(lugar);
            return true;
        }
        if (id == R.id.accion_editar) {
            editarLugar(pos);
            return true;
        }
        if (id == R.id.accion_borrar) {
            borrarLugar(pos);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void borrarLugar(int id) {
        new AlertDialog.Builder(this)
                .setTitle("Borrado de lugar")
                .setMessage("¿Estás seguro que quieres eliminar este lugar?")
                .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        lugares.borrar(id);
                        finish();
                    }
                })
                .setNegativeButton("NO", null)
                .show();
    }

void editarLugar(int pos) {
        Intent i = new Intent(this, EdicionLugarActivity.class);
        i.putExtra("pos", pos);
        startActivity(i);
        /*Toast.makeText(MainActivity.this, "Selección: " +
                        ((Aplicacion) getApplication()).lugares.elemento(pos).getNombre(),
                Toast.LENGTH_SHORT).show();*/
    }

    @Override
    public void onResume() {
        super.onResume();
        actualizaVistas();
    }

    public void verMapa(View view) {
        verMapa(lugar);
    }
    public void llamarTelefono(View view) {
        llamarTelefono(lugar);
    }
    public void verPgWeb(View view) {
        verPgWeb(lugar);
    }







}
