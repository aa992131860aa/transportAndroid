package com.otqc.transbox.controller.mixture;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.otqc.transbox.R;
import com.otqc.transbox.bean.BoxNum;
import com.otqc.transbox.json.TransferHistoryJson;
import com.otqc.transbox.json.TransferJson;

import java.util.List;

/**
 * Created by 99213 on 2017/4/23.
 */

public class BoxNumAdapter extends RecyclerView.Adapter<BoxNumAdapter.MyViewHolder> {
    //自定义监听事件
     interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view);
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;
    public void refreshList(List<TransferHistoryJson.ObjBean> boxNums){
        mBoxNums = boxNums;
        this.notifyDataSetChanged();
    }
    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    private List<TransferHistoryJson.ObjBean> mBoxNums;
    private Context mContext;

    public BoxNumAdapter(List<TransferHistoryJson.ObjBean> boxNums, Context context) {
        mBoxNums = boxNums;
        mContext = context;
    }

    @Override
    public BoxNumAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.box_num_item, parent, false);

        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(BoxNumAdapter.MyViewHolder holder, final int position) {
        holder.box_num_item_tv_no.setText("器官段号:" + mBoxNums.get(position).getOrganSeg());
        holder.box_num_item_tv_time.setText(mBoxNums.get(position).getGetTime());
        holder.box_num_item_tv_start.setText("转运起始地:" + mBoxNums.get(position).getFromCity());
        holder.box_num_item_tv_end.setText("转运目的地:" + mBoxNums.get(position).getToHospName());

        holder.box_num_item_llyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnItemClickListener!=null) {
                    mOnItemClickListener.onItemClick(v, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mBoxNums.size();
    }

    //自定义ViewHolder，用于加载图片
    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView box_num_item_tv_no;
        TextView box_num_item_tv_time;
        TextView box_num_item_tv_start;
        TextView box_num_item_tv_end;
        LinearLayout box_num_item_llyt;

        public MyViewHolder(View view) {
            super(view);
            box_num_item_tv_no = (TextView) view.findViewById(R.id.box_num_item_tv_no);
            box_num_item_tv_time = (TextView) view.findViewById(R.id.box_num_item_tv_time);
            box_num_item_tv_start = (TextView) view.findViewById(R.id.box_num_item_tv_start);
            box_num_item_tv_end = (TextView) view.findViewById(R.id.box_num_item_tv_end);
            box_num_item_llyt = (LinearLayout) view.findViewById(R.id.box_num_item_llyt);


        }
    }
}
