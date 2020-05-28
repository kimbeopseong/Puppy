package com.example.puppy.ui.list;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.ListFragment;

import com.example.puppy.R;
import com.example.puppy.ResultActivity;


public class MenuListFragment extends ListFragment {

    ListViewAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        adapter = new ListViewAdapter();
        setListAdapter(adapter);

//      예시로 하나 추가해둠.
        adapter.addItem(ContextCompat.getDrawable(getActivity(), R.drawable.ic_dashboard_black_24dp), "20200521","DashBoard", "dashboard");

//      분석 창에서 확인 버튼을 누르면 분석결과의 내용이 addItem될 수 있도록 함수 구현

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        ListViewItem item = (ListViewItem) l.getItemAtPosition(position);
        Intent intent = new Intent(v.getContext(), ResultActivity.class);
        startActivity(intent);
//        클릭된 아이템에 해당하는 분석 결과를 호출하도록 함수 구현
        super.onListItemClick(l, v, position, id);
    }
}
