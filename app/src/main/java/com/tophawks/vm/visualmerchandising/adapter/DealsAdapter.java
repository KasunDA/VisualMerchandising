package com.tophawks.vm.visualmerchandising.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tophawks.vm.visualmerchandising.Modules.SalesManagement.SalesHomeActivity;
import com.tophawks.vm.visualmerchandising.R;
import com.tophawks.vm.visualmerchandising.model.Deals;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HP on 04-04-2017.
 */

public class DealsAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    private List<Deals> dealsList = new ArrayList<Deals>();
    private Context context;

    public DealsAdapter(Context context) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dealsList = SalesHomeActivity.dealsList;
    }

    @Override
    public int getCount() {
        return dealsList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItem = inflater.inflate(R.layout.deal_list_item, null, true);

        TextView dealText = (TextView) listItem.findViewById(R.id.dealstext);
        TextView status = (TextView) listItem.findViewById(R.id.statustext);
        TextView deadLine = (TextView) listItem.findViewById(R.id.deadlinetext);
        ImageView dropDown = (ImageView) listItem.findViewById(R.id.dropdown);

        dealText.setText(dealsList.get(position).getDealName());
        status.setText(dealsList.get(position).getDealStatus());
        deadLine.setText(dealsList.get(position).getDeadline());


        dropDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return listItem;
    }

}
