package com.dargo.mignon.common;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by sdargo on 06/08/16.
 */
public class ToastWrapper {
    private final Toast toast;

    public ToastWrapper(Context context) {
        this.toast = Toast.makeText(context, "initial message", Toast.LENGTH_SHORT);
    }

    public void makeText(String text) {
        this.toast.setText(text);
        this.toast.show();
    }
}
