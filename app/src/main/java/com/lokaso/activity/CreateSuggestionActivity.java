package com.lokaso.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cropimage.CropImage;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.lokaso.R;
import com.lokaso.adapter.InterestArrayAdapter;
import com.lokaso.dao.DaoController;
import com.lokaso.dao.DaoFunctions;
import com.lokaso.model.Interest;
import com.lokaso.model.Place;
import com.lokaso.model.Suggestion;
import com.lokaso.model.SuggestionPost;
import com.lokaso.preference.MyPreferencesManager;
import com.lokaso.retrofit.RestClient;
import com.lokaso.retromodel.RetroInterest;
import com.lokaso.retromodel.RetroResponse;
import com.lokaso.retromodel.RetroSuggestionPost;
import com.lokaso.util.Constant;
import com.lokaso.util.Helper;
import com.lokaso.util.MarshmallowPermission;
import com.lokaso.util.MyDialog;
import com.lokaso.util.MyFont1;
import com.lokaso.util.MyLog;
import com.lokaso.util.MyToast;
import com.lokaso.util.NetworkConnection;
import com.lokaso.util.Picaso;
import com.lokaso.util.PopupInfo;

import org.hybridsquad.android.library.CropHandler;
import org.hybridsquad.android.library.CropHelper;
import org.hybridsquad.android.library.CropParams;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

public class CreateSuggestionActivity extends AppCompatActivity {

    private String TAG = CreateSuggestionActivity.class.getSimpleName()/*, imagePic = ""*/, errorMsg = "";
    private Context context = CreateSuggestionActivity.this;

    private TextView tvSuggestHint, tvHint, tvInterestHint, tvInterest, tvLocationHint, tvLocation, tvSuggestionHint,
            tvSuggestion, tvImageHint, tvCaption/*, tvCaptionHint*/, tvCreditHint;


    private RelativeLayout progress_layout;

    //private double lat = 0, lng = 0;
    private String lat = "0", lng = "0";
    private ImageView ivPic;
    private MarshmallowPermission marshmallowPermission;
    public static List<Interest> interestList = new ArrayList<>();
    private InterestArrayAdapter interestArrayAdapter;
    /*private List<Question> questionList = new ArrayList<>();
    private QuestionAdapter questionAdapter;
    private RecyclerView recyclerView;*/
    private ImageButton bEdit;
    private int interest_id = 1;
    private float picx, picy;
    private Button bSubmit, bMore;
    private NetworkConnection networkConnection;
    private AlertDialog.Builder builder;
    private TypedFile typedFile = null;
    private boolean checkButton = false, checkEdit = false, checkLocation = false;
    private Suggestion suggestion;
    private Uri imageToUploadUri, tempUri = null;


    private int i = 0, v=0;

    private String image = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_suggestion);

        suggestion = (Suggestion) getIntent().getSerializableExtra(Constant.SUGGESTION);
        builder = new AlertDialog.Builder(context);
        networkConnection = new NetworkConnection(context);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        tvSuggestHint = (TextView) findViewById(R.id.tvSuggestHint);
        tvHint = (TextView) findViewById(R.id.tvHint);
        tvCaption = (TextView) findViewById(R.id.tvCaption);
//        tvCaptionHint = (TextView) findViewById(R.id.tvCaptionHint);
        tvLocation = (TextView) findViewById(R.id.tvLocation);
        ivPic = (ImageView) findViewById(R.id.ivPic);
        bEdit = (ImageButton) findViewById(R.id.bEdit);
        tvInterest = (TextView) findViewById(R.id.tvInterest);
        tvInterestHint = (TextView) findViewById(R.id.tvInterestHint);
        tvLocationHint = (TextView) findViewById(R.id.tvLocationHint);
        tvSuggestionHint = (TextView) findViewById(R.id.tvSuggestionHint);
        tvSuggestion = (TextView) findViewById(R.id.tvSuggestion);
        tvImageHint = (TextView) findViewById(R.id.tvImageHint);
        tvCreditHint = (TextView) findViewById(R.id.tvCreditHint);
        bSubmit = (Button) findViewById(R.id.bSubmit);
        bMore = (Button) findViewById(R.id.bMore);

        progress_layout = (RelativeLayout) findViewById(R.id.progress_layout);

