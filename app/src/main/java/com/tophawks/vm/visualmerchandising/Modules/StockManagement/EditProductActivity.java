package com.tophawks.vm.visualmerchandising.Modules.StockManagement;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StreamDownloadTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.tophawks.vm.visualmerchandising.R;
import com.tophawks.vm.visualmerchandising.model.Product;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class EditProductActivity extends AppCompatActivity {

    //REQUEST CODES
    private static final int GALLERY_REQUEST_CODE = 299;
    private static final int MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE = 123;
    private static final int PICK_IMAGE_REQUEST_CODE = 213;

    //PRODUCT KEY FROM LIST INTENT
    private String product_key = "";

    //DECLARE THE REFERENCES FOR VIEWS AND WIDGETS
    ImageButton productImage;
    EditText productName, originalPrice, discountPrice, wholeSalePrice, retailPrice, proQuantity, proColor, proSpec;
    Spinner categoryS, brandNameS;
    LinearLayout saveEditProduct;

    //STRING FIELDS
    String whoPrice, orgPrice, disPrice, retPrice, proName, quantity, proColorName, proSpecification, category, brandName, imageUrlIfNotChanged;

    //IMAGE HOLDING URI
    Uri imageHold = null;
    private Uri outputFileUri;

    //DATABASE AND STORAGE REFERENCES
    StorageReference mStorageReference;
    DatabaseReference mDatabaseReference;
    ArrayAdapter brandNameAdapter, categoryAdapter;

    //PROGRESS DIALOG
    ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        saveEditProduct = (LinearLayout) findViewById(R.id.saveEditProductButton);

        categoryS = (Spinner) findViewById(R.id.detail_category_s);
        brandNameS = (Spinner) findViewById(R.id.detail_brand_name_s);

        //GET PRODUCT KEY
        product_key = getIntent().getStringExtra("product_key_edit").toString();

        mProgress = new ProgressDialog(this);


        //SET SPINNER ITEMS
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
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Product").child(product_key);
        mStorageReference = FirebaseStorage.getInstance().getReference();


        //SET CURRENT PRODUCT DETAILS
        setCurrentProductDetails();

        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissionRequest();
                imageChooser();

            }
        });

        saveEditProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                productPost();
            }


        });

    }

    private void setCurrentProductDetails() {

        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Product productItem = ((Product)dataSnapshot.getValue(Product.class));


                wholeSalePrice.setText(String.valueOf(productItem.getWholeSalePrice()));
                retailPrice.setText(String.valueOf(productItem.getRetailPrice()));
                discountPrice.setText(String.valueOf(productItem.getDiscountPrice()));
                originalPrice.setText(String.valueOf(productItem.getOriginalPrice()));
                productName.setText(String.valueOf(productItem.getProductName()));
                proQuantity.setText(String.valueOf(productItem.getProductQuantity()));
                proColor.setText(productItem.getProductColor());
                proSpec.setText(productItem.getProductSpecification());
                Picasso.with(getApplicationContext()).load(productItem.getImageUrl()).into(productImage);

                imageUrlIfNotChanged = productItem.getImageUrl();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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

                        //ENTER ALL THE PRODUCTS WITH KEYS IN THE DATASBSE
                        Product productRef = new Product(product_key
                                , proName, proColorName
                                , proSpecification
                                , downloadUri.toString()
                                , Float.valueOf(whoPrice)
                                , Float.valueOf(retPrice)
                                , Float.valueOf(orgPrice)
                                , Float.valueOf(disPrice)
                                , Integer.valueOf(quantity)
                                , category, brandName, 0);
                        mDatabaseReference.setValue(productRef);
                        finish();
                        startActivity(new Intent(EditProductActivity.this, UpdateProductList.class));
                        mProgress.dismiss();
                    }
                });
            }else

            {
                //ENTER ALL THE PRODUCTS WITH KEYS IN THE DATASBSE
                Product productRef = new Product(product_key
                        , proName, proColorName
                        , proSpecification
                        , imageUrlIfNotChanged
                        , Float.valueOf(whoPrice)
                        , Float.valueOf(retPrice)
                        , Float.valueOf(orgPrice)
                        , Float.valueOf(disPrice)
                        , Integer.valueOf(quantity)
                        , category, brandName, 0);
                mDatabaseReference.setValue(productRef);
                finish();
                startActivity(new Intent(EditProductActivity.this, UpdateProductList.class));
                mProgress.dismiss();

            }

        } else {

            mProgress.dismiss();
            Toast.makeText(this, "Please make sure you enter all fields", Toast.LENGTH_LONG).show();

        }

    }

    private void permissionRequest() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE);
            }
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE);
            }
        }
    }

    //IMAGE PICKER WHEN CHOOSE IMAGE BUTTON IS CLICKED
    private void imageChooser() {

        File root = new File(Environment.getExternalStorageDirectory() + File.separator + "Field Attendance" + File.separator);
        root.mkdirs();
        final String fname = "profpic" + System.currentTimeMillis() + ".jpg";
        final File sdImageMainDirectory = new File(root, fname);
        outputFileUri = Uri.fromFile(sdImageMainDirectory);

        //Camera
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final String localPackageName = res.activityInfo.loadLabel(packageManager).toString();
            if (localPackageName.toLowerCase().equals("camera")) {
                final Intent intent = new Intent(captureIntent);
                intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                intent.setPackage(packageName);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                cameraIntents.add(intent);
            }
        }
        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));
        startActivityForResult(chooserIntent, PICK_IMAGE_REQUEST_CODE);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST_CODE) {
                final boolean isCamera;
                if (data == null) {
                    isCamera = true;
                } else {
                    final String action = data.getAction();
                    if (action == null) {
                        isCamera = false;
                    } else {
                        isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    }
                }

                Uri selectedImageUri;
                if (isCamera) {

                    selectedImageUri = outputFileUri;
                } else {
                    selectedImageUri = data == null ? null : data.getData();
                }

                CropImage.activity(selectedImageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);

            }

            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    imageHold = result.getUri();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageHold);
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
                        byte[] bytesBitmap = byteArrayOutputStream.toByteArray();
//                        bitmap=BitmapFactory.decodeByteArray(bytesBitmap,0,bytesBitmap.length);
                        File temp = File.createTempFile("product", "pic");
                        FileOutputStream fileOutputStream = new FileOutputStream(temp);
                        fileOutputStream.write(bytesBitmap);
                        fileOutputStream.flush();
                        fileOutputStream.close();
                        imageHold = Uri.fromFile(temp);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    productImage.setImageURI(imageHold);
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
            }
        }

    }

}
