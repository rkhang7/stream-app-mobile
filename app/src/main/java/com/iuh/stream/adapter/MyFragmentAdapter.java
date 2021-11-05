package com.iuh.stream.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.iuh.stream.fragment.GroupContactFragment;
import com.iuh.stream.fragment.PersonalContactFragment;

import java.util.ArrayList;
import java.util.List;

public class MyFragmentAdapter extends FragmentStateAdapter {

    private List<Fragment> fragmentList;

    public MyFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        fragmentList = new ArrayList<>();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
       return fragmentList.get(position);
    }

    public void addFragment(Fragment fragment){
        fragmentList.add(fragment);
    }

    @Override
    public int getItemCount() {
        return fragmentList.size();
    }




}
