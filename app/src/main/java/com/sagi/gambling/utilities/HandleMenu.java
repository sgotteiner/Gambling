package com.sagi.gambling.utilities;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;

public class HandleMenu {
    private Context context;
    private View view;
    private int[] arrHideItemsByCriterion;
    private CallbackMenuHandler mListener;
    private int menuId;

    public HandleMenu(Context context, View view, int menuId, int[] arrHideItemsByCriterion, CallbackMenuHandler callbackMenuHandler) {
        this.context = context;
        this.view = view;
        this.menuId = menuId;
        this.arrHideItemsByCriterion = arrHideItemsByCriterion;
        mListener = callbackMenuHandler;

        showMenu();
    }

    private void showMenu() {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.inflate(menuId);

        boolean isNeedToHide = mListener.handleCriterionToHide();

        for (int i = 0; i < arrHideItemsByCriterion.length; i++) {
            popupMenu.getMenu().findItem(arrHideItemsByCriterion[i]).setVisible(!isNeedToHide);
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                mListener.onItemClick(item.getItemId());
                return true;
            }
        });
        popupMenu.show();
    }

    public interface CallbackMenuHandler {
        void onItemClick(int itemId);

        boolean handleCriterionToHide();
    }
}
