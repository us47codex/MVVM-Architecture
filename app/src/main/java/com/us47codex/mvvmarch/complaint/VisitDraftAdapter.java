package com.us47codex.mvvmarch.complaint;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.us47codex.mvvmarch.R;
import com.us47codex.mvvmarch.helper.AppUtils;
import com.us47codex.mvvmarch.interfaces.OnItemClickListener;
import com.us47codex.mvvmarch.roomDatabase.VisitDraft;

import java.util.List;

/**
 * Created by Upendra Shah on 05 September, 2019 for
 * Project : MVVM-Architecture
 * Company : US47Codex
 * Email : us47codex@gmail.com
 **/
public class VisitDraftAdapter extends RecyclerView.Adapter<VisitDraftAdapter.ComplainsViewHolder> {
    private Context context;
    private List<VisitDraft> complaintList;
    private OnItemClickListener onItemClickListener;

    public VisitDraftAdapter(Context context, List<VisitDraft> complaintList) {
        this.context = context;
        this.complaintList = complaintList;
    }

    @NonNull
    @Override
    public ComplainsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_visit_draft, parent, false);
        return new ComplainsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ComplainsViewHolder holder, int position) {
        VisitDraft complaint = complaintList.get(position);
        holder.txvComplaintNo.setText(String.valueOf(complaint.getId()));
        holder.txvCustomerName.setText(complaint.getCustomerName());
        holder.txvMachineType.setText(String.format("%s %s", complaint.getMcType(), AppUtils.isEmpty(complaint.getVisitType()) ? "" : ": " + complaint.getVisitType()));
        holder.txvDate.setText(complaint.getVisitDate());

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