package com.sagi.gambling.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sagi.gambling.R;
import com.sagi.gambling.entities.User;
import com.sagi.gambling.interfaces.IResultFriends;
import com.sagi.gambling.utilities.DownloadImage;
import com.sagi.gambling.utilities.Patch;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by User on 14/01/2019.
 */

public class AdapterResultFreinds extends RecyclerView.Adapter<AdapterResultFreinds.PlaceHolder> {

    private List<User> usersList;
    private LayoutInflater layoutInflater;
    private Context context;
    private CallbackAdapterResultFriends mListener;
    private boolean isAddBtn;


    public AdapterResultFreinds(List<User> usersList, Context context, boolean isAddBtn, CallbackAdapterResultFriends callbackAdapterResultFriends) {
        this.usersList = usersList;
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
        mListener = callbackAdapterResultFriends;
        this.isAddBtn = isAddBtn;
    }

    @Override
    public AdapterResultFreinds.PlaceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.item_friend, parent, false);
        return new AdapterResultFreinds.PlaceHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PlaceHolder holder, int position) {

        final User user = usersList.get(position);
        holder.txtName.setText(user.getFirstName() + "\n" + user.getEmail());
        if (isAddBtn)
            holder.btnAddFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onAddFriends(user);
                }
            });
        else holder.btnAddFriend.setVisibility(View.GONE);


        new DownloadImage(Patch.PROFILES, user.getEmail(), new DownloadImage.IDownloadImage() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(context).load(uri).fit().into(holder.imageViewProfile);
            }

            @Override
            public void onFail(String error) {

            }
        }).startLoading();


    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class PlaceHolder extends RecyclerView.ViewHolder {

        private TextView txtName;
        private Button btnAddFriend;
        private ImageView imageViewProfile;

        public PlaceHolder(View view) {
            super(view);

            btnAddFriend = view.findViewById(R.id.btnAddFriend);
            txtName = view.findViewById(R.id.txtName);
            imageViewProfile = view.findViewById(R.id.imageViewProfile);
        }
    }


    public interface CallbackAdapterResultFriends {

        void onAddFriends(User user);
    }
}
