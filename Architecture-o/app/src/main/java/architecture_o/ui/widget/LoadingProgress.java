package architecture_o.ui.widget;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import architecture_o.R;


/**
 * Created by Herbert on 2015/1/22.
 */
public class LoadingProgress extends FrameLayout {
    public LoadingProgress(Context context) {
        this(context, null);
    }

    public LoadingProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.loading_progress, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
//        ViewCompat.setAlpha(this, 0);
    }

    private static final int MIN_SHOW_TIME = 500; // ms
    private static final int MIN_DELAY = 500; // ms

    private long mStartTime = -1;

    private boolean mPostedHide = false;

    private boolean mPostedShow = false;

    private boolean mDismissed = false;

    private final Runnable mDelayedHide = new Runnable() {

        @Override
        public void run() {
            mPostedHide = false;
            mStartTime = -1;
//            setVisibility(View.GONE);
            ViewCompat.animate(LoadingProgress.this).alpha(0).start();
        }
    };

    private final Runnable mDelayedShow = new Runnable() {

        @Override
        public void run() {
            mPostedShow = false;
            if (!mDismissed) {
                mStartTime = System.currentTimeMillis();
//                setVisibility(View.VISIBLE);
                ViewCompat.animate(LoadingProgress.this).alpha(1).start();
            }
        }
    };

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        removeCallbacks();
//        startAnimation();
    }

    @Override
    public void onDetachedFromWindow() {
//        Drawable drawable = getDrawable();
//        if (drawable != null && drawable instanceof Animatable)
//            ((Animatable) drawable).stop();

        super.onDetachedFromWindow();
        removeCallbacks();
    }

    private void removeCallbacks() {
        removeCallbacks(mDelayedHide);
        removeCallbacks(mDelayedShow);
    }

    //    /**
//     * Hide the progress view if it is visible. The progress view will not be
//     * hidden until it has been shown for at least a minimum show time. If the
//     * progress view was not yet visible, cancels showing the progress view.
//     */
//    public void hide() {
//        mDismissed = true;
//        removeCallbacks(mDelayedShow);
//        long diff = System.currentTimeMillis() - mStartTime;
//        if (diff >= MIN_SHOW_TIME || mStartTime == -1) {
////            setVisibility(View.GONE);
//            ViewCompat.setAlpha(this, 0);
//        } else {
//            if (!mPostedHide) {
//                postDelayed(mDelayedHide, MIN_SHOW_TIME - diff);
//                mPostedHide = true;
//            }
//        }
//    }
//
//    /**
//     * Show the progress view after waiting for a minimum delay. If
//     * during that time, hide() is called, the view is never made visible.
//     */
//    public void show() {
//        // Reset the start time.
//        mStartTime = -1;
//        mDismissed = false;
//        removeCallbacks(mDelayedHide);
//        if (!mPostedShow) {
//            postDelayed(mDelayedShow, MIN_DELAY);
//            mPostedShow = true;
//        }
//    }
    public void show() {
//        ViewCompat.setAlpha(this, 1);
        setVisibility(View.VISIBLE);
    }

    public boolean isShowing(){
        return getVisibility() == View.VISIBLE;
    }

    public void hide() {
        setVisibility(View.GONE);
    }
}
