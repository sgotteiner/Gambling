package com.sagi.gambling.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sagi.gambling.R;
import com.sagi.gambling.entities.User;
import com.sagi.gambling.interfaces.IUserFragmentGetEventFromMain;
import com.sagi.gambling.utilities.DownloadImage;
import com.sagi.gambling.utilities.ImageUtils;
import com.sagi.gambling.utilities.Patch;
import com.sagi.gambling.utilities.SharedPreferencesHelper;
import com.sagi.gambling.utilities.UploadImage;
import com.sagi.gambling.utilities.Utils;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class UserFragment extends Fragment implements IUserFragmentGetEventFromMain {

    private static final int REQUEST_CODE_STORAGE_PERMISSION = 20;
    private OnFragmentInteractionListener mListener;
    private Button btnSave;
    private ImageView imgProfilePicture;
    private final int IMG_FROM_GALLERY = 2;
    private Bitmap bitmapProfile = null;
    private User user;
    private EditText edtFName, edtLName;
    private TextView txtBirthday, txtEmail, txtMoney;
    private long dateBirthDay = -1;
    private ProgressDialog progressDialogDownload;
    private ProgressDialog progressDialogUpload;

    public UserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user, container, false);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION) {

            boolean isOk = true;
            for (int i = 0; i < grantResults.length; i++) {
                boolean isAccept = grantResults[i] == PackageManager.PERMISSION_GRANTED;
                if (!isAccept)
                    isOk = false;
            }
            if (isOk) {
                showGallery();
            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //progressDialog = new ProgressDialog(getContext());
        user = SharedPreferencesHelper.getInstance(getContext()).getUser();
        dateBirthDay = user.getBirthDay();

        loadAllFields(view);

        imgProfilePicture = view.findViewById(R.id.imgProfilePicture);
        loadImageProfile();

        btnSave = view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Utils.isValid(user.getEmail(), edtFName.getText().toString(), edtLName.getText().toString(), dateBirthDay, getContext()))
                    return;

                updateEntityUser();
                // Utils.showProgressDialod(progressDialog);
                if (bitmapProfile != null) {
                    showDialogUpload();
                    mListener.updateProfile(user);
                    uploadImage();

                } else {
                    mListener.updateProfileWithoutBitmap(user);
                }
            }

        });
        imgProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if ((ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                        || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    showGallery();
                } else {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
                }

            }
        });
    }

    private void showGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, IMG_FROM_GALLERY);
    }

    private void showDialogUpload() {
        progressDialogUpload = new ProgressDialog(getContext());
        progressDialogUpload.setMessage("Uploading");
        progressDialogUpload.setTitle("Waiting");
        progressDialogUpload.setCancelable(false);
        progressDialogUpload.setIcon(R.drawable.logo_not_free);
        progressDialogUpload.show();
    }

    private void showDialogDownload() {
        progressDialogDownload = new ProgressDialog(getContext());
        progressDialogDownload.setMessage("Download");
        progressDialogDownload.setTitle("Waiting");
        progressDialogDownload.setCancelable(false);
        progressDialogDownload.setIcon(R.drawable.logo_not_free);
        progressDialogDownload.show();
    }


    private void uploadImage() {
        new UploadImage(Patch.PROFILES, user.getEmail(), bitmapProfile, new UploadImage.IUploadImage() {
            @Override
            public void onSuccess() {
                progressDialogUpload.dismiss();
                mListener.showHomePage();
            }

            @Override
            public void onFail(String error) {
                progressDialogUpload.dismiss();
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProgress(int progress) {
                progressDialogUpload.setMessage("Uploading " + progress + "%");
            }
        }).startUpload();
    }

    private void loadImageProfile() {
        showDialogDownload();
        new DownloadImage(Patch.PROFILES, user.getEmail(), new DownloadImage.IDownloadImage() {
            @Override
            public void onSuccess(Uri uri) {
                stopProgressBar();
                Picasso.with(getContext()).load(uri).fit().into(imgProfilePicture);
            }

            @Override
            public void onFail(String error) {
                stopProgressBar();
            }
        }).startLoading();
    }

    private void loadAllFields(View view) {
        edtFName = view.findViewById(R.id.edtFName);
        edtFName.setText(getUserFirstNameWithUpperCase(user.getFirstName()));
        edtLName = view.findViewById(R.id.edtLName);
        edtLName.setText(user.getLastName());
        txtEmail = view.findViewById(R.id.txtEmail);
        txtEmail.setText(user.getEmail());
        txtMoney = view.findViewById(R.id.txtMoney);
        txtMoney.setText(String.valueOf(user.getTotalMoney()));
        txtBirthday = view.findViewById(R.id.txtBDay);
        txtBirthday.setText(getStringDateFromLong(user.getBirthDay()));
        txtBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        dateBirthDay = Utils.getTimeStampFromDate(year, month, day);
                    }
                }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });
    }


    private String getUserFirstNameWithUpperCase(String firstName) {
        return Utils.geteFirstLattersUpperCase(firstName);
    }

    private String getStringDateFromLong(long date) {
        DateFormat f = new SimpleDateFormat("dd/MM/yyyy");
        String dateAsString = f.format(date);
        return dateAsString;
    }

    private void updateEntityUser() {

        user.setFirstName(edtFName.getText().toString().toLowerCase());
        user.setLastName(edtLName.getText().toString());
        user.setBirthDay(dateBirthDay);
        SharedPreferencesHelper.getInstance(getContext()).setUser(user);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMG_FROM_GALLERY && resultCode == Activity.RESULT_OK && data != null) {
            Uri uriImageGallery = data.getData();

            imgProfilePicture.setBackgroundResource(0);
            bitmapProfile = ImageUtils.handleImageGallery(uriImageGallery, getContext());
            imgProfilePicture.setImageBitmap(bitmapProfile);
            bitmapProfile = ImageUtils.scaleDown(bitmapProfile, 200, false);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
            mListener.registerEventFromMain(this);
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener.registerEventFromMain(null);
        mListener = null;
    }

    @Override
    public void onBackPressedInActivity() {
        btnSave.setBackgroundColor(Color.GREEN);
    }

    @Override
    public void onDownloadUri(Uri uriProfile) {
        Picasso.with(getContext()).load(uriProfile).fit().into(imgProfilePicture);
        //progressDialog.dismiss();
    }


    @Override
    public void stopProgressBar() {
        // progressDialog.dismiss();
        progressDialogDownload.dismiss();
    }


    public interface OnFragmentInteractionListener {
        void registerEventFromMain(IUserFragmentGetEventFromMain iUserFragmentGetEventFromMain);

        void updateProfileWithoutBitmap(User user);

        void showHomePage();

        void updateProfile(User user);
    }
}
