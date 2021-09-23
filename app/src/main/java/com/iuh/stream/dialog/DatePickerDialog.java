package com.iuh.stream.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.format.DateFormat;
import android.view.WindowManager;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.iuh.stream.R;

import java.util.Date;

public class DatePickerDialog extends Dialog {
    public DatePickerDialog(@NonNull Context context, EditText edtDate) {
        super(context);
        setContentView(R.layout.datepicker_dialog);

        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        DatePicker datePicker = findViewById(R.id.calendar);

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            this.dismiss();
        });

        findViewById(R.id.btnOk).setOnClickListener(v -> {
            edtDate.setText(String.format("%d/%d/%d", datePicker.getDayOfMonth(), datePicker.getMonth(), datePicker.getYear()));
            this.dismiss();
        });
    }


}
