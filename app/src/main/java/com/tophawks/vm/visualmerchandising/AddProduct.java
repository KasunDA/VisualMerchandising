package com.tophawks.vm.visualmerchandising;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.tophawks.vm.visualmerchandising.model.Product;

public class AddProduct extends AppCompatActivity {

    private static final int GALLERY_REQUEST_CODE = 299;
    //DECLARE THE REFERENCES FOR VIEWS AND WIDGETS
    ImageButton productImage;
    EditText productName, originalPrice, discountPrice, wholeSalePrice, retailPrice, proQuantity, proColor, proSpec;
    Spinner categoryS, brandNameS;
    LinearLayout addProduct;
    //IMAGE HOLDING URI
    Uri imageHold = null;

    //STRING FIELDS
    String whoPrice, orgPrice, disPrice, retPrice, proName, quantity, proColorName, proSpecification, category, brandName;

    //DATABASE AND STORAGE REFERENCES
    StorageReference mStorageReference;
    DatabaseReference mDatabaseReference;
    ArrayAdapter brandNameAdapter, categoryAdapter;

    //PROGRESS DIALOG
    ProgressDialog mProgress;

    //CUSTOM TOOLBAR
    private Toolbar customToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //CREATE THE CUSTOM TOOLBAR
        customToolbar = (Toolbar) findViewById(R.id.app_bar);
        customToolbar.setTitle("Add Product");
        customToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(customToolbar);


        mProgress = new ProgressDialog(this);

        //ASSIGN ID'S TO OUR FIELDS
        productImage = (ImageButton) findViewById(R.id.productImageButton);
        productName = (EditText) findViewById(R.id.product_name_edittext);
        originalPrice = (EditText) findViewById(R.id.original_price_edittext);
        retailPrice = (EditText) findViewById(R.id.retail_price_edittext);
        wholeSalePrice = (EditText) findViewById(R.id.wholesale_price_edittext);
        discountPrice = (EditText) findViewById(R.id.discount_price_edittext);
        proQuantity = (EditText) findViewById(R.id.quantity);
        proColor = (EditText) findViewById(R.id.product_color);
        proSpec = (EditText) findViewById(R.id.product_specification);
        addProduct = (LinearLayout) findViewById(R.id.addProductButton);

        categoryS = (Spinner) findViewById(R.id.detail_category_s);
        brandNameS = (Spinner) findViewById(R.id.detail_brand_name_s);
        brandNameAdapter = ArrayAdapter.createFromResource(this, R.array.product_brand_name, android.R.layout.simple_spinner_item);
        categoryAdapter = ArrayAdapter.createFromResource(this, R.array.product_category, android.R.layout.simple_spinner_item);
        brandNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        brandNameS.setAdapter(brandNameAdapter);
        categoryS.setAdapter(categoryAdapter);

        categoryS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        brandNameS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                brandName = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //ASSIGN REFERENCES
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();

        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent imagePickIntent = new Intent(Intent.ACTION_PICK);
                imagePickIntent.setType("image/*");
                startActivityForResult(imagePickIntent, GALLERY_REQUEST_CODE);
            }
        });

        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                productPost();
            }


        });
    }
    private void productPost() {

        whoPrice = wholeSalePrice.getText().toString().trim();
        retPrice = retailPrice.getText().toString().trim();
        disPrice = discountPrice.getText().toString().trim();
        orgPrice = originalPrice.getText().toString().trim();
        proName = productName.getText().toString().trim();
        quantity = proQuantity.getText().toString().trim();
        proColorName = proColor.getText().toString().trim();
        proSpecification = proSpec.getText().toString().trim();

        mProgress.setMessage("Uploading Image..");
        mProgress.show();

        if (!TextUtils.isEmpty(whoPrice)
                && !TextUtils.isEmpty(orgPrice)
                && !TextUtils.isEmpty(retPrice)
                && !TextUtils.isEmpty(proName)
                && !TextUtils.isEmpty(disPrice)
                && !TextUtils.isEmpty(quantity)
                && !TextUtils.isEmpty(proColorName)
                && !TextUtils.isEmpty(proSpecification)
                && !TextUtils.isEmpty(category)
                && !TextUtils.isEmpty(brandName)) {

            if (imageHold != null) {

                StorageReference mChildStorage = mStorageReference.child("Product_Images").child(imageHold.getLastPathSegment());

                mChildStorage.putFile(imageHold).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        //GET THE DOWNLOAD URL FROM THE TASK SUCCESS
                        //noinspection VisibleForTests
                        Uri downloadUri =taskSnapshot.getDownloadUrl();
                        DatabaseReference mChildDatabase = mDatabaseReference.child("Product").push();
                        //ENTER ALL THE PRODUCTS WITH KEYS IN THE DATASBSE
                        Product productRef = new Product(mChildDatabase.getKey()
                                , proName, proColorName
                                , proSpecification
                                , downloadUri.toString()
                                , Float.valueOf(whoPrice)
                                , Float.valueOf(retPrice)
                                , Float.valueOf(orgPrice)
                                , Float.valueOf(disPrice)
                                , Integer.valueOf(quantity)
                                , category, brandName, 0);
                        mChildDatabase.setValue(productRef);
                        mProgress.dismiss();
                    }
                });
            }

        } else {

            mProgress.dismiss();
            Toast.makeText(this, "Please make sure you enter all fields", Toast.LENGTH_LONG).show();

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {

            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(2, 1)
                    .start(this);


        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageHold = result.getUri();

                productImage.setImageURI(imageHold);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.main_all_products) {
            startActivity(new Intent(AddProduct.this, AllProducts.class));

        }

        return super.onOptionsItemSelected(item);
    }
}
