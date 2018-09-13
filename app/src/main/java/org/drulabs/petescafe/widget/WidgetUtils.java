package org.drulabs.petescafe.widget;

import android.support.annotation.DrawableRes;

import org.drulabs.petescafe.R;

public class WidgetUtils {

    @DrawableRes
    public static int getDrawableResId(String recipeName) {
        switch (recipeName.toLowerCase()) {
            case "yellow cake":
            case "new yellow cake":
                return R.drawable.yellowcake;
            case "brownies":
            case "new brownies":
                return R.drawable.brownies;
            case "cheesecake":
            case "new cheesecake":
                return R.drawable.cheesecake;
            default:
                return R.drawable.nutellacake;
        }
    }

}
