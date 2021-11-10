package com.iuh.stream.fragment;


import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.iuh.stream.R;
import com.iuh.stream.activity.AddGroupActivity;
import com.iuh.stream.activity.SearchActivity;


public class ChatFragment extends Fragment {
    private LinearLayout searchLayout;
    private TextView searchTv;
    private ImageButton addGroupBtn;
    private View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_chat, container, false);

        addControls();
        addEvents();

     

        return view;
    }

    private void addEvents() {
        addGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AddGroupActivity.class));
            }
        });
    }

    private void addControls() {
        searchLayout = view.findViewById(R.id.search_layout);
        searchTv = view.findViewById(R.id.search_tv);
        addGroupBtn = view.findViewById(R.id.add_group_btn);
    }

}