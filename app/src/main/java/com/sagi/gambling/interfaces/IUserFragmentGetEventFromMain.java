package com.sagi.gambling.interfaces;

import android.net.Uri;

public interface IUserFragmentGetEventFromMain extends IWaitingProgressBar {
    void onBackPressedInActivity();
    void onDownloadUri(Uri uriProfile);
}
