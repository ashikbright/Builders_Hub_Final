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

import dev.afnan.builders_hub.Models.OutPayment;

public class paymentOutAdapter extends RecyclerView.Adapter<paymentOutAdapter.MyViewHolder> {
    Context context;
    ArrayList<OutPayment> outlist;

    public paymentOutAdapter(Context context, ArrayList<OutPayment> outlist) {
        this.context = context;
        this.outlist = outlist;
    }

    @NonNull
    @Override
    public paymentOutAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.payment_details_out, parent, false);
        return new paymentOutAdapter.MyViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull paymentOutAdapter.MyViewHolder holder, int position) {
        OutPayment outPayment = outlist.get(position);
        holder.date.setText(outPayment.getDateOUT());
        holder.desc.setText(outPayment.getDescriptionOut());
        holder.categ2.setText(outPayment.getC_type());
        holder.out.setText(new StringBuilder().append("₹").append(outPayment.getAmtPaid()));
        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        return outlist.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView date, desc, categ2, out;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.txtdateout);
            desc = itemView.findViewById(R.id.txtdescout);
            categ2 = itemView.findViewById(R.id.txtcout);
            out = itemView.findViewById(R.id.txtamtout);
        }
    }
}
