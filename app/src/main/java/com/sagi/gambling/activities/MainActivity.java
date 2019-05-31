package com.sagi.gambling.activities;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sagi.gambling.R;
import com.sagi.gambling.entities.Gamble;
import com.sagi.gambling.entities.Game;
import com.sagi.gambling.entities.Group;
import com.sagi.gambling.entities.HandleGroup;
import com.sagi.gambling.entities.HandleGroupMoney;
import com.sagi.gambling.entities.HistoryGamble;
import com.sagi.gambling.entities.MessageChat;
import com.sagi.gambling.entities.PaidGamble;
import com.sagi.gambling.entities.PaidGroup;
import com.sagi.gambling.entities.User;
import com.sagi.gambling.fragments.ChatFragment;
import com.sagi.gambling.fragments.GroupChatFragment;
import com.sagi.gambling.fragments.HomePageFragment;
import com.sagi.gambling.fragments.ManagerPanelFragment;
import com.sagi.gambling.fragments.MyGroupsFragment;
import com.sagi.gambling.fragments.MyHistoryGamblesFragment;
import com.sagi.gambling.fragments.RequestFriendFragment;
import com.sagi.gambling.fragments.SearchFriendsFragment;
import com.sagi.gambling.fragments.SettingsFragment;
import com.sagi.gambling.fragments.UserFragment;
import com.sagi.gambling.fragments.dialogs.DialogGameFragment;
import com.sagi.gambling.fragments.dialogs.DialogListFriendsFragment;
import com.sagi.gambling.interfaces.IChatFragment;
import com.sagi.gambling.interfaces.IDialogFragment;
import com.sagi.gambling.interfaces.IHistoryGambles;
import com.sagi.gambling.interfaces.IHomepage;
import com.sagi.gambling.interfaces.IUserFragmentGetEventFromMain;
import com.sagi.gambling.services.ListenerFireService;
import com.sagi.gambling.utilities.Patch;
import com.sagi.gambling.broadcast_recivers.PowerConnectionReciever;
import com.sagi.gambling.utilities.SharedPreferencesHelper;
import com.sagi.gambling.utilities.UploadImage;
import com.sagi.gambling.utilities.Utils;
import com.sagi.gambling.utilities.constant.FireBaseConstant;
import com.sagi.gambling.utilities.constant.GeneralConstants;

