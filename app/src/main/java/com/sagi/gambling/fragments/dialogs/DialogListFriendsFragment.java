package com.sagi.gambling.fragments.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sagi.gambling.R;
import com.sagi.gambling.adapters.AdapterResultFreinds;
import com.sagi.gambling.entities.Group;
import com.sagi.gambling.entities.GroupMember;
import com.sagi.gambling.entities.User;
import com.sagi.gambling.utilities.constant.FireBaseConstant;
import com.sagi.gambling.utilities.constant.GeneralConstants;

import java.util.ArrayList;

public class DialogListFriendsFragment extends DialogFragment implements AdapterResultFreinds.CallbackAdapterResultFriends {

    private OnFragmentInteractionListener mListener;
    private String groupKey;
    private AdapterResultFreinds adapterResultFreinds;
    private DatabaseReference myRef;


    public DialogListFriendsFragment() {
    }

    public static DialogListFriendsFragment newInstance(String groupKey) {
        DialogListFriendsFragment fragment = new DialogListFriendsFragment();
        Bundle args = new Bundle();
        args.putString(GeneralConstants.GROUP_KEY, groupKey);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            groupKey = getArguments().getString(GeneralConstants.GROUP_KEY);
        }
        myRef = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        int width = getResources().getDimensionPixelSize(R.dimen.popup_width);
        int height = getResources().getDimensionPixelSize(R.dimen.popup_height);
        getDialog().getWindow().setLayout(width, height);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerUsers = view.findViewById(R.id.recyclerViewGroupFriends);
        loadGroupMembersKeys(groupKey);
        adapterResultFreinds = new AdapterResultFreinds(arrGroupMembers, getContext(), false, this);
        recyclerUsers.setHasFixedSize(true);
        recyclerUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerUsers.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerUsers.setAdapter(adapterResultFreinds);

    }

    private void loadGroupMembersKeys(final String groupKey) {

        myRef.child(FireBaseConstant.MEMBERS_GROUP_TABLE).child(groupKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GroupMember groupMember = snapshot.getValue(GroupMember.class);
                    loadGroupMembersFromKeys(groupMember.getEmailUser());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private ArrayList<User> arrGroupMembers = new ArrayList<>();

    private void loadGroupMembersFromKeys(String memberKey) {

        myRef.child(FireBaseConstant.USERS_TABLE).child(memberKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                arrGroupMembers.add(user);
                adapterResultFreinds.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_list_friends, container, false);
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


    @Override
    public void onAddFriends(User user) {

    }


    public interface OnFragmentInteractionListener {

    }
}
