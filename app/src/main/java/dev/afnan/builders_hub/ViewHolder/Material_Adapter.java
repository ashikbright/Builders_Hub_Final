package dev.afnan.builders_hub.ViewHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import dev.afnan.builders_hub.R;

import java.util.ArrayList;

import dev.afnan.builders_hub.Models.MaterialsModel;

public class Material_Adapter extends RecyclerView.Adapter<Material_Adapter.ViewHolder> {

    ArrayList<MaterialsModel> list;
    Context context;
    int selected = 1;


    public Material_Adapter(ArrayList<MaterialsModel> list, Context context, int i) {
        this.list = list;
        this.context = context;
        selected = i;
    }

    @NonNull
    @Override
    public Material_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        if (selected == 1) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.material_true_row, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.material_false_row, parent, false);
        }

        return new Material_Adapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Material_Adapter.ViewHolder holder, int position) {
        MaterialsModel materialsModel = list.get(position);

        holder.txtMaterial.setText(materialsModel.getMaterial());
        holder.txtPurchased.setText(materialsModel.getQuantity());

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtMaterial;
        public TextView txtPurchased;
        public TextView txtUsed;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtMaterial = (TextView) itemView.findViewById(R.id.tvMaterial);
            txtPurchased = (TextView) itemView.findViewById(R.id.tvPurchased);


        }

    }
}
