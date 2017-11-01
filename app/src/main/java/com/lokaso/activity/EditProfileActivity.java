package com.lokaso.activity;

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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.lokaso.R;
import com.lokaso.adapter.ProfessionAdapter;
import com.lokaso.dao.DaoController;
import com.lokaso.dao.DaoFunctions;
import com.lokaso.model.Interest;
import com.lokaso.model.Profession;
import com.lokaso.model.Profile;
import com.lokaso.preference.MyPreferencesManager;
import com.lokaso.retrofit.RestClient;
import com.lokaso.retromodel.RetroProfession;
import com.lokaso.retromodel.RetroResponse;
import com.lokaso.util.AlertDialogList;
import com.lokaso.util.Constant;
import com.lokaso.util.Helper;
import com.lokaso.util.MarshmallowPermission;
import com.lokaso.util.MyFont1;
import com.lokaso.util.MyLog;
import com.lokaso.util.MyToast;
import com.lokaso.util.NetworkConnection;
import com.lokaso.util.Picaso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class EditProfileActivity extends AppCompatActivity {

    private String TAG = EditProfileActivity.class.getSimpleName(), errorMsg = "", imagePic = "";
    private Context context = EditProfileActivity.this;

    private MarshmallowPermission marshmallowPermission;
    private double latitudeVal = 0, longitudeVal = 0;
    private TextView tvLocation, tvLocationNew, tvProfession, tvEmail, tvNameHint, tvAboutMeHint, tvEmailHint, tvProfessionHint,
            tvLocationHint;
    //private TextView tvFName;
    private EditText tvFName;

    private TextView tvInterest, tvInterestHint;
    private EditText tvAboutMe;
    private Button bSaveProfile;
    private CircleImageView ivPic;
    private ImageButton bEdit;
    private List<Profession> professionList = new ArrayList<>();
    private ProfessionAdapter professionAdapter;
    private SwitchCompat notification_toggle;
    private int profession_id = 0, notification_flag = 0;
    private NetworkConnection networkConnection;
    private AlertDialog.Builder builder;
    private boolean checkLocation = false;

    private AlertDialogList alertDialogList;


    public static Profile profile;

    boolean isFromLocationSelect = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        try {
            profile = (Profile) getIntent().getSerializableExtra(Constant.PROFILE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        builder = new AlertDialog.Builder(context);
        networkConnection = new NetworkConnection(context);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        marshmallowPermission = new MarshmallowPermission(EditProfileActivity.this);
        ivPic = (CircleImageView) findViewById(R.id.ivPic);
        bEdit = (ImageButton) findViewById(R.id.bEdit);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        tvLocation = (TextView) findViewById(R.id.tvLocation);
        tvLocationNew = (TextView) findViewById(R.id.tvLocation);
        //tvFName = (TextView) findViewById(R.id.tvFName);
        tvFName = (EditText) findViewById(R.id.tvFName);
        tvAboutMe = (EditText) findViewById(R.id.tvAboutMe);
        tvProfession = (TextView) findViewById(R.id.tvProfession);
        bSaveProfile = (Button) findViewById(R.id.bSave);
        notification_toggle = (SwitchCompat) findViewById(R.id.notification_toggle);
        tvNameHint = (TextView) findViewById(R.id.tvNameHint);
        tvAboutMeHint = (TextView) findViewById(R.id.tvAboutMeHint);
        tvEmailHint = (TextView) findViewById(R.id.tvEmailHint);
        tvProfessionHint = (TextView) findViewById(R.id.tvProfessionHint);
        tvLocationHint = (TextView) findViewById(R.id.tvLocationHint);
        tvInterest = (TextView) findViewById(R.id.tvInterest);
        tvInterestHint = (TextView) findViewById(R.id.tvInterestHint);


        if (toolbar != null) {
            toolbar.setTitle(R.string.edit_profile);
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        professionList = new ArrayList<>();
        professionAdapter = new ProfessionAdapter(context, professionList);

        professionList = DaoController.getAllProfession(context);

        if (professionList.size() > 0) {
            professionAdapter.refresh(professionList);
            for (int i = 0; i < professionList.size(); i++) {
                if (professionList.get(i).getName().equals(profile.getProfession())) {
                    profession_id = (int) professionList.get(i).getId();
                    break;
                }
            }

        } else {
            getProfessions();
        }

        alertDialogList = new AlertDialogList(context);
        alertDialogList.setOnClickListener(new AlertDialogList.OnItemClickListener() {
            @Override
            public void onItemClick(Object object) {

                Profession profession = (Profession) object;

                tvProfession.setText(profession.getName());
                profession_id = (int) profession.getId();
            }
        });

        tvProfession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogList.show(professionList);
            }
        });


        new MyFont1(context).setAppFont((ViewGroup) findViewById(R.id.container));

        tvLocationNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkLocation) {
                    checkLocation = true;
                    int PLACE_PICKER_REQUEST = Constant.LOCATION_SELECTION;
                    PlaceAutocomplete.IntentBuilder builder = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN);
                    try {
                        startActivityForResult(builder.build(EditProfileActivity.this), PLACE_PICKER_REQUEST);
//                    overridePendingTransition(R.anim.open_enter_animate, R.anim.open_exit_animate);
                    } catch (GooglePlayServicesRepairableException e) {
                        e.printStackTrace();
                        checkLocation = false;
                    } catch (GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                        checkLocation = false;
                    }
                }
            }
        });


        tvInterest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, SelectInterestActivity.class);
                intent.putExtra(Constant.MODE, SelectInterestActivity.MODE_EDIT);
                intent.putExtra(Constant.PROFILE, profile);
                startActivity(intent);

            }
        });

        notification_toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    notification_flag = 1;
                } else {
                    notification_flag = 0;
                }
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

        bSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = tvFName.getText().toString().trim();
                String image = imagePic;
                String about = tvAboutMe.getText().toString().trim();
                String location = tvLocationNew.getText().toString().trim();
                String profession = tvProfession.getText().toString().trim();

                if (verifyRegForm(name, image, location, profession)) {
                    saveProfile(name, image, about, location, profession, notification_flag);

                } else {
                    MyToast.tshort(context, errorMsg + "");
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(isFromLocationSelect){
            isFromLocationSelect = false;
        }
        else {
            setData();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //overridePendingTransition(R.anim.close_enter_animate, R.anim.close_exit_animate);
    }

    /**
     * method used to get professions
     */
    private void getProfessions() {

        final String failMessage = "Oops. Some error occurred.";

        if (networkConnection.isNetworkAvailable()) {

            String updated_date = DaoController.getLatestUpdateProfessionDate(context);
            RestClient.getLokasoApi().professions(
                    updated_date,
                    new Callback<RetroProfession>() {
                @Override
                public void success(RetroProfession retroProfession, Response response) {
                    if (retroProfession.getSuccess()) {
                        professionList = retroProfession.getDetails();
                        DaoController.updateProfessionList(context, professionList);

                        professionAdapter.refresh(professionList);

                        for (int i = 0; i < professionList.size(); i++) {
                            if (professionList.get(i).getName().equals(profile.getProfession())) {
                                profession_id = (int) professionList.get(i).getId();
                                break;
                            }
                        }

                    } else {
                        professionList = new ArrayList<>();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    professionList = new ArrayList<>();
                    MyLog.e(TAG, "professions : " + error);
                    MyToast.tshort(context, "" + failMessage);
                }
            });
        }
    }

    /**
     * method used to save profile of current user using API call
     *
     * @param name              : Name of the current user
     * @param image             : Profile image of the current user
     * @param about             : About section of the current user
     * @param location          : Location of the current user
     * @param profession        : Profession of the current user
     * @param notification_flag : notification_flag = 1 will allow user to get notification and notification_flag = 0 vice versa
     */
    private void saveProfile(final String name, String image, String about, final String location, final String profession,
                             int notification_flag) {
        if (networkConnection.isNetworkAvailable()) {
            RestClient.getLokasoApi().updateProfile(
                    MyPreferencesManager.getId(context),
                    name,   // Not editable
                    image,  // This check is made from api
                    about,  // From edittext
                    profession_id,
                    location,
                    latitudeVal,
                    longitudeVal,
                    notification_flag,
                    new Callback<RetroResponse>() {
                        @Override
                        public void success(RetroResponse retroResponse, Response response) {

                            if(retroResponse!=null) {
                                MyToast.tshort(context, retroResponse.getMessage());
                                if(retroResponse.getSuccess()) {

                                    MyPreferencesManager.saveName(context, name);
                                    MyPreferencesManager.saveProfession(context, profession);
                                    MyPreferencesManager.saveProfessionId(context, profession_id);
                                    MyPreferencesManager.setUserLocation(context, location);
                                    MyPreferencesManager.setUserLatitude(context, latitudeVal);
                                    MyPreferencesManager.setUserLongitude(context, longitudeVal);

//                                    MyPreferencesManager.saveImage(context, retroResponse.getDetails());
                                    MyLog.e(TAG, "Profession : " + MyPreferencesManager.getProfession(context));
                                    finish();
                                }
                                else {

                                }
                            }
                            else {

                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            MyToast.tshort(context, error);
                        }
                    });
        } else {
            MyToast.tshort(context, getString(R.string.check_network));
        }
    }


    /**
     * method used to set current profile data
     */
    private void setData() {

        if(profile!=null) {
            String name = profile.getName();

            String imageUrl = profile.getImage();
            MyLog.e(TAG, "userImageUrl : " + imageUrl);
            Picaso.loadUser(context, imageUrl, ivPic);

            tvAboutMe.setText(profile.getAbout_me());
            tvLocationNew.setText(""+profile.getLocation());
            latitudeVal = profile.getCurrent_lat();
            longitudeVal = profile.getCurrent_lng();

            tvFName.setText(name);
            tvEmail.setText(profile.getEmail());
            tvProfession.setText(profile.getProfession());
            profession_id = profile.getProfession_id(); //MyPreferencesManager.getProfessionId(context);
            if (profile.getNotification_flag() == 1) {
                notification_toggle.setChecked(true);
            } else {
                notification_toggle.setChecked(false);
            }

/*
            List<AddInterest> myInterest = new ArrayList<>();

            List<AddInterest> userInterestList = new ArrayList<>();

            //userInterestList.addAll(profile.getInterestList());


            Set<String> prefInterestIds = MyPreferencesManager.getUserInterestList(context);
            String[] arrayInterest = (String[]) prefInterestIds.toArray();

            for (int i = 0; i < profile.getInterestList().size(); i++) {
                int interestId = profile.getInterestList().get(i).getInterest_id();
                List<Interest> allInterests = SplashActivity.interestList;
                for (int x = 0; x < arrayInterest.length; x++) {

                    int interestIntId = Integer.parseInt(arrayInterest[x]);
                    if (interestIntId == interestId) {
                        Interest interest = allInterests.get(x);
                        interestName += interest.getName() + ", ";
                        int id = interest.getId();
                        int interestId = interest.getId();
                        int userId = interest.get;
                        AddInterest addInterest = new AddInterest(id, interestId, userId)
                        userInterestList.add(addInterest);
                        break;
                    }
                }
            }

*/


            Set<String> prefInterestIds = MyPreferencesManager.getUserInterestList(context);
            MyLog.e(TAG, "prefInterestIds : " + prefInterestIds);
            Object[] arrayInterest = prefInterestIds.toArray();

            DaoFunctions daoFunctions = new DaoFunctions(context);
            List<Interest> allInterests = daoFunctions.getAllInterest();
            daoFunctions.close();

            String interestName = "";
            for (int i = 0; i < arrayInterest.length; i++) {
                int interestIntId = Integer.parseInt(""+arrayInterest[i]);
                for (int x = 0; x < allInterests.size(); x++) {
                    if (allInterests.get(x).getId() == interestIntId) {
                        Interest interest = allInterests.get(x);
                        interestName += interest.getName() + ", ";
                        break;
                    }
                }
            }
/*

            if (userInterestList != null) {
                MyLog.e(TAG, "interest : " + userInterestList);

                myInterest.addAll(userInterestList);
                MyLog.e(TAG, "interest size: " + myInterest.size());
            }
            String interestName = "";
            for (int i = 0; i < myInterest.size(); i++) {
                int interestId = myInterest.get(i).getInterest_id();
                List<Interest> allInterests = SplashActivity.interestList;
                for (int x = 0; x < allInterests.size(); x++) {
                    if (allInterests.get(x).getId() == interestId) {
                        Interest interest = allInterests.get(x);
                        interestName += interest.getName() + ", ";
                        break;
                    }
                }
            }
*/
            MyLog.e(TAG, " 1. interestName : (" + interestName + ")");
            interestName = interestName.trim();
            MyLog.e(TAG, " 2. interestName : (" + interestName + ")");
            interestName = Helper.removeLastComma(interestName);
            MyLog.e(TAG, " 3. interestName : (" + interestName + ")");

            tvInterest.setText(interestName);
        }
        else {
            MyLog.e(TAG, "Profile is null");
        }
    }

    /**
     * method used to validate form
     *
     * @param fname
     * @param image
     * @param location
     * @param profession
     * @return
     */
    private boolean verifyRegForm(String fname, String image, String location, String profession) {

        if (fname.isEmpty()) {
            errorMsg = "First name field is empty";
            return false;
        }

        if (fname.length() < 3) {
            errorMsg = "First name field should be greater than 2 characters";
            return false;
        }

        if (profession.isEmpty()) {
            errorMsg = "Select a Profession";
            return false;
        }

        if (profession_id<=0) {
            errorMsg = "Select a Profession";
            return false;
        }

        if (location.isEmpty()) {
            errorMsg = "Location field is empty";
            return false;
        }

        if (latitudeVal==0 && longitudeVal==0) {
            errorMsg = "Location is not set";
            return false;
        }

        if (image.isEmpty() && profile.getImage().isEmpty()) {
            errorMsg = "Choose profile picture";
            return false;
        }

        return true;
    }

    /**
     * method used to show image capture options
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Constant.CAMERA_PIC_REQUEST:
                try {
                    Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                    ivPic.setImageBitmap(getResizedBitmap(thumbnail, Constant.size_medium, Constant.size_medium));
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

                        ivPic.setImageBitmap(getResizedBitmap(BitmapFactory.decodeFile(imgPath), Constant.size_medium,
                                Constant.size_medium));
                        imagePic = convertBitmapImageToString(((BitmapDrawable) ivPic.getDrawable()).getBitmap());
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                break;

            case Constant.LOCATION_SELECTION:

                MyLog.e(TAG, "LOCATION_SELECTION");
                checkLocation = false;
                if (resultCode == RESULT_OK) {
                    MyLog.e(TAG, "LOCATION_SELECTION RESULT_OK");
                    Place place = PlaceAutocomplete.getPlace(this, data);
                    MyLog.e(TAG, "LOCATION_SELECTION place.getName() : "+place.getName());
                    //tvLocationHint.setText("yyy "+place.getName() + "");
                    tvLocationNew.setText(""+place.getName() + "");
                    latitudeVal = place.getLatLng().latitude;
                    longitudeVal = place.getLatLng().longitude;
                    MyLog.e(TAG, "LOCATION_SELECTION place tvLocation  : "+latitudeVal+ " , "+longitudeVal+ " , "+tvLocation.getText());

                    isFromLocationSelect = true;

                }
                break;

            default:
                break;
        }
    }
}
