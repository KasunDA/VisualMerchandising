package com.tophawks.vm.visualmerchandising.Modules.StockManagement;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.tophawks.vm.visualmerchandising.R;
import com.tophawks.vm.visualmerchandising.model.Product;

public class UpdateProductList extends AppCompatActivity {

    //FIREBASE DATABASE REFERENCE
    DatabaseReference mProductUpdateListDatabase;

    //RECYCLERVIEW FIELD
    RecyclerView mUpdateListRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_product_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //ASSIGN FIREBASE INSTANCE
        mProductUpdateListDatabase = FirebaseDatabase.getInstance().getReference().child("Product");

        //ASSIGN RECYCLERVIEW ID
        mUpdateListRecyclerView = (RecyclerView) findViewById(R.id.update_product_rv);
        mUpdateListRecyclerView.setHasFixedSize(true);
        mUpdateListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();

        //SET FIREBASE ADATAPTER WITH 4 PARAMETERS AS ARGUMENTS
        FirebaseRecyclerAdapter<Product, UpdateViewHolder> updateProductAdapter =

                new FirebaseRecyclerAdapter<Product, UpdateViewHolder>(

                        Product.class,
                        R.layout.product_list_edit_card,
                        UpdateViewHolder.class,
                        mProductUpdateListDatabase

                ) {
                    @Override
                    protected void populateViewHolder(UpdateViewHolder viewHolder, Product model, int position) {

                        viewHolder.setProductImage(getApplicationContext(), model.getImageUrl());
                        viewHolder.setProductNameTextView(model.getProductName());
                        viewHolder.setProductQuantityTextView(String.valueOf(model.getProductQuantity()));

                        //WHEN USER CLICK PEN TO EDIT THE PRODUCT INFORMATION
                        viewHolder.editProductPenImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {



                            }
                        });

                    }
                };

        //SET ADAPTER
        mUpdateListRecyclerView.setAdapter(updateProductAdapter);

    }

    //VIEWHOLDER CLASS FOR HOLDING THE VIEW OF EACH CARD
    private static class UpdateViewHolder extends RecyclerView.ViewHolder
    {

        //VIEWHOLDER FIELDS
        View mViewEntireCard;
        ImageView productImage, editProductPenImage;
        TextView productNameTextView, productQuantityTextView;

        //CONSTRUCT FOR VIEWHOLDER CLASS
        public UpdateViewHolder(View itemView) {
            super(itemView);

            //TAKE COMPLETE VIEW OF A CARD
            mViewEntireCard = itemView;

            //ASSIGN ID'S TO ALL THE FIELDS
            productImage = (ImageView) itemView.findViewById(R.id.product_Edit_Card_ImageView);
            editProductPenImage = (ImageView) itemView.findViewById(R.id.edit_Product_Card_Pen);
            productNameTextView = (TextView) itemView.findViewById(R.id.product_Name_Edit_Card_UpdateList);
            productQuantityTextView = (TextView) itemView.findViewById(R.id.product_Quantity_Card_Edit);

        }

        public void setProductImage(Context ctx, String imageUrl) {

            //SET PRODUCT IMAGE WITH PICASSO LIBRARY
            Picasso.with(ctx).load(imageUrl).into(productImage);

        }

        public void setProductNameTextView(String productName) {

            //SET PRODUCT IMAGE
            productNameTextView.setText(productName);

        }

        public void setProductQuantityTextView(String productQuantity) {

            //SET PRODUCT QUANTITY
            productQuantityTextView.setText(productQuantity);

        }
    }


}
