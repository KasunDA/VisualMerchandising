package com.tophawks.vm.visualmerchandising.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.tophawks.vm.visualmerchandising.Modules.StockManagement.AvailableProductActivity;
import com.tophawks.vm.visualmerchandising.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class StockMHomeFragment extends Fragment implements View.OnClickListener {

    ImageView addStoreIV;
    ImageView updateStoreIV;
    ImageView checkProductIV;
    ImageView updateProductIV;
    ImageView stockReport;

    StockMHomeFragmentListener fragmentListener;
    public StockMHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onClick(View v) {
        fragmentListener.onClickInActivityListener(v);
    }

    @Override
    public void onAttach(Context context) {

//        Activity a=(Activity)context;
        if (context instanceof StockMHomeFragmentListener) {
            fragmentListener = (StockMHomeFragmentListener) context;
        }
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_stock_mhome, container, false);
        addStoreIV = (ImageView) v.findViewById(R.id.stock_home_add_store_iv);
        updateStoreIV = (ImageView) v.findViewById(R.id.stock_home_update_store_iv);
        checkProductIV = (ImageView) v.findViewById(R.id.stock_home_check_product_iv);
        updateProductIV = (ImageView) v.findViewById(R.id.stock_home_update_product_iv);
        stockReport = (ImageView) v.findViewById(R.id.stock_home_stock_report_iv);

        addStoreIV.setOnClickListener(this);
        updateProductIV.setOnClickListener(this);
        updateStoreIV.setOnClickListener(this);
        checkProductIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getContext(), AvailableProductActivity.class));

            }
        });
        stockReport.setOnClickListener(this);

        return v;
    }

    public interface StockMHomeFragmentListener {
        void onClickInActivityListener(View v);
    }


}