import static com.sagi.gambling.utilities.constant.FireBaseConstant.USERS_TABLE;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        UserFragment.OnFragmentInteractionListener,
        ChatFragment.OnFragmentInteractionListener,
        GroupChatFragment.OnFragmentInteractionListener,
        HomePageFragment.OnFragmentInteractionListener,
        ManagerPanelFragment.OnFragmentInteractionListener,
        DialogGameFragment.OnFragmentInteractionListener,
        MyHistoryGamblesFragment.OnFragmentInteractionListener,
        MyGroupsFragment.OnFragmentInteractionListener,
        SearchFriendsFragment.OnFragmentInteractionListener,
        RequestFriendFragment.OnFragmentInteractionListener,
        SettingsFragment.OnFragmentInteractionListener,
        DialogListFriendsFragment.OnFragmentInteractionListener {

    private Fragment fragment;
    private StorageReference mStorageRef;
    private DatabaseReference myRef;
    private IUserFragmentGetEventFromMain iUserFragmentGetEventFromMain;
    private IDialogFragment iDialogFragment;
    private IHomepage iHomepage;
    private IChatFragment mIChatFragment;
    private IHistoryGambles mIHistoryFragment;
    private NavigationView mNavigationView;
    private PowerConnectionReciever mReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        runMyService();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        myRef = FirebaseDatabase.getInstance().getReference();
        updateLastSeen();
        fragment = getDefFragmwnt();
        showFragment(fragment);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        drawerListener(drawer);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        if (SharedPreferencesHelper.getInstance(this).getUser().isManagerApp())
            handleManager();

        handleMoney();
    }

    private PaidGroup paidGroup;
    private PaidGamble paidGamble;

    private void handleMoney() {
        paidGroup = new PaidGroup(this, new HandleGroupMoney.CallbackHandleMoney() {
            @Override
            public void onLoseTheGame(String theWinner, int money, String yourSelected, String group1, String group2) {
                showDialogFinishGame(theWinner, money, yourSelected, group1, group2, getString(R.string.you_lose));
            }

            @Override
            public void onWinYourGamble(String theWinner, int money, String yourSelected, String group1, String group2) {
                showDialogFinishGame(theWinner, money, yourSelected, group1, group2, getString(R.string.you_won));
            }
        });
        paidGamble = new PaidGamble(this, new HandleGroupMoney.CallbackHandleMoney() {
            @Override
            public void onLoseTheGame(String theWinner, int money, String yourSelected, String group1, String group2) {
                showDialogFinishGame(theWinner, money, yourSelected, group1, group2, getString(R.string.you_lose));
            }

            @Override
            public void onWinYourGamble(String theWinner, int money, String yourSelected, String group1, String group2) {
                showDialogFinishGame(theWinner, money, yourSelected, group1, group2, getString(R.string.you_won));
            }
        });
    }

    private Fragment getDefFragmwnt() {
        Intent intent = getIntent();
        boolean isNeedShowRequestScreen = intent.getBooleanExtra(GeneralConstants.REQUEST_SCREEN_KEY, false);

        if (isNeedShowRequestScreen) {
            ListenerFireService.idNotification = 0;
            return new RequestFriendFragment();
        } else
            return new HomePageFragment();
    }

    private void runMyService() {
        Intent intentService = new Intent(this, ListenerFireService.class);
        startService(intentService);
    }

    private void showDialogFinishGame(String theWinner, int money, String yourSelected, String group1, String group2, String header) {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_win);
        TextView txtgroup1Name, txtgroup2Name, txtMoney, txtWinner, txtTitle;
        Button btnClose;
        txtgroup1Name = dialog.findViewById(R.id.txtGroup1Name);
        txtgroup2Name = dialog.findViewById(R.id.txtGroup2Name);
        txtWinner = dialog.findViewById(R.id.txtWinner);
        txtMoney = dialog.findViewById(R.id.txtMoney);
        txtTitle = dialog.findViewById(R.id.txtTitle);

        if (yourSelected.equals(group1)) {
            txtgroup1Name.setBackgroundResource(R.drawable.shape_editext);
        } else if (yourSelected.equals(group2)) {
            txtgroup2Name.setBackgroundResource(R.drawable.shape_editext);
        } else {
            txtTitle.setBackgroundResource(R.drawable.shape_editext);
        }


        txtTitle.setText(header);
        txtgroup1Name.setText(group1);
        txtgroup2Name.setText(group2);
        txtWinner.setText("The Winner: " + theWinner);
        txtMoney.setText(money + "$");
        btnClose = dialog.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    private void drawerListener(final DrawerLayout drawer) {
        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View view, float v) {

            }

            @Override
            public void onDrawerOpened(@NonNull View view) {
                closeKeyBoard();
            }

            @Override
            public void onDrawerClosed(@NonNull View view) {

            }

            @Override
            public void onDrawerStateChanged(int i) {
            }
        });
    }

    public void closeKeyBoard() {
        if (getCurrentFocus() == null)
            return;
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    private void handleManager() {
        mNavigationView.getMenu().findItem(R.id.nav_manager).setVisible(true);
    }

    public void updateTotalMoney(User user) {
        myRef.child(FireBaseConstant.USERS_TABLE).child(user.textEmailForFirebase()).child("totalMoney").setValue(user.getTotalMoney());
    }

    @Override
    public void showChatScreen(Game game) {
        User user = SharedPreferencesHelper.getInstance(this).getUser();
        fragment = ChatFragment.newInstance(game, user);
        showFragment(fragment);
    }

    public void sendNewMessageToGame(Game game, User user, String msg) {
        MessageChat messageChat = new MessageChat(msg, System.currentTimeMillis(), user.getFirstName() + " " + user.getLastName(), user.getEmail());
        myRef.child(FireBaseConstant.NAME_CHAT_TABLE).child(game.getKey()).push().setValue(messageChat);
    }

    private void insertToFirebaseNewGroup(String nameOfGroup) {
        String textForFirebase = getTextForFirebasse(nameOfGroup);
        myRef.child(FireBaseConstant.NAME_GROUPS_TABLE).child(textForFirebase).setValue(nameOfGroup);
    }

    private void insertToFirebaseNewCategory(String nameOfCategory) {
        String textForFirebase = getTextForFirebasse(nameOfCategory);
        myRef.child(FireBaseConstant.NAME_CATEGORIES_TABLE).child(textForFirebase).setValue(nameOfCategory);
    }

    public void loadAllGroupsName() {
        myRef.child(FireBaseConstant.NAME_GROUPS_TABLE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String[] allGroupsName = new String[(int) dataSnapshot.getChildrenCount() + 1];
                allGroupsName[0] = "Press to select";
                int index = 1;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String nameOfGroup = snapshot.getValue(String.class);
                    allGroupsName[index++] = nameOfGroup;
                }

                if (iDialogFragment != null) {
                    for (int i = 0; i < allGroupsName.length; i++) {
                        allGroupsName[i] = Utils.geteFirstLattersUpperCase(allGroupsName[i]);
                    }
                    iDialogFragment.getAllArrNamesGroup(allGroupsName);
                    iDialogFragment.loadImagesOfGroups();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void loadAllCategoriesName() {
        myRef.child(FireBaseConstant.NAME_CATEGORIES_TABLE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String[] allCategoriesName = new String[(int) dataSnapshot.getChildrenCount() + 1];
                allCategoriesName[0] = "Press to select";
                int index = 1;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String nameOfCategory = snapshot.getValue(String.class);
                    allCategoriesName[index++] = nameOfCategory;
                }

                if (iDialogFragment != null) {
                    for (int i = 0; i < allCategoriesName.length; i++) {
                        allCategoriesName[i] = Utils.geteFirstLattersUpperCase(allCategoriesName[i]);
                    }
                    iDialogFragment.getAllArrNamesCategory(allCategoriesName);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void startLoadingImageGroup(String path, final ImageView imgGroup) {
        path = path.replace(" ", "_");
        path = path.toLowerCase();
        mStorageRef.child(FireBaseConstant.FOLDER_GROUPS_RESOURCES).child(path).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onSuccess(Uri uri) {

                if (iDialogFragment != null) {
                    iDialogFragment.onDownloadUri(uri, imgGroup);
                    iDialogFragment.stopProgressBar();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });
    }

    @Override
    public void updateGame(Game game, Bitmap bitmap1, Bitmap bitmap2, final Boolean isNewGroup1, final Boolean isNewGroup2, Boolean isNewCategory) {
        myRef.child(FireBaseConstant.GAMES_TABLE).child(game.getKey()).setValue(game);
        uploadGroupImage(game, bitmap1, bitmap2, isNewGroup1, isNewGroup2, isNewCategory);

        if (iHomepage != null)
            iHomepage.updateSpecificGame(game);

    }

    private void uploadGroupImage(Game game, Bitmap bitmap1, Bitmap bitmap2, final Boolean isNewGroup1, final Boolean isNewGroup2, final Boolean isNewCategory) {
        int sizeAllImages = 0;
        final int[] isFinishUpload = {0};
        if (isNewGroup1) {
            sizeAllImages = bitmap1.getByteCount();
        }
        if (isNewGroup2) {
            sizeAllImages += bitmap2.getByteCount();
        }

        if (isNewCategory)
            insertToFirebaseNewCategory(game.getCategoryName());

        if (isNewGroup1) {
            insertToFirebaseNewGroup(game.getGroup1name());
            new UploadImage(Patch.GROUPS_RESOURCES, getTextForFirebasse(game.getGroup1name().toLowerCase()), bitmap1, new UploadImage.IUploadImage() {
                @Override
                public void onSuccess() {
                    isFinishUpload[0]++;
                    if (isNewGroup2) {
                        if (isFinishUpload[0] == 2) {
                            if (iDialogFragment != null) {
                                iDialogFragment.stopProgressBar();
                                iDialogFragment.dismissDialog();
                                if (iHomepage != null)
                                    iHomepage.refreshList();
                            }
                        }
                    } else {
                        if (iDialogFragment != null) {
                            iDialogFragment.stopProgressBar();
                            iDialogFragment.dismissDialog();
                            if (iHomepage != null)
                                iHomepage.refreshList();
                        }
                    }
                    Toast.makeText(MainActivity.this, "Image Saved", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onFail(String error) {
                    Toast.makeText(MainActivity.this, "couldn't save the image", Toast.LENGTH_SHORT).show();
                    if (iDialogFragment != null) {
                        iDialogFragment.stopProgressBar();
                    }
                }

                @Override
                public void onProgress(int progress) {

                }
            }).startUpload();
        }
        if (isNewGroup2) {
            insertToFirebaseNewGroup(game.getGroup2name());
            new UploadImage(Patch.GROUPS_RESOURCES, getTextForFirebasse(game.getGroup2name().toLowerCase()), bitmap2, new UploadImage.IUploadImage() {
                @Override
                public void onSuccess() {
                    isFinishUpload[0]++;
                    if (isNewGroup1) {
                        if (isFinishUpload[0] == 2) {
                            if (iDialogFragment != null) {
                                iDialogFragment.stopProgressBar();
                                iDialogFragment.dismissDialog();
                                if (iHomepage != null)
                                    iHomepage.refreshList();
                            }
                        }
                    } else {
                        if (iDialogFragment != null) {
                            iDialogFragment.stopProgressBar();
                            iDialogFragment.dismissDialog();
                            if (iHomepage != null)
                                iHomepage.refreshList();
                        }
                    }
                    Toast.makeText(MainActivity.this, "Image Saved", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFail(String error) {
                    Toast.makeText(MainActivity.this, "couldn't save the image", Toast.LENGTH_SHORT).show();
                    if (iDialogFragment != null) {
                        iDialogFragment.stopProgressBar();
                    }
                }

                @Override
                public void onProgress(int progress) {
                }
            }).startUpload();
        }
        if (!isNewGroup1 && !isNewGroup2) {
            if (iDialogFragment != null) {
                iDialogFragment.stopProgressBar();
                iDialogFragment.dismissDialog();
            }
        }
    }

    @Override
    public void registerEventFromMain(IHomepage iHomepage) {
        this.iHomepage = iHomepage;
    }

    @Override
    public void insertGambleToFireBase(Gamble gamble) {
        User user = SharedPreferencesHelper.getInstance(MainActivity.this).getUser();
        String idKeyGamble = myRef.child(FireBaseConstant.GAMBLES_TABLE).push().getKey();
        gamble.setKey(idKeyGamble);
        myRef.child(FireBaseConstant.GAMBLES_TABLE).child(idKeyGamble).setValue(gamble);
        myRef.child(FireBaseConstant.HISTORY_GAMBLES_TABLE).child(user.textEmailForFirebase()).push().setValue(idKeyGamble);

        if (paidGamble!=null){
            paidGamble.addListenerChangeGame(gamble);
        }
    }

    @Override
    public void deleteGame(Game game) {
        myRef.child(FireBaseConstant.GAMES_TABLE).child(game.getKey()).removeValue();
    }

    @Override
    public void showGameDialog(Game game, boolean isUpdateDialogGame) {
        DialogGameFragment dialogGameFragment = DialogGameFragment.newInstance(isUpdateDialogGame, game);
        dialogGameFragment.show(getSupportFragmentManager(), "tag");
    }

    private void updateLastSeen() {
        User user = SharedPreferencesHelper.getInstance(MainActivity.this).getUser();
        myRef.child(FireBaseConstant.USERS_TABLE).child(user.textEmailForFirebase()).child(FireBaseConstant.LAST_TIME_SEEN).setValue(System.currentTimeMillis());
    }


    @Override
    protected void onPause() {

        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }


        updateIsUserActiveInApp(false);
        super.onPause();
    }

    private void updateIsUserActiveInApp(boolean isUserActive) {
        User user = SharedPreferencesHelper.getInstance(MainActivity.this).getUser();
        if (user == null)
            return;
        myRef.child(FireBaseConstant.USERS_TABLE).child(user.textEmailForFirebase()).child(FireBaseConstant.IS_USER_ACTIVE).setValue(isUserActive);
    }

    @Override
    protected void onStart() {
        if (mReceiver == null) {
            mReceiver = new PowerConnectionReciever();
            registerReceiver(mReceiver, mReceiver.getIntentFilter());
        }

        updateIsUserActiveInApp(true);
        super.onStart();
    }


    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void registerEventFromMain(IUserFragmentGetEventFromMain iUserFragmentGetEventFromMain) {
        this.iUserFragmentGetEventFromMain = iUserFragmentGetEventFromMain;
    }

    public void updateProfile(User user) {
        myRef.child(USERS_TABLE).child(user.textEmailForFirebase()).setValue(user);
    }

    @Override
    public void updateProfileWithoutBitmap(User user) {
        myRef.child(USERS_TABLE).child(user.textEmailForFirebase()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (iUserFragmentGetEventFromMain != null) {
                    iUserFragmentGetEventFromMain.stopProgressBar();
                    fragment = new HomePageFragment();
                    showFragment(fragment);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (iUserFragmentGetEventFromMain != null) {
                    iUserFragmentGetEventFromMain.stopProgressBar();
                    fragment = new HomePageFragment();
                    showFragment(fragment);
                }
            }
        });
    }


    @Override
    public void showHomePage() {
        fragment = new HomePageFragment();
        showFragment(fragment);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            if (fragment instanceof HomePageFragment)
                return false;
            showHomePage();
        } else if (id == R.id.nav_history) {
            if (fragment instanceof MyHistoryGamblesFragment)
                return false;
            fragment = new MyHistoryGamblesFragment();
            showFragment(fragment);
        } else if (id == R.id.nav_my_group) {
            if (fragment instanceof MyGroupsFragment)
                return false;
            fragment = new MyGroupsFragment();
            showFragment(fragment);
        } else if (id == R.id.nav_profile) {
            if (fragment instanceof UserFragment)
                return false;
            fragment = new UserFragment();
            showFragment(fragment);
        } else if (id == R.id.nav_request_group) {
            if (fragment instanceof RequestFriendFragment)
                return false;
            fragment = new RequestFriendFragment();
            showFragment(fragment);
        } else if (id == R.id.nav_settings) {
            if (fragment instanceof SettingsFragment)
                return false;
            fragment = new SettingsFragment();
            showFragment(fragment);
        } else if (id == R.id.nav_logout) {
            logOutFromApp();
        } else if (id == R.id.nav_manager) {
            showGameDialog(null, false);
//            if (fragment instanceof ManagerPanelFragment)
//                return false;
//            fragment = new ManagerPanelFragment();
//            showFragment(fragment);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void showFragment(android.support.v4.app.Fragment fragment) {
        getSupportFragmentManager().
                beginTransaction().
                replace(R.id.frameLayoutContainerMain, fragment)
                .commit();
    }

    private void logOutFromApp() {
        updateIsUserActiveInApp(false);

        SharedPreferencesHelper.getInstance(this).resetSharedPreferences();
        Intent intent = new Intent(this, SplashActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void registerFromDialogFragmentEvent(IDialogFragment iDialogFragment) {
        this.iDialogFragment = iDialogFragment;
    }

    @Override
    public void addNewGame(Game game, Bitmap bitmap1, Bitmap bitmap2, final boolean isNewGroup1, final boolean isNewGroup2, final boolean isNewCategory) {

        String idKeyGame = myRef.child(FireBaseConstant.GAMES_TABLE).push().getKey();
        game.setKey(idKeyGame);
        myRef.child(FireBaseConstant.GAMES_TABLE).child(idKeyGame).setValue(game);
        uploadGroupImage(game, bitmap1, bitmap2, isNewGroup1, isNewGroup2, isNewCategory);
        addGameChat(game);
    }

    private void addGameChat(Game game) {
        MessageChat messageChat = new MessageChat("Chat " + game.getGroup1name() + " VS " + game.getGroup2name(), System.currentTimeMillis(), "Manager", "Manager");
        myRef.child(FireBaseConstant.NAME_CHAT_TABLE).child(game.getKey()).push().setValue(messageChat);
    }

    private void addGroupChat(Group group) {
        MessageChat messageChat = new MessageChat("Chat " + group.getGroupName(), System.currentTimeMillis(), "Manager", "Manager");
        myRef.child(FireBaseConstant.NAME_CHAT_TABLE).child(group.getGroupKey()).push().setValue(messageChat);
    }

    private ChildEventListener chatEventListener;

    @Override
    public void loadAndListenChatByGame(Game game) {
        chatEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                MessageChat messageChat = dataSnapshot.getValue(MessageChat.class);
                if (mIChatFragment != null)
                    mIChatFragment.onNewMessageAdded(messageChat);
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
        };

        myRef.child(FireBaseConstant.NAME_CHAT_TABLE).child(game.getKey()).addChildEventListener(chatEventListener);
    }

    @Override
    public void removeListenChatByGame(Game game) {
        if (chatEventListener != null)
            myRef.child(FireBaseConstant.NAME_CHAT_TABLE).child(game.getKey()).removeEventListener(chatEventListener);
        chatEventListener = null;
    }

    @Override
    public void registerEventFromMain(IChatFragment iChatFragment) {
        mIChatFragment = iChatFragment;
    }

    @Override
    public void loadAllMyHistoryGambles() {
        myRef.child(FireBaseConstant.HISTORY_GAMBLES_TABLE).child(SharedPreferencesHelper.getInstance(this).getUser().textEmailForFirebase()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String gambleKey = snapshot.getValue(String.class);
                    loadGambleFromKey(gambleKey);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadGambleFromKey(String gambleKey) {
        myRef.child(FireBaseConstant.GAMBLES_TABLE).child(gambleKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Gamble gamble = dataSnapshot.getValue(Gamble.class);
                loadGameFromGamble(gamble);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadGameFromGamble(final Gamble gamble) {
        myRef.child(FireBaseConstant.GAMES_TABLE).child(gamble.getKeyGame()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Game game = dataSnapshot.getValue(Game.class);
                HistoryGamble historyGamble = new HistoryGamble(game, gamble);

                if (mIHistoryFragment != null)
                    mIHistoryFragment.onLoadGamble(historyGamble);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void attachEventFromMain(IHistoryGambles iHistoryGambles) {
        mIHistoryFragment = iHistoryGambles;
    }


    @Override
    public void updateGroupInFirebase(Group group, Bitmap bitmapGroupProfile) {
        String idKeyGroup = myRef.child(FireBaseConstant.GROUPS_CHAT_TABLE).push().getKey();
        group.setGroupKey(idKeyGroup);
        myRef.child(FireBaseConstant.GROUPS_CHAT_TABLE).child(idKeyGroup).setValue(group);
        uploadGroupImage(group.getGroupKey(), bitmapGroupProfile);
        addGroupChat(group);
        myRef.child(FireBaseConstant.REQUEST_FREINDS_GROUPS_TABLE).child(SharedPreferencesHelper.getInstance(this).getUser().textEmailForFirebase()).child(group.getGroupKey()).setValue(group.getGroupKey());
    }


    @Override
    public void showAddFriendsFragment(Group group) {
        fragment = SearchFriendsFragment.newInstance(group);
        showFragment(fragment);
    }

    @Override
    public void showListGroupFriends(String groupKey) {
        DialogListFriendsFragment dialogListFriendsFragment = DialogListFriendsFragment.newInstance(groupKey);
        dialogListFriendsFragment.show(getSupportFragmentManager(), "tag");
    }


    @Override
    public void addListenerToThisGame(HandleGroup handleGroup, Group group) {
        if (paidGroup!=null){
            paidGroup.addListenerChangeGame(group,handleGroup);
        }
    }

    private void uploadGroupImage(String groupKey, Bitmap bitmapGroupProfile) {
        new UploadImage(Patch.GROUPS_PROFILES, groupKey, bitmapGroupProfile, new UploadImage.IUploadImage() {
            @Override
            public void onSuccess() {

                Toast.makeText(MainActivity.this, "Image Saved", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFail(String error) {
                Toast.makeText(MainActivity.this, "couldn't save the image", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onProgress(int progress) {
            }
        }).startUpload();


    }

    @Override
    public void showChatGroup(Group group) {
        fragment = GroupChatFragment.newInstance(group);
        showFragment(fragment);
    }

    private String getTextForFirebasse(String nameOfGroup) {
        String textForFirebase = nameOfGroup.replace(" ", "_");
        return textForFirebase;
    }
}
