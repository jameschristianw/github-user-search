package com.potatodev.jameschristianwira_takehome;

import android.content.res.Resources;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CustomDecorator extends RecyclerView.ItemDecoration {
    private int space;

    public CustomDecorator(int space) {
        this.space = pxToDp(space);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        if (parent.getChildAdapterPosition(view) == 0){
            outRect.top = space;
        }
        outRect.left = space;
        outRect.right = space;
        outRect.bottom = space;
    }

    public static int pxToDp(int px) {
        return (int) (px * Resources.getSystem().getDisplayMetrics().density);
    }
}
