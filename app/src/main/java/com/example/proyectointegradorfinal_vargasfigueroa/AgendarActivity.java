package com.example.proyectointegradorfinal_vargasfigueroa;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AgendarActivity extends AppCompatActivity {
    private EditText editTextDireccion, editTextCantidadAceite;
    private Button btnDetectarUbicacion, btnAgendar, btnCancelar;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final int PUNTOS_POR_LITRO = 10; // 10 puntos por cada litro de aceite

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agendar);

        // Inicializar
        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(getApplicationContext());
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Verificar si el usuario está logueado
        if (!sessionManager.isLoggedIn()) {
            finish();
            return;
        }

        // Referencias UI
        editTextDireccion = findViewById(R.id.editTextDireccion);
        editTextCantidadAceite = findViewById(R.id.editTextCantidadAceite);
        btnDetectarUbicacion = findViewById(R.id.btnDetectarUbicacion);
        btnAgendar = findViewById(R.id.btnAgendar);
        btnCancelar = findViewById(R.id.btnCancelar);

        // Configurar eventos
        btnDetectarUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detectarUbicacion();
            }
        });

        btnAgendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agendarRecoleccion();
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void detectarUbicacion() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            Geocoder geocoder = new Geocoder(AgendarActivity.this, Locale.getDefault());
                            try {
                                List<Address> addresses = geocoder.getFromLocation(
                                        location.getLatitude(),
                                        location.getLongitude(),
                                        1);
                                if (addresses != null && addresses.size() > 0) {
                                    Address address = addresses.get(0);
                                    StringBuilder direccion = new StringBuilder();
                                    for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                                        direccion.append(address.getAddressLine(i));
                                        if (i < address.getMaxAddressLineIndex()) {
                                            direccion.append(", ");
                                        }
                                    }
                                    editTextDireccion.setText(direccion.toString());
                                }
                            } catch (IOException e) {
                                Toast.makeText(AgendarActivity.this,
                                        "No se pudo obtener la dirección",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(AgendarActivity.this,
                                    "No se pudo obtener la ubicación actual",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void agendarRecoleccion() {
        // Obtener datos del formulario
        String direccion = editTextDireccion.getText().toString().trim();
        String cantidadStr = editTextCantidadAceite.getText().toString().trim();

        // Validar campos
        if (direccion.isEmpty() || cantidadStr.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convertir cantidad de aceite
        double cantidadAceite;
        try {
            cantidadAceite = Double.parseDouble(cantidadStr);
            if (cantidadAceite <= 0) {
                Toast.makeText(this, "La cantidad de aceite debe ser mayor a 0", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Ingresa una cantidad válida", Toast.LENGTH_SHORT).show();
            return;
        }

        // Calcular puntos por la recolección
        int puntos = (int) (cantidadAceite * PUNTOS_POR_LITRO);

        // Obtener datos del usuario actual
        HashMap<String, String> user = sessionManager.getUserDetails();
        String email = user.get(SessionManager.KEY_EMAIL);
        long usuarioId = dbHelper.obtenerUsuarioId(email);

        // Registrar recolección
        long recoleccionId = dbHelper.agregarRecoleccion(direccion, cantidadAceite, usuarioId, puntos);
        if (recoleccionId == -1) {
            Toast.makeText(this, "Error al agendar la recolección", Toast.LENGTH_SHORT).show();
            return;
        }

        // Actualizar puntos del usuario
        int puntosActuales = dbHelper.obtenerPuntosUsuario(email);
        int nuevosPuntos = puntosActuales + puntos;
        dbHelper.actualizarPuntosUsuario(usuarioId, nuevosPuntos);

        Toast.makeText(this, "Recolección agendada con éxito. Has ganado " + puntos + " puntos!", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                detectarUbicacion();
            } else {
                Toast.makeText(this, "Se requieren permisos de ubicación para esta función", Toast.LENGTH_LONG).show();
            }
        }
    }
}