package com.us47codex.mvvmarch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.us47codex.mvvmarch.interfaces.OnItemClickListener;
import com.us47codex.mvvmarch.roomDatabase.Complaint;

import java.util.List;

/**
 * Created by Upendra Shah on 05 September, 2019 for
 * Project : MVVM-Architecture
 * Company : US47Codex
 * Email : us47codex@gmail.com
 **/
public class ComplaintsAdapter extends RecyclerView.Adapter<ComplaintsAdapter.ComplainsViewHolder> {
    private Context context;
    private List<Complaint> complaintList;
    private OnItemClickListener onItemClickListener;

    public ComplaintsAdapter(Context context, List<Complaint> complaintList) {
        this.context = context;
        this.complaintList = complaintList;
    }

    @NonNull
    @Override
    public ComplainsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_complaint, parent, false);
        return new ComplainsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ComplainsViewHolder holder, int position) {
        Complaint complaint = complaintList.get(position);
        holder.txvComplaintNo.setText(String.valueOf(complaint.getId()));
        holder.txvCustomerName.setText(complaint.getCustomerFullName());
        holder.txvMachineType.setText(complaint.getMcType());
        holder.txvStatus.setText(complaint.getStatus());
        holder.txvDate.setText(complaint.getUpdatedAt());

        holder.llComplaint.setOnClickListener(view -> {
            onItemClickListener.onItemClick(view, complaint);
        });
    }

    @Override
    public int getItemCount() {
        return complaintList.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class ComplainsViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView txvMachineType, txvCustomerName, txvStatus, txvComplaintNo, txvDate;
        LinearLayoutCompat llComplaint;

        public ComplainsViewHolder(@NonNull View itemView) {
            super(itemView);
            llComplaint = itemView.findViewById(R.id.llComplaint);
            txvMachineType = itemView.findViewById(R.id.txvMachineType);
            txvCustomerName = itemView.findViewById(R.id.txvCustomerName);
            txvStatus = itemView.findViewById(R.id.txvStatus);
            txvComplaintNo = itemView.findViewById(R.id.txvComplaintNo);
            txvDate = itemView.findViewById(R.id.txvDate);
        }
    }
}