//        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        marshmallowPermission = new MarshmallowPermission(CreateSuggestionActivity.this);

        interestList = new ArrayList<>();
        interestArrayAdapter = new InterestArrayAdapter(context, interestList);

        interestList = DaoController.getAllInterest(context);

        if(interestList!=null && interestList.size()>0) {
            interestArrayAdapter.refresh(interestList);
        }
        else {
            getInterests();
        }
        /*
        if (interestList != null) {
            if (interestList.size() == 0) {
                getInterests();
            }
            interestArrayAdapter = new InterestArrayAdapter(context, interestList);
//            getQuestions();
        } else {
            getInterests();
        }
*/
        if (suggestion != null) {
            checkEdit = true;
            lat = ""+suggestion.getLat();
            lng = ""+suggestion.getLng();
            tvLocation.setText(suggestion.getLocation());
            interest_id = suggestion.getInterest_id();
            if (interestList != null) {
                if (interestList.size() > 0) {
                    tvInterest.setText(interestList.get(interest_id - 1).getName());
                }
            }
            tvSuggestion.setText(suggestion.getSuggestion());

            String imageUrl = suggestion.getImage();
            Picaso.loadSuggestion(context, imageUrl, ivPic);

            tvCaption.setText(suggestion.getCaption());
        }

        /*recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.hasFixedSize();*/

        if (toolbar != null) {
            if (!checkEdit) {
                toolbar.setTitle(R.string.create_suggestion_toolbar);
            } else {
                toolbar.setTitle(R.string.edit_suggestion);
            }
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        tvInterest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle(getString(R.string.select_interest))
                        .setAdapter(interestArrayAdapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                tvInterest.setText(interestArrayAdapter.getItem(which).getName());
                                interest_id = (int)interestArrayAdapter.getItem(which).getId();
//                                getQuestions();
                            }
                        })
                        .show();
            }
        });

        tvLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!checkLocation)
                {
                    checkLocation = true;

                    Intent intent = new Intent(context, LocationSelectActivity.class);
                    startActivityForResult(intent, Constant.SELECTION_LOCATION);


                }

                /*
                if (!checkLocation)
                {
                    checkLocation = true;
                    int PLACE_PICKER_REQUEST = Constant.LOCATION_SELECTION;
                    PlaceAutocomplete.IntentBuilder builder = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN);
                    try {
                        startActivityForResult(builder.build(CreateSuggestionActivity.this), PLACE_PICKER_REQUEST);
                    } catch (GooglePlayServicesRepairableException e) {
                        e.printStackTrace();
                        checkLocation = false;
                    } catch (GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                        checkLocation = false;
                    }
                }
                */
            }
        });

        bEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (marshmallowPermission.checkPermissionForExternalStorage()) {
                    imageCaptureOptions();

                } else {
                    marshmallowPermission.requestPermissionForExternalStorage();
                }
            }
        });

        /*ivPic.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent event) {
                MyLog.e(TAG, "onTouch");
                float curX, curY;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        picx = event.getX();
                        picy = event.getY();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        curX = event.getX();
                        curY = event.getY();
                        ivPic.scrollBy((int) (picx - curX), (int) (picy - curY));
                        picx = curX;
                        picy = curY;
                        break;

                    case MotionEvent.ACTION_UP:
                        curX = event.getX();
                        curY = event.getY();
                        ivPic.scrollBy((int) (picx - curX), (int) (picy - curY));
                        break;
                }

                return true;
            }
        });*/

        ivPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (marshmallowPermission.checkPermissionForExternalStorage()) {
                    imageCaptureOptions();

                } else {
                    marshmallowPermission.requestPermissionForExternalStorage();
                }
            }
        });


        setFont();

        registerReceiver(placeReceiver, new IntentFilter(Constant.PLACE_ACTIVITY));
    }




    private BroadcastReceiver placeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            MyLog.e(TAG, "placereceiver");


            if (intent!=null) {
                if(intent.hasExtra(Constant.PLACE)) {
                    Place place = (Place) intent.getSerializableExtra(Constant.PLACE);

                    MyLog.e(TAG, "onActivityResult SELECTION_LOCATION : data place : "+place);

                    if (place.getName() != null) {
                        tvLocation.setText(place.getName());
                        lat = place.getLatitude();
                        lng = place.getLongitude();

                    } else {
                        MyToast.tshort(context, "Please select location");
                    }
                }
                else {
                    MyLog.e(TAG, "onActivityResult SELECTION_LOCATION : data no place extra ");
                }
            }
            else {
                MyLog.e(TAG, "onActivityResult SELECTION_LOCATION : data null ");
            }
        }
    };

    @Override
    public void onBackPressed() {
        showConfirmationDialog();
    }

    private void showConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(getString(R.string.dialog_title_on_cancel));
        builder.setMessage(getString(R.string.dialog_message_on_cancel));
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                exit();
            }
        });
        builder.setNegativeButton("No", null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void exit() {
        super.onBackPressed();
    }

    /**
     * method used to get interests
     */
    private void getInterests() {
        if (networkConnection.isNetworkAvailable()) {

            String updated_date = DaoController.getLatestUpdateInterestDate(context);

            RestClient.getLokasoApi().interests(
                    updated_date,
                    new Callback<RetroInterest>() {
                @Override
                public void success(RetroInterest retroInterest, Response response) {
                    if (retroInterest.getSuccess()) {
                        interestList = retroInterest.getDetails();

                        DaoController.updateInterestList(context, interestList);

                        interestArrayAdapter = new InterestArrayAdapter(context, interestList);

                        if (suggestion != null) {
                            interest_id = suggestion.getInterest_id();
                            tvInterest.setText(interestList.get(interest_id - 1).getName());
                        }
                    } else {
                        interestList = new ArrayList<>();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    interestList = new ArrayList<>();
                    MyToast.tshort(context, "" + error);
                }
            });
        }
    }

    /**
     * Method used to set Fonts
     */
    private void setFont() {

        new MyFont1(context).setAppFont((ViewGroup)findViewById(R.id.container));
        /*
        MyFont myFont = new MyFont(context);
        myFont.setTypeface(tvSuggestHint);
        myFont.setTypeface(tvHint);
        myFont.setTypeface(tvLocationHint);
        myFont.setTypeface(tvLocation);
        myFont.setTypeface(tvInterestHint);
        myFont.setTypeface(tvInterest);
        myFont.setTypeface(tvSuggestionHint);
        myFont.setTypeface(tvSuggestion);
        myFont.setTypeface(tvImageHint);
        myFont.setTypeface(tvCaption);
//        myFont.setTypeface(tvCaptionHint);
        myFont.setTypeface(tvCreditHint);
        myFont.setTypeface(bSubmit);
        myFont.setTypeface(bMore);
        */
    }

    /**
     * method used to get questions using API call
     */
    private void getQuestions() {
        /*if (networkConnection.isNetworkAvailable()) {
            questionList = new ArrayList<>();

            RestClient.getLokasoApi().getQuestions(interest_id, new Callback<RetroQuestion>() {
                @Override
                public void success(RetroQuestion retroQuestion, Response response) {
                    if (retroQuestion.getSuccess().equals(getString(R.string.SUCCESS_TRUE))) {
                        questionList = retroQuestion.getDetails();
                        MyLog.e(TAG, "Question name : " + questionList.get(0).getName());
                        setQuestions();

                    } else {
                        MyToast.tshort(context, retroQuestion.getMessage() + "");
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    MyToast.tshort(context, "" + error);
                }
            });
        } else {
            MyToast.tshort(context, getString(R.string.check_network));
        }*/
    }

    /**
     * method used to set Questions adapter
     */
    private void setQuestions() {
        /*questionAdapter = new QuestionAdapter(context, questionList);
        recyclerView.setAdapter(questionAdapter);*/
    }

    /**
     * method used to provide options to capture image
     */

    private void imageCaptureOptions() {

        takePic();

        if(true)
            return;

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
        builderSingle.setTitle(R.string.select_profile_picture);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.select_dialog_singlechoice);
        arrayAdapter.add(getString(R.string.take_photo));
        arrayAdapter.add(getString(R.string.choose_from_library));
        builderSingle.setAdapter(
                arrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = arrayAdapter.getItem(which);
                        if (getString(R.string.take_photo).equalsIgnoreCase(strName)) {
                            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            String image_file_name = Helper.randomAlphaNumeric(Constant.IMAGENAME_COUNT) + "_img.jpg";
                            File f = new File(Environment.getExternalStorageDirectory() + Constant.DCIM_CAMERA_PATH, image_file_name);
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                            imageToUploadUri = Uri.fromFile(f);
                            startActivityForResult(cameraIntent, Constant.CAMERA_PIC_REQUEST);

                            /*String image_file_name = Helper.randomAlphaNumeric(Constant.IMAGENAME_COUNT) + "_img.jpg";
                            File f = new File(Environment.getExternalStorageDirectory() + Constant.DCIM_CAMERA_PATH, image_file_name);
                            imageToUploadUri = Uri.fromFile(f);

                            Intent intent = CropHelper.buildCaptureIntent(imageToUploadUri);
                            startActivityForResult(intent, CropHelper.REQUEST_CAMERA);*/

                        } else if (getString(R.string.choose_from_library).equalsIgnoreCase(strName)) {
                            selectFile();
                        }
                    }
                });
        builderSingle.show();
    }

    /**
     * method used to select file
     */
    private void selectFile() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, Constant.SELECT_FILE1);

        /*CropParams cropParams = new CropParams();
        cropParams.crop = "true";
        cropParams.aspectX = Constant.ASPECTX;
        cropParams.aspectY = Constant.ASPECTY;
        cropParams.outputX = Constant.OUTPUTX;
        cropParams.outputY = Constant.OUTPUTY;
        cropParams.returnData = true;

        Intent intent = CropHelper.buildCropFromGalleryIntent(cropParams);
        startActivityForResult(intent, CropHelper.REQUEST_CROP);*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MarshmallowPermission.EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    imageCaptureOptions();
                }
                return;
            }

            default:
                return;
        }
    }

    CropHandler cropHandler = new CropHandler() {
        @Override
        public void onPhotoCropped(Uri uri) {

            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String imgPath = cursor.getString(columnIndex);
                MyLog.e(TAG, "imgPath : " + imgPath);
                cursor.close();

                Bitmap thumbnail = BitmapFactory.decodeFile(imgPath);
//                          thumbnail.compress(Bitmap.CompressFormat.JPEG, 40, new FileOutputStream(imgPath));
                int targetHeight = (int) (thumbnail.getHeight() * Constant.size_big / (double) thumbnail.getWidth());
                thumbnail = getResizedBitmap(thumbnail, Constant.size_big, targetHeight);

                Uri tempUri = getImageUri(getApplicationContext(), thumbnail);
                File imageFile = new File(getRealPathFromURI(tempUri));

                typedFile = new TypedFile("image/jpg", imageFile);
                ivPic.setImageBitmap(thumbnail);
            }
        }

        @Override
        public void onCropCancel() {

        }

        @Override
        public void onCropFailed(String message) {

        }

        @Override
        public CropParams getCropParams() {
            return null;
        }

        @Override
        public Activity getContext() {
            return null;
        }
    };

    @Override
    protected void onDestroy() {
        if (cropHandler.getCropParams() != null)
            CropHelper.clearCachedCropFile(cropHandler.getCropParams().uri);


        try {
            unregisterReceiver(placeReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }


        super.onDestroy();
    }
/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        CropHelper.handleResult(cropHandler, requestCode, resultCode, data);
        switch (requestCode) {
            case Constant.CAMERA_PIC_REQUEST:
                try {
//                    Bitmap thumbnail = (Bitmap) data.getExtras().get(Constant.DATA);
                    Uri imgPath = imageToUploadUri;
                    tempUri = imageToUploadUri;
                    MyLog.e(TAG, "Camera imgPath : " + imgPath);
                    boolean check = performCrop(imgPath);

//                    if (!check) {
////                      thumbnail.compress(Bitmap.CompressFormat.JPEG, 40, new ByteArrayOutputStream());
//                        int targetHeight = (int) (thumbnail.getHeight() * Constant.size_big / (double) thumbnail.getWidth());
//                        thumbnail = getResizedBitmap(thumbnail, Constant.size_big, targetHeight);
//
//                        Uri tempUri = getImageUri(context, thumbnail);
//
//                        File imageFile = new File(getRealPathFromURI(tempUri));
//                        typedFile = new TypedFile("image/jpg", imageFile);
//                        ivPic.setImageBitmap(thumbnail);
//                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                break;

            case Constant.SELECT_FILE1:
                try {
                    Uri selectedImage = data.getData();
                    tempUri = data.getData();
                    boolean check = performCrop(selectedImage);

                    if (!check) {
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                        if (cursor != null) {
                            cursor.moveToFirst();
                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            String imgPath = cursor.getString(columnIndex);
                            MyLog.e(TAG, "imgPath : " + imgPath);
                            cursor.close();

                            Bitmap thumbnail = BitmapFactory.decodeFile(imgPath);
//                          thumbnail.compress(Bitmap.CompressFormat.JPEG, 40, new FileOutputStream(imgPath));
                            int targetHeight = (int) (thumbnail.getHeight() * Constant.size_big / (double) thumbnail.getWidth());
                            thumbnail = getResizedBitmap(thumbnail, Constant.size_big, targetHeight);

                            Uri tempUri = getImageUri(getApplicationContext(), thumbnail);
                            File imageFile = new File(getRealPathFromURI(tempUri));

                            typedFile = new TypedFile("image/jpg", imageFile);
                            ivPic.setImageBitmap(thumbnail);
                        }
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                break;

            case Constant.CROP_PIC:
                try {
                    Bundle extras = data.getExtras();
                    Bitmap thePic = extras.getParcelable(Constant.DATA);
                    Uri tempUri = getImageUri(getApplicationContext(), thePic);
                    File imageFile = new File(getRealPathFromURI(tempUri));
                    typedFile = new TypedFile("image/jpg", imageFile);
                    ivPic.setImageBitmap(thePic);

                } catch (Exception e) {
                    e.printStackTrace();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(tempUri, filePathColumn, null, null, null);
                    if (cursor != null) {
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String imgPath = cursor.getString(columnIndex);
                        MyLog.e(TAG, "imgPath : " + imgPath);
                        cursor.close();

                        Bitmap thumbnail = BitmapFactory.decodeFile(imgPath);
//                          thumbnail.compress(Bitmap.CompressFormat.JPEG, 40, new FileOutputStream(imgPath));
                        int targetHeight = (int) (thumbnail.getHeight() * Constant.size_big / (double) thumbnail.getWidth());
                        thumbnail = getResizedBitmap(thumbnail, Constant.size_big, targetHeight);

                        Uri tempUri = getImageUri(getApplicationContext(), thumbnail);
                        File imageFile = new File(getRealPathFromURI(tempUri));

                        typedFile = new TypedFile("image/jpg", imageFile);
                        ivPic.setImageBitmap(thumbnail);
                    }

                }
                break;

            default:
                break;
        }
    }
*/


    /**
     * this function does the crop operation.
     *
     * @param selectedImage
     */
    private boolean performCrop(Uri selectedImage) {
        if (selectedImage != null) {
            try {
                Intent cropIntent = new Intent("com.android.camera.action.CROP");
                cropIntent.setDataAndType(selectedImage, "image/*");
                cropIntent.putExtra("crop", "true");
                cropIntent.putExtra("aspectX", Constant.ASPECTX);
                cropIntent.putExtra("aspectY", Constant.ASPECTY);
                cropIntent.putExtra("outputX", Constant.OUTPUTX);
                cropIntent.putExtra("outputY", Constant.OUTPUTY);
                cropIntent.putExtra("return-data", true);
                startActivityForResult(cropIntent, Constant.CROP_PIC);
                return true;

            } catch (ActivityNotFoundException anfe) {
                MyToast.tshort(context, "This device doesn't support the crop action!");
                return false;
            }

        } else {
            MyToast.tshort(context, "Selected image is null");
            return false;
        }
    }

    private boolean performCrop2(Uri selectedImage) {
        if (selectedImage != null) {
            try {
                Intent cropIntent = new Intent("com.android.camera.action.CROP");
                cropIntent.setDataAndType(selectedImage, "image/*");
                cropIntent.setClassName("com.android.gallery", "com.android.camera.CropImage");
                cropIntent.putExtra("crop", "true");
                cropIntent.putExtra("aspectX", Constant.ASPECTX);
                cropIntent.putExtra("aspectY", Constant.ASPECTY);
                cropIntent.putExtra("outputX", Constant.OUTPUTX);
                cropIntent.putExtra("outputY", Constant.OUTPUTY);
                cropIntent.putExtra("return-data", true);
                startActivityForResult(cropIntent, Constant.CROP_PIC);
                return true;

            } catch (ActivityNotFoundException anfe) {
                MyToast.tshort(context, "This device doesn't support the crop action!");
                return false;
            }

        } else {
            MyToast.tshort(context, "Selected image is null");
            return false;
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        /*ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);*/
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Suggestion", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    /**
     * method used to resize the bitmap
     *
     * @param image
     * @param bitmapWidth
     * @param bitmapHeight
     * @return
     */
    public Bitmap getResizedBitmap(Bitmap image, int bitmapWidth, int bitmapHeight) {
        return Bitmap.createScaledBitmap(image, bitmapWidth, bitmapHeight, true);
    }

    /**
     * method used to convert bitmap image to string
     *
     * @param bitmap
     * @return
     */
    private String convertBitmapImageToString(Bitmap bitmap) {
        if (bitmap != null) {
            byte[] byteArray = null;
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
                byteArray = byteArrayOutputStream.toByteArray();
                cleanImageData(byteArrayOutputStream);
            } catch (OutOfMemoryError ome) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 35, byteArrayOutputStream);
                byteArray = byteArrayOutputStream.toByteArray();
                cleanImageData(byteArrayOutputStream);
            } finally {
                return Base64.encodeToString(byteArray, Base64.DEFAULT);
            }
        } else {
            return "";
        }
    }

    /**
     * method used to clean image data
     *
     * @param byteArrayOutputStream
     */
    private void cleanImageData(ByteArrayOutputStream byteArrayOutputStream) {
        if (byteArrayOutputStream != null) {
            try {
                byteArrayOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * method used on click of Submit button which submit data to server and finish the activity
     *
     * @param view : Button view
     */
    public void onSubmitClick(View view) {
        String location = tvLocation.getText().toString().trim();
        String suggestion = tvSuggestion.getText().toString().trim();
        String caption = tvCaption.getText().toString().trim();

        if (validateForm(suggestion, caption, location, typedFile)) {
            checkButton = false;
            if (!checkEdit) {
                setSuggestion(location, interest_id, suggestion, caption, checkButton);

            } else {
                int suggestion_id = this.suggestion.getId();
                editSuggestion(suggestion_id, location, interest_id, suggestion, caption, checkButton);
            }

        } else {
            MyToast.tshort(context, errorMsg + "");
        }
    }

    /**
     * method used on click of More button which submit data to server and opens the same form
     *
     * @param view : Button view
     */
    public void onMoreClick(View view) {
        String location = tvLocation.getText().toString().trim();
        String suggestion = tvSuggestion.getText().toString().trim();
        String caption = tvCaption.getText().toString().trim();

        if (validateForm(suggestion, caption, location, typedFile)) {
            checkButton = true;
            if (!checkEdit) {
                setSuggestion(location, interest_id, suggestion, caption, checkButton);

            } else {
                int suggestion_id = this.suggestion.getId();
                editSuggestion(suggestion_id, location, interest_id, suggestion, caption, checkButton);
            }

        } else {
            MyToast.tshort(context, errorMsg + "");
        }
    }

    /**
     * method used to post discovery using API call
     *
     * @param location    : Location selected for the suggestion
     * @param interest_id : Interest id for the suggestion
     * @param suggestion  : Suggestion detail
     * @param caption     : Caption for the image selected
     */
    private void setSuggestion(String location, int interest_id, String suggestion, String caption, final boolean checkButton) {
        if (networkConnection.isNetworkAvailable()) {

            progress_layout.setVisibility(View.VISIBLE);

            Map<String, Object> params = new HashMap<>();
            params.put(Constant.PARAM_USER_ID, MyPreferencesManager.getId(context));
            params.put(Constant.PARAM_LOCATION, location);
            params.put(Constant.PARAM_INTEREST_ID, interest_id);
            params.put(Constant.PARAM_SUGGESTION, suggestion);
            params.put(Constant.PARAM_CAPTION, caption);
            params.put(Constant.PARAM_LAT, lat);
            params.put(Constant.PARAM_LNG, lng);
            //params.put(Constant.PARAM_IMAGE, image!=null ? image : image);

            RestClient.getLokasoApi().createSuggestion(params, typedFile, "dummy",
                    new Callback<RetroSuggestionPost>() {
                @Override
                public void success(RetroSuggestionPost retroResponse, Response response) {
                    //Testing
                    //Toast.makeText(getApplicationContext(),"hey!",Toast.LENGTH_SHORT).show();
                    //Log.d("vasa_out_msg","hi");

                    progress_layout.setVisibility(View.GONE);
                    if (retroResponse.isSuccess()) {

                        SuggestionPost suggestionPost = retroResponse.getDetails();

                        MyDialog.with(context)
                                .title(suggestionPost.getTitle())
                                .message(suggestionPost.getMessage())
                                .positive("Great")
                                .setOnClickListener(new MyDialog.OnClickListener() {
                                    @Override
                                    public void onClick(MyDialog tour) {
                                        if (checkButton) {
                                            Intent intent = new Intent(context, CreateSuggestionActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            finish();
                                        }
                                    }
                                })
                                .show();
                    }
                    MyToast.tshort(context, retroResponse.getMessage() + "");
                }

                @Override
                public void failure(RetrofitError error) {
                    progress_layout.setVisibility(View.GONE);
                    MyToast.tshort(context, "" + error);
                }
            });

        } else {
            MyToast.tshort(context, getString(R.string.check_network));
        }
    }

    /**
     * Method used to Edit Suggestion using API call
     */
    private void editSuggestion(int suggestion_id, String location, int interest_id, String suggestion, String caption,
                                final boolean checkButton) {
        if (networkConnection.isNetworkAvailable()) {
            Map<String, Object> params = new HashMap<>();
            params.put(Constant.PARAM_USER_ID, MyPreferencesManager.getId(context));
            params.put(Constant.PARAM_SUGGESTION_ID, suggestion_id);
            params.put(Constant.PARAM_LOCATION, location);
            params.put(Constant.PARAM_INTEREST_ID, interest_id);
            params.put(Constant.PARAM_SUGGESTION, suggestion);
            params.put(Constant.PARAM_CAPTION, caption);
            params.put(Constant.PARAM_LAT, lat);
            params.put(Constant.PARAM_LNG, lng);
            //params.put(Constant.PARAM_IMAGE, image!=null ? image : image);

            RestClient.getLokasoApi().editSuggestion(params, typedFile, "dummy", new Callback<RetroResponse>() {
                @Override
                public void success(RetroResponse retroResponse, Response response) {
                    if (retroResponse.getSuccess()) {
                        if (checkButton) {
                            Intent intent = new Intent(context, CreateSuggestionActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(intent);
                            finish();

                        } else {
                            finish();
                        }
                    }
                    MyToast.tshort(context, retroResponse.getMessage() + "");
                }

                @Override
                public void failure(RetrofitError error) {
                    MyToast.tshort(context, "" + error);
                }
            });

        } else {
            MyToast.tshort(context, getString(R.string.check_network));
        }
    }

    /**
     * method used to validate the form
     *
     * @param suggestion
     * @param caption
     * @param location
     * @param typedFile
     * @return
     */
    private boolean validateForm(String suggestion, String caption, String location, TypedFile typedFile) {
        if (location.isEmpty()) {
            errorMsg = "Location field is empty";
            return false;
        }

        if (suggestion.isEmpty()) {
            errorMsg = "Suggestion field is empty";
            return false;
        }

        /*if (typedFile == null) {
            errorMsg = "Select image";
            return false;
        }*/

        /*if (caption.isEmpty()) {
            errorMsg = "Caption field is empty";
            return false;
        }*/

        return true;
    }




    private static final int CAMERA_REQUEST = 1;
    private static final int RESULT_LOAD_IMAGE = 2;
    private static final int REQUEST_CODE_CROP_IMAGE = 3;

    String member_image;
    private String path = null;
    boolean imageSent = true;

    public void takePic(){
        PackageManager pm = context.getPackageManager();

        CharSequence[] cs;
        if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            cs = new CharSequence[] {getString(R.string.choose_from_library), getString(R.string.take_photo)};
        }else{
            cs = new CharSequence[] {getString(R.string.choose_from_library)};
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.select_profile_picture);
        builder.setItems(cs,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                callGallery();
                                break;

                            case 1:
                                callCamera();
                                break;

                            default:
                                break;
                        }
                    }
                });

        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();


        checkLocation = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //FOR IMAGE VIEW
//        if (resultCode != RESULT_OK)
//            return;

        imageSent = false;
        switch (requestCode) {
            case CAMERA_REQUEST:

                if (resultCode == Activity.RESULT_OK)
                {
                    try {/*
						File imagesFolder = new File(Environment.getExternalStorageDirectory(), getString(R.string.app_name));
						File image = new File(imagesFolder, "image_001.jpg");
						*/
                        String image_file_name = // Helper.randomAlphaNumeric(Constant.IMAGENAME_COUNT) +
                                "temp_img.jpg";
                        File f = new File(Environment.getExternalStorageDirectory() + Constant.DCIM_CAMERA_PATH, image_file_name);
                        File image = f; //Files.getVehicleImageFile();
                        //	Bitmap bitmap = decodeFile(image.getAbsolutePath());
                        //	userImage.setImageBitmap(bitmap);
                        String P=image.getAbsolutePath();
                        MyLog.e("", P);
                        startCropImage(P);
                    } catch (Exception e) {
                        e.printStackTrace();
                        MyToast.tshort(context, "Some Error Occurred");
                    }
                }
                break;

            case RESULT_LOAD_IMAGE:
            {
                if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {

                    try {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = { MediaStore.Images.Media.DATA };
                        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String picturePath = cursor.getString(columnIndex);
                        cursor.close();

                        Bitmap bitmap = decodeFile(picturePath);
                        //	 userImage.setImageBitmap(bitmap);
                        String P = picturePath;

                        startCropImage(P);
                        MyLog.e("", P);
                    } catch (Exception e) {
                        e.printStackTrace();
                        MyToast.tshort(context, "Please select Image from gallery");

                    }
                }
            }
            break;

            case REQUEST_CODE_CROP_IMAGE:{

                try {
                    if(data!=null){
                        path = data.getStringExtra(CropImage.IMAGE_PATH);
                        // if nothing received
                        if (path == null) {
                            path = "";
                            return;
                        }
                        MyLog.e(TAG, "IMAGE AFTER CROP : "+path);
                        File imageFile = new File(path);
                        Bitmap bitmap = decodeFile(path);
                        MyLog.e(TAG, "IMAGE AFTER CROP GOT BITMAP " + bitmap.getHeight() + " , "+bitmap.getWidth());
                        ivPic.setImageBitmap(bitmap);
                        MyLog.e(TAG, "IMAGE AFTER CROP SET IMAGE ");

                        member_image = "User" + new Date().getTime() + ".jpg";

                        typedFile = new TypedFile("image/jpg", imageFile);


                        try {
                            image = ""+getImageFromPath(path);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


/*
						Ion.with(userImage)
								.transform(new RoundedTransformationIon())
								.placeholder(R.drawable.default_profile_icon)
								.error(R.drawable.default_profile_icon)
								.load(path + "");*/
                    }
                } catch (Exception e) {
                    MyLog.e(TAG, "IMAGE AFTER CROP ERROR : "+e.getMessage());
                    e.printStackTrace();
                }
            }
            break;


/*
            case Constant.LOCATION_SELECTION:
                if (resultCode == RESULT_OK) {
                    Place place = PlaceAutocomplete.getPlace(this, data);
                    if (place.getName() != null) {
                        tvLocation.setText(place.getName());
                        lat = place.getLatLng().latitude;
                        lng = place.getLatLng().longitude;

                    } else {
                        MyToast.tshort(context, "Please select location");
                    }
                }
                break;
*/
/*
            case Constant.SELECTION_LOCATION:
                MyLog.e(TAG, "onActivityResult SELECTION_LOCATION");

                if (data!=null) {
                    if(data.hasExtra(Constant.PLACE)) {
                        Place place = (Place) data.getSerializableExtra(Constant.PLACE);

                        MyLog.e(TAG, "onActivityResult SELECTION_LOCATION : data place : "+place);

                        if (place.getName() != null) {
                            tvLocation.setText(place.getName());
                            lat = place.getLatitude();
                            lng = place.getLongitude();

                        } else {
                            MyToast.tshort(context, "Please select location");
                        }
                    }
                    else {

                        MyLog.e(TAG, "onActivityResult SELECTION_LOCATION : data no place extra ");
                    }
                }
                else {

                    MyLog.e(TAG, "onActivityResult SELECTION_LOCATION : data null ");
                }

                break;
*/
        }
    }

    private void startCropImage(String p) {

        String savePath = getSavepath();

        Intent intent = new Intent(this, CropImage.class);
        intent.putExtra(CropImage.IMAGE_PATH, p);
        intent.putExtra(CropImage.IMAGE_SAVE_PATH, savePath);
        intent.putExtra(CropImage.SCALE, true);

        intent.putExtra(CropImage.ASPECT_X, 2);
        intent.putExtra(CropImage.ASPECT_Y, 2);

        startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);
    }

    private String getSavepath() {
        String image_file_name = //Helper.randomAlphaNumeric(Constant.IMAGENAME_COUNT) +
                "temp_img.jpg";
        File f = new File(Environment.getExternalStorageDirectory() + Constant.DCIM_CAMERA_PATH, image_file_name);

        File image = f; //Files.getVehicleImageFile();
        return image.getAbsolutePath();
    }


    private Bitmap decodeFile(String path) {
        File f = new File(path);
        try {
            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            //The new size we want to scale to
            final int REQUIRED_SIZE = 200;

            //Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE)
                scale *= 2;

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
        }
        return null;
    }


    private String getImageFromPath(String imagePath) {

        if(imagePath!=null)
        {
            if(imagePath.length()!=0)
            {
                Bitmap bitmap = decodeFile(imagePath);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream); /* compress to which format you want. */
                byte [] byte_arr = stream.toByteArray();
                String imageStr = com.lokaso.util.Base64.encodeBytes(byte_arr);
                return imageStr;
            }
        }
        return null;
    }


    public void callCamera() {

        String image_file_name = //Helper.randomAlphaNumeric(Constant.IMAGENAME_COUNT) +
                "temp_img.jpg";
        File f = new File(Environment.getExternalStorageDirectory() + Constant.DCIM_CAMERA_PATH, image_file_name);
        File image = f; //Files.getVehicleImageFile();

        Uri uriSavedImage = Uri.fromFile(image);

        Intent i = new Intent("android.media.action.IMAGE_CAPTURE");
        i.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
        startActivityForResult(i, CAMERA_REQUEST);

    }
    protected void callGallery()
    {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }


    public void onLocationInfoClick(View view) {

        PopupInfo popupInfo = new PopupInfo(context, view);
        popupInfo.show(getString(R.string.popup_message_location));
    }
}
