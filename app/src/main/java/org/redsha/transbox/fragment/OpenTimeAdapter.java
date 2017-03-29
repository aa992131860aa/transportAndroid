package org.redsha.transbox.fragment;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.redsha.transbox.App;
import org.redsha.transbox.R;
import org.redsha.transbox.bean.CrashBean;
import org.redsha.transbox.engine.BasicAdapter;

import java.util.ArrayList;

public class OpenTimeAdapter extends BasicAdapter {
    public OpenTimeAdapter(Context context, ArrayList list) {
        super(context, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(App.getContext(), R.layout.adapter_open_time, null);
        }
        Holder holder = Holder.getHolder(convertView);
        //set data
        CrashBean info = (CrashBean) list.get(position);
        holder.tvOpen.setText(info.getoInfo());
        holder.tvCrash.setText(info.getcInfo());
        return convertView;
    }

    static class Holder {
        TextView tvOpen, tvCrash;

        private Holder(View convertView) {
            tvOpen = (TextView) convertView.findViewById(R.id.tvOpen);
            tvCrash = (TextView) convertView.findViewById(R.id.tvCrash);
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
