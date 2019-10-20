package com.sagi.gambling.fragments.dialogs;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.sagi.gambling.R;
import com.sagi.gambling.entities.Game;
import com.sagi.gambling.interfaces.IDialogFragment;
import com.sagi.gambling.utilities.ImageUtils;
import com.sagi.gambling.utilities.Utils;
import com.sagi.gambling.utilities.constant.GeneralConstants;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.Calendar;


public class DialogGameFragment extends android.support.v4.app.DialogFragment implements IDialogFragment {

    private static final String IS_UPDATE_DIALOG = "IS_UPDATE_DIALOG";
    private static final String GAME_KEY = "GAME_KEY";
    private static final int REQUEST_CODE_STORAGE_PERMISSION_IMAG2 = 22;
    private static final int REQUEST_CODE_STORAGE_PERMISSION_IMAG1 = 21;
    private OnFragmentInteractionListener mListener;
    private ImageView imgGroup1, imgGroup2, imageViewClose;
    private final int IMG1_FROM_GALLERY = 3;
    private final int IMG2_FROM_GALLERY = 4;
    private int hour = -1, minute = -1;
    private long timeStampDateOfGame = -1;
    private long timeStampDateAndTime;
    private Bitmap bitmapGroup1 = null, bitmapGroup2 = null;
    private Spinner spnGroup1Name, spnGroup2Name, spnCategory;
    private Boolean isNewGroup1 = true;
    private Boolean isNewGroup2 = true;
    private Boolean isNewCategory = true;
    private boolean isUpdateDialog;
    private EditText edtGroup1Name, edtGroup2Name, edtCategory, edtDecription;
    private SeekBar seekBarCance1;
    private Button btnAddGame, btnDate, btnHour;
    //private final String STATUS_NONE = "none";
    private String mStatus = GeneralConstants.STATUS_ACTIVE;// = STATUS_NONE;
    private TextView txtChance;
    private RadioGroup rgStatus;
    private Game mGame;
    private boolean isEditDialog;


    public DialogGameFragment() {
        // Required empty public constructor
    }

