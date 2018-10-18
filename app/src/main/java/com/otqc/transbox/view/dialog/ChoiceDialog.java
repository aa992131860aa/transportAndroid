package com.otqc.transbox.view.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.otqc.transbox.R;

public class ChoiceDialog extends AlertDialog {

    private TextView tvTitle, tvCancel, tvOk;
    private String titile;
    private String cancel;
    private String ok;

    public ChoiceDialog(Context context, String tvTitle, String cancel, String ok) {
        super(context);
        this.titile = tvTitle;
        this.cancel = cancel;
        this.ok = ok;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_choice);
        setCanceledOnTouchOutside(false);   //对话框以外不可点击
        tvTitle = (TextView) findViewById(R.id.dialog_c_title);
        tvTitle.setText(titile);
        tvCancel = (TextView) findViewById(R.id.dialog_c_cancel);
        tvCancel.setText(cancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.setCancelClick();
                }
                dismiss();  //点击后结束掉dialog
            }
        });

        tvOk = (TextView) findViewById(R.id.dialog_c_ok);
        tvOk.setText(ok);
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.setOkClick();
                }
                dismiss();
            }
        });
    }

    public interface OnChoiceClickListener {

        void setCancelClick();

        void setOkClick();
    }

    public void setOnChoiceClickListener(OnChoiceClickListener listener) {
        this.listener = listener;
    }

    private OnChoiceClickListener listener;
}