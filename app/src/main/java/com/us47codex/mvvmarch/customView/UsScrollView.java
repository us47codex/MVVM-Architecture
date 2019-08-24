package com.us47codex.mvvmarch.customView;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ScrollView;

import com.us47codex.mvvmarch.R;


/**
 * Created by Upen on 24 August, 2019 for
 * Project : MVVM-Architecture
 * Company : US47Codex
 * Email : us47codex@gmail.com
 **/

public class UsScrollView extends ScrollView {

    private int maxHeight;

    public UsScrollView(Context context) {
        this(context, null);
    }

    public UsScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UsScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.UsMaxHeight);
        try {
            maxHeight = styledAttrs.getDimensionPixelSize(R.styleable.UsMaxHeight_custom_maxHeight, 0);
        } finally {
            styledAttrs.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (maxHeight > 0) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}