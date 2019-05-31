package com.sagi.gambling.interfaces;

import android.net.Uri;
import android.widget.ImageView;

import com.sagi.gambling.entities.Game;

/**
 * Created by User on 28/11/2018.
 */

public interface IDialogFragment {
    void getAllArrNamesGroup(String [] allNamesGroups);
    void getAllArrNamesCategory(String [] allNamesCategories);
    void onDownloadUri(Uri uriGroup, ImageView imgGroup);
    void stopProgressBar();
    void loadImagesOfGroups();

    void dismissDialog();

//    void refreshChangesInHomepage();
}
