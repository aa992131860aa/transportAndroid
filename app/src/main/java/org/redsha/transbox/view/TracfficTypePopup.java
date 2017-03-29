package org.redsha.transbox.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import org.redsha.transbox.App;
import org.redsha.transbox.R;
import org.redsha.transbox.engine.BasicAdapter;

import java.util.ArrayList;

public class TracfficTypePopup extends PopupWindow {
    private BasicListView listView;
    private final FilterAdapter adapter;
    private ArrayList<String> mData;
    public static int lastPosition = -1;  //记录pos

    public TracfficTypePopup(final Activity context, ArrayList<String> dataList) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.popup_single, null);
        mData = dataList;

        this.setContentView(v);
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        this.setFocusable(true);
        this.setOutsideTouchable(false);
        this.setBackgroundDrawable(new BitmapDrawable());

        listView = (BasicListView) v.findViewById(R.id.lv);
        listView.setListViewHeight(320);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        adapter = new FilterAdapter(App.getContext(), mData);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // 记录选中的pos
                lastPosition = position;
                if (mListener != null) {
                    mListener.OnClickChange(position);
                }
                adapter.notifyDataSetChanged();
                dismiss();
            }

        });

        // 初始化选中的pos
        if (lastPosition > -1) {
            listView.setItemChecked(lastPosition, true);
        }

    }

    /**
     * 显示popupWindow
     */
    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow
//            this.showAsDropDown(parent, -20, 5);
            this.showAsDropDown(parent, 80, 0);
        } else {
            this.dismiss();
        }
    }

    class FilterAdapter extends BasicAdapter<String> {

        public FilterAdapter(Context context, ArrayList<String> list) {
            super(context, list);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(App.getContext(), R.layout.adapter_pop, null);
                holder = new ViewHolder();
                holder.tv = (TextView) convertView.findViewById(R.id.id_popup_tv);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            String str = list.get(position);
            holder.tv.setText(str);

            // update bg
            updateBackgroud(position, holder.tv);
            return convertView;
        }

        private void updateBackgroud(int position, TextView tv) {
            int backgroundId;
            int tvColor;
            if (listView.isItemChecked(position)) {
                backgroundId = R.color.indicator_gray;
                tvColor = R.color.black;
            } else {
                backgroundId = R.color.white;
                tvColor = R.color.black;
            }
            tv.setBackgroundResource(backgroundId);
            tv.setTextColor(App.getContext().getResources().getColor(tvColor));
        }

        class ViewHolder {
            private TextView tv;
        }
    }

    public interface OnClickChangeListener {
        public void OnClickChange(int position);
    }

    public void setOnClickChangeListener(OnClickChangeListener clickChangeListener) {
        mListener = clickChangeListener;
    }

    private OnClickChangeListener mListener;

}