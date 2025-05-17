package com.example.proyectointegradorfinal_vargasfigueroa;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.HashMap;

public class MenuActivity extends AppCompatActivity {
    private TextView txtBienvenida;
    private CardView cardMisRecolecciones, cardAgendar, cardRecomendaciones;
    private ImageButton btnLogout;
    private SessionManager sessionManager;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Inicializar Session Manager
        sessionManager = new SessionManager(getApplicationContext());
        // Verificar si el usuario está logueado
        if (!sessionManager.isLoggedIn()) {
            // Redirigir al login
            redirectToLogin();
            return;
        }

        // Inicializar base de datos
        dbHelper = new DatabaseHelper(this);

        // Referencias UI
        txtBienvenida = findViewById(R.id.txtBienvenida);
        cardMisRecolecciones = findViewById(R.id.cardMisRecolecciones);
        cardAgendar = findViewById(R.id.cardAgendar);
        cardRecomendaciones = findViewById(R.id.cardRecomendaciones);
        btnLogout = findViewById(R.id.btnLogout);

        // Obtener datos del usuario actual
        HashMap<String, String> user = sessionManager.getUserDetails();
        String email = user.get(SessionManager.KEY_EMAIL);

        // Configurar mensaje de bienvenida
        if (email != null) {
            txtBienvenida.setText("¡Bienvenido/a al sistema de recolección de aceite usado!");
        }

        // Configurar eventos Click
        cardMisRecolecciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, RecoleccionesActivity.class);
                startActivity(intent);
            }
        });

        cardAgendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, AgendarActivity.class);
                startActivity(intent);
            }
        });

        cardRecomendaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, RecomendacionesActivity.class);
                startActivity(intent);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.logoutUser();
                redirectToLogin();
            }
        });
    }

    private void redirectToLogin() {
        Intent intent = new Intent(MenuActivity.this, LoginActivity.class);
        // Limpiar el historial de actividades para que el usuario no pueda
        // volver atrás después de cerrar sesión
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}