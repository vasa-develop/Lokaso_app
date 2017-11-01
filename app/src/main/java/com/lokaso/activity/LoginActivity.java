package com.lokaso.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.lokaso.R;
import com.lokaso.adapter.MainPagerAdapter;
import com.lokaso.fragment.SigninFragment;
import com.lokaso.model.Locations;
import com.lokaso.util.Constant;
import com.lokaso.util.MarshmallowPermission;
import com.lokaso.util.MyFont;
import com.lokaso.util.MyFont1;
import com.lokaso.util.MyLog;
import com.lokaso.preference.MyPreferencesManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class LoginActivity extends AppCompatActivity {

    private String TAG = LoginActivity.class.getSimpleName();
    private Context context = LoginActivity.this;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ImageView ivLogo;
    private CircleImageView ivPic;
    private Map<String, Locations> locationsMap = new HashMap<>();
    public static String imagePic = "";
    private MarshmallowPermission marshmallowPermission;

    private LocationListener locationListener;

    /**
     * Interface to pass location from activity to fragment
     */
    public interface LocationListener {
        void onChange(String place, double latitude, double longitude);
    }

    /**
     * Interface method location change listener
     *
     * @param locationListener
     */
    public void setOnLocationChangeListener(LocationListener locationListener) {
        this.locationListener = locationListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        getWindow().setBackgroundDrawableResource(R.mipmap.background_login);
        init();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SigninFragment.callbackManager.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Constant.CAMERA_PIC_REQUEST:
                try {
                    Bitmap thumbnail = (Bitmap) data.getExtras().get(Constant.DATA);
                    int targetHeight = (int) (thumbnail.getHeight() * Constant.size_big / (double) thumbnail.getWidth());
                    thumbnail = getResizedBitmap(thumbnail, Constant.size_big, targetHeight);

                    Uri tempUri = getImageUri(getApplicationContext(), thumbnail);
                    File imageFile = new File(getRealPathFromURI(tempUri));

//                    ivPic.setImageBitmap(getResizedBitmap(thumbnail, Constant.size_medium, Constant.size_medium));
                    ivPic.setImageBitmap(thumbnail);
                    imagePic = convertBitmapImageToString(((BitmapDrawable) ivPic.getDrawable()).getBitmap());

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                break;

            case Constant.SELECT_FILE1:
                try {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    if (cursor != null) {
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String imgPath = cursor.getString(columnIndex);
                        MyLog.e(TAG, "imgPath : " + imgPath);
                        cursor.close();

                        Bitmap thumbnail = BitmapFactory.decodeFile(imgPath);
//                        thumbnail.compress(Bitmap.CompressFormat.JPEG, 40, new FileOutputStream(imgPath));
                        int targetHeight = (int) (thumbnail.getHeight() * Constant.size_big / (double) thumbnail.getWidth());
                        thumbnail = getResizedBitmap(thumbnail, Constant.size_big, targetHeight);

                        Uri tempUri = getImageUri(getApplicationContext(), thumbnail);
                        File imageFile = new File(getRealPathFromURI(tempUri));

                        /*ivPic.setImageBitmap(getResizedBitmap(BitmapFactory.decodeFile(imgPath), Constant.size_medium,
                                Constant.size_medium));*/
                        ivPic.setImageBitmap(thumbnail);
                        imagePic = convertBitmapImageToString(((BitmapDrawable) ivPic.getDrawable()).getBitmap());
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                break;

            case Constant.LOCATION_SELECTION:
                if (resultCode == RESULT_OK) {
                    Place place = PlaceAutocomplete.getPlace(this, data);
                    MyLog.e(TAG, "Place : " + place.getName());

                    if (locationListener != null) {
                        MyLog.e(TAG, "place onResult : " + place.getName());
                        locationListener.onChange(place.getName().toString(), place.getLatLng().latitude,
                                place.getLatLng().longitude);
                    }
                }
                else {

                    if (locationListener != null) {
                        locationListener.onChange("", 0, 0);
                    }
                }
                break;

            default:
                break;
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    /**
     * initialise
     */
    private void init() {
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        ivLogo = (ImageView) findViewById(R.id.ivLogo);
        ivPic = (CircleImageView) findViewById(R.id.ivPic);

        marshmallowPermission = new MarshmallowPermission(LoginActivity.this);

        tabLayout.addTab(tabLayout.newTab().setText(R.string.sign_in));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.sign_up));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        MyFont1 myFont = new MyFont1(context);
        ViewGroup slidingTabStrip = (ViewGroup) tabLayout.getChildAt(0);
        for (int i = 0, count = adapter.getCount(); i < count; i++) {
            AppCompatTextView view = (AppCompatTextView) ((ViewGroup) slidingTabStrip.getChildAt(i)).getChildAt(1);
            myFont.setFont(view, MyFont1.CENTURY_GOTHIC_BOLD);
        }

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 0) {
                    ivLogo.setVisibility(View.VISIBLE);
                    ivPic.setVisibility(View.GONE);

                } else {
                    ivLogo.setVisibility(View.GONE);
                    ivPic.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

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
    }

    /**
     * method used to down_from_top image capture options
     */
    private void imageCaptureOptions() {
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
                            startActivityForResult(cameraIntent, Constant.CAMERA_PIC_REQUEST);

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

    /**
     * method used to get resized bitmap
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
}
