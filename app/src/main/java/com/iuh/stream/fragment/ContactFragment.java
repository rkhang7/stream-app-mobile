package com.iuh.stream.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.iuh.stream.R;
import com.iuh.stream.activity.AddFriendActivity;
import com.iuh.stream.adapter.MyFragmentAdapter;


public class ContactFragment extends Fragment {
    // views
    private View view;
    private ImageButton addFiendBtn;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_contact, container, false);
        addControls();
        addEvents();
        return view;
    }

    private void addEvents() {
        addFiendBtn.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), AddFriendActivity.class);
            startActivity(intent);
        });
    }

    private void addControls() {
        // init views
        TabLayout tabLayout = view.findViewById(R.id.contacts_tab_layout);
        ViewPager2 viewPager2 = view.findViewById(R.id.contacts_pager2);
        MyFragmentAdapter myFragmentAdapter = new MyFragmentAdapter(getActivity());
        myFragmentAdapter.addFragment(new PersonalContactFragment());
        myFragmentAdapter.addFragment(new GroupContactFragment());
        viewPager2.setAdapter(myFragmentAdapter);
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            switch (position){
                case 0:
                    tab.setText("Danh bạ");
                    break;
                case 1:
                    tab.setText("Nhóm");
                    break;
            }
        }).attach();
        addFiendBtn = view.findViewById(R.id.add_friend_btn);

    }


}