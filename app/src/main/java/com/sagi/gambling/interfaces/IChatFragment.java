package com.sagi.gambling.interfaces;

import com.sagi.gambling.entities.MessageChat;

/**
 * Created by User on 28/12/2018.
 */

public interface IChatFragment {
    void onNewMessageAdded(MessageChat messageChat);
}
