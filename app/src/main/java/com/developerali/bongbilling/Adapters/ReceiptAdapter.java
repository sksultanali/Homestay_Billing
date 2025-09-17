package com.developerali.bongbilling.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.developerali.bongbilling.Models.InvoiceData;
import com.developerali.bongbilling.R;
import com.developerali.bongbilling.ReceiptDetails;
import com.developerali.bongbilling.databinding.ChildReceiptBinding;

import java.util.List;

public class ReceiptAdapter extends RecyclerView.Adapter<ReceiptAdapter.ViewHolder>{

    Activity activity;
    List<InvoiceData> dataList;

    public ReceiptAdapter(Activity activity, List<InvoiceData> dataList) {
        this.activity = activity;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.child_receipt, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InvoiceData data = dataList.get(position);

        if (data.getInvoiceNo().contains("SIT")){
            holder.binding.img.setImageDrawable(activity.getDrawable(R.drawable.sittong));
        }else if (data.getInvoiceNo().contains("KOL")){
            holder.binding.img.setImageDrawable(activity.getDrawable(R.drawable.kolakham));
        } else if (data.getInvoiceNo().contains("KAF")) {
            holder.binding.img.setImageDrawable(activity.getDrawable(R.drawable.kaffergaon11));
        }else if (data.getInvoiceNo().contains("CHAR")){
            holder.binding.img.setImageDrawable(activity.getDrawable(R.drawable.charkholea));
        } else if (data.getInvoiceNo().contains("PED")) {
            holder.binding.img.setImageDrawable(activity.getDrawable(R.drawable.peddong_img));
        }else if (data.getInvoiceNo().contains("DAW")) {
            holder.binding.img.setImageDrawable(activity.getDrawable(R.drawable.dawaipani));
        }else {
            holder.binding.img.setImageDrawable(activity.getDrawable(R.drawable.place_img));
        }

        holder.binding.invoiceNo.setText(data.getInvoiceNo() + "\n" + data.getgName());
        holder.binding.invoiceDate.setText(data.getCreated_at());
        holder.binding.journeyDate.setText(Html.fromHtml(
                "Due - <b>" + ((int) data.getDue()) + "</b>, Dis - <b>" + ((int) data.getDiscount()) + "</b>"
        ));
        holder.binding.email.setText(Html.fromHtml(
                "Total - <b>" + ((int) data.getTotal()) + "</b>, Adv - <b>" + ((int) data.getAdvance()) + "</b>"
        ));

        holder.itemView.setOnClickListener(v->{
            Intent i = new Intent(activity.getApplicationContext(), ReceiptDetails.class);
            i.putExtra("model", data);
            activity.startActivity(i);
        });



    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ChildReceiptBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ChildReceiptBinding.bind(itemView);
        }
    }
}
