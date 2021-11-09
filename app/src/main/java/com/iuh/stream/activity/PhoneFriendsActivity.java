package com.iuh.stream.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.iuh.stream.R;
import com.iuh.stream.adapter.ContactAdapter;
import com.iuh.stream.api.RetrofitService;
import com.iuh.stream.datalocal.DataLocalManager;
import com.iuh.stream.models.Contact;
import com.iuh.stream.models.User;
import com.iuh.stream.utils.Constants;
import com.iuh.stream.utils.Util;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhoneFriendsActivity extends AppCompatActivity {
    // views
    private List<Contact> contactList;
    private ContactAdapter contactAdapter;
    private RecyclerView recyclerView;
    private EditText searchContactEt;
    private TextView notFoundTv;

    // firebase
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    private User tempUser;
    private String phoneNumberConverted = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_friends);
        addControls();
        addEvents();
    }

    private void addEvents() {
        searchContactEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filterContacts(editable.toString());
            }
        });
    }

    private void filterContacts(String key) {
        List<Contact> filterContacts = new ArrayList<>();
        for (Contact contact : contactList) {
            if (contact.getFirstName().toLowerCase().contains(key)
                    || contact.getLastName().toLowerCase().contains(key) || contact.getPhoneName().toLowerCase().contains(key)) {
                filterContacts.add(contact);
            }
        }

        if (filterContacts.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            notFoundTv.setVisibility(View.GONE);
            contactAdapter.setData(filterContacts);
        } else {
            recyclerView.setVisibility(View.GONE);
            notFoundTv.setVisibility(View.VISIBLE);
        }
    }

    private void addControls() {
        // set title
        Objects.requireNonNull(getSupportActionBar()).setTitle("Bạn từ danh bạ máy");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // init firebase
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        // init recyclerview
        contactList = new ArrayList<>();
        contactAdapter = new ContactAdapter(this);
        contactAdapter.setData(contactList);
        recyclerView = findViewById(R.id.contact_rv);


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(contactAdapter);

        // init views
        searchContactEt = findViewById(R.id.search_contact_et);
        notFoundTv = findViewById(R.id.not_found_tv);

        getContactList();

    }

    private void getContactList() {
        // init uri
        Uri uri = ContactsContract.Contacts.CONTENT_URI;

        // Sort by ascending
        String sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC";

        // init cursor

        Cursor cursor = getContentResolver().query(
                uri, null, null, null, sort
        );

        // check condition
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                tempUser = null;
                // get contact id
                int temp = cursor.getColumnIndex(ContactsContract.Contacts._ID);
                String id = cursor.getString(temp);

                // get contact name
                int temp2 = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                String name = cursor.getString(temp2);

                // init uri phone
                Uri uriPhone = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

                // init selection
                String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " =?";

                // init phone cursor
                Cursor phoneCursor = getContentResolver().query(
                        uriPhone, null, selection, new String[]{id}, null
                );

                // check condition
                if (phoneCursor.moveToNext()) {
                    int temp3 = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    String number = phoneCursor.getString(temp3);

                    // convert (012) 345-6711 --->> 0123456711
                    phoneNumberConverted = "";

                    if (number.charAt(0) == '(') {
                        for (int i = 0; i < number.length(); i++) {
                            if (i == 0 || i == 4 || i == 5 || i == 9) {
                                continue;
                            }
                            phoneNumberConverted += number.charAt(i);
                        }
                    }

                    else{
                        phoneNumberConverted = number;
                    }

//

                    if (phoneNumberConverted.length() == 10) {
                        // init contact model

                        // get user by phone number
                        RetrofitService.getInstance.getUserByPhoneNumber(phoneNumberConverted, DataLocalManager.getStringValue(Constants.ACCESS_TOKEN)).enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                                if (response.code() == 403) {
                                    String REFRESH_TOKEN = DataLocalManager.getStringValue(Constants.REFRESH_TOKEN);
                                    Util.refreshToken(REFRESH_TOKEN);
                                    RetrofitService.getInstance.getUserByPhoneNumber(phoneNumberConverted, DataLocalManager.getStringValue(Constants.ACCESS_TOKEN))
                                            .enqueue(new Callback<User>() {
                                                @Override
                                                public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                                                    tempUser = response.body();
                                                    if (tempUser != null) {
                                                        if (!tempUser.get_id().equals(mUser.getUid())) {
                                                            Contact contact = new Contact();
                                                            contact.setPhoneNumber(number);
                                                            contact.setPhoneName(name);
                                                            contact.setFirstName(tempUser.getFirstName());
                                                            contact.setLastName(tempUser.getLastName());
                                                            contact.setId(tempUser.get_id());
                                                            contact.setAvatar(tempUser.getImageURL());
                                                            contactList.add(contact);
                                                        }
                                                    }
                                                    contactAdapter.setData(contactList);
                                                    recyclerView.setAdapter(contactAdapter);
                                                }

                                                @Override
                                                public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                                                    Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    tempUser = response.body();
                                    if (tempUser != null) {
                                        if (!tempUser.get_id().equals(mUser.getUid())) {
                                            Contact contact = new Contact();
                                            contact.setPhoneNumber(number);
                                            contact.setPhoneName(name);
                                            contact.setFirstName(tempUser.getFirstName());
                                            contact.setLastName(tempUser.getLastName());
                                            contact.setId(tempUser.get_id());
                                            contact.setAvatar(tempUser.getImageURL());
                                            contactList.add(contact);
                                        }
                                    }
                                    contactAdapter.setData(contactList);
                                    recyclerView.setAdapter(contactAdapter);
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });


                    }

                    // close cursor
                    phoneCursor.close();
                }
            }

            // close cursor
            cursor.close();
        }


    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // allow back previous activity
        return super.onSupportNavigateUp();
    }
}