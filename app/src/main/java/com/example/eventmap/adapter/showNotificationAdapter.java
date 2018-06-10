package com.example.eventmap.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.eventmap.R;
import com.example.eventmap.models.clubCardModel;
import com.example.eventmap.models.notificationCardModel;

import java.util.ArrayList;

/**
 * Created by SS on 6/5/2018.
 */

public class showNotificationAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<notificationCardModel> notificationCardModels;

    public showNotificationAdapter (Context context, ArrayList<notificationCardModel> notificationModels){
        this.context = context;
        this.notificationCardModels = notificationModels;
    }

    @Override
    public int getCount() {
        return notificationCardModels.size();
    }

    @Override
    public Object getItem(int i) {
        return notificationCardModels.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null){
            view = View.inflate(context, R.layout.notification_list_items, null);
        }

        TextView nameHolder = (TextView)view.findViewById(R.id.nameHolder);
        TextView placeHolder = (TextView)view.findViewById(R.id.placeHolder);
        TextView timeHolder = (TextView)view.findViewById(R.id.timeHolder);

        final notificationCardModel notificationModel = notificationCardModels.get(i);
        nameHolder.setText(notificationModel.getEventName());
        if (notificationModel.getRoomNumber().equals("")){
            placeHolder.setText(notificationModel.getPlace());
        } else {
            placeHolder.setText(notificationModel.getPlace() + ". Room " + notificationModel.getRoomNumber());
        }
        timeHolder.setText(notificationModel.getStartTime() + " - " + notificationModel.getEndTime());


        return view;
    }
}
