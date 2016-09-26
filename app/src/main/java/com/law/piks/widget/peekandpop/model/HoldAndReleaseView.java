package com.law.piks.widget.peekandpop.model;

import android.support.annotation.NonNull;
import android.view.View;


import com.law.piks.widget.peekandpop.PeekAndPop;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Vincent on 9/01/2016.
 */
public class HoldAndReleaseView {

    protected Timer holdAndReleaseTimer;
    private View view;
    private int position;

    public HoldAndReleaseView(View view) {
        this.view = view;
        this.position = -1;
        this.holdAndReleaseTimer = new Timer();
    }

    public void startHoldAndReleaseTimer(@NonNull final PeekAndPop peekAndPop, final int position, long duration) {
        final Timer holdAndReleaseTimer = new Timer();
        this.position = position;

        holdAndReleaseTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                peekAndPop.setCurrentHoldAndReleaseView(HoldAndReleaseView.this);
                peekAndPop.triggerOnHoldEvent(view, position);
            }
        }, duration);

        this.holdAndReleaseTimer = holdAndReleaseTimer;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public Timer getHoldAndReleaseTimer() {
        return holdAndReleaseTimer;
    }

    public void setHoldAndReleaseTimer(Timer holdAndReleaseTimer) {
        this.holdAndReleaseTimer = holdAndReleaseTimer;
    }
}
