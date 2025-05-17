package com.example.proyectointegradorfinal_vargasfigueroa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecomendacionAdapter extends RecyclerView.Adapter<RecomendacionAdapter.RecomendacionViewHolder> {
    private Context context;
    private List<RecomendacionModel> recomendacionesList;

    public RecomendacionAdapter(Context context, List<RecomendacionModel> recomendacionesList) {
        this.context = context;
        this.recomendacionesList = recomendacionesList;
    }

    @NonNull
    @Override
    public RecomendacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recomendacion, parent, false);
        return new RecomendacionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecomendacionViewHolder holder, int position) {
        RecomendacionModel recomendacion = recomendacionesList.get(position);
        holder.txtTitulo.setText(recomendacion.getTitulo());
        holder.txtDescripcion.setText(recomendacion.getDescripcion());
    }

    @Override
    public int getItemCount() {
        return recomendacionesList.size();
    }

    public class RecomendacionViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitulo, txtDescripcion;

        public RecomendacionViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitulo = itemView.findViewById(R.id.txtTitulo);
            txtDescripcion = itemView.findViewById(R.id.txtDescripcion);
        }
    }
}