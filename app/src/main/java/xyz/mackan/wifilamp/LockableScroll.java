package xyz.mackan.wifilamp;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class LockableScroll extends ScrollView{
    private boolean enableScrolling = true;

    public boolean isEnableScrolling() {
        return enableScrolling;
    }

    public void setEnableScrolling(boolean enableScrolling) {
        this.enableScrolling = enableScrolling;
    }

    public LockableScroll(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public LockableScroll(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LockableScroll(Context context) {
        super(context);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        if (isEnableScrolling()) {
            return super.onInterceptTouchEvent(ev);
        } else {
            return false;
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isEnableScrolling()) {
            return super.onTouchEvent(ev);
        } else {
            return false;
        }
    }
}
