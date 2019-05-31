package com.sagi.gambling.utilities;

import android.graphics.Color;

import com.sagi.gambling.entities.MessageChat;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by User on 04/01/2019.
 */

public class HandleColorByUser {
    private final int INCRUMENT = 50;
    private int red = 150, green = 30, blue = 200;
    private HashMap<String, Integer> hashMapColors = new HashMap<>();


    public HandleColorByUser() {
    }

    public void init(MessageChat messageChat) {
      boolean isExist=  hashMapColors.containsKey(messageChat.getUserKey());
      if (isExist){
          int myColor = hashMapColors.get(messageChat.getUserKey());
          messageChat.setColorUser(myColor);
      }else {
          int color = Color.argb(255, red, green, blue);
          messageChat.setColorUser(color);
          hashMapColors.put(messageChat.getUserKey(), color);
          incrumentColor();
      }
    }

    private void incrumentColor() {
        red += INCRUMENT;
        green += INCRUMENT + 15;
        blue += INCRUMENT + 8;

        red = red % 255;
        green = green % 255;
        blue = blue % 255;

    }
}
