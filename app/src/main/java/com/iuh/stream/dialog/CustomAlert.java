package com.iuh.stream.dialog;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.iuh.stream.R;

public class CustomAlert {
    public static final int WARNING = -1;
    public static final int INFO = 1;
    public static void showToast(Activity activity, int type, String message) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast, null);

        ImageView icon = layout.findViewById(R.id.icon);
        TextView tvMessage = layout.findViewById(R.id.message);

        if(type == WARNING) {
            icon.setImageResource(R.drawable.ic_baseline_warning_32);
            layout.setBackgroundTintList(ContextCompat.getColorStateList(activity, R.color.danger_50));
        } else {
            icon.setImageResource(R.drawable.ic_baseline_info_24);
            layout.setBackgroundTintList(ContextCompat.getColorStateList(activity, R.color.info_70));
        }

        tvMessage.setText(message);

        Toast toast = new Toast(activity);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);

        toast.show();
    }
}
