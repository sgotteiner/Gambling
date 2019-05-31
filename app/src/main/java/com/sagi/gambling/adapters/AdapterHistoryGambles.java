package com.sagi.gambling.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sagi.gambling.R;
import com.sagi.gambling.entities.Gamble;
import com.sagi.gambling.entities.HistoryGamble;
import com.sagi.gambling.entities.MessageChat;
import com.sagi.gambling.utilities.DownloadImage;
import com.sagi.gambling.utilities.Patch;
import com.sagi.gambling.utilities.SharedPreferencesHelper;
import com.sagi.gambling.utilities.Utils;
import com.sagi.gambling.utilities.constant.GeneralConstants;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by User on 14/01/2019.
 */

public class AdapterHistoryGambles extends RecyclerView.Adapter<AdapterHistoryGambles.PlaceHolder> {

    private List<HistoryGamble> myGambles;
    private LayoutInflater layoutInflater;
    private Context context;


    public AdapterHistoryGambles(List<HistoryGamble> myGambles, Context context) {
        this.myGambles = myGambles;
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public AdapterHistoryGambles.PlaceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.item_gamble, parent, false);
        return new AdapterHistoryGambles.PlaceHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PlaceHolder holder, int position) {

        final HistoryGamble historyGamble = myGambles.get(position);

        holder.txtRatio1.setBackgroundResource(0);
        holder.txtRatio2.setBackgroundResource(0);
        holder.txtRatioTie.setBackgroundResource(0);

        holder.txtDate.setText(Utils.getTimeFromTimeStamp(historyGamble.getTimeStamp()));
        holder.txtTitleGroup1.setText(historyGamble.getNameGroup1());
        holder.txtTitleGroup2.setText(historyGamble.getNameGroup2());
        switch (historyGamble.getStatus()) {
            case GeneralConstants.STATUS_ACTIVE:
                holder.txtMyStatus.setText("the game is not over");
                // holder.txtProfit.setText("0");
                break;
            case GeneralConstants.STATUS_PROGRESS:
                holder.txtMyStatus.setText("the game is not over");
                // holder.txtProfit.setText("0");
                break;
            case GeneralConstants.STATUS_ENDED:
                if (historyGamble.isPaid()) {
                    if (historyGamble.isMySelectionWin()) {
                        holder.txtMyStatus.setText("You won: ");
                        holder.txtProfit.setText((((historyGamble.getGambleMoney() * historyGamble.getChanceWin()) / 100)+historyGamble.getGambleMoney() ) + "$");
                    } else {
                        holder.txtMyStatus.setText("You lose: ");
                        holder.txtProfit.setText(historyGamble.getGambleMoney() + "$");
                    }
                } else
                    break;
        }
        holder.txtDescription.setText(historyGamble.getDescription());


        holder.txtRatio1.setText(historyGamble.getNameGroup1() + "\n(" +  ((200-historyGamble.getChanceWin())/(float)100) + ")");
        holder.txtRatio2.setText(historyGamble.getNameGroup2() + "\n(" + ((100+historyGamble.getChanceWin())/(float)100) + ")");
        if (historyGamble.getCategory().equals("Soccer"))
            holder.txtRatioTie.setText("Tie\n(" + (1.5) + ")");
        holder.txtMyGamble.setText("My Gamble was: " + historyGamble.getGambleMoney() + "$");

        if (historyGamble.getGroupNameSelected().toLowerCase().equals(historyGamble.getNameGroup1().toLowerCase())) {
            handleBackgroundChoice(holder.txtRatio1);
        } else if (historyGamble.getGroupNameSelected().toLowerCase().equals(historyGamble.getNameGroup2().toLowerCase())) {
            handleBackgroundChoice(holder.txtRatio2);
        } else {
            handleBackgroundChoice(holder.txtRatioTie);
        }


        new DownloadImage(Patch.GROUPS_RESOURCES, historyGamble.getNameGroup1(), new DownloadImage.IDownloadImage() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(context).load(uri).fit().into(holder.img1);
            }

            @Override
            public void onFail(String error) {

            }
        }).startLoading();


        new DownloadImage(Patch.GROUPS_RESOURCES, historyGamble.getNameGroup2(), new DownloadImage.IDownloadImage() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(context).load(uri).fit().into(holder.img2);
            }

            @Override
            public void onFail(String error) {

            }
        }).startLoading();

    }

    private void handleBackgroundChoice(TextView textView) {
        textView.setBackgroundResource(R.drawable.shape_picked_group);
    }

    @Override
    public int getItemCount() {
        return myGambles.size();
    }

    public class PlaceHolder extends RecyclerView.ViewHolder {

        private TextView txtDate, txtTitleGroup2, txtTitleGroup1, txtDescription, txtRatio1, txtRatioTie, txtRatio2, txtMyStatus, txtProfit, txtMyGamble;
        private ImageView img1, img2;

        public PlaceHolder(View view) {
            super(view);
            txtDate = view.findViewById(R.id.txtDate);
            txtTitleGroup1 = view.findViewById(R.id.txtTitleGroup1);
            txtTitleGroup2 = view.findViewById(R.id.txtTitleGroup2);
            txtDescription = view.findViewById(R.id.txtDescription);
            txtRatio1 = view.findViewById(R.id.txtRatio1);
            txtRatioTie = view.findViewById(R.id.txtRatioTie);
            txtRatio2 = view.findViewById(R.id.txtRatio2);
            txtMyStatus = view.findViewById(R.id.txtMyStatus);
            txtProfit = view.findViewById(R.id.txtProfit);
            txtMyGamble = view.findViewById(R.id.txtMyGamble);
            img1 = view.findViewById(R.id.imgGroup1);
            img2 = view.findViewById(R.id.imgGroup2);
        }
    }


}
