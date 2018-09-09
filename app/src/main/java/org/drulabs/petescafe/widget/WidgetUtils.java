package org.drulabs.petescafe.widget;

import android.support.annotation.DrawableRes;

import org.drulabs.petescafe.R;

public class WidgetUtils {

    @DrawableRes
    public static int getDrawableResId(String recipeName) {
        switch (recipeName.toLowerCase()) {
            case "yellow cake":
                return R.drawable.yellowcake;
            case "brownies":
                return R.drawable.brownies;
            case "cheesecake":
                return R.drawable.cheesecake;
            default:
                return R.drawable.nutellacake;
        }
    }

}
