package com.otqc.transbox.controller.create;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.otqc.transbox.R;
import com.otqc.transbox.util.SelectTime.TimeSelector;

/**
 * Created by Administrator on 2017/3/22 0022.
 */

public class TestActivity extends AppCompatActivity {
    TextView tv_show;
    TimeSelector timeSelector;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        tv_show = (TextView)findViewById(R.id.textView);
        tv_show.setText("2017-03-21 10:43");
        tv_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeSelector = new TimeSelector(TestActivity.this, new TimeSelector.ResultHandler() {
                    @Override
                    public void handle(String time) {
                        tv_show.setText(time);
                    }
                }, "2017-02-21 10:43", "2017-11-21 11:20");
                timeSelector.show();
            }
        });



    }
}
