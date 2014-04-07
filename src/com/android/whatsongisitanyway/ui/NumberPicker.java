package com.android.whatsongisitanyway.ui;

import java.lang.reflect.Method;

import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.android.whatsongisitanyway.R;

public class NumberPicker {
    private Object picker;
    private Class<?> classPicker;

    public NumberPicker(LinearLayout numberPickerView) {
        picker = numberPickerView;
        classPicker = picker.getClass();

        View upButton = numberPickerView.getChildAt(0);
        upButton.setBackgroundResource(R.drawable.up_button);

        EditText edDate = (EditText) numberPickerView.getChildAt(1);
        edDate.setTextSize(17);
        edDate.setBackgroundResource(R.drawable.number_box);

        View downButton = numberPickerView.getChildAt(2);
        downButton.setBackgroundResource(R.drawable.down_button);
    }

    public void setRange(int start, int end) {
        try {
            Method m = classPicker.getMethod("setRange", int.class, int.class);
            m.invoke(picker, start, end);
        } catch (Exception e) {
        }
    }

    public Integer getCurrent() {
        Integer current = -1;
        try {
            Method m = classPicker.getMethod("getCurrent");
            current = (Integer) m.invoke(picker);
        } catch (Exception e) {
        }
        return current;
    }

    public void setCurrent(int current) {
        try {
            Method m = classPicker.getMethod("setCurrent", int.class);
            m.invoke(picker, current);
        } catch (Exception e) {
        }
    }
}