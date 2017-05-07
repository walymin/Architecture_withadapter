package architecture_o.ui.widget;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.HashSet;

import architecture_o.R;


/**
 * Created by Herbert on 2015/12/2.
 */
public class DecorationUtil {

    public static class LineDecoration extends RecyclerView.ItemDecoration {
        Paint paint = new Paint();
        int thickness = 1;
        boolean lineOnBottom = false;

        public LineDecoration() {
            this.thickness = 1;
            paint.setColor(0);
            paint.setStrokeWidth(thickness);
        }

        public LineDecoration(int thickness) {
            this.thickness = thickness;
            paint.setColor(0);
            paint.setStrokeWidth(thickness);
        }

        public LineDecoration(int thickness, int color) {
            this.thickness = thickness;
            paint.setColor(color);
            paint.setStrokeWidth(thickness);
        }

        public void setOnBottom(){
            lineOnBottom = true;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            if (position > 0) {
                if (lineOnBottom)
                    outRect.set(0, 0, 0, thickness);
                else
                    outRect.set(0, thickness, 0, 0);
            } else {
                outRect.set(0, 0, 0, 0);
            }
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            for (int i = 1; i < parent.getChildCount(); i++) {
                View view = parent.getChildAt(i);
                if (lineOnBottom)
                    c.drawLine(view.getLeft(), view.getBottom(), view.getRight(), view.getBottom(), paint);
                else
                    c.drawLine(view.getLeft(), view.getTop() - thickness, view.getRight(), view.getTop() - thickness, paint);
            }
        }
    }

    public static class LineDecorationViewType extends RecyclerView.ItemDecoration {
        Paint paint = new Paint();
        HashSet<Integer> viewTypeSet = new HashSet<>();

        public LineDecorationViewType(int viewType) {
            viewTypeSet.add(viewType);
        }

        public LineDecorationViewType(int[] viewTypes) {
            for (int viewType : viewTypes) {
                this.viewTypeSet.add(viewType);
            }
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            int space = 1;
            if (position > 0 && viewTypeSet.contains(parent.getAdapter().getItemViewType(position))) {
                outRect.set(0, space, 0, 0);
            } else {
                outRect.set(0, 0, 0, 0);
            }
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int space = 1;
            paint.setColor(parent.getResources().getColor(R.color.line));
            paint.setStrokeWidth(space);
            for (int i = 1; i < parent.getChildCount(); i++) {
                View view = parent.getChildAt(i);
                if (viewTypeSet.contains(parent.getChildViewHolder(view).getItemViewType()))
                    c.drawLine(view.getLeft(), view.getTop() - space, view.getRight(), view.getTop() - space, paint);
            }
        }
    }

    public static class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;
        private boolean horizontal;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge, boolean horizontal) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
            this.horizontal = horizontal;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                if (horizontal) {
                    outRect.top = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                    outRect.bottom = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                    if (position < spanCount) { // top edge
                        outRect.left = spacing;
                    }
                    outRect.right = spacing; // item bottom
                } else {
                    outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                    outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                    if (position < spanCount) { // top edge
                        outRect.top = spacing;
                    }
                    outRect.bottom = spacing; // item bottom
                }
            } else {
                if (horizontal) {
                    outRect.top = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                    outRect.bottom = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                    if (position >= spanCount) {
                        outRect.left = spacing; // item top
                    }
                } else {
                    outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                    outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                    if (position >= spanCount) {
                        outRect.top = spacing; // item top
                    }
                }
            }
        }
    }
}
