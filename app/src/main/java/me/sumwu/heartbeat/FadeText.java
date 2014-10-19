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

import org.w3c.dom.Text;

public class FadeText extends TextView {

    public FadeText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public FadeText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FadeText(Context context) {
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
                        TextView view = (TextView) v;
                        view.setAlpha(0.5f);
                        view.setShadowLayer(25, 0, 0, getResources().getColor(R.color.gray));
                        view.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        TextView view = (TextView) v;
                        view.setAlpha(1f);
                        view.setShadowLayer(0, 0, 0, 0);
                        view.invalidate();
                        break;
                    }
                }
                return false;
            }
        });
    }

}