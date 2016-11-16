package com.shell.loca.other;

import android.content.Context;
import android.content.Intent;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.shell.loca.R;
import com.shell.loca.activity.FriendLocationActivity;
import com.shell.loca.activity.MainActivity;

/**
 * Created by shell on 11/11/16.
 */

public class ContactsAdapter extends RecyclerView.Adapter<ContactViewHolder> {

    private Context mContext;

    final LayoutInflater mLayoutInflater;
    SortedList<Contact> mContacts;

    public ContactsAdapter(Context context, LayoutInflater layoutInflater, Contact... items) {
        mContext = context;

        mLayoutInflater = layoutInflater;
        mContacts = new SortedList<Contact>(Contact.class, new SortedListAdapterCallback<Contact>(this) {
            @Override
            public int compare(Contact t0, Contact t1) {
                return t0.getName().compareTo(t1.getName());
            }

            @Override
            public boolean areContentsTheSame(Contact oldItem,
                                              Contact newItem) {
                return oldItem.getName().equals(newItem.getName());
            }

            @Override
            public boolean areItemsTheSame(Contact item1, Contact item2) {
                return item1.getMobileNo() == item2.getMobileNo();
            }
        });

        if (items != null) {
            for (Contact item : items) {
                mContacts.add(item);
            }
        }
    }

    public void addItem(Contact item) {
        mContacts.add(item);
    }

    @Override
    public ContactViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        return new ContactViewHolder(
                mLayoutInflater.inflate(R.layout.contact_list_item, parent, false)) {
            @Override
            void onDoneChanged(boolean isDone) {
                int adapterPosition = getAdapterPosition();
                if (adapterPosition == RecyclerView.NO_POSITION) {
                    return;
                }
                mContacts.recalculatePositionOfItemAt(adapterPosition);
            }
        };
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        ContactViewHolder contactViewHolder = (ContactViewHolder) holder;
        Contact contact = mContacts.get(position);
        contactViewHolder.mTextViewName.setText(contact.getName());
        contactViewHolder.mTextViewMobileNo.setText(contact.getMobileNo());
    }

    @Override
    public int getItemCount() {
        if (mContacts != null) {
            return mContacts.size();
        } else {
            return 0;
        }
    }
}

