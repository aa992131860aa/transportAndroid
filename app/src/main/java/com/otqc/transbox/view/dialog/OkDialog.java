package com.otqc.transbox.view.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.otqc.transbox.R;

//import android.;

public class OkDialog extends AlertDialog {

    private TextView tvTitle, tvConfirm;
    private String titile;
    private String ok;

    public OkDialog(Context context, String tvTitle, String tvConfirm) {
        super(context);
        this.titile = tvTitle;
        this.ok = tvConfirm;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_ok);
        setCanceledOnTouchOutside(false);   //对话框以外 可关闭
        tvTitle = (TextView) findViewById(R.id.dialog_title);
        tvTitle.setText(titile);
        tvConfirm = (TextView) findViewById(R.id.dialog_ok);
        tvConfirm.setText(ok);
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (listener != null) {
                    listener.setOkClick();
                }
                // 点击结束后，关闭掉对话框
                dismiss();
            }
        });
    }

    public interface OnChoiceClickListener {
        void setOkClick();
    }

    public void setOnChoiceClickListener(OnChoiceClickListener listener) {
        this.listener = listener;
    }

    private OnChoiceClickListener listener;
}
