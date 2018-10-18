package com.otqc.transbox.fragment;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.otqc.transbox.App;
import com.otqc.transbox.R;
import com.otqc.transbox.bean.CrashBean;
import com.otqc.transbox.engine.BasicAdapter;

import java.util.ArrayList;

public class OpenTimeAdapter extends BasicAdapter {
    public OpenTimeAdapter(Context context, ArrayList list) {
        super(context, list);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(App.getContext(), R.layout.adapter_open_time, null);
        }
        Holder holder = Holder.getHolder(convertView);
        //set data

        holder.tvOpen.setText((String)list.get(position));

        return convertView;
    }

    static class Holder {
        TextView tvOpen;

        private Holder(View convertView) {
            tvOpen = (TextView) convertView.findViewById(R.id.tvOpen);
          //  tvCrash = (TextView) convertView.findViewById(R.id.tvCrash);
        }

        private static Holder getHolder(View convertView) {
            Holder holder = (Holder) convertView.getTag();
            if (holder == null) {
                holder = new Holder(convertView);
                convertView.setTag(holder);
            }
            return holder;
        }
    }

}
