package com.iuh.stream.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.iuh.stream.R;

public class ShowLoginMethodsDialog extends Dialog {
    public ShowLoginMethodsDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.show_login_method_dialog);

        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            dismiss();
        });
    }


}
