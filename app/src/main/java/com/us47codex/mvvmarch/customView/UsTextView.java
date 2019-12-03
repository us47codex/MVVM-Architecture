package com.us47codex.mvvmarch.customView;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatTextView;

import com.us47codex.mvvmarch.R;

/**
 * Created by Upendra Shah on 24 August, 2019 for
 * Project : MVVM-Architecture
 * Company : US47Codex
 * Email : us47codex@gmail.com
 **/

public class UsTextView extends AppCompatTextView {

    private Drawable drawableRight;
    private Drawable drawableLeft;
    private Drawable drawableTop;
    private Drawable drawableBottom;

    int actionX, actionY, mHeight;

    private DrawableClickListener clickListener;
    private String customFontName;


    public UsTextView(Context context) {
        super(context);
    }

    public UsTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        customAttr(context, attrs);
    }

    public UsTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        customAttr(context, attrs);
    }

    private void customAttr(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.UsTypeface);
            int typeface = a.getInt(R.styleable.UsTypeface_custom_typeface, 0);
            int inputType = a.getInt(R.styleable.UsTypeface_custom_input_type, 0);
            int fontName;
            try {
                switch (typeface) {
                    case 1:
                        fontName = R.string.typeface_open_sans_regular;
                        break;
                    case 2:
                        fontName = R.string.typeface_open_sans_semi_bold;
                        break;
                    case 3:
                        fontName = R.string.typeface_open_sans_bold_light;
                        break;
                    case 4:
                        fontName = R.string.typeface_open_sans_bold;
                        break;
                    default:
                        fontName = R.string.typeface_open_sans_regular;
                }

                customFontName = getResources().getString(fontName);
                Typeface myTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/" + customFontName);
                this.setTypeface(myTypeface);

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (this.getText().length() > 0) {
                    String text = this.getText().toString();
                    String[] strArray;
                    StringBuilder builder;
                    switch (inputType) {
                        case 0:
                            this.setText(text);
                            break;
                        case 1:
                            this.setText(text.toLowerCase());
                            break;
                        case 2:
                            this.setText(text.toUpperCase());
                            break;
                        case 3:
                            strArray = text.split(" ");
                            builder = new StringBuilder();
                            for (String s : strArray) {
                                String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
                                builder.append(cap + " ");
                            }
                            this.setText(builder.toString());
                            break;
                        case 4:
                            strArray = text.split("\\. ");
                            builder = new StringBuilder();
                            for (String s : strArray) {
                                String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
                                builder.append(cap + " ");
                            }
                            this.setText(builder.toString());
                            break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            a.recycle();
        }

    }

    public String wordFirstCap(String str) {
        String[] words = str.trim().split(" ");
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            if (words[i].trim().length() > 0) {
                ret.append(Character.toUpperCase(words[i].trim().charAt(0)));
                ret.append(words[i].trim().substring(1));
                if (i < words.length - 1) {
                    ret.append(' ');
                }
            }
        }

        return ret.toString();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mHeight = h;
        super.onSizeChanged(w, h, oldw, oldh);
    }

