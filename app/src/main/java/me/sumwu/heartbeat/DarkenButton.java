package me.sumwu.heartbeat;

/**
 * Created by swu on 10/19/14.
 */

import android.content.Context;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class DarkenButton extends ImageView {

    public DarkenButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public DarkenButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DarkenButton(Context context) {
        super(context);
        init();
    }

    //set the ontouch listener
    private void init() {
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ImageView view = (ImageView) v;
                        view.setColorFilter(R.color.gray, PorterDuff.Mode.MULTIPLY);
                        view.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        ImageView view = (ImageView) v;
                        view.setColorFilter(null);
                        view.invalidate();
                        break;
                    }
                }
                return false;
            }
        });
    }

}