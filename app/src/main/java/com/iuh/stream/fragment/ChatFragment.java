package com.iuh.stream.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.iuh.stream.R;
import com.iuh.stream.activity.StartActivity;


public class ChatFragment extends Fragment {
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
        view.findViewById(R.id.btnLogout).setOnClickListener(v -> {
            //Sign out firebase account
            FirebaseAuth.getInstance().signOut();

            //SignOut google account
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString((R.string.default_client_id)))
                    .requestEmail()
                    .build();

            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
            mGoogleSignInClient.signOut();

            //Go to Start activity
            Intent intent = new Intent(getActivity(), StartActivity.class);
            getActivity().startActivity(intent);
            getActivity().finish();
        });
    }

    private void addControls() { ;
    }

}