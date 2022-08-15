package dev.twgroups.builders_hub.ViewHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import dev.twgroups.builders_hub.R;

import java.util.ArrayList;

import dev.twgroups.builders_hub.Models.MaterialsModel;

public class MaterialsUsedReceivedAdapter extends RecyclerView.Adapter<MaterialsUsedReceivedAdapter.ViewHolder> {

    ArrayList<MaterialsModel> list;
    Context context;
    int selected;


    public MaterialsUsedReceivedAdapter(ArrayList<MaterialsModel> list, Context context, int i) {
        this.list = list;
        this.context = context;
        selected = i;
    }

    @NonNull
    @Override
    public MaterialsUsedReceivedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        if (selected == 1) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.material_true_row, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.material_false_row, parent, false);
        }

        return new MaterialsUsedReceivedAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MaterialsUsedReceivedAdapter.ViewHolder holder, int position) {
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
