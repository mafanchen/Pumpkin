package com.video.test.ui.widget;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

/**
 * @author Enoch Created on 2018/11/26.
 */
public class DividerItemDecoration extends RecyclerView.ItemDecoration {
    private static final String TAG = "DividerItemDecoration";

    private int mLeftRight;
    private int mTopBottom;
    private Drawable mDivider;

    public DividerItemDecoration(int mLeftRight, int mTopBottom, int color) {
        this.mLeftRight = mLeftRight;
        this.mTopBottom = mTopBottom;
        if (color != 0) {
            mDivider = new ColorDrawable(color);
        }
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        GridLayoutManager layoutManager = (GridLayoutManager) parent.getLayoutManager();
        GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) view.getLayoutParams();
        final int childPosition = parent.getChildLayoutPosition(view);
        final int spanCount = layoutManager.getSpanCount();
        int viewLayoutPosition = ((GridLayoutManager.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();


        if (layoutManager.getOrientation() == GridLayoutManager.VERTICAL) {
            int dividerWidth = mLeftRight / 2;
            int spanGroupIndex = layoutManager.getSpanSizeLookup().getSpanGroupIndex(childPosition, spanCount);
            int spanSize = lp.getSpanSize();
            int spanIndex = lp.getSpanIndex();
            // 忽略合并项的问题,只考虑占满和单一的情况
            // 占满整行的情况
            if (spanSize == spanCount) {
                outRect.left = 0;
                outRect.right = 0;
            } else {
                if (spanIndex == 0) {
                    //SpanIndex = 0时,代表的是 当前spanGroup的第一个,左边距为0.
                    outRect.left = 0;
                    outRect.right = dividerWidth;
                } else if (spanIndex >= spanCount - spanSize) {
                    // spanIndex = spanCount -1 时, 代表的是 当前spanGroup的倒数第一个,右边距为0.
                    outRect.right = 0;
                    outRect.left = dividerWidth;
                } else {
                    //总共就3个 item  第一个设置右边 第三个设置左边 中间的不用再设置即可
                   /* outRect.left = (int) (((float) (spanCount - spanIndex)) / spanCount * mLeftRight);
                    outRect.right = (int) (((float) mLeftRight * (spanCount + 1) / spanCount) - outRect.left);*/
                    outRect.left = dividerWidth;
                    outRect.right = dividerWidth;
                }
            }

        } else {
            //横向 GridLayout 如果是第一排 第一排的上面需要加空间
            if (layoutManager.getSpanSizeLookup().getSpanGroupIndex(childPosition, spanCount) == 0) {
                outRect.left = mLeftRight;
            }
            outRect.right = mLeftRight;
            if (lp.getSpanSize() == spanCount) {
                outRect.left = mTopBottom;
                outRect.bottom = mTopBottom;
            } else {
                outRect.top = (int) (((float) (spanCount - lp.getSpanIndex())) / spanCount * mTopBottom);
                outRect.bottom = (int) (((float) mTopBottom * (spanCount + 1) / spanCount) - outRect.top);
            }
        }
    }


    private int getSpanCount(RecyclerView parent) {
        int spanCount = -1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            spanCount = ((StaggeredGridLayoutManager) layoutManager).getSpanCount();
        }
        return spanCount;
    }

    private void drawHorizontal(Canvas canvas, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int left = child.getLeft() - params.leftMargin;
            int right = child.getRight() + params.rightMargin + mDivider.getIntrinsicWidth();
            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(canvas);
        }
    }

    private void drawVertical(Canvas canvas, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int top = child.getTop() - params.topMargin;
            int bottom = child.getBottom() + params.bottomMargin;
            int left = child.getRight() + params.rightMargin;
            int right = left + mDivider.getIntrinsicWidth();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(canvas);
        }
    }

    private boolean isLastColumn(RecyclerView parent, int pos, int spanCount, int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            return (pos + 1) % spanCount == 0;
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager).getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                return (pos + 1) % spanCount == 0;
            } else {
                childCount = childCount - childCount % spanCount;
                return pos >= childCount;
            }
        }
        return false;
    }

    private boolean isLastRaw(RecyclerView parent, int pos, int spanCount, int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            childCount = childCount - childCount % spanCount;
            return pos >= childCount;
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int orientation = ((StaggeredGridLayoutManager) layoutManager).getOrientation();
            if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                childCount = childCount - childCount % spanCount;
                return pos >= childCount;
            } else {
                // StaggeredGridLayoutManager 且横线滚动
                return (pos + 1) % spanCount == 0;
            }
        }
        return false;
    }

  /*  @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        drawHorizontal(c, parent);
        drawVertical(c, parent);
    }*/

/*    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent,
                               @NonNull RecyclerView.State state) {
        int spanCount = getSpanCount(parent);
        int childCount = parent.getAdapter().getItemCount();
        int itemPosition = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
        if (isLastRaw(parent, itemPosition, spanCount, childCount)) {
            outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
        } else if (isLastColumn(parent, itemPosition, spanCount, childCount)) {
            outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
        } else {
            outRect.set(0, 0, mDivider.getIntrinsicWidth(), mDivider.getIntrinsicHeight());
        }
    }*/

}
