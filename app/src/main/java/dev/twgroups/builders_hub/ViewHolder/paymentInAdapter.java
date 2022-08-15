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

import dev.twgroups.builders_hub.Models.InPayment;

public class paymentInAdapter extends RecyclerView.Adapter<paymentInAdapter.MyViewHolder> {
    Context context;
    ArrayList<InPayment> inList;

    // ArrayList<OutPayment> outlist;

    public paymentInAdapter(Context context, ArrayList<InPayment> inList) {             //, ArrayList<OutPayment> outlist) {
        this.context = context;
        this.inList = inList;

    }

    @NonNull
    @Override
    public paymentInAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.payment_details_in, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull paymentInAdapter.MyViewHolder holder, int position) {
        InPayment inPayment = inList.get(position);
        holder.date.setText(inPayment.getDateIN());
        holder.desc.setText(inPayment.getDescriptionIn());
        holder.categ1.setText(inPayment.getC_type());
        holder.in.setText(new StringBuilder().append("₹").append(inPayment.getAmtrecieved()));
        holder.setIsRecyclable(false);

    }

    @Override
    public int getItemCount() {
        return inList.size();//+ outlist.size();
        // return outlist.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView date, desc, categ1, in;//,categ2,out;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.txtdate);
            desc = itemView.findViewById(R.id.txtdesc);
            categ1 = itemView.findViewById(R.id.txtcin);
            in = itemView.findViewById(R.id.txtamtin);
//            categ2=itemView.findViewById(R.id.txtout);
//            out=itemView.findViewById(R.id.txtamtout);
        }
    }
}
