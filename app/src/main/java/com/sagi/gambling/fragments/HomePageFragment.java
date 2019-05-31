package com.sagi.gambling.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
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
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sagi.gambling.R;
import com.sagi.gambling.adapters.AdapterGames;
import com.sagi.gambling.entities.Gamble;
import com.sagi.gambling.entities.Game;
import com.sagi.gambling.entities.Group;
import com.sagi.gambling.entities.User;
import com.sagi.gambling.interfaces.IHomepage;
import com.sagi.gambling.utilities.ImageUtils;
import com.sagi.gambling.utilities.SharedPreferencesHelper;
import com.sagi.gambling.utilities.constant.FireBaseConstant;
import com.sagi.gambling.utilities.constant.GeneralConstants;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.ArrayList;

import static com.sagi.gambling.utilities.constant.GeneralConstants.STATUS_PRIVATE;
import static com.sagi.gambling.utilities.constant.GeneralConstants.STATUS_PUBLIC;
public class HomePageFragment extends Fragment implements AdapterGames.CallBackAdapterGame, IHomepage  /*IHomePageFragment*/ {

    private static final int REQUEST_CODE_STORAGE_PERMISSION = 20;
    private OnFragmentInteractionListener mListener;
    private Spinner spnFilterListGame;
    private RecyclerView recyclerViewGames;
    private ArrayList<Game> arrGames;
    private String[] allCategoriesName;
    private AdapterGames adapterGames;
    private DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    private Bitmap bitmapGroupProfile = null;
    private ImageView imgProfile;
    private Dialog dialogCreateGroup;
    private final int IMG_FROM_GALLERY = 1;

    public HomePageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_page, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerViewGames = view.findViewById(R.id.recyclerViewGames);
        arrGames = new ArrayList<Game>();
        loadOnlyGamesInCategory(GeneralConstants.ALL);
        adapterGames = new AdapterGames(arrGames, getContext(), this);
        recyclerViewGames.setHasFixedSize(true);
        recyclerViewGames.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewGames.setAdapter(adapterGames);

