package com.sagi.gambling.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sagi.gambling.R;
import com.sagi.gambling.adapters.AdapterResultFreinds;
import com.sagi.gambling.adapters.AdapterTag;
import com.sagi.gambling.entities.Group;
import com.sagi.gambling.entities.User;
import com.sagi.gambling.utilities.SharedPreferencesHelper;
import com.sagi.gambling.utilities.Utils;
import com.sagi.gambling.utilities.constant.FireBaseConstant;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;

public class SearchFriendsFragment extends Fragment implements AdapterResultFreinds.CallbackAdapterResultFriends, AdapterTag.CallbackAdapterTag {

    private OnFragmentInteractionListener mListener;
    private DatabaseReference myRef;
    private AdapterResultFreinds adapterResultFreinds;
    private AdapterTag adapterTag;
    private ArrayList<User> arryListUsers = new ArrayList<>();
    private ArrayList<Map.Entry<String, String>> entryArrayListTags = new ArrayList<>();
    private RecyclerView recyclerUsers;
    private RecyclerView recyclerTags;
    private Group group;

    public SearchFriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_friends, container, false);
    }

    private static final String GROUP_KEY = "GROUP_KEY";

    public static SearchFriendsFragment newInstance(Group group) {
        Bundle args = new Bundle();
        args.putSerializable(GROUP_KEY, group);
        SearchFriendsFragment fragment = new SearchFriendsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        myRef = FirebaseDatabase.getInstance().getReference();


        group = (Group) getArguments().getSerializable(GROUP_KEY);


        EditText edtSearchFriends = view.findViewById(R.id.edtSearchFriends);
        recyclerUsers = view.findViewById(R.id.recyclerUsers);
       ImageView imageViewDone = view.findViewById(R.id.imageViewDone);
        adapterResultFreinds = new AdapterResultFreinds(arryListUsers, getContext(),true, this);
        recyclerUsers.setHasFixedSize(true);
        recyclerUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerUsers.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerUsers.setAdapter(adapterResultFreinds);

        recyclerTags = view.findViewById(R.id.recyclerTags);

        adapterTag = new AdapterTag(entryArrayListTags, getContext(), this);
        recyclerTags.setHasFixedSize(true);
        recyclerTags.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerTags.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerTags.setAdapter(adapterTag);


        imageViewDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.closeKeyBoard();
                mListener.showHomePage();
            }
        });
        edtSearchFriends.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String nameUppercase = editable.toString().toLowerCase();
                loadResult(nameUppercase);
            }
        });
    }

    private void loadResult(String userName) {

        myRef.child(FireBaseConstant.USERS_TABLE).orderByChild("firstName").equalTo(userName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                clearList();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (user == null) {
                        clearList();
                        return;//Todo not finished
                    }

                    arryListUsers.add(user);
                    adapterResultFreinds.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void clearList() {
        arryListUsers.clear();
        adapterResultFreinds.notifyDataSetChanged();
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
        isAlreadyInGroup(user);
    }

    private void isAlreadyInGroup(final User user) {

        if (isAlreadySentRequest(user) || isMe(user)){
            Toast.makeText(getContext(), "user already hsa request", Toast.LENGTH_SHORT).show();
            return;
        }


        myRef.child(FireBaseConstant.MEMBERS_GROUP_TABLE).child(group.getGroupKey()).child(user.textEmailForFirebase()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isExist = dataSnapshot.exists();
                if (isExist) {
                    Toast.makeText(getContext(), "user already exists", Toast.LENGTH_SHORT).show();
                    return;
                }

                    myRef.child(FireBaseConstant.REQUEST_FREINDS_GROUPS_TABLE).child(user.textEmailForFirebase()).child(group.getGroupKey()).setValue(group.getGroupKey());

                    entryArrayListTags.add(new AbstractMap.SimpleEntry<String, String>(user.getFirstName(), user.getEmail()));
                    adapterTag.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    private boolean isMe(User user) {
        return user.getEmail().equals(SharedPreferencesHelper.getInstance(getContext()).getUser().getEmail());
    }

    private boolean isAlreadySentRequest(User user) {

        for (int i = 0; i < entryArrayListTags.size(); i++) {
            if (user.getEmail().equals(entryArrayListTags.get(i).getValue())){
                return true;
            }
        }
        return false;
    }


    @Override
    public void onRemoveClick(Map.Entry<String, String> entry) {
        String emailForFirebase = Utils.textEmailForFirebase(entry.getValue());
        myRef.child(FireBaseConstant.REQUEST_FREINDS_GROUPS_TABLE).child(emailForFirebase).child(group.getGroupKey()).removeValue();
        entryArrayListTags.remove(entry);
        adapterTag.notifyDataSetChanged();
    }

    public interface OnFragmentInteractionListener {

        void closeKeyBoard();

        void showHomePage();
    }
}
