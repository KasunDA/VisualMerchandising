package com.tophawks.vm.visualmerchandising.Modules.VisualMerchandising;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.tophawks.vm.visualmerchandising.R;
import com.tophawks.vm.visualmerchandising.adapter.SearchViewRecyclerAdapter;
import com.tophawks.vm.visualmerchandising.model.Product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AllProducts extends AppCompatActivity implements SearchView.OnQueryTextListener {
    RecyclerView searchResultsRV;
    Toolbar searchToolbar;
    ArrayList<Product> productArrayList;
    ArrayList<Product> newList;
    SearchViewRecyclerAdapter adapter;
    DatabaseReference databaseReference;
    FirebaseRecyclerAdapter<Product, ProductViewHolder> firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        searchToolbar = (Toolbar) findViewById(R.id.search_toolbar);
        setSupportActionBar(searchToolbar);

        searchResultsRV = (RecyclerView) findViewById(R.id.home_search_results_rv);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Products");
        databaseReference.keepSynced(true);
        productArrayList = new ArrayList<>();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Product, ProductViewHolder>(
                Product.class, R.layout.search_result_row, ProductViewHolder.class, databaseReference
        ) {
            @Override
            protected void populateViewHolder(ProductViewHolder holder, final Product model, int position) {

                productArrayList.add(model);
                Picasso.with(getApplicationContext()).load(model.getImageUrl()).into(holder.productThumbIV);
                int originalPrice = (int) productArrayList.get(position).getOriginalPrice();
                int discountPrice = (int) productArrayList.get(position).getDiscountPrice();
                int discountPercentage = (int) (100 - ((float) discountPrice / originalPrice) * 100);
                holder.productOriginalPriceTV.setText("₹ " + originalPrice);
                holder.productDiscountPriceTV.setText("₹ " + discountPrice);
                holder.productOriginalPriceTV.setPaintFlags(holder.productOriginalPriceTV.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
                holder.productDiscountPercentageTV.setText("" + discountPercentage + "% OFF!!");
                holder.productNameTV.setText(productArrayList.get(position).getProductName());
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent blogDetail = new Intent(AllProducts.this, ProductDescription.class);
                        String itemIdForIntent = model.getProductId().toString();
                        blogDetail.putExtra("product_id", itemIdForIntent);
                        startActivity(blogDetail);
                    }
                });

            }
        };
        adapter=new SearchViewRecyclerAdapter(this,productArrayList);
        searchResultsRV.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        searchResultsRV.setAdapter(firebaseRecyclerAdapter);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem searchAction = menu.findItem(R.id.search_item);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchAction);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.sort_name_item:
                if (newList == null)
                    newList = new ArrayList<>(productArrayList);
                Collections.sort(newList, new Comparator<Product>() {
                    @Override
                    public int compare(Product o1, Product o2) {
                        return o1.getProductName().compareTo(o2.getProductName());
                    }
                });
                adapter.sortProduct(newList);
                break;
            case R.id.sort_price_item:
                if (newList == null)
                    newList = new ArrayList<>(productArrayList);
                Collections.sort(newList, new Comparator<Product>() {
                    @Override
                    public int compare(Product o1, Product o2) {
                        if (o1.getOriginalPrice() == o2.getOriginalPrice())
                            return 0;
                        else if (o1.getOriginalPrice() > o2.getOriginalPrice())
                            return 1;
                        else
                            return -1;
                    }
                });
                adapter.sortProduct(newList);
                break;
            case R.id.sort_popularity_item:
                if (newList == null)
                    newList = new ArrayList<>(productArrayList);
                Collections.sort(newList, new Comparator<Product>() {
                    @Override
                    public int compare(Product o1, Product o2) {
                        if (o1.getProductPopularity() == o2.getProductPopularity())
                            return 0;
                        else if (o1.getProductPopularity() > o2.getProductPopularity())
                            return 1;
                        else
                            return -1;
                    }
                });
                adapter.sortProduct(newList);
                break;
            case R.id.sort_date_item:
                //TODO Sort on basis of time
                if (newList == null)
                    newList = new ArrayList<>(productArrayList);
                Collections.sort(newList, new Comparator<Product>() {
                    @Override
                    public int compare(Product o1, Product o2) {
                        return o1.getProductName().compareTo(o2.getProductName());
                    }
                });
                adapter.sortProduct(newList);
                break;

        }
        searchResultsRV.setAdapter(adapter);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        newText = newText.toLowerCase();
        newList = new ArrayList<>();
        for (Product product : productArrayList) {
            if (product.getProductName().toLowerCase().contains(newText)) {
                newList.add(product);
            }

        }
        adapter.productFilter(newList);
        searchResultsRV.setAdapter(adapter);
        return true;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        View mView;
        ImageView productThumbIV;
        TextView productOriginalPriceTV;
        TextView productDiscountPriceTV;
        TextView productNameTV;
        TextView productDiscountPercentageTV;

        public ProductViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            this.productThumbIV = (ImageView) itemView.findViewById(R.id.row_item_thum_iv);
            this.productOriginalPriceTV = (TextView) itemView.findViewById(R.id.row_item_original_price_tv);
            this.productDiscountPriceTV = (TextView) itemView.findViewById(R.id.row_item_discount_price_tv);
            this.productNameTV = (TextView) itemView.findViewById(R.id.row_item_name_tv);
            this.productDiscountPercentageTV = (TextView) itemView.findViewById(R.id.row_item_discount_percent_tv);
        }
    }
}