        spnFilterListGame = view.findViewById(R.id.spnFilterListGame);
        loadCategoriesToFilter();
        spnFilterListGame.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                arrGames = new ArrayList<>();
                loadOnlyGamesInCategory(allCategoriesName[i]);
                adapterGames = new AdapterGames(arrGames, getContext(), HomePageFragment.this);
                recyclerViewGames.setHasFixedSize(true);
                recyclerViewGames.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerViewGames.setAdapter(adapterGames);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        recyclerViewGames.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

    }

    private void loadOnlyGamesInCategory(final String categoryName) {
        Query query = myRef.child(FireBaseConstant.GAMES_TABLE);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Game game = dataSnapshot.getValue(Game.class);
                if (game.getCategoryName().toLowerCase().equals(categoryName.toLowerCase()))
                    updateNewGameAdded(game);

//                if (categoryName.equals(GeneralConstants.ALL))
//                    updateNewGameAdded(game);

                switch (categoryName) {
                    case GeneralConstants.ALL:
                        updateNewGameAdded(game);
                        break;
                    case GeneralConstants.STATUS_ACTIVE:
                        if (game.getStatus().equals(GeneralConstants.STATUS_ACTIVE))
                            updateNewGameAdded(game);
                        break;
                    case GeneralConstants.STATUS_PROGRESS:
                        if (game.getStatus().equals(GeneralConstants.STATUS_PROGRESS))
                            updateNewGameAdded(game);
                        break;
                    case GeneralConstants.STATUS_ENDED:
                        if (game.getStatus().equals(GeneralConstants.STATUS_ENDED))
                            updateNewGameAdded(game);
                        break;
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void pickGroupProfileImage() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, IMG_FROM_GALLERY);
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
                pickGroupProfileImage();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMG_FROM_GALLERY && resultCode == Activity.RESULT_OK && data != null) {
            Uri uriImageGallery = data.getData();
            imgProfile.setBackgroundResource(0);
            bitmapGroupProfile = ImageUtils.handleImageGallery(uriImageGallery, getContext());
            imgProfile.setImageBitmap(bitmapGroupProfile);
            bitmapGroupProfile = ImageUtils.scaleDown(bitmapGroupProfile, 200, false);
        }
    }



    private void loadCategoriesToFilter() {
        myRef.child(FireBaseConstant.NAME_CATEGORIES_TABLE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int index = 0;
                allCategoriesName = new String[(int) dataSnapshot.getChildrenCount() + 4];
                allCategoriesName[index++] = GeneralConstants.ALL;
                allCategoriesName[index++] = GeneralConstants.STATUS_ACTIVE;
                allCategoriesName[index++] = GeneralConstants.STATUS_PROGRESS;
                allCategoriesName[index++] = GeneralConstants.STATUS_ENDED;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String nameOfCategory = snapshot.getValue(String.class);
                    allCategoriesName[index++] = nameOfCategory;
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_spinner_dropdown_item, allCategoriesName);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spnFilterListGame.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getAllActiveGames() {
        Query query = myRef.child(FireBaseConstant.GAMES_TABLE);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Game game = dataSnapshot.getValue(Game.class);
                updateNewGameAdded(game);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateNewGameAdded(Game game) {
        arrGames.add(game);
        adapterGames.notifyDataSetChanged();
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
    public void insertGambleToFirebase(Gamble gamble) {
        mListener.insertGambleToFireBase(gamble);
    }

    @Override
    public void deleteGame(Game game) {
        mListener.deleteGame(game);
    }

    @Override
    public void showEditGameDialog(Game game) {
        mListener.showGameDialog(game, true);
    }

    @Override
    public void updateTotalMoney(User user) {
        mListener.updateTotalMoney(user);
    }

    @Override
    public void showChatScreen(Game game) {
        mListener.showChatScreen(game);
    }

    @Override
    public void createGroup(Game game) {
        if (game.getStatus().equals(GeneralConstants.STATUS_ENDED)){
            Toast.makeText(getContext(), "Game is over", Toast.LENGTH_SHORT).show();
            return;
        }
        dialogCreateGroup = new Dialog(getContext());
        dialogCreateGroup.setContentView(R.layout.dialog_create_group);
        final Group group = new Group();

        group.setKeyGame(game.getKey());
        group.setGroupName1(game.getGroup1name());
        group.setGroupName2(game.getGroup2name());
        int money = SharedPreferencesHelper.getInstance(getContext()).getUser().getTotalMoney();
        Button btnCancel = dialogCreateGroup.findViewById(R.id.btnCancel);
        final TextView txtEntryAmount = dialogCreateGroup.findViewById(R.id.txtEntryAmouny);
        final SeekBar seekBarMoney = dialogCreateGroup.findViewById(R.id.seekBarStandart);
        seekBarMoney.setMax(money);
        seekBarMoney.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                txtEntryAmount.setText(i + "$");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        RadioGroup radioGroup = dialogCreateGroup.findViewById(R.id.rbPrivacyGroup);
        imgProfile = dialogCreateGroup.findViewById(R.id.imgGroupProfile);
        final EditText edtGroupName = dialogCreateGroup.findViewById(R.id.edtGroupName);
        final TextView txtLength = dialogCreateGroup.findViewById(R.id.txtLength);
        edtGroupName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                txtLength.setText(s.length()+"/10");
            }
        });

        Button btnOK = dialogCreateGroup.findViewById(R.id.btnOK);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rbOpenGroup:
                        group.setStatus(STATUS_PUBLIC);
                        break;
                    case R.id.rbClosedGroup:
                        group.setStatus(STATUS_PRIVATE);
                        break;
                }
            }
        });
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                        || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    pickGroupProfileImage();
                } else {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
                }
            }
        });
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bitmapGroupProfile==null || edtGroupName.equals("") || group.getStatus() == null) {
                    Toast.makeText(getContext(), "you must fill all the feilds", Toast.LENGTH_SHORT).show();
                    return;
                }
                String emailManager = SharedPreferencesHelper.getInstance(getContext()).getUser().textEmailForFirebase();
                group.setGroupName(edtGroupName.getText().toString());
                group.setTimestamp(System.currentTimeMillis());
                group.setUserManagerKey(emailManager);
                group.setEntryAmount(seekBarMoney.getProgress());
                mListener.updateGroupInFirebase(group, bitmapGroupProfile);

                User user = SharedPreferencesHelper.getInstance(getContext()).getUser();
                user.removeMoney(group.getEntryAmount());
                SharedPreferencesHelper.getInstance(getContext()).setUser(user);
                mListener.updateTotalMoney(user);

                mListener.showAddFriendsFragment(group);
                dialogCreateGroup.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bitmapGroupProfile = null;
                dialogCreateGroup.dismiss();
            }
        });
        dialogCreateGroup.setCancelable(false);
        dialogCreateGroup.show();
    }

    @Override
    public void refreshList() {
        adapterGames.notifyDataSetChanged();
    }

    @Override
    public void updateSpecificGame(Game game) {
        for (int i = 0; i < arrGames.size(); i++) {
            if (game.getKey().equals(arrGames.get(i).getKey())){
                arrGames.remove(i);
                arrGames.add(i,game);
                adapterGames.notifyDataSetChanged();
                return;
            }
        }
    }


    public interface OnFragmentInteractionListener {
        void registerEventFromMain(IHomepage iHomepage);

        void insertGambleToFireBase(Gamble gamble);

        void deleteGame(Game game);

        void showGameDialog(Game game, boolean isUpdateDialogGame);

        void updateTotalMoney(User user);

        void showChatScreen(Game game);

        void updateGroupInFirebase(Group group, Bitmap bitmapGroupProfile);


        void showAddFriendsFragment(Group group);
    }
}
