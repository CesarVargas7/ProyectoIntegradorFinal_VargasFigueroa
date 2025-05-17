package com.example.proyectointegradorfinal_vargasfigueroa;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RecomendacionesActivity extends AppCompatActivity {
    private Button btnVolver;
    private RecyclerView recyclerViewRecomendaciones;
    private RecomendacionAdapter recomendacionAdapter;
    private List<RecomendacionModel> recomendacionesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recomendaciones);

        // Referencias UI
        btnVolver = findViewById(R.id.btnVolver);
        recyclerViewRecomendaciones = findViewById(R.id.recyclerViewRecomendaciones);

        // Configurar RecyclerView
        recomendacionesList = new ArrayList<>();
        recomendacionAdapter = new RecomendacionAdapter(this, recomendacionesList);
        recyclerViewRecomendaciones.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewRecomendaciones.setAdapter(recomendacionAdapter);

        // Agregar recomendaciones
        cargarRecomendaciones();

        // Configurar evento de botón volver
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void cargarRecomendaciones() {
        recomendacionesList.add(new RecomendacionModel(
                "No arrojes aceite por el desagüe",
                "Un solo litro de aceite usado puede contaminar hasta 1,000 litros de agua. Utiliza nuestro servicio de recolección."
        ));

        recomendacionesList.add(new RecomendacionModel(
                "Almacena el aceite usado en botellas plásticas",
                "Una vez que el aceite se haya enfriado, guárdalo en botellas de plástico con tapa hermética para evitar derrames."
        ));

        recomendacionesList.add(new RecomendacionModel(
                "Filtra el aceite antes de almacenarlo",
                "Utiliza un colador o paño fino para eliminar residuos sólidos antes de almacenar el aceite usado."
        ));

        recomendacionesList.add(new RecomendacionModel(
                "Reutilización del aceite",
                "No uses el mismo aceite más de dos veces, ya que puede generar sustancias dañinas para la salud."
        ));

        recomendacionesList.add(new RecomendacionModel(
                "Beneficios del reciclaje de aceite",
                "El aceite usado puede convertirse en biodiesel, jabones y otros productos. ¡Contribuye al medio ambiente!"
        ));

        // Notificar al adaptador
        recomendacionAdapter.notifyDataSetChanged();
    }
}