//    @Override
//    public void setText(CharSequence text, BufferType type) {
//        if (text.length() > 0) {
//            text = String.valueOf(text.charAt(0)).toUpperCase() + text.subSequence(1, text.length());
//        }
//        super.setText(text, type);
//    }

    @Override
    public void setCompoundDrawables(Drawable left, Drawable top,
                                     Drawable right, Drawable bottom) {
        if (left != null) {
            drawableLeft = left;
        }
        if (right != null) {
            drawableRight = right;
        }
        if (top != null) {
            drawableTop = top;
        }
        if (bottom != null) {
            drawableBottom = bottom;
        }
        super.setCompoundDrawables(left, top, right, bottom);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Rect bounds;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            actionX = (int) event.getX();
            actionY = (int) event.getY();
            if (drawableBottom != null
                    && drawableBottom.getBounds().contains(actionX, actionY)) {
                clickListener.onClick(DrawableClickListener.DrawablePosition.BOTTOM);
                return super.onTouchEvent(event);
            }

            if (drawableTop != null
                    && drawableTop.getBounds().contains(actionX, actionY)) {
                clickListener.onClick(DrawableClickListener.DrawablePosition.TOP);
                return super.onTouchEvent(event);
            }

            // this works for left since container shares 0,0 origin with bounds
            if (drawableLeft != null) {
                bounds = null;
                bounds = drawableLeft.getBounds();

                int x, y;
//				int extraTapArea = (int) (13 * getResources()
//						.getDisplayMetrics().density + 0.5);
                int extraTapArea = (int) (13 * getResources()
                        .getDisplayMetrics().density + 0.5);

                x = actionX;
                y = actionY;

                if (!bounds.contains(actionX, actionY)) {
                    /** Gives the +20 area for tapping. */
                    x = (int) (actionX - extraTapArea);
                    y = (int) (actionY - extraTapArea);

                    if (x <= 0)
                        x = actionX;
                    if (y <= 0)
                        y = actionY;

                    /** Creates square from the smallest value */
                    if (x < y) {
                        y = x;
                    }
                }

                if (bounds.contains(x, y) && clickListener != null) {
                    clickListener
                            .onClick(DrawableClickListener.DrawablePosition.LEFT);
                    event.setAction(MotionEvent.ACTION_CANCEL);
                    return false;

                }
            }

            if (drawableRight != null) {

                bounds = null;
                bounds = drawableRight.getBounds();

                int x, y;
                int extraTapArea = 13;//13

                /**
                 * IF USER CLICKS JUST OUT SIDE THE RECTANGLE OF THE DRAWABLE
                 * THAN ADD X AND SUBTRACT THE Y WITH SOME VALUE SO THAT AFTER
                 * CALCULATING X AND Y CO-ORDINATE LIES INTO THE DRAWBABLE
                 * BOUND. - this process help to increase the tappable area of
                 * the rectangle.
                 */
                x = (int) (actionX + extraTapArea);
                y = (int) (actionY - extraTapArea);

                /**
                 * Since this is right drawable subtract the value of x from the
                 * width of view. so that width - tappedarea will result in x
                 * co-ordinate in drawable bound.
                 */
                x = getWidth() - x;

                /*
                 * x can be negative if user taps at x co-ordinate just near the
                 * width. e.g views width = 300 and user taps 290. Then as per
                 * previous calculation 290 + 13 = 303. So subtract X from
                 * getWidth() will result in negative value. So to avoid this
                 * add the value previous added when x goes negative.
                 */

                if (x <= 0) {
                    x += extraTapArea;
                }

                /*
                 * If result after calculating for extra tappable area is
                 * negative. assign the original value so that after subtracting
                 * extratapping area value doesn't go into negative value.
                 */

                if (y <= 0)
                    y = actionY;

                /**
                 * If drawble bounds contains the x and y points then move
                 * ahead.
                 */
                if (bounds.contains(x, y) && clickListener != null) {
                    clickListener
                            .onClick(DrawableClickListener.DrawablePosition.RIGHT);
                    event.setAction(MotionEvent.ACTION_CANCEL);
                    return false;
                }
                return super.onTouchEvent(event);
            }

        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void finalize() throws Throwable {
        drawableRight = null;
        drawableBottom = null;
        drawableLeft = null;
        drawableTop = null;
        super.finalize();
    }

    public void setDrawableClickListener(DrawableClickListener listener) {
        this.clickListener = listener;
    }

    public void setCustomTypeface(String fontName) {
        Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/" + fontName);
        this.setTypeface(myTypeface);
    }

    public interface DrawableClickListener {
        public static enum DrawablePosition {
            TOP, BOTTOM, LEFT, RIGHT
        }

        public void onClick(DrawablePosition target);
    }
}