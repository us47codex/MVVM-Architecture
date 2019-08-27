package com.us47codex.mvvmarch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.jakewharton.rxbinding2.view.RxView;
import com.us47codex.mvvmarch.interfaces.OnItemClickListener;
import com.us47codex.mvvmarch.models.NavDrawerModel;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.annotations.Nullable;

public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.MyViewHolder> {
    private final Context context;
    private final List<NavDrawerModel> items;
    private OnItemClickListener onClickListener;

    public NavigationDrawerAdapter(Context context, List<NavDrawerModel> items) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_navigation_drawer, parent, false);
        return new MyViewHolder(v);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        NavDrawerModel item = items.get(position);
        holder.navTitle.setText(String.format("%s", item.getTitle()));
        holder.navGroupTitle.setVisibility(View.GONE);

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.mipmap.ic_launcher);
        requestOptions.error(R.mipmap.ic_launcher);
        Glide.with(context)
                .setDefaultRequestOptions(requestOptions)
                .load(item.getImgId())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, com.bumptech.glide.request.target.Target<Drawable> target, boolean isFirstResource) {

                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, com.bumptech.glide.request.target.Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                        return false;
                    }
                }).into(holder.imgIcon);

    }

    @Override
    public int getItemCount() {
        if (items == null) {
            return 0;
        }
        return items.size();
    }

    public void setOnClickListener(OnItemClickListener onLoadMoreListener) {
        this.onClickListener = onLoadMoreListener;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private final LinearLayout linMain;
        private final ImageView imgIcon;
        private final AppCompatTextView navTitle, navGroupTitle;

        @SuppressWarnings("ResultOfMethodCallIgnored")
        @SuppressLint("CheckResult")
        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            linMain = itemView.findViewById(R.id.linMain);
            navTitle = itemView.findViewById(R.id.navTitle);
            navGroupTitle = itemView.findViewById(R.id.navGroupTitle);
            imgIcon = itemView.findViewById(R.id.imgIcon);

            RxView.clicks(linMain).throttleFirst(500, TimeUnit.MILLISECONDS).subscribe(empty -> {
                if (onClickListener != null) {
                    onClickListener.onItemClick(linMain, getAdapterPosition(), items.get(getAdapterPosition()));
                }
            });
        }
    }
}