package com.developerali.bongbilling.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.developerali.bongbilling.Helpers.Helpers;
import com.developerali.bongbilling.Models.DetailsModel;
import com.developerali.bongbilling.R;
import com.developerali.bongbilling.databinding.ChildRecordEntryBinding;

import java.util.ArrayList;

public class DetailsAdapter extends RecyclerView.Adapter<DetailsAdapter.ViewHolder>{

    ArrayList<DetailsModel> arrayList;
    Activity activity;
    OnItemLongClickListener longClickListener;

    public DetailsAdapter(ArrayList<DetailsModel> arrayList, Activity activity) {
        this.arrayList = arrayList;
        this.activity = activity;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.longClickListener = onItemLongClickListener;
    }

    // Setter for the long-click listener
    public interface OnItemLongClickListener {
        void onItemLongClicked(int position); // Pass only the position
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.child_record_entry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        DetailsModel detailsModel = arrayList.get(position);

        holder.binding.slNo.setText(String.valueOf(position+1));
        holder.binding.des.setText(Helpers.capitalizeSentences(detailsModel.getDescription()));

        holder.binding.price.setText(String.valueOf(detailsModel.getPrice()));
        holder.binding.pax.setText(String.valueOf(detailsModel.getPAX()));
        holder.binding.room.setText(String.valueOf(detailsModel.getRoom()));
        holder.binding.night.setText(String.valueOf(detailsModel.getNights()));
        holder.binding.total.setText(String.valueOf(detailsModel.getTotal()));

        // Hide fields if value is 0
        if (detailsModel.getPrice() == 0) holder.binding.price.setText("");
        if (detailsModel.getPAX() == 0) holder.binding.pax.setText("");
        if (detailsModel.getRoom() == 0) holder.binding.room.setText("");
        if (detailsModel.getNights() == 0) holder.binding.night.setText("");
        if (detailsModel.getTotal() == 0) holder.binding.total.setText("");

        // Hide SL No if all values are 0
        if (detailsModel.getTotal() == 0 && detailsModel.getNights() == 0 && detailsModel.getPAX() == 0
                && detailsModel.getRoom() == 0 && detailsModel.getPrice() == 0) {
            holder.binding.slNo.setText("");
        }

        // Alternate row background color
        if (position % 2 == 0) {
            holder.binding.mainBack.setBackgroundColor(activity.getColor(R.color.gray));
        }

        // Handle long-click event
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // Show confirmation dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("Delete Entry");
                builder.setMessage("Are you sure you want to delete this entry?");
                builder.setPositiveButton("Delete", (dialog, which) -> {
                    // Remove the item from the list
                    arrayList.remove(position);
                    notifyItemRemoved(position);

                    // Notify the listener
                    if (longClickListener != null) {
                        longClickListener.onItemLongClicked(position);
                    }
                });
                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
                builder.show();
                return true; // Consume the long-click event
            }
        });
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ChildRecordEntryBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ChildRecordEntryBinding.bind(itemView);
        }
    }
}
