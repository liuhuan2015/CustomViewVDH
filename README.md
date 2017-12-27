# CustomViewVDH
自定义View之ViewDragHelper学习
当我们想在某一种布局(比如:LinearLayout)中可以拖拽其子控件时,我们可以自建一个ViewGroup,继承已知布局(比如:LinearLayout),<br>
在其内部定义一个ViewDragHelper类型的成员变量,在构造方法中对其进行初始化,并重写其回调方法<br>

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

重写所继承布局的onInterceptTouchEvent(...)和onTouchEvent(...)<br>

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
如果子类控件是View类型(比如TextView,Button等),如果其能处理点击事件,我们需要重写回调方法中的getViewHorizontalDragRange(...)和getViewVerticalDragRange(...),
以使其依然可以被拖拽<br>
如果其不会处理点击事件,我们就不需要重写getViewHorizontalDragRange(...)和getViewVerticalDragRange(...).
博文中这样写:
>需要注意的地方：如果ViewGroup的子控件会消耗点击事件，例如按钮，在触摸屏幕的时候就会先走onInterceptTouchEvent方法，判断是否可以捕获，
>而在判断的过程中会去判断另外两个回调的方法：getViewHorizontalDragRange和getViewVerticalDragRange，只有这两个方法返回大于0的值才能正常的捕获。



