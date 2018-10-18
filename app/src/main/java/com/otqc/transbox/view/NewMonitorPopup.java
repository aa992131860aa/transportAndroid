package com.otqc.transbox.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.otqc.transbox.R;
import com.otqc.transbox.util.ToastUtil;

public class NewMonitorPopup extends PopupWindow implements View.OnClickListener {
    private Context mContext;
    private int statusBarHeight = 0;
    private String mPwdNumber = "";
    ImageView iv_close;
    TextView tv_one;
    TextView tv_two;
    TextView tv_three;
    TextView tv_four;
    TextView tv_five;
    TextView tv_six;
    TextView tv_seven;
    TextView tv_eight;
    TextView tv_nine;
    TextView tv_zero;

    TextView one;
    TextView two;
    TextView three;
    TextView four;

    TextView tv_deal;
    LinearLayout ll_del;

    private String mPwd;
    private String  mType;

    public NewMonitorPopup(final Context context,String type) {
        mContext = context;

        mType = type;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.new_monitor_pop, null);
        iv_close = (ImageView) v.findViewById(R.id.iv_close);
        tv_one = (TextView) v.findViewById(R.id.tv_one);
        tv_two = (TextView) v.findViewById(R.id.tv_two);
        tv_three = (TextView) v.findViewById(R.id.tv_three);
        tv_four = (TextView) v.findViewById(R.id.tv_four);
        tv_five = (TextView) v.findViewById(R.id.tv_five);
        tv_six = (TextView) v.findViewById(R.id.tv_six);
        tv_seven = (TextView) v.findViewById(R.id.tv_seven);
        tv_eight = (TextView) v.findViewById(R.id.tv_eight);
        tv_nine = (TextView) v.findViewById(R.id.tv_nine);
        tv_zero = (TextView) v.findViewById(R.id.tv_zero);

        one = (TextView) v.findViewById(R.id.one);
        two = (TextView) v.findViewById(R.id.two);
        three = (TextView) v.findViewById(R.id.three);
        four = (TextView) v.findViewById(R.id.four);

        tv_deal = (TextView) v.findViewById(R.id.tv_deal);
        ll_del = (LinearLayout) v.findViewById(R.id.ll_del);

        tv_one.setOnClickListener(this);
        tv_two.setOnClickListener(this);
        tv_three.setOnClickListener(this);
        tv_four.setOnClickListener(this);
        tv_five.setOnClickListener(this);
        tv_six.setOnClickListener(this);
        tv_seven.setOnClickListener(this);
        tv_eight.setOnClickListener(this);
        tv_nine.setOnClickListener(this);
        tv_zero.setOnClickListener(this);


        tv_deal.setOnClickListener(this);
        iv_close.setOnClickListener(this);
        ll_del.setOnClickListener(this);

        tv_deal.setText(type);

        this.setContentView(v);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setFocusable(true);
        this.setOutsideTouchable(false);
        this.setBackgroundDrawable(new BitmapDrawable());


    }


    /**
     * 显示popupWindow
     */
    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow
//            this.showAsDropDown(parent, -20, 5);
            this.showAsDropDown(parent, 0, 0);
        } else {
            this.dismiss();
        }
    }

    private void dealText(String number) {
        mPwdNumber += number;
        if (mPwdNumber.length() == 1) {
            one.setText(number);
        } else if (mPwdNumber.length() == 2) {
            two.setText(number);
        } else if (mPwdNumber.length() == 3) {
            three.setText(number);
        } else if (mPwdNumber.length() == 4) {
            four.setText(number);
        }

    }

    private void delText() {
        if (mPwdNumber.length() > 0) {
            if (mPwdNumber.length() > 4) {
                mPwdNumber = mPwdNumber.substring(0, 3);
            } else {
                mPwdNumber = mPwdNumber.substring(0, mPwdNumber.length() - 1);
            }
            if (mPwdNumber.length() == 0) {
                one.setText("");

            } else if (mPwdNumber.length() == 1) {
                two.setText("");
            } else if (mPwdNumber.length() == 2) {
                three.setText("");
            }
            else if (mPwdNumber.length() == 3) {
                four.setText("");
            }
        }

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.iv_close:
                dismiss();
                break;

            case R.id.ll_del:
                delText();
                break;

            case R.id.tv_deal:
                mListener.OnClickChange(mPwdNumber);
                break;

            case R.id.tv_one:
                dealText(tv_one.getText().toString());
                break;
            case R.id.tv_two:
                dealText(tv_two.getText().toString());
                break;

            case R.id.tv_three:
                dealText(tv_three.getText().toString());
                break;

            case R.id.tv_four:
                dealText(tv_four.getText().toString());
                break;
            case R.id.tv_five:
                dealText(tv_five.getText().toString());
                break;

            case R.id.tv_six:
                dealText(tv_six.getText().toString());
                break;

            case R.id.tv_seven:
                dealText(tv_seven.getText().toString());
                break;

            case R.id.tv_eight:
                dealText(tv_eight.getText().toString());
                break;

            case R.id.tv_nine:
                dealText(tv_nine.getText().toString());
                break;
            case R.id.tv_zero:
                dealText(tv_zero.getText().toString());
                break;




        }
    }


    public interface OnClickChangeListener {
        public void OnClickChange(String number);
    }

    public void setOnClickChangeListener(OnClickChangeListener clickChangeListener) {
        mListener = clickChangeListener;
    }

    private OnClickChangeListener mListener;

}