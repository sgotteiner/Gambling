package com.sagi.gambling.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.sagi.gambling.R;
import com.sagi.gambling.adapters.AdapterChat;
import com.sagi.gambling.entities.Game;
import com.sagi.gambling.entities.MessageChat;
import com.sagi.gambling.entities.User;
import com.sagi.gambling.interfaces.IChatFragment;
import com.sagi.gambling.utilities.HandleColorByUser;
import com.sagi.gambling.utilities.Utils;

import java.util.ArrayList;

public class ChatFragment extends Fragment implements IChatFragment {


    private OnFragmentInteractionListener mListener;
    private static final String GAME_KEY = "GAME_KEY";
    private static final String USER_KEY = "USER_KEY";
    private Game game;
    private User user;
    private ImageView imgSend;
    private EditText edtMessage;
    private ArrayList<MessageChat> allMessagesList = new ArrayList<>();
    private AdapterChat adapterChat;
    private RecyclerView recyclerViewChats;

    public ChatFragment() {
    }

    public static ChatFragment newInstance(Game game, User user) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putSerializable(GAME_KEY, game);
        args.putSerializable(USER_KEY, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            game = (Game) getArguments().getSerializable(GAME_KEY);
            user = (User) getArguments().getSerializable(USER_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
         int width = displayMetrics.widthPixels;



        recyclerViewChats = view.findViewById(R.id.recyclerViewChat);
        adapterChat = new AdapterChat(allMessagesList, getContext() );
        recyclerViewChats.setHasFixedSize(true);
        recyclerViewChats.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewChats.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        recyclerViewChats.setAdapter(adapterChat);
        imgSend = view.findViewById(R.id.imgSend);
        imgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendNewMessage();
            }
        });
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
        loadAllMsg(game);
    }

    private void sendNewMessage() {
        String message = edtMessage.getText().toString();
        if (message.isEmpty())
            return;
        mListener.sendNewMessageToGame(game, user, message);

        edtMessage.setText("");
    }

    private void loadAllMsg(Game game) {
        mListener.loadAndListenChatByGame(game);
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
        mListener.removeListenChatByGame(game);
        mListener = null;
    }

    private HandleColorByUser handleColorByUser = new HandleColorByUser();

    @Override
    public void onNewMessageAdded(MessageChat messageChat) {
        handleColorByUser.init(messageChat);
        allMessagesList.add(messageChat);
        adapterChat.notifyDataSetChanged();

        recyclerViewChats.post(new Runnable() {
            @Override
            public void run() {
                recyclerViewChats.smoothScrollToPosition(adapterChat.getItemCount() - 1);
            }
        });

    }

    public interface OnFragmentInteractionListener {
        void loadAndListenChatByGame(Game game);

        void removeListenChatByGame(Game game);

        void registerEventFromMain(IChatFragment iChatFragment);

        void sendNewMessageToGame(Game game, User user, String message);
    }
}
