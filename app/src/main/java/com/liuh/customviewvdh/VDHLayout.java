package com.liuh.customviewvdh;

import android.content.Context;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Date: 2017/12/27 10:16
 * Description:自定义View之ViewDragHelper学习
 * 当我们想在某一种布局(比如:LinearLayout)中可以拖拽其子控件时,我们可以自建一个ViewGroup,继承已知布局(比如:LinearLayout)
 * 在其内部定义一个ViewDragHelper类型的成员变量,在构造方法中对其进行初始化,并重写其回调方法
 *
 * 重写所继承布局的onInterceptTouchEvent(...)和onTouchEvent(...)
 *
 */

public class VDHLayout extends LinearLayout {

    private ViewDragHelper mDragHelper;

    private View mDragView;
    private View mAutoBackView;
    private View mEdgeTrackerView;

    private Point mAutoBackOriginPos = new Point();

    public VDHLayout(Context context) {
        this(context, null);
    }

    public VDHLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VDHLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                //是否捕获child
                //mEdgeTrackerView禁止直接移动
                return child == mDragView || child == mAutoBackView;
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                //对child横向移动的边界进行控制(控制child左边和右边均不会超出父控件边界)
                int leftBound = getPaddingLeft();

                int rightBound = getWidth() - child.getWidth() - getPaddingRight();

                int newLeft = Math.min(Math.max(left, leftBound), rightBound);

                return newLeft;
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                //对child竖向移动的边界进行控制
                int topBound = getPaddingTop();

                int bottomBound = getHeight() - child.getHeight() - getPaddingBottom();

                int newTop = Math.min(Math.max(topBound, top), bottomBound);

                return newTop;
            }

            @Override
            public int getViewHorizontalDragRange(View child) {
                return getMeasuredWidth() - child.getMeasuredWidth();
            }

            @Override
            public int getViewVerticalDragRange(View child) {
                return getMeasuredHeight() - child.getMeasuredHeight();
            }

            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                //当手指释放时,我们让mAutoBackView自动回到最初位置
                if (releasedChild == mAutoBackView) {
                    mDragHelper.settleCapturedViewAt(mAutoBackOriginPos.x, mAutoBackOriginPos.y);
                    invalidate();
                }
            }

            @Override
            public void onEdgeDragStarted(int edgeFlags, int pointerId) {
                //拖动发生在边界
                mDragHelper.captureChildView(mEdgeTrackerView, pointerId);
            }
        });

        mDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //是否拦截当前事件
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //处理事件
        mDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mAutoBackOriginPos.x = mAutoBackView.getLeft();
        mAutoBackOriginPos.y = mAutoBackView.getTop();
    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            invalidate();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mDragView = getChildAt(0);
        mAutoBackView = getChildAt(1);
        mEdgeTrackerView = getChildAt(2);
    }
}
