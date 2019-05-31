package com.sagi.gambling.adapters;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sagi.gambling.R;
import com.sagi.gambling.entities.Gamble;
import com.sagi.gambling.entities.Game;
import com.sagi.gambling.entities.User;
import com.sagi.gambling.utilities.DownloadImage;
import com.sagi.gambling.utilities.HandleMenu;
import com.sagi.gambling.utilities.Patch;
import com.sagi.gambling.utilities.SharedPreferencesHelper;
import com.sagi.gambling.utilities.Utils;
import com.sagi.gambling.utilities.constant.GeneralConstants;
import com.squareup.picasso.Picasso;

import java.util.List;


public class AdapterGames extends RecyclerView.Adapter<AdapterGames.PlaceHolder> {

    private List<Game> games;
    private LayoutInflater layoutInflater;
    private CallBackAdapterGame mListener;
    private Context context;
    private String groupName = "";
    private int maxMoney;
    private boolean isManagerApp;


    public AdapterGames(List<Game> games, Context context, CallBackAdapterGame callBackAdapterGame) {
        this.games = games;
        this.context = context;
        maxMoney = SharedPreferencesHelper.getInstance(context).getUser().getTotalMoney();
        this.layoutInflater = LayoutInflater.from(context);
        mListener = callBackAdapterGame;
        isManagerApp = SharedPreferencesHelper.getInstance(context).getUser().isManagerApp();
    }


    public class PlaceHolder extends RecyclerView.ViewHolder {

        private TextView txtGameDate, txtChanceGroup1, txtChanceGroup2, txtTitleGame;
        private ImageView imgGroup1, imgGroup2, imgMenu;
        private Button btnBet;

        public PlaceHolder(View view) {
            super(view);

            txtTitleGame = view.findViewById(R.id.txtTitleGame);
            txtGameDate = view.findViewById(R.id.txtGameDate);
            txtChanceGroup1 = view.findViewById(R.id.txtChanceGroup1);
            txtChanceGroup2 = view.findViewById(R.id.txtChanceGroup2);
            imgGroup1 = view.findViewById(R.id.imgGroup1);
            imgGroup2 = view.findViewById(R.id.imgGroup2);
            imgMenu = view.findViewById(R.id.imgMenu);
            btnBet = view.findViewById(R.id.btnBet);
        }
    }


