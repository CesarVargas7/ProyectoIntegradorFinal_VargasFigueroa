package com.example.proyectointegradorfinal_vargasfigueroa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecoleccionAdapter extends RecyclerView.Adapter<RecoleccionAdapter.RecoleccionViewHolder> {
    private Context context;
    private List<RecoleccionModel> recoleccionesList;

    public RecoleccionAdapter(Context context, List<RecoleccionModel> recoleccionesList) {
        this.context = context;
        this.recoleccionesList = recoleccionesList;
    }

    @NonNull
    @Override
    public RecoleccionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recoleccion, parent, false);
        return new RecoleccionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecoleccionViewHolder holder, int position) {
        RecoleccionModel recoleccion = recoleccionesList.get(position);
        holder.txtDireccion.setText("Dirección: " + recoleccion.getDireccion());
        holder.txtCantidadAceite.setText("Cantidad de aceite: " + recoleccion.getCantidadAceite() + " litros");
        holder.txtPuntos.setText("Puntos: " + recoleccion.getPuntos());
    }

    @Override
    public int getItemCount() {
        return recoleccionesList.size();
    }

    public class RecoleccionViewHolder extends RecyclerView.ViewHolder {
        TextView txtDireccion, txtCantidadAceite, txtPuntos;

        public RecoleccionViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDireccion = itemView.findViewById(R.id.txtDireccion);
            txtCantidadAceite = itemView.findViewById(R.id.txtCantidadAceite);
            txtPuntos = itemView.findViewById(R.id.txtPuntos);
        }
    }
}