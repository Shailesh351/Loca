package com.shell.loca.other;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shell.loca.R;

import java.util.List;

/**
 * Created by shell on 11/11/16.
 */

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactViewHolder> {

    private List<Contact> mContactsList;

    public  class ContactViewHolder extends RecyclerView.ViewHolder{
        TextView mTextViewName, mTextViewMobileNo;

        public ContactViewHolder(View itemView) {
            super(itemView);
            mTextViewName = (TextView) itemView.findViewById(R.id.listItemContactName);
            mTextViewMobileNo = (TextView) itemView.findViewById(R.id.listItemMobileNo);
        }
    }

    public ContactsAdapter(List<Contact> mContactsList) {
        this.mContactsList = mContactsList;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_list_item, parent, false);

        return new ContactViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        Contact contact = mContactsList.get(position);
        holder.mTextViewName.setText(contact.getName());
        holder.mTextViewMobileNo.setText(contact.getMobileNo());
    }

    @Override
    public int getItemCount() {
        return mContactsList.size();
    }
}
