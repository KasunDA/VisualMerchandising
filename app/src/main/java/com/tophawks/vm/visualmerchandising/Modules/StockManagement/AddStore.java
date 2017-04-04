package com.tophawks.vm.visualmerchandising.Modules.StockManagement;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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
import com.tophawks.vm.visualmerchandising.R;
import com.tophawks.vm.visualmerchandising.model.Store;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddStore extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final int MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE = 123;
    private static final int PICK_IMAGE_REQUEST_CODE = 213;
    //DECLARE THE REFERENCES FOR VIEWS AND WIDGETS
    ImageButton storePictureIB;
    EditText storeNameET, ownerNameET, emailIdET, capacityET, contactNumberET, shopAddressET, cityET, stateET;
    Spinner channelS, classificationS, categoryS;
    LinearLayout addProduct;
    //IMAGE HOLDING URI
    Uri imageHold = null;
    String callFromAddProduct;
    //STRING FIELDS
    String storeId, contactNumber, ownerName, emailId, capacity, shopAddress, storeName, channel, classification, category, city, state;
    //DATABASE AND STORAGE REFERENCES
    StorageReference mStorageReference;
    DatabaseReference mDatabaseReference;
    DatabaseReference mChildDatabase;
    StorageReference mChildStorage;
    Store storeRef;
    //PROGRESS DIALOG
    ProgressDialog mProgress;
    //CUSTOM TOOLBAR
    private Toolbar customToolbar;
    private Uri outputFileUri;

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_store);
        //CREATE THE CUSTOM TOOLBAR
        customToolbar = (Toolbar) findViewById(R.id.app_bar);
        customToolbar.setTitle("Add Store");
        customToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(customToolbar);
        callFromAddProduct = getIntent().getStringExtra("callFromAddProduct");

        mProgress = new ProgressDialog(AddStore.this);
        //ASSIGN ID'S TO OUR FIELDS
        storePictureIB = (ImageButton) findViewById(R.id.store_picture_ib);
        storeNameET = (EditText) findViewById(R.id.store_name_et);
        ownerNameET = (EditText) findViewById(R.id.store_owner_name_et);
        emailIdET = (EditText) findViewById(R.id.store_email_id_et);
        shopAddressET = (EditText) findViewById(R.id.store_shop_Address_et);
        contactNumberET = (EditText) findViewById(R.id.store_contact_number_et);
        capacityET = (EditText) findViewById(R.id.store_capacity_et);
        cityET = (EditText) findViewById(R.id.store_city_et);
        stateET = (EditText) findViewById(R.id.store_state_et);
        addProduct = (LinearLayout) findViewById(R.id.add_store_b);
        channelS = (Spinner) findViewById(R.id.store_channel_s);
        classificationS = (Spinner) findViewById(R.id.store_classification_s);
        categoryS = (Spinner) findViewById(R.id.store_category_s);
        String[] channelList = getResources().getStringArray(R.array.store_channel);
        String[] categoryList = getResources().getStringArray(R.array.store_category);
        String[] classificationList = getResources().getStringArray(R.array.store_classification);
        channelS.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, channelList));
        categoryS.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, categoryList));
        classificationS.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, classificationList));
        channelS.setOnItemSelectedListener(this);
        classificationS.setOnItemSelectedListener(this);
        categoryS.setOnItemSelectedListener(this);


        //ASSIGN REFERENCES
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();

        storePictureIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissionRequest();
                imageChooser();

            }
        });

        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StorePost();
            }


        });
    }

    private void StorePost() {

        ownerName = ownerNameET.getText().toString().trim();
        storeName = storeNameET.getText().toString().trim();
        emailId = emailIdET.getText().toString().trim();
        contactNumber = contactNumberET.getText().toString().trim();
        shopAddress = shopAddressET.getText().toString().trim();
        capacity = capacityET.getText().toString().trim();
        city = cityET.getText().toString().trim();
        state = stateET.getText().toString().trim();

        mProgress.setMessage("Uploading Image..");
        mProgress.show();

        if (!TextUtils.isEmpty(contactNumber)
                && !TextUtils.isEmpty(ownerName)
                && !TextUtils.isEmpty(emailId)
                && !TextUtils.isEmpty(shopAddress)
                && !TextUtils.isEmpty(storeName)
                && !TextUtils.isEmpty(capacity)
                && !TextUtils.isEmpty(channel)
                && !TextUtils.isEmpty(city)
                && !TextUtils.isEmpty(state)
                && imageHold != null
                && !TextUtils.isEmpty(category)
                && !TextUtils.isEmpty(contactNumber)
                && !TextUtils.isEmpty(classification)) {


            mChildStorage = mStorageReference.child("Store_Images").child(imageHold.getLastPathSegment());
            mChildDatabase = mDatabaseReference.child("Store").push();
            storeId = mChildDatabase.getKey();

            mChildStorage.putFile(imageHold).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    //GET THE DOWNLOAD URL FROM THE TASK SUCCESS
                    //noinspection VisibleForTests
                    Uri downloadUri = taskSnapshot.getDownloadUrl();


                    //ENTER ALL THE PRODUCTS WITH KEYS IN THE DATASBSE
                    storeRef = new Store(storeId
                            , storeName
                            , ownerName
                            , emailId
                            , shopAddress
                            , channel
                            , classification
                            , category
                            , Long.valueOf(capacity)
                            , Long.valueOf(contactNumber)
                            , downloadUri.toString()
                            , city
                            , state);
                    mChildDatabase.setValue(storeRef);

                }
            });

            mProgress.dismiss();

        } else {

            mProgress.dismiss();
            Toast.makeText(this, "Please make sure you enter all fields", Toast.LENGTH_LONG).show();

        }

        DatabaseReference mChildDatabase1 = mDatabaseReference.child("StoreNames");
        mChildDatabase1.child(storeId).setValue(storeName);

        if (callFromAddProduct != null && callFromAddProduct.equals("true")) {
            Intent returnStoreNameToAddProduct = new Intent();
            returnStoreNameToAddProduct.putExtra("storeNameForProduct", storeName);
            returnStoreNameToAddProduct.putExtra("storeIdForProduct", storeId);
            setResult(RESULT_OK, returnStoreNameToAddProduct);
            mProgress.dismiss();
            finish();
        } else {

            AlertDialog.Builder doneBuilder = new AlertDialog.Builder(getApplicationContext())
                    .setMessage("Store is added !!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            doneBuilder.create().show();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    //PERMISSIONS REQUIRED FOR ACCESSING EXTERNAL STORAGE

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
    @RequiresApi(api = Build.VERSION_CODES.DONUT)
    private void imageChooser() {

        File root = new File(Environment.getExternalStorageDirectory() + File.separator + "Track'n'Train" + File.separator + "Store Picture" + File.separator);
        root.mkdirs();
        final String fname = "storePic" + System.currentTimeMillis() + ".jpg";
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
                        File temp = File.createTempFile("store", "pic.jpg");
                        FileOutputStream fileOutputStream = new FileOutputStream(temp);
                        fileOutputStream.write(bytesBitmap);
                        fileOutputStream.flush();
                        fileOutputStream.close();
                        imageHold = Uri.fromFile(temp);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    storePictureIB.setImageURI(imageHold);
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int spinnerId = parent.getId();
        String selectedItem = parent.getItemAtPosition(position).toString();
        switch (spinnerId) {
            case R.id.store_channel_s:
                channel = selectedItem;
                break;
            case R.id.store_classification_s:
                classification = selectedItem;
                break;
            case R.id.store_category_s:
                category = selectedItem;
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
