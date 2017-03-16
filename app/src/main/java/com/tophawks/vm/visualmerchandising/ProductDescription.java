package com.tophawks.vm.visualmerchandising;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProductDescription extends AppCompatActivity {


    //FIREBASE DATABASE FIELDS
    DatabaseReference mFirebaseDtabase;
    //FIELDS FOR VIEWS AND STRINGS
    private String product_key_id = null;
    private TextView productName, retailPrice, wholeSalePrice, originalPrice, discountPrice, category, brandName, specification, color, quantity;
    private ImageView productDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_description);

        //ASSIGN REFERENCES TO THE FIELDS
        retailPrice = (TextView) findViewById(R.id.retail_price_TextView);
        productName = (TextView) findViewById(R.id.product_name_TextView);
        wholeSalePrice = (TextView) findViewById(R.id.wholesale_price_TextView);
        originalPrice = (TextView) findViewById(R.id.original_price_TextView);
        discountPrice = (TextView) findViewById(R.id.discount_price_TextView);
        category = (TextView) findViewById(R.id.product_category_textView);
        brandName = (TextView) findViewById(R.id.product_brand_textView);
        specification = (TextView) findViewById(R.id.product_specification_textView);
        color = (TextView) findViewById(R.id.product_color_textView);
        quantity = (TextView) findViewById(R.id.quantity_textview);

        productDisplay = (ImageView) findViewById(R.id.productDisplayOnClickImage);

        //GET INTENT EXTRA
        product_key_id = getIntent().getStringExtra("product_id").toString();

        if (!TextUtils.isEmpty(product_key_id)) {

            Log.d("halwa", product_key_id);

            //ASSIGN FIREBASE DATABASE INSTANCE
            mFirebaseDtabase = FirebaseDatabase.getInstance().getReference().child("Product");
        } else {

            Toast.makeText(ProductDescription.this, "Unable to retrive the product info", Toast.LENGTH_LONG).show();
            finish();

        }

        mFirebaseDtabase.child(product_key_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                productName.setText(String.valueOf(dataSnapshot.child("productName").getValue()));
                retailPrice.setText(String.valueOf(dataSnapshot.child("retailPrice").getValue()));
                wholeSalePrice.setText(String.valueOf(dataSnapshot.child("wholeSalePrice").getValue()));
                originalPrice.setText(String.valueOf(dataSnapshot.child("originalPrice").getValue()));
                discountPrice.setText(String.valueOf(dataSnapshot.child("discountPrice").getValue()));
                specification.setText(dataSnapshot.child("productSpecification").getValue().toString());
                color.setText(dataSnapshot.child("productColor").getValue().toString());
                quantity.setText(String.valueOf(dataSnapshot.child("productQuantity").getValue()));
                category.setText(dataSnapshot.child("category").getValue().toString());
                brandName.setText(dataSnapshot.child("brandName").getValue().toString());
                Picasso.with(getApplicationContext()).load(dataSnapshot.child("imageUrl").getValue().toString()).into(productDisplay);

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