    public static DialogGameFragment newInstance(boolean isUpdateDialog, Game game) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(IS_UPDATE_DIALOG, isUpdateDialog);
        bundle.putSerializable(GAME_KEY, game);
        DialogGameFragment fragment = new DialogGameFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dialog_game, container, false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION_IMAG1
                || requestCode == REQUEST_CODE_STORAGE_PERMISSION_IMAG2) {
            boolean isOk = true;
            for (int i = 0; i < grantResults.length; i++) {
                boolean isAccept = grantResults[i] == PackageManager.PERMISSION_GRANTED;
                if (!isAccept)
                    isOk = false;
            }
            if (isOk) {
                boolean isRequestImage1 = (requestCode == REQUEST_CODE_STORAGE_PERMISSION_IMAG1);
                showGallery(isRequestImage1 ? IMG1_FROM_GALLERY : IMG2_FROM_GALLERY);
            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mListener.loadAllGroupsName();
        mListener.loadAllCategoriesName();

        loadViews(view);
        loadBundle();
        loadListeners();
    }


    private void loadBundle() {
        Bundle bundle = getArguments();
        isUpdateDialog = bundle.getBoolean(IS_UPDATE_DIALOG);
        if (isUpdateDialog) {
            mGame = (Game) bundle.getSerializable(GAME_KEY);
            timeStampDateOfGame = mGame.getTimeStampStartGame();
            getHour();
            getMinute();
            loadValuesToViews(mGame);
        } else {
            rgStatus.setVisibility(View.GONE);
        }
    }

    private void loadListeners() {
        edtGroup1Name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                isNewGroup1 = isNewGroupCreated(editable.toString(), spnGroup1Name);
                if (isNewGroup1) {
                    imgGroup1.setImageResource(0);
                }
            }
        });
        edtGroup2Name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                isNewGroup2 = isNewGroupCreated(editable.toString(), spnGroup2Name);
                if (isNewGroup2) {
                    imgGroup2.setImageResource(0);
                }
            }
        });
        edtCategory.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                isNewCategory = isNewCategoryCreated(editable.toString(), spnCategory);
            }
        });
        spnGroup1Name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    return;
                }
                edtGroup1Name.setText(adapterView.getItemAtPosition(i).toString());
                loadImageGroup(edtGroup1Name.getText().toString(), imgGroup1);
                isNewGroup1 = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spnGroup2Name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    return;
                }
                edtGroup2Name.setText(adapterView.getItemAtPosition(i).toString());
                loadImageGroup(edtGroup2Name.getText().toString(), imgGroup2);
                isNewGroup2 = false;

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spnCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0)
                    return;

                edtCategory.setText(adapterView.getItemAtPosition(i).toString());
                isNewCategory = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        seekBarCance1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                txtChance.setText("Chance first group " + i + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        imgGroup1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImageFromGallery(IMG1_FROM_GALLERY);
            }
        });
        imgGroup2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImageFromGallery(IMG2_FROM_GALLERY);
            }
        });
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        timeStampDateOfGame = Utils.getTimeStampFromDate(year, month, day);
                    }
                }, getYear(), getMonth(), getDay());
                datePickerDialog.show();
            }
        });

        btnHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timeDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    public void onTimeSet(TimePicker view, int hourOfDay, int minuteInHour) {
                        hour = hourOfDay;
                        minute = minuteInHour;
                    }
                }, getHour(), getMinute(), true);
                timeDialog.show();
            }
        });
        if (isUpdateDialog)
            rgStatus.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int id) {
                    switch (id) {
                        case R.id.rbActive:
                            mStatus = GeneralConstants.STATUS_ACTIVE;
                            break;
                        case R.id.rbEnded:
                            mStatus = GeneralConstants.STATUS_ENDED;
                            final Dialog dialog = new Dialog(getContext());
                            dialog.setContentView(R.layout.dialog_winner_team);
                            TextView txtGroup1Name, txtGroup2Name, txtTie;
                            txtGroup1Name = dialog.findViewById(R.id.txtGroup1Name);
                            txtGroup1Name.setText(mGame.getGroup1name());
                            txtGroup1Name.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    mGame.setWinner(1);
                                    dialog.dismiss();
                                }
                            });
                            txtGroup2Name = dialog.findViewById(R.id.txtGroup2Name);
                            txtGroup2Name.setText(mGame.getGroup2name());
                            txtGroup2Name.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    mGame.setWinner(2);
                                    dialog.dismiss();
                                }
                            });
                            if (mGame.getCategoryName().equals(GeneralConstants.SOCCER)) {
                                txtTie = dialog.findViewById(R.id.txtTie);
                                txtTie.setText(GeneralConstants.TIE);
                                txtTie.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        mGame.setWinner(3);
                                        dialog.dismiss();
                                    }
                                });
                            }
                            dialog.show();
                            break;
                        case R.id.rbProgress:
                            mStatus = GeneralConstants.STATUS_PROGRESS;
                            break;
                    }
                }
            });
        btnAddGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidInput(edtGroup1Name.getText().toString(), edtGroup2Name.getText().toString())) {
                    Game game = new Game(timeStampDateAndTime, seekBarCance1.getProgress(), edtGroup1Name.getText().toString(), edtGroup2Name.getText().toString(), mStatus
                            , edtDecription.getText().toString(), edtCategory.getText().toString());

                    Log.d("Test", game.toString());
                    Log.d("Test", "bitmapGroup1 " + (bitmapGroup1) + " bitmapGroup2 " + bitmapGroup2 + "\n" + "isNewGroup1 " + isNewGroup1 + " isNewGroup2 " + isNewGroup2);

                    if (isUpdateDialog) {
                        if (game.isSameGame(mGame) && (bitmapGroup2 == null && bitmapGroup1 == null)) {
                            Toast.makeText(getContext(), "it is the same game", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        game.setKey(mGame.getKey());
                        game.setWinner(mGame.getWinner());
                        mListener.updateGame(game, bitmapGroup1, bitmapGroup2, isNewGroup1, isNewGroup2, isNewCategory);
                    } else {
                        showDialogProgress();
                        mListener.addNewGame(game, bitmapGroup1, bitmapGroup2, isNewGroup1, isNewGroup2, isNewCategory);
                    }
                }
            }
        });
        imageViewClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissAddDialog();
            }
        });

    }

    private int getYear() {
        if (isUpdateDialog) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(mGame.getTimeStampStartGame());
            return calendar.get(Calendar.YEAR);
        } else {
            return Calendar.getInstance().get(Calendar.YEAR);
        }
    }

    private int getMonth() {
        if (isUpdateDialog) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(mGame.getTimeStampStartGame());
            return calendar.get(Calendar.MONTH);
        } else {
            return Calendar.getInstance().get(Calendar.MONTH);
        }
    }

    private int getDay() {
        if (isUpdateDialog) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(mGame.getTimeStampStartGame());
            return calendar.get(Calendar.DAY_OF_MONTH);
        } else {
            return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        }
    }

    private int getHour() {
        if (isUpdateDialog) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(mGame.getTimeStampStartGame());
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            return hour;
        } else {
            return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        }
    }

    private int getMinute() {
        if (isUpdateDialog) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(mGame.getTimeStampStartGame());
            minute = calendar.get(Calendar.MINUTE);
            return minute;
        } else {
            return Calendar.getInstance().get(Calendar.MINUTE);
        }
    }

    private void loadViews(View view) {
        spnGroup1Name = view.findViewById(R.id.spnGroup1Name);
        imageViewClose = view.findViewById(R.id.imageViewClose);
        txtChance = view.findViewById(R.id.textViewStatus);
        edtGroup1Name = view.findViewById(R.id.edtGroup1Name);
        edtGroup2Name = view.findViewById(R.id.edtGroup2Name);
        edtCategory = view.findViewById(R.id.edtCategory);
        edtDecription = view.findViewById(R.id.edtDecription);
        spnGroup2Name = view.findViewById(R.id.spnGroup2Name);
        spnCategory = view.findViewById(R.id.spnCategory);
        seekBarCance1 = view.findViewById(R.id.seekBarChance1);
        btnHour = view.findViewById(R.id.btnHour);
        btnAddGame = view.findViewById(R.id.btnAddGame);
        btnDate = view.findViewById(R.id.btnDate);
        imgGroup1 = view.findViewById(R.id.imgGroup1);
        imgGroup2 = view.findViewById(R.id.imgGroup2);
        rgStatus = view.findViewById(R.id.rgStatus);
    }

    private void loadValuesToViews(Game game) {
        edtGroup1Name.setText(game.getGroup1name());
        edtGroup2Name.setText(game.getGroup2name());
        edtCategory.setText(game.getCategoryName());
        edtDecription.setText(game.getDescription());
        seekBarCance1.setProgress(game.getChanceWinGroup1());
        btnAddGame.setText("Update Game");
        txtChance.setText("Chance first group " + game.getChanceWinGroup1() + "%");
        switch (game.getStatus()) {
            case GeneralConstants.STATUS_ACTIVE:
                rgStatus.check(R.id.rbActive);
                break;
            case GeneralConstants.STATUS_ENDED:
                rgStatus.check(R.id.rbEnded);
                break;
            case GeneralConstants.STATUS_PROGRESS:
                rgStatus.check(R.id.rbProgress);
                break;
        }
    }

    private void dismissAddDialog() {
        dismiss();
    }

    private Boolean isNewCategoryCreated(String category, Spinner spnCategory) {
        for (int i = 1; i < listAllNamesCategories.length; i++) {
            if (listAllNamesCategories[i].toLowerCase().equals(category.toLowerCase())) {
                spnCategory.setSelection(i);
                return false;
            }
        }
        spnCategory.setSelection(0);
        return true;
    }

    private void showDialogProgress() {
        if (progressDialog != null)
            return;
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setTitle(isUpdateDialog ? "Wait, downloading" : "Wait, uploading");
        progressDialog.setMessage(isUpdateDialog ? "Downloading..." : "Uploading...");
        progressDialog.setIndeterminate(true);
        progressDialog.setIcon(R.drawable.logo_not_free);
        progressDialog.show();
    }

    private boolean isNewGroupCreated(String text, Spinner spinnerUpdate) {

        for (int i = 1; i < listAllNamesGroups.length; i++) {
            if (listAllNamesGroups[i].toLowerCase().equals(text.toLowerCase())) {
                spinnerUpdate.setSelection(i);
                return false;
            }
        }
        spinnerUpdate.setSelection(0);
        return true;
    }

    private void pickImageFromGallery(int requestCode) {

        if ((ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            showGallery(requestCode);
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION_IMAG1);
        }

    }

    private void showGallery(int requestCode) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, requestCode);
    }

    private boolean isValidInput(String group1Name, String group2Name) {
        if (isNewGroup1 && isNewGroup2) {
            if (group1Name.equals("") || group2Name.equals("") || timeStampDateOfGame == -1 || hour == -1 || minute == -1) {
                Toast.makeText(getContext(), "Fill all fields", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if (group1Name.equals("") || group2Name.equals("") || timeStampDateOfGame == -1 || hour == -1 || minute == -1) {
            Toast.makeText(getContext(), "Fill all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        timeStampDateAndTime = Utils.getTimeStampFromDateAndTime(timeStampDateOfGame, hour, minute);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        boolean isRequestImgGroup1 = (requestCode == IMG1_FROM_GALLERY);

        Log.d("IMG!!", isRequestImgGroup1 + "");

        if (data == null)
            return;
        if (requestCode == IMG1_FROM_GALLERY || requestCode == IMG2_FROM_GALLERY && resultCode == Activity.RESULT_OK && data != null) {

            Uri uriImageGallery = data.getData();

            if (isRequestImgGroup1) {
                bitmapGroup1 = ImageUtils.handleImageGallery(uriImageGallery, getContext());
                imgGroup1.setImageBitmap(bitmapGroup1);
                bitmapGroup1 = ImageUtils.scaleDown(bitmapGroup1, 200, false);
                isNewGroup1 = true;
            } else {
                bitmapGroup2 = ImageUtils.handleImageGallery(uriImageGallery, getContext());
                imgGroup2.setImageBitmap(bitmapGroup2);
                bitmapGroup2 = ImageUtils.scaleDown(bitmapGroup2, 200, false);
                isNewGroup2 = true;
            }
        }
    }




    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
            mListener.registerFromDialogFragmentEvent(this);
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener.registerFromDialogFragmentEvent(null);
        mListener = null;
    }

    private String[] listAllNamesGroups, listAllNamesCategories;
    private ProgressDialog progressDialog = null;

    @Override
    public void getAllArrNamesGroup(String[] allNamesGroups) {
        listAllNamesGroups = allNamesGroups;
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, allNamesGroups);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnGroup1Name.setAdapter(adapter);
        spnGroup2Name.setAdapter(adapter);
    }

    @Override
    public void getAllArrNamesCategory(String[] allNamesCategories) {
        listAllNamesCategories = allNamesCategories;
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, allNamesCategories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCategory.setAdapter(adapter);
        isNewCategoryCreated(edtCategory.getText().toString(),spnCategory);
    }


    @Override
    public void onDownloadUri(Uri uriGroup, ImageView imgGroup) {
        Picasso.with(getContext()).load(uriGroup).fit().into(imgGroup);
    }

    @Override
    public void stopProgressBar() {
        if (progressDialog == null)
            return;
        progressDialog.dismiss();
        progressDialog = null;
    }

    @Override
    public void loadImagesOfGroups() {
        isNewGroup1 = isNewGroupCreated(edtGroup1Name.getText().toString(), spnGroup1Name);
        isNewGroup2 = isNewGroupCreated(edtGroup2Name.getText().toString(), spnGroup2Name);
    }

    @Override
    public void dismissDialog() {
        dismiss();
    }


    private void loadImageGroup(String groupName, ImageView imgGroup) {
        showDialogProgress();
        mListener.startLoadingImageGroup(groupName, imgGroup);
    }

    public interface OnFragmentInteractionListener {

        void registerFromDialogFragmentEvent(IDialogFragment iDialogFragment);

        void addNewGame(Game game, Bitmap bitmap1, Bitmap bitmap2, boolean isNewGroup1, boolean isNewGroup2, boolean isNewCategory);

        void loadAllGroupsName();

        void loadAllCategoriesName();

        void startLoadingImageGroup(String path, ImageView imgGroup);

        void updateGame(Game game, Bitmap bitmapGroup1, Bitmap bitmapGroup2, Boolean isNewGroup1, Boolean isNewGroup2, Boolean isNewCategory);
    }
}
