package com.sagi.gambling.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sagi.gambling.R;
import com.sagi.gambling.adapters.AdapterChat;
import com.sagi.gambling.entities.Group;
import com.sagi.gambling.entities.MessageChat;
import com.sagi.gambling.entities.User;
import com.sagi.gambling.utilities.DownloadImage;
import com.sagi.gambling.utilities.HandleColorByUser;
import com.sagi.gambling.utilities.Patch;
import com.sagi.gambling.utilities.SharedPreferencesHelper;
import com.sagi.gambling.utilities.UploadImage;
import com.sagi.gambling.utilities.constant.FireBaseConstant;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.ArrayList;


public class GroupChatFragment extends Fragment {

    private TextView txtDateCreated, txtNumPeople, txtTotalAmount, txtGroupName, txtGroup1Name, txtGroup2Name;
    private ImageView imgGroupProfile;
    private RecyclerView recyclerViewChat;
    private Group group;
    private AdapterChat adapterChat;
    private ArrayList<MessageChat> allMessagesList=new ArrayList();
    private DatabaseReference myRef;
    private final int IMG_FROM_GALLERY = 2;
    private Bitmap bitmapProfile = null;
    private OnFragmentInteractionListener mListener;
    public static final String GROUP_KEY = "GROUP_KEY";
    private EditText edtMessage;
    private ImageView imgSend;

    public GroupChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        myRef = FirebaseDatabase.getInstance().getReference();

        txtDateCreated = view.findViewById(R.id.txtDateCreated);
        txtNumPeople = view.findViewById(R.id.txtNumPeople);
        txtTotalAmount = view.findViewById(R.id.txtTotalAmount);
        txtGroupName = view.findViewById(R.id.txtGroupName);
        txtGroup1Name = view.findViewById(R.id.txtGroup1Name);
        txtGroup2Name = view.findViewById(R.id.txtGroup2Name);
        imgGroupProfile = view.findViewById(R.id.imgGroupProfile);
        recyclerViewChat = view.findViewById(R.id.recyclerViewChat);

        loadGroup();
        loadValuesToViews();
        loadListeners();

        configurationRecyclerView();
        listenerMessageChat();
        loadNumberOfPepoleInGroup();
        edtMessage = view.findViewById(R.id.edtMessage);
        edtMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendNewMessage();
                    return true;
                }
                return false;
            }
        });

        imgSend = view.findViewById(R.id.imgSend);
        imgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendNewMessage();
            }
        });
    }

    private void loadNumberOfPepoleInGroup() {
        myRef.child(FireBaseConstant.GROUPS_CHAT_TABLE).child(group.getGroupKey()).child("countUsers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer countUsers=dataSnapshot.getValue(Integer.class);
                txtNumPeople.setText(countUsers+"/256");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadListeners() {
        txtGroupName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        imgGroupProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, IMG_FROM_GALLERY);
            }
        });
    }


    private void sendNewMessage() {
        String message = edtMessage.getText().toString();
        if (message.isEmpty())
            return;
        User user=SharedPreferencesHelper.getInstance(getContext()).getUser();
        MessageChat messageChat = new MessageChat(message, System.currentTimeMillis(), user.getFirstName() + " " + user.getLastName(), user.getEmail());
        myRef.child(FireBaseConstant.NAME_CHAT_TABLE).child(group.getGroupKey()).push().setValue(messageChat);

        edtMessage.setText("");
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMG_FROM_GALLERY && resultCode == Activity.RESULT_OK && data != null) {
            Uri uriImageGallery = data.getData();
            try {
                bitmapProfile = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uriImageGallery);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imgGroupProfile.setBackgroundResource(0);
            convertBitmapToLowQuality(uriImageGallery);
        }
    }
    private void uploadImage() {
        new UploadImage(Patch.GROUPS_PROFILES, group.getGroupKey(), bitmapProfile, new UploadImage.IUploadImage() {
            @Override
            public void onSuccess() {
               // progressDialogUpload.dismiss();

            }

            @Override
            public void onFail(String error) {
              //  progressDialogUpload.dismiss();
//                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProgress(int progress) {
              //  progressDialogUpload.setMessage("Uploading " + progress + "%");
            }
        }).startUpload();
    }
    private void convertBitmapToLowQuality(Uri uriImageGallery) {
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                imgGroupProfile.setImageBitmap(bitmap);
                bitmapProfile = bitmap;

                uploadImage();
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        Picasso.with(getContext()).load(uriImageGallery).resize(200, 200).centerInside().into(target);
    }


    private HandleColorByUser handleColorByUser = new HandleColorByUser();

    private  ChildEventListener chatEventListener;
    private void listenerMessageChat() {
         chatEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                MessageChat messageChat = dataSnapshot.getValue(MessageChat.class);
                handleColorByUser.init(messageChat);
                allMessagesList.add(messageChat);
                adapterChat.notifyDataSetChanged();

                recyclerViewChat.post(new Runnable() {
                    @Override
                    public void run() {
                        recyclerViewChat.smoothScrollToPosition(adapterChat.getItemCount() - 1);
                    }
                });

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

        myRef.child(FireBaseConstant.NAME_CHAT_TABLE).child(group.getGroupKey()).addChildEventListener(chatEventListener);
    }

    private void configurationRecyclerView() {
        adapterChat = new AdapterChat(allMessagesList, getContext() );
        recyclerViewChat.setHasFixedSize(true);
        recyclerViewChat.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewChat.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerViewChat.setAdapter(adapterChat);
    }

    private void loadValuesToViews() {
        txtDateCreated.setText(group.getTimeAndDate());
        txtGroup1Name.setText(group.getGroupName1());
        txtGroup2Name.setText(group.getGroupName2());
        txtNumPeople.setText(group.getCountUsers()+"/256");
        txtTotalAmount.setText(loadTextTimeLeftForGame());
        txtGroupName.setText(group.getGroupName());
        txtTotalAmount.setText("Total money: "+(group.getEntryAmount()*group.getCountUsers())+"$");
        new DownloadImage(Patch.GROUPS_PROFILES, group.getGroupKey(), new DownloadImage.IDownloadImage() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(getContext()).load(uri).fit().into(imgGroupProfile);
            }

            @Override
            public void onFail(String error) {
                Log.e("DownloadImage", error);
            }
        }).startLoading();
    }

    private String loadTextTimeLeftForGame() {
        return "time";
    }

    private void loadGroup() {
        Bundle bundle = getArguments();
        group = (Group) bundle.getSerializable(GROUP_KEY);
    }

    public static GroupChatFragment newInstance(Group group) {
        GroupChatFragment fragment = new GroupChatFragment();
        Bundle args = new Bundle();
        args.putSerializable(GROUP_KEY, group);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group_chat, container, false);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        myRef.child(FireBaseConstant.NAME_CHAT_TABLE).child(group.getGroupKey()).removeEventListener(chatEventListener);
        mListener = null;
    }


    public interface OnFragmentInteractionListener {


    }
}
