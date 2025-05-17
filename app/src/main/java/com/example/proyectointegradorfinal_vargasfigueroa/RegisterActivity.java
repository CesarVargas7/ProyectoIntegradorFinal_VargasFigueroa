package com.example.proyectointegradorfinal_vargasfigueroa;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {
    private EditText editTextNombre, editTextEmail, editTextPassword, editTextDomicilio;
    private Button btnRegistro, btnDetectarUbicacion, btnCancelar;
    private RadioGroup radioGroupSexo, radioGroupTipoDomicilio;
    private DatabaseHelper dbHelper;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inicializar la base de datos
        dbHelper = new DatabaseHelper(this);

        // Inicializar cliente de ubicación
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Referenciar componentes UI
        editTextNombre = findViewById(R.id.editTextNombre);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextDomicilio = findViewById(R.id.editTextDomicilio);
        radioGroupSexo = findViewById(R.id.radioGroupSexo);
        radioGroupTipoDomicilio = findViewById(R.id.radioGroupTipoDomicilio);
        btnRegistro = findViewById(R.id.btnRegistro);
        btnDetectarUbicacion = findViewById(R.id.btnDetectarUbicacion);
        btnCancelar = findViewById(R.id.btnCancelar);

        // Configurar eventos de botones
        btnDetectarUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detectarUbicacion();
            }
        });

        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarUsuario();
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                            Geocoder geocoder = new Geocoder(RegisterActivity.this, Locale.getDefault());
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
                                    editTextDomicilio.setText(direccion.toString());
                                }
                            } catch (IOException e) {
                                Toast.makeText(RegisterActivity.this,
                                        "No se pudo obtener la dirección",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(RegisterActivity.this,
                                    "No se pudo obtener la ubicación actual",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void registrarUsuario() {
        // Obtener datos del formulario
        String nombre = editTextNombre.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String domicilio = editTextDomicilio.getText().toString().trim();

        // Obtener sexo seleccionado
        int sexoRadioId = radioGroupSexo.getCheckedRadioButtonId();
        if (sexoRadioId == -1) {
            Toast.makeText(this, "Por favor selecciona tu sexo", Toast.LENGTH_SHORT).show();
            return;
        }
        RadioButton radioSexo = findViewById(sexoRadioId);
        String sexo = radioSexo.getText().toString();

        // Obtener tipo de domicilio seleccionado
        int tipoDomicilioRadioId = radioGroupTipoDomicilio.getCheckedRadioButtonId();
        if (tipoDomicilioRadioId == -1) {
            Toast.makeText(this, "Por favor selecciona el tipo de domicilio", Toast.LENGTH_SHORT).show();
            return;
        }
        RadioButton radioTipoDomicilio = findViewById(tipoDomicilioRadioId);
        String tipoDomicilio = radioTipoDomicilio.getText().toString();

        // Validar campos
        if (nombre.isEmpty() || email.isEmpty() || password.isEmpty() || domicilio.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Registrar domicilio primero
        long domicilioId = dbHelper.agregarDomicilio(domicilio, tipoDomicilio);
        if (domicilioId == -1) {
            Toast.makeText(this, "Error al registrar el domicilio", Toast.LENGTH_SHORT).show();
            return;
        }

        // Registrar usuario
        long usuarioId = dbHelper.agregarUsuario(nombre, sexo, domicilioId, password, email);
        if (usuarioId == -1) {
            Toast.makeText(this, "Error al registrar el usuario, el email ya puede estar en uso", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show();
        finish(); // Volver a la actividad anterior (login)
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