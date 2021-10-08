package com.iuh.stream.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.iuh.stream.R;
import com.iuh.stream.adapter.ContactFragmentAdapter;


public class ContactFragment extends Fragment {

    private View view;
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private ContactFragmentAdapter contactFragmentAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_contact, container, false);
        addControls();
        return view;
    }

    private void addControls() {
        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager2 = view.findViewById(R.id.pager);
        contactFragmentAdapter = new ContactFragmentAdapter(this.getActivity());
        viewPager2.setAdapter(contactFragmentAdapter);
        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position){
                    case 0:
                        tab.setText("Danh dạ");
                        break;
                    case 1:
                        tab.setText("Nhóm");
                        break;
                }
            }
        }).attach();
    }
}