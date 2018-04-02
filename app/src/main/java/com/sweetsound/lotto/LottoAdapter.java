package com.sweetsound.lotto;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ljeongseok on 2018. 3. 30..
 */

public class LottoAdapter extends RecyclerView.Adapter<LottoAdapter.ItemViewHolder> {
    private ArrayList<NLottoConn.NLottoResponse> mListItem;

    public LottoAdapter() {
    }

    @Override
    public LottoAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View listitem = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ItemViewHolder(listitem);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        holder.setData(mListItem.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mListItem == null ? 0 : mListItem.size();
    }

    public void setItem(ArrayList<NLottoConn.NLottoResponse> listItem) {
        mListItem = listItem;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView mItemTextView;

        public ItemViewHolder(View itemView) {
            super(itemView);

            mItemTextView = (TextView) itemView.findViewById(R.id.item_textview);
        }

        public void setData(NLottoConn.NLottoResponse itemData, int position) {
            if (itemData.drwNo == null) {
                mItemTextView.setText("Loading....");
            } else {
                mItemTextView.setText(itemData.toString());
            }
        }
    }
}
