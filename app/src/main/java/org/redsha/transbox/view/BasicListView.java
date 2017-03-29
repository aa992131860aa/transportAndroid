package org.redsha.transbox.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class BasicListView extends ListView {
    private int listViewHeight; // listView的高度

    public int getListViewHeight() {
        return listViewHeight;
    }

    public void setListViewHeight(int listViewHeight) {
        this.listViewHeight = listViewHeight;
    }

    public BasicListView(Context context) {
        this(context, null);
    }

    public BasicListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BasicListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setDividerHeight(0);
        setSelector(android.R.color.transparent);
        setOverScrollMode(ListView.OVER_SCROLL_NEVER);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (listViewHeight > -1) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(listViewHeight,
                    MeasureSpec.AT_MOST);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
