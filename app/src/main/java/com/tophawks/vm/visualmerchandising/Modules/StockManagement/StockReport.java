package com.tophawks.vm.visualmerchandising.Modules.StockManagement;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tophawks.vm.visualmerchandising.R;
import com.tophawks.vm.visualmerchandising.model.Product;

import org.joda.time.LocalDate;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class StockReport extends AppCompatActivity implements View.OnClickListener {

    TextView startDateTV;
    TextView endDateTV;
    Button selectStoreB;
    Button generateReport;
    RecyclerView storeNameRV, productNameRV, availableRV, dateRV;
    ArrayList<Integer> checkedItemsPositions = new ArrayList<Integer>();
    ArrayList<String> checkedStoreNames = new ArrayList<>();
    ArrayList<String> checkedStoreKeys = new ArrayList<>();
    ArrayList<String> storeNamesListForRV = new ArrayList<>();
    ArrayList<String> productNamesListForRV = new ArrayList<>();
    ArrayList<String> availableItemsListForRV = new ArrayList<>();
    ArrayList<String> dateListForRV = new ArrayList<>();
    DatePickerDialog datePickerDialog;
    LinearLayout reportLinearLayout;
    ProgressDialog storeSelectProgressDialog;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_report);
        startDateTV = (TextView) findViewById(R.id.stock_report_start_date_tv);
        endDateTV = (TextView) findViewById(R.id.stock_report_end_date_tv);
        selectStoreB = (Button) findViewById(R.id.stock_report_select_store_b);
        generateReport = (Button) findViewById(R.id.stock_report_generate_report_b);
        storeNameRV = (RecyclerView) findViewById(R.id.stock_report_store_name_rv);
        productNameRV = (RecyclerView) findViewById(R.id.stock_report_product_name_rv);
        availableRV = (RecyclerView) findViewById(R.id.stock_report_available_rv);
        dateRV = (RecyclerView) findViewById(R.id.stock_report_date_rv);
        reportLinearLayout = (LinearLayout) findViewById(R.id.report_linear_layout);
        storeSelectProgressDialog = new ProgressDialog(StockReport.this);

        startDateTV.setOnClickListener(this);
        endDateTV.setOnClickListener(this);
        selectStoreB.setOnClickListener(this);
        generateReport.setOnClickListener(this);

        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.stock_report_start_date_tv:
                datePickerDialog = new DatePickerDialog(StockReport.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        startDateTV.setText("" + i2 + "/" + (i1 + 1) + "/" + i);
                    }
                }, LocalDate.now().getYear(), LocalDate.now().getMonthOfYear() - 1, LocalDate.now().getDayOfMonth());
                datePickerDialog.show();
                break;
            case R.id.stock_report_end_date_tv:
                datePickerDialog = new DatePickerDialog(StockReport.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        endDateTV.setText("" + i2 + "/" + (i1 + 1) + "/" + i);
                    }
                }, LocalDate.now().getYear(), LocalDate.now().getMonthOfYear() - 1, LocalDate.now().getDayOfMonth());
                datePickerDialog.show();
                break;
            case R.id.stock_report_select_store_b:
                storeSelectProgressDialog.setMessage("Getting all stores!!");
                storeSelectProgressDialog.setProgressPercentFormat(NumberFormat.getPercentInstance());
                storeSelectProgressDialog.show();
                dialogBoxBuild();

                break;
            case R.id.stock_report_generate_report_b:
                reportLinearLayout.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void dialogBoxBuild() {
        final ArrayList<String> storeNames = new ArrayList<>();
        final ArrayList<String> storeKeys = new ArrayList<>();

        final DatabaseReference databaseChildReference = databaseReference.child("StoreNames");
        databaseChildReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, String> nameMap = (HashMap<String, String>) dataSnapshot.getValue();
                final boolean checkedStoresBool[] = new boolean[nameMap.keySet().size()];
                checkedItemsPositions = new ArrayList<>();
                for (String key : nameMap.keySet()) {
                    storeNames.add(nameMap.get(key));
                    storeKeys.add(key);
                }
                CharSequence stores[] = storeNames.toArray(new CharSequence[storeNames.size()]);
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(StockReport.this);
                dialogBuilder.setTitle("Stores:")
                        .setMultiChoiceItems(stores, checkedStoresBool, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                if (which == 1) {

                                }
                                if (isChecked) {
                                    if (!checkedItemsPositions.contains(which)) {
                                        checkedItemsPositions.add(which);
                                    }
                                } else {
                                    if (checkedItemsPositions.contains(which)) {
                                        checkedItemsPositions.remove((Integer) which);
                                    }
                                }
                            }
                        })
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                checkedStoreNames = new ArrayList<>(checkedItemsPositions.size());
                                checkedStoreKeys = new ArrayList<>(checkedItemsPositions.size());
                                for (int i : checkedItemsPositions) {
                                    checkedStoreNames.add(storeNames.get(i));
                                    checkedStoreKeys.add(storeKeys.get(i));
                                }

                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                for (int i = 0; i < checkedStoresBool.length; i++) {
                                    checkedStoresBool[i] = false;
                                }
                                checkedItemsPositions.clear();
                                checkedStoreNames.clear();
                                checkedStoreKeys.clear();
                            }
                        })
                        .create()
                        .show();
                storeSelectProgressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(StockReport.this, "DataBase Error:  " +
                        databaseError, Toast.LENGTH_LONG).show();

            }
        });

        stockReportGeneration(checkedStoreKeys);
    }

    private void stockReportGeneration(ArrayList<String> checkedStoreKeys) {

        for (String storeKey : checkedStoreKeys) {
            final DatabaseReference databaseChildReference = databaseReference.child("Store").child(storeKey).child("Products");
            databaseChildReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    HashMap<String, Product> products = (HashMap<String, Product>) dataSnapshot.getValue();
                    if (products != null) {
                        for (String productKey : products.keySet()) {
                            Product currentProduct = products.get(productKey);
                            productNamesListForRV.add(currentProduct.getProductName());
                            storeNamesListForRV.add(currentProduct.getStoreName());
                            availableItemsListForRV.add("" + currentProduct.getProductQuantity());
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

}
