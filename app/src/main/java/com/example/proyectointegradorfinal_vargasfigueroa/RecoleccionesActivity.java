package com.example.proyectointegradorfinal_vargasfigueroa;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecoleccionesActivity extends AppCompatActivity {
    private TextView txtPuntos;
    private Button btnPuntos, btnRegistros, btnVolver;
    private RecyclerView recyclerViewRecolecciones;
    private RecoleccionAdapter recoleccionAdapter;
    private List<RecoleccionModel> recoleccionesList;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recolecciones);

        // Inicializar
        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(getApplicationContext());

        // Verificar si el usuario está logueado
        if (!sessionManager.isLoggedIn()) {
            finish();
            return;
        }

        // Referencias UI
        txtPuntos = findViewById(R.id.txtPuntos);
        btnPuntos = findViewById(R.id.btnPuntos);
        btnRegistros = findViewById(R.id.btnRegistros);
        btnVolver = findViewById(R.id.btnVolver);
        recyclerViewRecolecciones = findViewById(R.id.recyclerViewRecolecciones);

        // Configurar RecyclerView
        recoleccionesList = new ArrayList<>();
        recoleccionAdapter = new RecoleccionAdapter(this, recoleccionesList);
        recyclerViewRecolecciones.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewRecolecciones.setAdapter(recoleccionAdapter);

        // Configurar eventos
        btnPuntos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarPuntos();
                btnPuntos.setBackgroundResource(R.drawable.button_selected);
                btnRegistros.setBackgroundResource(R.drawable.button_normal);
            }
        });

        btnRegistros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargarRecolecciones();
                btnRegistros.setBackgroundResource(R.drawable.button_selected);
                btnPuntos.setBackgroundResource(R.drawable.button_normal);
            }
        });

        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Por defecto mostrar puntos al inicio
        mostrarPuntos();
        btnPuntos.setBackgroundResource(R.drawable.button_selected);
    }

    private void mostrarPuntos() {
        // Ocultar la lista de recolecciones
        recyclerViewRecolecciones.setVisibility(View.GONE);
        txtPuntos.setVisibility(View.VISIBLE);

        // Obtener el email del usuario actual
        HashMap<String, String> user = sessionManager.getUserDetails();
        String email = user.get(SessionManager.KEY_EMAIL);

        // Obtener los puntos acumulados del usuario
        int puntos = dbHelper.obtenerPuntosUsuario(email);
        txtPuntos.setText("Puntos acumulados: " + puntos);
    }

    private void cargarRecolecciones() {
        // Mostrar la lista de recolecciones
        recyclerViewRecolecciones.setVisibility(View.VISIBLE);
        txtPuntos.setVisibility(View.GONE);

        // Limpiar lista previa
        recoleccionesList.clear();

        // Obtener el email del usuario actual
        HashMap<String, String> user = sessionManager.getUserDetails();
        String email = user.get(SessionManager.KEY_EMAIL);

        // Obtener el ID del usuario
        long usuarioId = dbHelper.obtenerUsuarioId(email);

        // Obtener recolecciones
        Cursor cursor = dbHelper.obtenerRecoleccionesPorDomicilio(usuarioId);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_RECOLECCION_ID));
                String direccion = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_RECOLECCION_DIRECCION));
                double cantAceite = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_RECOLECCION_CANT_ACEITE));
                int puntos = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_RECOLECCION_PTS));

                RecoleccionModel recoleccion = new RecoleccionModel(id, direccion, cantAceite, puntos);
                recoleccionesList.add(recoleccion);
            } while (cursor.moveToNext());

            cursor.close();
        }

        // Notificar al adaptador
        recoleccionAdapter.notifyDataSetChanged();

        // Si no hay recolecciones, mostrar mensaje
        if (recoleccionesList.isEmpty()) {
            Toast.makeText(this, "No hay recolecciones registradas", Toast.LENGTH_SHORT).show();
        }
    }
}