
/*
 * Copyright 2018 tarekmabdallah91@gmail.com
 *
 * Licensed under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.gmail.tarekmabdallah91.bakingapp.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.tarekmabdallah91.bakingapp.R;
import com.gmail.tarekmabdallah91.bakingapp.data.room.RoomPresenter;
import com.gmail.tarekmabdallah91.bakingapp.models.UserEntry;
import com.gmail.tarekmabdallah91.bakingapp.utils.BitmapUtils;
import com.gmail.tarekmabdallah91.bakingapp.utils.DrawerUtil;
import com.gmail.tarekmabdallah91.bakingapp.utils.UserDataUtils;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mikepenz.materialdrawer.Drawer;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.VISIBLE;
import static com.gmail.tarekmabdallah91.bakingapp.utils.BakingConstants.EMPTY_TEXT;
import static com.gmail.tarekmabdallah91.bakingapp.utils.BakingConstants.FORMAT_GEO;
import static com.gmail.tarekmabdallah91.bakingapp.utils.BakingConstants.GENDER_MALE;
import static com.gmail.tarekmabdallah91.bakingapp.utils.BakingConstants.IMAGE_INTENT_TYPE;
import static com.gmail.tarekmabdallah91.bakingapp.utils.BakingConstants.IMAGE_ROTATION;
import static com.gmail.tarekmabdallah91.bakingapp.utils.BakingConstants.LATITUDE_KEYWORD;
import static com.gmail.tarekmabdallah91.bakingapp.utils.BakingConstants.LONGITUDE_KEYWORD;
import static com.gmail.tarekmabdallah91.bakingapp.utils.BakingConstants.PROVIDER_AUTHORITY;
import static com.gmail.tarekmabdallah91.bakingapp.utils.BakingConstants.REQUEST_CAPTURE_IMAGE;
import static com.gmail.tarekmabdallah91.bakingapp.utils.BakingConstants.REQUEST_PICK_IMAGE_FROM_GALLERY;
import static com.gmail.tarekmabdallah91.bakingapp.utils.BakingConstants.REQUEST_PLACE_PIKER;
import static com.gmail.tarekmabdallah91.bakingapp.utils.BakingConstants.REQUEST_STORAGE_PERMISSION;
import static com.gmail.tarekmabdallah91.bakingapp.utils.BakingConstants.USER_FIRST_NAME;
import static com.gmail.tarekmabdallah91.bakingapp.utils.BakingConstants.USER_GENDER;
import static com.gmail.tarekmabdallah91.bakingapp.utils.BakingConstants.USER_LAST_NAME;
import static com.gmail.tarekmabdallah91.bakingapp.utils.BakingConstants.USER_LOCATION;
import static com.gmail.tarekmabdallah91.bakingapp.utils.BakingConstants.USER_PICTURE_PATH;
import static com.gmail.tarekmabdallah91.bakingapp.utils.BakingConstants.USER_STRING_ID;
import static com.gmail.tarekmabdallah91.bakingapp.utils.BakingConstants.ZERO;
import static com.gmail.tarekmabdallah91.bakingapp.utils.BakingConstants.ZOOM_RATIO;

public class EditProfileActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = EditProfileActivity.class.getSimpleName();

    @BindView(R.id.layout_activity_profile)
    View layout_activity;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.profile_picture)
    ImageView profilePictureIv;
    @BindView(R.id.take_picture)
    TextView takePictureTV;
    @BindView(R.id.get_image_from_gallery)
    TextView getImageFromGalleryTV;
    @BindView(R.id.get_first_name_tv)
    TextView getFirstNameTV;
    @BindView(R.id.get_last_name_tv)
    TextView getLastNameTV;
    @BindView(R.id.get_gender_radio_btn)
    RadioGroup getGenderRG;
    @BindView(R.id.female_choice)
    RadioButton femaleChoice;
    @BindView(R.id.male_choice)
    RadioButton maleChoice;
    @BindView(R.id.get_location)
    TextView getLocationIV;
    @BindView(R.id.save_btn)
    TextView saveBtn;
    @BindView(R.id.cancel_btn)
    TextView cancelBtn;
    @BindView(R.id.first_name_et)
    EditText firstNameET;
    @BindView(R.id.last_name_et)
    EditText lastNameET;

    private SupportMapFragment mapFragment;
    private Bundle userData;
    private UserEntry user;
    private double latitude;
    private double longitude;
    private String imageFilePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_layout);
        ButterKnife.bind(this);

        setUI();
    }

    private void setUI() {
        initiateValues();
        layout_activity.setVisibility(VISIBLE);
        setSupportActionBar(toolbar);

        setMap();
        if (null != user) { // may be null if there is not any entry in userDb
            displayImageByPicasso(user.getImageFilePath());  // set image view
            firstNameET.setText(user.getFirstName()); // set first name
            lastNameET.setText(user.getLastName()); // set last name
            int gender = user.getGender(); // get saved gender then set it in the radio group
            if (gender == GENDER_MALE) maleChoice.setChecked(true);
            else femaleChoice.setChecked(true);
            // get user place then set lat lng
            latitude = user.getLatitude();
            longitude = user.getLongitude();
        }
    }

    private void initiateValues() {
        userData = new Bundle();
        user = RoomPresenter.getLastUserEntry(this);
        setGenderRadioGroup(); // get gender should be here to listen to all changes
    }


    private void setMap() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) mapFragment.getMapAsync(this);
    }

    private void openTheCamera() {
        Intent openTheCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (openTheCamera.resolveActivity(getPackageManager()) != null) {
            //Create a file to store the image
            File photoFile = null;
            try {
                photoFile = BitmapUtils.createImageFile(this);
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                //String imageFilePathTemp = photoFile.getAbsolutePath();
                Uri photoURI = FileProvider.getUriForFile(this, PROVIDER_AUTHORITY, photoFile);
                //userData.putString(USER_PICTURE_PATH, String.valueOf(photoURI));
                imageFilePath = String.valueOf(photoURI);
                openTheCamera.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(openTheCamera, REQUEST_CAPTURE_IMAGE);
            }
        }
    }

    private void pickImageFromGallery() {
        Intent pickImageFromGallery = new Intent(Intent.ACTION_PICK); //  , Uri.parse("images/*")
        pickImageFromGallery.setType(IMAGE_INTENT_TYPE);
        startActivityForResult(pickImageFromGallery, REQUEST_PICK_IMAGE_FROM_GALLERY);

    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CAPTURE_IMAGE: // to get uri for profile picture to save it in Room (UserEntry)
                if (RESULT_OK != resultCode) {
                    // when user didn't capture a photo, set imageFilePath with last image uri
                    imageFilePath = user.getImageFilePath();
                }// else save new image uri as String userData.putString(USER_PICTURE_PATH, imageFilePath); - moved to onClickSaveBtn
                break;
            case REQUEST_PICK_IMAGE_FROM_GALLERY: // to pick image from gallery
                if (RESULT_OK == resultCode && null != data) {
                    imageFilePath = String.valueOf(data.getData());
                } else {// when false set imageFilePath with last image uri
                    imageFilePath = user.getImageFilePath();
                }
                break;
            case REQUEST_PLACE_PIKER: // to get user location
                if (resultCode == RESULT_OK && null != data) {
                    Place place;
                    place = PlacePicker.getPlace(this, data);
                    if (place == null) {
                        Log.i(TAG, getString(R.string.no_place_selected_msg));
                        Toast.makeText(this, getString(R.string.no_place_selected_msg), Toast.LENGTH_LONG).show();
                    } else {
                        // Extract the place information from the API
                        latitude = place.getLatLng().latitude;
                        longitude = place.getLatLng().longitude;

                        // Insert the place in the bundle userData
                        String uriLocation = String.format(Locale.ENGLISH, FORMAT_GEO, latitude, longitude);
                        userData.putString(USER_LOCATION, uriLocation);
                        userData.putDouble(LATITUDE_KEYWORD, latitude);
                        userData.putDouble(LONGITUDE_KEYWORD, longitude);
                    }
                }
                break;
        }
        // in case if user not captured image or picked an image it will load the old image if exist or show user image as placeholder
        displayImageByPicasso(imageFilePath);
    }

    /**
     * to avoid repeating writing the same code many times to display an image with picasso in the same ImageView
     *
     * @param imageFilePath -
     */
    private void displayImageByPicasso(String imageFilePath) {
        Picasso.get().load(Uri.parse(imageFilePath)).fit().centerInside().rotate(IMAGE_ROTATION)
                .error(R.drawable.ic_user2).into(profilePictureIv);
    }

    @OnClick(R.id.take_picture)
    void takePicture() {
        // Check for the external storage permission
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // If you do not have permission, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_PERMISSION);
        } else {
            // Launch the camera if the permission exists
            openTheCamera();
        }
    }

    @OnClick(R.id.get_image_from_gallery)
    void onClickGetImageFormGallery() {
        pickImageFromGallery();
    }

    @OnClick(R.id.get_location)
    void onClickGetLocation() {
        // Check for the external storage permission
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // If you do not have permission, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PLACE_PIKER);
        } else {
            // get location
            getLocation();
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    setLocationOnMap(googleMap);
                }
            });
        }


    }

    /**
     * to get the user data and validate it then save it each time
     */
    @OnClick(R.id.save_btn)
    void onClickSaveUserData() {
        // check if user selected / captured photo ?
        userData.putString(USER_PICTURE_PATH, imageFilePath);
        if (null == userData.getString(USER_PICTURE_PATH)) {
            if (null == user) {// if != null that means that there is an image so continue and don't return
                UserDataUtils.showToastMsg(this, getString(R.string.photo_required_msg));
                return;
            } else {
                // if the user didn't change the image then put it's path/uri in the userData again
                // because when user click on save btn it get all new data and reset the user data again
                userData.putString(USER_PICTURE_PATH, user.getImageFilePath());
            }
        }

        // get texts from EditTexts
        if (!getValidUserName()) {
            UserDataUtils.showToastMsg(this, getString(R.string.not_valid_data_msg));
            return;
        }

        getUserGender();// to update/set user gender

        // get user String Id - TODO to get it's value from Fire base.
        String userId = UserDataUtils.setUserId();
        userData.putString(USER_STRING_ID, userId);

        // get user's location if not selected before
        if (null == user) {
            String userLocation = userData.getString(USER_LOCATION);
            if (null == userLocation || userLocation.isEmpty()) {
                Toast.makeText(this, R.string.check_location_msg, Toast.LENGTH_LONG).show();
                return;
            }
        }

        // when all values are valid save user data to Room then finish this activity
        RoomPresenter.getInstance(this).setUserEntry(this, userData);
        finish();
    }

    @OnClick(R.id.cancel_btn)
    void onClickCancelBtn() {
        // TO DO to add dialog msg to make sure that user wanted to leave the activity
        finish();
    }

    /**
     * get texts from EditTexts
     *
     * @return true if  the user entered valid text or false if not valid .
     */
    private boolean getValidUserName() {
        String firstName = UserDataUtils.getTextsFromEditText(this, firstNameET);
        String lastName = UserDataUtils.getTextsFromEditText(this, lastNameET);
        if (EMPTY_TEXT.equals(firstName)) return false;
        else userData.putString(USER_FIRST_NAME, firstName);

        if (EMPTY_TEXT.equals(lastName)) return false;
        else userData.putString(USER_LAST_NAME, lastName);

        return true;
    }

    /**
     * to get user gender from user choice on the profile Activity
     */
    private void getUserGender() {
        if (maleChoice.isChecked()) userData.putInt(USER_GENDER, GENDER_MALE);
        else userData.putInt(USER_GENDER, ZERO); // 0 as female int value
    }

    private void setGenderRadioGroup() {
        RadioGroup.OnCheckedChangeListener changeListenerRadioGroup = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int id = group.getCheckedRadioButtonId();
                RadioButton selectedRadioBtn = findViewById(id);
                userData.putInt(USER_GENDER, Integer.parseInt(String.valueOf(selectedRadioBtn.getTag())));
            }
        };
        getGenderRG.setOnCheckedChangeListener(changeListenerRadioGroup);
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, getString(R.string.need_location_permission_msg), Toast.LENGTH_LONG).show();
            return;
        }
        try {
            // Start a new Activity for the Place Picker API, this will trigger {@code #onActivityResult}
            // when a place is selected or with the user cancels.
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            Intent picker = builder.build(this);
            startActivityForResult(picker, REQUEST_PLACE_PIKER);
        } catch (GooglePlayServicesRepairableException e) {
            Log.e(TAG, String.format(getString(R.string.google_play_service_not_available_msg), e.getMessage()));
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e(TAG, String.format(getString(R.string.google_play_service_not_available_msg), e.getMessage()));
        } catch (Exception e) {
            Log.e(TAG, String.format(getString(R.string.place_picker_exception), e.getMessage()));
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // Called when you request permission to read and write to external storage
        switch (requestCode) {
            case REQUEST_STORAGE_PERMISSION:
                if (grantResults.length > ZERO
                        && grantResults[ZERO] == PackageManager.PERMISSION_GRANTED) {
                    // If you get permission, launch the camera
                    openTheCamera();
                } else {
                    // If you do not get permission, show a Toast
                    Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_PLACE_PIKER:
                if (grantResults.length > ZERO
                        && grantResults[ZERO] == PackageManager.PERMISSION_GRANTED) {
                    // If you get permission, get location
                    getLocation();
                } else {
                    // If you do not get permission, show a Toast
                    Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        DrawerUtil drawerUtil = new DrawerUtil(this, toolbar);
        Drawer drawer = drawerUtil.buildDrawer();
        if (drawer.isDrawerOpen()) drawer.closeDrawer();
    }

    /**
     * runs when the actvity is created
     *
     * @param googleMap -
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        setLocationOnMap(googleMap);
    }

    /**
     * to set location on map immediately
     *
     * @param googleMap -
     */
    private void setLocationOnMap(GoogleMap googleMap) {
        // set map type
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // add marker
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        // zoom to location
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), ZOOM_RATIO));
    }

}
