package com.sagi.gambling.interfaces;

import com.sagi.gambling.entities.Game;

/**
 * Created by User on 06/12/2018.
 */

public interface IHomepage {
    void refreshList();
    void updateSpecificGame(Game game);
}
