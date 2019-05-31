package com.sagi.gambling.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sagi.gambling.R;
import com.sagi.gambling.adapters.AdapterHistoryGambles;
import com.sagi.gambling.entities.Gamble;
import com.sagi.gambling.entities.Game;
import com.sagi.gambling.entities.HistoryGamble;
import com.sagi.gambling.interfaces.IHistoryGambles;
import com.sagi.gambling.utilities.SharedPreferencesHelper;
import com.sagi.gambling.utilities.constant.FireBaseConstant;
import com.sagi.gambling.utilities.constant.GeneralConstants;

import java.util.ArrayList;


public class MyHistoryGamblesFragment extends Fragment implements IHistoryGambles {

    private RecyclerView recyclerViewMyHistoryGambles;
    private ArrayList<HistoryGamble> allMyGamblesList = new ArrayList<>();
    private AdapterHistoryGambles adapterHistoryGambles;

    private OnFragmentInteractionListener mListener;

    public MyHistoryGamblesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_history_gambles, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mListener.loadAllMyHistoryGambles();

        recyclerViewMyHistoryGambles =view.findViewById(R.id.recyclerViewGambles);
        adapterHistoryGambles=new AdapterHistoryGambles(allMyGamblesList,getContext());
        if(allMyGamblesList.size()==0)
            Toast.makeText(getContext(), "there are no gambles", Toast.LENGTH_LONG).show();
        recyclerViewMyHistoryGambles.setHasFixedSize(true);
        recyclerViewMyHistoryGambles.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewMyHistoryGambles.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerViewMyHistoryGambles.setAdapter(adapterHistoryGambles);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
            mListener.attachEventFromMain(this);
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener.attachEventFromMain(null);
        mListener = null;
    }


    @Override
    public void onLoadGamble(HistoryGamble historyGamble) {
        allMyGamblesList.add(historyGamble);
        adapterHistoryGambles.notifyDataSetChanged();
    }


    public interface OnFragmentInteractionListener {
        void loadAllMyHistoryGambles();
        void attachEventFromMain(IHistoryGambles iHistoryGambles);
    }
}
