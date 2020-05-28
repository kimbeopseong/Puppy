package com.example.puppy.ui.list;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.puppy.R;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {

    private ArrayList<ListViewItem> listViewItemArraylist = new ArrayList<ListViewItem>();

    public ListViewAdapter() {}

    @Override
    public int getCount() {
        return listViewItemArraylist.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, parent, false);
        }

        ImageView dailyPicView = (ImageView) convertView.findViewById(R.id.daily_pictiure);
        TextView dailyDateView = (TextView) convertView.findViewById(R.id.daily_date);
        TextView dailyStatView = (TextView) convertView.findViewById(R.id.daily_stat);
        TextView dailyLvView = (TextView) convertView.findViewById(R.id.daily_lv);

        ListViewItem listViewItem = listViewItemArraylist.get(position);

        dailyPicView.setImageDrawable(listViewItem.getIcon());
        dailyDateView.setText(listViewItem.getDate());
        dailyStatView.setText(listViewItem.getStat());
        dailyLvView.setText(listViewItem.getLv());

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return listViewItemArraylist.get(position);
    }


//  분석 결과 창의 확인 버튼을 누르면 결과의 데이터 기록할 수 있도록 호출
    public void addItem(Drawable icon, String date, String stat, String lv){
        ListViewItem item = new ListViewItem();

        item.setIcon(icon);
        item.setDate(date);
        item.setStat(stat);
        item.setLv(lv);

        listViewItemArraylist.add(item);
    }

}
