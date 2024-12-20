package com.dhosio.voucherscan.utils;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.dhosio.voucherscan.R;

public class Animations {

    private final Context context;

    public Animations(Context context) {
        this.context = context;
    }

    public Animation getFromBottomAnimation() {
        return AnimationUtils.loadAnimation(this.context, R.anim.bottom_animation);
    }

    public Animation getFromTopAnimation() {
        return AnimationUtils.loadAnimation(context, R.anim.top_animation);
    }

    public Animation getFromLeftAnimation() {
        return AnimationUtils.loadAnimation(context, R.anim.left_animation);
    }

    public Animation getFromRightAnimation() {
        return AnimationUtils.loadAnimation(context, R.anim.right_animation);
    }

    public Animation getFadeInAnimation() {
        return AnimationUtils.loadAnimation(context, R.anim.fade_in);
    }

    public Animation getFadeOutAnimation() {
        return AnimationUtils.loadAnimation(context, R.anim.fade_out);
    }

    public Animation getShortFadeInAnimation() {
        return AnimationUtils.loadAnimation(context, R.anim.short_fade_in);
    }

    public Animation getShortFadeOutAnimation() {
        return AnimationUtils.loadAnimation(context, R.anim.short_fade_out);
    }
}
