package com.sagi.gambling.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sagi.gambling.R;
import com.sagi.gambling.entities.Group;
import com.sagi.gambling.utilities.DownloadImage;
import com.sagi.gambling.utilities.HandleMenu;
import com.sagi.gambling.utilities.Patch;
import com.sagi.gambling.utilities.SharedPreferencesHelper;
import com.sagi.gambling.utilities.constant.GeneralConstants;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by User on 22/02/2019.
 */

public class AdapterGroups extends RecyclerView.Adapter<AdapterGroups.PlaceHolder> {

    private List<Group> groups;
    private LayoutInflater layoutInflater;
    private Context context;
    private CallBackAdapterGroups mListener;
    private boolean isRequestScreen;


    public AdapterGroups(List<Group> groups, Context context, boolean isRequestScreen, CallBackAdapterGroups callBackAdapterGroups) {
        this.groups = groups;
        this.mListener = callBackAdapterGroups;
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.isRequestScreen = isRequestScreen;
    }


    @Override
    public void onBindViewHolder(final PlaceHolder holder, int position) {

        final Group group = groups.get(position);

        holder.imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int[] arrHideItemsByCriterion = new int[]{R.id.itemAddFriend};
                new HandleMenu(context, view, R.menu.menu_group, arrHideItemsByCriterion, new HandleMenu.CallbackMenuHandler() {
                    @Override
                    public void onItemClick(int itemId) {
                        switch (itemId) {
                            case R.id.itemAddFriend:
                                addFriend(group);
                                break;
                            case R.id.itemListGroupFriends:
                                showListGroupFriends(group);
                            break;
                        }
                    }

                    @Override
                    public boolean handleCriterionToHide() {
                        boolean isManager = group.getUserManagerKey().equals(SharedPreferencesHelper.getInstance(context).getUser().textEmailForFirebase());
                        boolean isItemAddFriendNeedToHide = !isManager && group.getStatus().equals(GeneralConstants.STATUS_PRIVATE);
                        if(isRequestScreen)
                            isItemAddFriendNeedToHide=true;

                        return isItemAddFriendNeedToHide;
                    }
                });
            }
        });

        holder.txtGroupName.setText(group.getGroupName());
        holder.txtOpenDate.setText(group.getTimeAndDate());
        holder.txtStatusGroup.setText(group.getStatus().toLowerCase().replace("_", " "));
        holder.txtNumPeople.setText(group.getCountUsers() + "/256");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onClickItem(group);
            }
        });
        holder.txtGroupGamble.setVisibility(isRequestScreen ? View.VISIBLE : View.INVISIBLE);
        holder.txtGroupGamble.setText("Entry amount: "+group.getEntryAmount()+"$");
        holder.relativeLayoutButtons.setVisibility(isRequestScreen ? View.VISIBLE : View.INVISIBLE);
        holder.relativeLayoutLeaveGroup.setVisibility(isRequestScreen ? View.INVISIBLE : View.VISIBLE);
        holder.btnJoinGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (group.loadMyGroupGamble() == null) {
                    Toast.makeText(context, "Choose your gamble", Toast.LENGTH_SHORT).show();
                    return;
                }
                mListener.joinGroup(group);
            }
        });
        holder.btnIgnoreGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.removeFromRequestGroup(group);
            }
        });
        holder.btnLeaveGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.removeFromGroup(group);
            }
        });

        new DownloadImage(Patch.GROUPS_PROFILES, group.getGroupKey(), new DownloadImage.IDownloadImage() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(context).load(uri).fit().into(holder.imgGroupProfile);
            }

            @Override
            public void onFail(String error) {
                Log.e("DownloadImage", error);

            }
        }).startLoading();

        holder.txtRatio1.setBackgroundResource(0);
        holder.txtRatio2.setBackgroundResource(0);
        holder.txtRatioTie.setBackgroundResource(0);
        holder.txtRatio1.setText(group.getGroupName1());
        holder.txtRatio2.setText(group.getGroupName2());
        holder.txtRatioTie.setText(GeneralConstants.TIE);
        if (isRequestScreen) {
            holder.txtRatio1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    group.setMyGroupGamble(group.getGroupName1());
                    handleSelectedGamble(holder, R.drawable.shape_picked_group, 0, 0);
                }
            });

            holder.txtRatioTie.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    group.setMyGroupGamble(GeneralConstants.TIE);
                    handleSelectedGamble(holder, 0, 0, R.drawable.shape_picked_group);
                }
            });

            holder.txtRatio2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    group.setMyGroupGamble(group.getGroupName2());
                    handleSelectedGamble(holder, 0, R.drawable.shape_picked_group, 0);
                }
            });
        }

        if (group.loadMyGroupGamble() == null) {
            return;
        } else if (group.loadMyGroupGamble().equals(group.getGroupName1())) {
            handleBackgroundChoice(holder.txtRatio1);
        } else if (group.loadMyGroupGamble().equals(group.getGroupName2())) {
            handleBackgroundChoice(holder.txtRatio2);
        } else {
            handleBackgroundChoice(holder.txtRatioTie);
        }
    }

    private void showListGroupFriends(Group group) {
        mListener.showListGroupFriends(group);
    }

    private void addFriend(Group group) {
        mListener.addFriend(group);
    }

    private void handleSelectedGamble(PlaceHolder holder, int restRatio1, int restRatio2, int restRatioTie) {
        holder.txtRatio1.setBackgroundResource(restRatio1);
        holder.txtRatio2.setBackgroundResource(restRatio2);
        holder.txtRatioTie.setBackgroundResource(restRatioTie);
    }

    private void handleBackgroundChoice(TextView textView) {
        textView.setBackgroundResource(R.drawable.shape_picked_group);
    }


    @Override
    public PlaceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.item_group, parent, false);
        return new PlaceHolder(itemView);
    }

    public class PlaceHolder extends RecyclerView.ViewHolder {

        private TextView txtGroupName, txtOpenDate, txtStatusGroup, txtNumPeople, txtRatio1, txtRatioTie, txtRatio2,txtGroupGamble;
        private Button btnJoinGroup, btnIgnoreGroup, btnLeaveGroup;
        private ImageView imgGroupProfile, imgMenu;
        private RelativeLayout relativeLayoutButtons, relativeLayoutLeaveGroup;

        public PlaceHolder(View view) {
            super(view);
            txtRatio1 = view.findViewById(R.id.txtRatio1);
            txtRatioTie = view.findViewById(R.id.txtRatioTie);
            txtRatio2 = view.findViewById(R.id.txtRatio2);
            txtGroupName = view.findViewById(R.id.txtGroupName);
            txtOpenDate = view.findViewById(R.id.txtOpenDate);
            txtStatusGroup = view.findViewById(R.id.txtStatusGroup);
            btnJoinGroup = view.findViewById(R.id.btnJoinGroup);
            btnLeaveGroup = view.findViewById(R.id.btnLeaveGroup);
            btnIgnoreGroup = view.findViewById(R.id.btnIgnoreGroup);
            imgGroupProfile = view.findViewById(R.id.imgGroupProfile);
            txtNumPeople = view.findViewById(R.id.txtNumPeople);
            txtGroupGamble = view.findViewById(R.id.txtGroupGamble);
            relativeLayoutButtons = view.findViewById(R.id.relativeLayoutButtons);
            relativeLayoutLeaveGroup = view.findViewById(R.id.relativeLayoutLeaveGroup);
            imgMenu = view.findViewById(R.id.imgMenu);
        }
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public interface CallBackAdapterGroups {

        void joinGroup(Group group);

        void removeFromRequestGroup(Group group);

        void onClickItem(Group group);

        void removeFromGroup(Group group);

        void addFriend(Group group);

        void showListGroupFriends(Group group);
    }
}
