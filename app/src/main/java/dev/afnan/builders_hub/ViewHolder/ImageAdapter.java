package dev.afnan.builders_hub.ViewHolder;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import dev.afnan.builders_hub.R;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import dev.afnan.builders_hub.Models.UploadImage;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder> {
    private ArrayList<UploadImage> mlist;
    private Context context;
    private OnItemClickListener mListner;

    public ImageAdapter(Context context, ArrayList<UploadImage> mlist) {
        this.context = context;
        this.mlist = mlist;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.image_container, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        UploadImage uploadImageCurrent = mlist.get(position);
        holder.textViewName.setText(uploadImageCurrent.getDesc());
        //To load images in the recyclerview
        Glide.with(context).load(mlist.get(position).getImageUrl()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener, View.OnClickListener {
        ImageView imageView;
        public TextView textViewName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.pay_out_title);
            imageView = itemView.findViewById(R.id.img);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select Action");
            MenuItem delete = menu.add(Menu.NONE, 1, 1, "Delete");
            delete.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (mListner != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    if (item.getItemId() == 1) {
                        mListner.onDeleteClick(position);
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public void onClick(View v) {
            if (mListner != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListner.onItemClick(position);
                }
            }
        }
    }

    public interface OnItemClickListener {

        void onItemClick(int position);

        void onDeleteClick(int position);
    }

    public void setOnItemClickListner(OnItemClickListener listner) {
        mListner = listner;
    }
}
