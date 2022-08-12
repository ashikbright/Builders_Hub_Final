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

public class MaterialUsedAdapter extends RecyclerView.Adapter<Material_Adapter.ViewHolder> {
    ArrayList<MaterialsModel> listU;
    Context context;


    public MaterialUsedAdapter(ArrayList<MaterialsModel> list, Context context) {
        this.listU = list;
        this.context = context;
    }

    @NonNull
    @Override
    public Material_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.material_true_row, parent, false);
        return new Material_Adapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Material_Adapter.ViewHolder holder, int position) {
        MaterialsModel materialsModel = listU.get(position);
        holder.txtMaterial.setText(materialsModel.getUsedMaterial());
        holder.txtUsed.setText(materialsModel.getUsedQty());

    }

    @Override
    public int getItemCount() {
        return listU.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtMaterial;
        public TextView txtUsed;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtMaterial = (TextView) itemView.findViewById(R.id.tvMaterialU);
            txtUsed = (TextView) itemView.findViewById(R.id.tvUsed);

//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (listener != null) {
//                        int position = getAdapterPosition();
//                        if (position != RecyclerView.NO_POSITION) {
//                            listener.onItemClick(position);
//                        }
//                    }
//                }
//            });
        }
    }
}
