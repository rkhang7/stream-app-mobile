package com.iuh.stream.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.iuh.stream.fragment.GroupContactFragment;
import com.iuh.stream.fragment.PersonalContactFragment;

public class ContactFragmentAdapter extends FragmentStateAdapter {
    public ContactFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position == 1){
            return new GroupContactFragment();
        }
        return new PersonalContactFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
