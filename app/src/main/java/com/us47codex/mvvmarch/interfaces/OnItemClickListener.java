package com.us47codex.mvvmarch.interfaces;

import android.view.View;

@SuppressWarnings("EmptyMethod")
public interface OnItemClickListener {
    void onItemClick(int pos);

    void onItemClick(View view, Object object);

    void onItemClick(View view, int pos, Object object);

    void onItemLongClick(View view, int pos, Object object);

    void onOtherItemSave(int id, View view, String value);
}
