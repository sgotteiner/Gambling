package com.sagi.gambling.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sagi.gambling.R;
import com.sagi.gambling.adapters.AdapterGroups;
import com.sagi.gambling.entities.Group;
import com.sagi.gambling.entities.GroupMember;
import com.sagi.gambling.entities.HandleGroup;
import com.sagi.gambling.utilities.SharedPreferencesHelper;
import com.sagi.gambling.utilities.constant.FireBaseConstant;
import com.sagi.gambling.utilities.constant.GeneralConstants;

import java.util.ArrayList;


public class RequestFriendFragment extends Fragment implements AdapterGroups.CallBackAdapterGroups {

    private OnFragmentInteractionListener mListener;
    private RecyclerView recyclerViewRequestGroups;
    private AdapterGroups adapterRequestGroups;
    private ArrayList<Group> arrRequestGroups;
    private DatabaseReference myRef;


    public RequestFriendFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_request_friend, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        myRef = FirebaseDatabase.getInstance().getReference();
        arrRequestGroups = new ArrayList<>();
        loadViews(view);
        adapterRequestGroups = new AdapterGroups(arrRequestGroups, getContext(), true, this);
        recyclerViewRequestGroups.setHasFixedSize(true);
        recyclerViewRequestGroups.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewRequestGroups.setAdapter(adapterRequestGroups);
        startLoadingRequestGroup();
    }

    private void startLoadingRequestGroup() {

        myRef.child(FireBaseConstant.REQUEST_FREINDS_GROUPS_TABLE).child(SharedPreferencesHelper.getInstance(getContext()).getUser().textEmailForFirebase()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String groupKey = dataSnapshot.getValue(String.class);
                loadGroupFromKey(groupKey);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String groupKey = dataSnapshot.getValue(String.class);
                for (int i = 0; i < arrRequestGroups.size(); i++) {
                    if (arrRequestGroups.get(i).getGroupKey().equals(groupKey)) {
                        arrRequestGroups.remove(i);
                    }
                }
                adapterRequestGroups.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadGroupFromKey(final String groupKey) {
        myRef.child(FireBaseConstant.GROUPS_CHAT_TABLE).child(groupKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Group group = dataSnapshot.getValue(Group.class);
                if (mLastGroupJoined != null && mLastGroupJoined.equals(group.getGroupKey())) {
                    myRef.child(FireBaseConstant.GROUPS_CHAT_TABLE).child(groupKey).removeEventListener(this);
                    return;
                }


                boolean isEdit = false;
                for (int i = 0; i < arrRequestGroups.size(); i++) {
                    if (arrRequestGroups.get(i).getGroupKey().equals(group.getGroupKey())) {
                        arrRequestGroups.get(i).setCountUsers(group.getCountUsers());
                        isEdit = true;
                    }
                }


                if (!isEdit)
                    arrRequestGroups.add(0, group);
                adapterRequestGroups.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadViews(View view) {
        recyclerViewRequestGroups = view.findViewById(R.id.recyclerViewRequestGroups);
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
        mListener = null;
    }


    private String mLastGroupJoined = null;
    private String status;

    @Override
    public void joinGroup(final Group group) {

         myRef.child(FireBaseConstant.GAMES_TABLE).child(group.getKeyGame()).child("status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                status=dataSnapshot.getValue(String.class);
                joinGroupFunc(group);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void joinGroupFunc(Group group) {
        if (!status.equals(GeneralConstants.STATUS_ACTIVE)){
            Toast.makeText(getContext(), "The game already started", Toast.LENGTH_SHORT).show();
            return;
        }

        mLastGroupJoined = group.getGroupKey();
        HandleGroup handleGroup = new HandleGroup(group.getGroupKey(), group.loadMyGroupGamble(), false);
        myRef.child(FireBaseConstant.MY_GROUPS_CHAT_TABLE).child(SharedPreferencesHelper.getInstance(getContext()).getUser().textEmailForFirebase()).child(group.getGroupKey()).setValue(handleGroup);


        GroupMember groupMember = new GroupMember(SharedPreferencesHelper.getInstance(getContext()).getUser().textEmailForFirebase(), group.loadMyGroupGamble());
        myRef.child(FireBaseConstant.MEMBERS_GROUP_TABLE).child(group.getGroupKey()).child(SharedPreferencesHelper.getInstance(getContext()).getUser().textEmailForFirebase()).setValue(groupMember);

        incrementGroupUsers(group);
        removeFromRequestGroup(group);
        mListener.addListenerToThisGame(handleGroup,group);
    }

    @Override
    public void removeFromRequestGroup(Group group) {
        myRef.child(FireBaseConstant.REQUEST_FREINDS_GROUPS_TABLE).child(SharedPreferencesHelper.getInstance(getContext()).getUser().textEmailForFirebase()).child(group.getGroupKey()).removeValue();
    }

    @Override
    public void onClickItem(Group group) {

    }

    @Override
    public void removeFromGroup(Group group) {

    }

    @Override
    public void addFriend(Group group) {

    }

    @Override
    public void showListGroupFriends(Group group) {
        if(group.getCountUsers()==0){
            Toast.makeText(getContext(), "Group is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        mListener.showListGroupFriends(group.getGroupKey());
    }

    private void incrementGroupUsers(final Group group) {
        myRef.child(FireBaseConstant.GROUPS_CHAT_TABLE).child(group.getGroupKey()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Group groupUpdated = dataSnapshot.getValue(Group.class);
                group.setCountUsers(groupUpdated.getCountUsers() + 1);
                myRef.child(FireBaseConstant.GROUPS_CHAT_TABLE).child(group.getGroupKey()).setValue(group);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public interface OnFragmentInteractionListener {

        void showListGroupFriends(String groupKey);

        void addListenerToThisGame(HandleGroup handleGroup, Group group);
    }
}
