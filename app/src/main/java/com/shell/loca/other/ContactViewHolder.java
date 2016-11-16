package com.shell.loca.other;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.shell.loca.R;

/**
 * Created by shell on 16/11/16.
 */

public abstract class ContactViewHolder extends RecyclerView.ViewHolder {

    public TextView mTextViewName, mTextViewMobileNo;

    public ContactViewHolder(View itemView) {
        super(itemView);
        mTextViewName = (TextView) itemView.findViewById(R.id.listItemContactName);
        mTextViewMobileNo = (TextView) itemView.findViewById(R.id.listItemMobileNo);
    }

    abstract void onDoneChanged(boolean isDone);
}