    @Override
    public PlaceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.item_game, parent, false);

        return new PlaceHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PlaceHolder holder, final int position) {

        final Game game = games.get(position);

        holder.txtTitleGame.setText(Utils.geteFirstLattersUpperCase(game.getGroup1name()) + " Vs " + Utils.geteFirstLattersUpperCase(game.getGroup2name()));
        holder.txtChanceGroup1.setText(Math.round(game.getChanceWinGroup1()) + "%");
        holder.txtChanceGroup2.setText(Math.round(100 - game.getChanceWinGroup1()) + "%");
        holder.txtGameDate.setText(game.getTimeAndDate());
        holder.imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int[] arrHideItemsByCriterion = new int[]{R.id.itemDelete, R.id.itemEdit};
                new HandleMenu(context, view, R.menu.menu_game, arrHideItemsByCriterion, new HandleMenu.CallbackMenuHandler() {
                    @Override
                    public void onItemClick(int itemId) {
                        switch (itemId) {
                            case R.id.itemDelete:
                                deleteItem(game);
                                break;
                            case R.id.itemEdit:
                                editGame(game);
                                break;
                            case R.id.itemChat:
                                mListener.showChatScreen(game);
                                break;
                            case R.id.itemCreateGroup:
                                mListener.createGroup(game);
                                break;
                        }
                    }
                    @Override
                    public boolean handleCriterionToHide() {
                        return !isManagerApp;
                    }
                });
            }
        });
        holder.btnBet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (game.getStatus().equals(GeneralConstants.STATUS_ACTIVE))
                    showBetGameDialog(game);
                else
                    Toast.makeText(context, "Game is not active", Toast.LENGTH_SHORT).show();
            }
        });

        new DownloadImage(Patch.GROUPS_RESOURCES, game.getGroup1name(), new DownloadImage.IDownloadImage() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(context).load(uri).fit().into(holder.imgGroup1);
            }

            @Override
            public void onFail(String error) {

            }
        }).startLoading();
        new DownloadImage(Patch.GROUPS_RESOURCES, game.getGroup2name(), new DownloadImage.IDownloadImage() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(context).load(uri).fit().into(holder.imgGroup2);
            }

            @Override
            public void onFail(String error) {

            }
        }).startLoading();
    }

    private void editGame(Game game) {
        showEditGameDialog(game);
    }

    private void showEditGameDialog(Game game) {
        mListener.showEditGameDialog(game);
    }

    private void deleteItem(Game game) {
        mListener.deleteGame(game);
        games.remove(game);
        notifyDataSetChanged();
    }

    private void showBetGameDialog(final Game game) {

        final Dialog addGameDialog = new Dialog(context);
        addGameDialog.setContentView(R.layout.dialog_bet_game);
        TextView txtDescription = addGameDialog.findViewById(R.id.txtDescription);
        txtDescription.setText(game.getDescription());

        final TextView txtRatio1 = addGameDialog.findViewById(R.id.txtRatio1);
        final TextView txtRatioTie = addGameDialog.findViewById(R.id.txtRatioTie);
        final TextView txtRatio2 = addGameDialog.findViewById(R.id.txtRatio2);
        TextView txtTitleGroup1 = addGameDialog.findViewById(R.id.txtTitleGroup1);
        TextView txtTitleGroup2 = addGameDialog.findViewById(R.id.txtTitleGroup2);
        txtTitleGroup1.setText(game.getGroup1name());
        txtTitleGroup2.setText(game.getGroup2name());
        double ratio1;
        double ratio2;

        if (game.getChanceWinGroup1() < 50) {
            ratio1 = 2 - Double.valueOf(game.getChanceWinGroup1()) / 100.0;
            ratio2 = 2 - Double.valueOf((100 - game.getChanceWinGroup1())) / 100;
        } else {
            ratio2 = 2 - Double.valueOf(game.getChanceWinGroup1()) / 100;
            ratio1 = 2 - Double.valueOf((100 - game.getChanceWinGroup1())) / 100;
        }


        if (game.getCategoryName().equals(GeneralConstants.SOCCER)) {
            double ratioTie = 1.5;
            txtRatioTie.setText("Tie\n x" + Math.floor(ratioTie * 100) / 100);
        }
        txtRatio1.setText(game.getGroup1name() + "\n x" + Math.floor(ratio1 * 100) / 100);
        txtRatio2.setText(game.getGroup2name() + "\n x" + Math.floor(ratio2 * 100) / 100);

        txtRatio1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                groupName = game.getGroup1name();
                txtRatio1.setBackgroundResource(R.drawable.shape_picked_group);
                txtRatioTie.setBackgroundResource(0);
                txtRatio2.setBackgroundResource(0);
            }
        });

        txtRatioTie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                groupName = GeneralConstants.TIE;
                txtRatio1.setBackgroundResource(0);
                txtRatioTie.setBackgroundResource(R.drawable.shape_picked_group);
                txtRatio2.setBackgroundResource(0);
            }
        });

        txtRatio2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                groupName = game.getGroup2name();
                txtRatio1.setBackgroundResource(0);
                txtRatioTie.setBackgroundResource(0);
                txtRatio2.setBackgroundResource(R.drawable.shape_picked_group);
            }
        });

        final TextView txtMoney = addGameDialog.findViewById(R.id.txtMoney);
        txtMoney.setText(0 + "$");

        final SeekBar seekBarMoney = addGameDialog.findViewById(R.id.seekBarMoney);
        seekBarMoney.setMax(maxMoney);
        seekBarMoney.setProgress(0);
        seekBarMoney.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                txtMoney.setText(i + "$");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        Button btnCancel = addGameDialog.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addGameDialog.dismiss();
            }
        });

        Button btnSave = addGameDialog.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user = SharedPreferencesHelper.getInstance(context).getUser();
                Gamble gamble = new Gamble(game.getKey(), user.getEmail(), groupName, seekBarMoney.getProgress(), System.currentTimeMillis());
                if (checkIsValidGamble(gamble)) {
                    user.removeMoney(gamble.getGambleMoney());
                    SharedPreferencesHelper.getInstance(context).setUser(user);
                    mListener.updateTotalMoney(user);
                    mListener.insertGambleToFirebase(gamble);
                    addGameDialog.dismiss();
                }
            }
        });

        addGameDialog.setCancelable(false);
        addGameDialog.show();
    }

    private boolean checkIsValidGamble(Gamble gamble) {
        boolean isValid = true;
        if (gamble.getGroupNameSelected().isEmpty()) {
            isValid = false;
            Toast.makeText(context, "must choose a group", Toast.LENGTH_SHORT).show();
        }
        if (gamble.getGambleMoney() == 0) {
            isValid = false;
            Toast.makeText(context, "must choose the amount of money", Toast.LENGTH_SHORT).show();
        }
        return isValid;
    }


    public interface CallBackAdapterGame {

        void insertGambleToFirebase(Gamble gamble);

        void deleteGame(Game game);

        void showEditGameDialog(Game game);

        void updateTotalMoney(User user);

        void showChatScreen(Game game);

        void createGroup(Game game);
    }

    @Override
    public int getItemCount() {
        return games.size();
    }
}
