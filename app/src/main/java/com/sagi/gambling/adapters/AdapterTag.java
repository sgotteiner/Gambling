package com.sagi.gambling.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sagi.gambling.R;


import java.util.ArrayList;
import java.util.Map;

public class AdapterTag extends RecyclerView.Adapter<AdapterTag.PlaceHolder> {

    private ArrayList<Map.Entry<String, String>> entryArrayListTags;
    private LayoutInflater layoutInflater;
    private Context context;
    private CallbackAdapterTag mListener;


    public AdapterTag(ArrayList<Map.Entry<String, String>> entries, Context context, CallbackAdapterTag callbackAdapterTag) {
        this.entryArrayListTags = entries;
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.mListener = callbackAdapterTag;
    }

    public class PlaceHolder extends RecyclerView.ViewHolder {

        private TextView txtName, txtEmail;
        private ImageView imgRemove;


        public PlaceHolder(View view) {
            super(view);
            txtName = view.findViewById(R.id.txtName);
            txtEmail = view.findViewById(R.id.txtEmail);
            imgRemove = view.findViewById(R.id.imgRemove);
        }
    }


    @Override
    public AdapterTag.PlaceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.item_tag, parent, false);

        return new AdapterTag.PlaceHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final AdapterTag.PlaceHolder holder, final int position) {

        final Map.Entry<String, String> entry = entryArrayListTags.get(position);

        holder.txtName.setText(entry.getKey());
        holder.txtEmail.setText(entry.getValue());
        holder.imgRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onRemoveClick(entry);
            }
        });
    }


    @Override
    public int getItemCount() {
        return entryArrayListTags.size();
    }

    public interface CallbackAdapterTag {

        void onRemoveClick(Map.Entry<String, String> entry);
    }
}
