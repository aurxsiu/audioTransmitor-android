package com.aurxsiu.audiotransmitor;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AddOrEditDialog extends Dialog {

    public interface OnSubmitListener {
        void onSubmit(String content, String remark);
    }

    public AddOrEditDialog(@NonNull Context context, @Nullable OptionData existing, @NonNull OnSubmitListener listener) {
        super(context);
        setContentView(R.layout.dialog_add_edit);

        EditText contentInput = findViewById(R.id.inputContent);
        EditText remarkInput = findViewById(R.id.inputRemark);
        Button confirmButton = findViewById(R.id.btnConfirm);

        if (existing != null) {
            contentInput.setText(existing.content);
            remarkInput.setText(existing.remark);
        }

        confirmButton.setOnClickListener(v -> {
            String content = contentInput.getText().toString().trim();
            String remark = remarkInput.getText().toString().trim();
            if (!content.isEmpty()) {
                listener.onSubmit(content, remark);
                dismiss();
            }
        });
    }

    @Override
    public void show() {
        super.show();
        // 设置宽度为屏幕宽度的 90%
        Window window = getWindow();
        if (window != null) {
            DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
            int width = (int) (metrics.widthPixels * 0.9);
            window.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}
