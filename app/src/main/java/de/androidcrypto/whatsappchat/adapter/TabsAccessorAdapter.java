package de.androidcrypto.whatsappchat.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import de.androidcrypto.whatsappchat.fragment.ChatsFragment;
import de.androidcrypto.whatsappchat.fragment.ContactsFragment;
import de.androidcrypto.whatsappchat.fragment.GroupsFragment;
import de.androidcrypto.whatsappchat.fragment.RequestsFragment;

// todo deprecated FragmentPagerAdapter
public class TabsAccessorAdapter extends FragmentPagerAdapter {

    public TabsAccessorAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position)
        {
            case 0:return new ChatsFragment();
            case 1:return new ContactsFragment();
            case 2:return new RequestsFragment();
            case 3:return new GroupsFragment();
        }
        return null;
    }

    @Override
    //public int getCount() {return 3;}
    public int getCount() {return 4;}

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position)
        {
            case 0:return "Chats";
            case 1:return "Contacts";
            case 2:return "Requests";
            case 3:return "Groups";
        }
        return null;
    }
}
