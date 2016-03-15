package com.example.administrator.tanhuangscrollview;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ScrollView;

/**
 * Created by Administrator on 2016/3/15.
 */
public class Tanhunag extends ScrollView {

    private static final float MOVE_FACTOR = 0.5f;

    private static final long ANIM_TIME = 300;

    private static final int MAX_DIS = 150;

    private View contentView;

    private boolean canPullUp = false;

    private boolean canPullDown = false;

    private boolean isMove = false;

    private Rect normalLoc = new Rect();

    private float startY;


    public Tanhunag(Context context) {
        super(context);
    }

    public Tanhunag(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() > 0) {
            contentView = getChildAt(0);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (contentView == null) {
            return;
        }

        normalLoc.set(contentView.getLeft(), contentView.getTop(), contentView.getRight(), contentView.getBottom());

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (contentView == null) {
            return super.dispatchTouchEvent(ev);
        }

        boolean isTouchOutSide = ev.getY() >= getHeight() || ev.getY() < 0;
        if (isTouchOutSide) {
            if (isMove) {
                boundBack();
            }
        }

        int action = ev.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                canPullDown = isCanPullDown();
                canPullUp = isCanPullUp();

                startY = ev.getY();

                break;
            }
            case MotionEvent.ACTION_UP: {
                boundBack();
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (!canPullDown && !canPullUp) {
                    startY = ev.getY();
                    canPullDown = isCanPullDown();
                    canPullUp = isCanPullUp();

                }

                float nowY = ev.getY();
                int deltay = (int) (nowY - startY);

                boolean showMove = (canPullDown && deltay > 0)
                        || (canPullUp && deltay < 0)
                        || (canPullUp && canPullDown);
                if (showMove) {
                    int offset = (int) (deltay * MOVE_FACTOR);
                    if (offset > 0 && offset > MAX_DIS) {
                        offset = MAX_DIS;
                    } else if (offset < 0) {
                        if (Math.abs(offset) > MAX_DIS) {
                            offset = -MAX_DIS;
                        } else {

                        }
                    }

                    contentView.layout(normalLoc.left, normalLoc.top + offset, normalLoc.right, normalLoc.bottom + offset);

                    isMove = true;
                }
            }
        }

        return super.dispatchTouchEvent(ev);

    }

    private void boundBack() {
        if (!isMove) {
            return;
        }

        TranslateAnimation animation = new TranslateAnimation(0, 0, contentView.getTop(), 0);
        animation.setDuration(ANIM_TIME);
        animation.start();

        contentView.layout(normalLoc.left, normalLoc.top, normalLoc.right, normalLoc.bottom);

        isMove = false;
        canPullDown = canPullUp = false;
    }

    private boolean isCanPullDown() {
        return getScrollY() == 0 || contentView.getHeight() < getHeight() + getScrollY();
    }

    private boolean isCanPullUp() {
        return getScrollY() <= contentView.getHeight() + getScrollY();
    }
}
