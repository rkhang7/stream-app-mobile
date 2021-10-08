package com.iuh.stream.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.iuh.stream.R;
import com.iuh.stream.adapter.ContactAdapter;
import com.iuh.stream.api.RetrofitService;
import com.iuh.stream.models.Contact;
import com.iuh.stream.models.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhoneFriendsActivity extends AppCompatActivity {
    private List<Contact> contactList;
    private ContactAdapter contactAdapter;
    private RecyclerView recyclerView;
    private User tempUser;
    // firebase
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_friends);
        addControls();
    }

    private void addControls() {
        getSupportActionBar().setTitle("Bạn từ danh bạ máy");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // init firebase
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        // setting recyclerview
        contactList = new ArrayList<>();
        contactAdapter = new ContactAdapter(this);
        contactAdapter.setData(contactList);
        recyclerView = findViewById(R.id.contact_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(contactAdapter);

        getContactList();
    }

    private void getContactList() {
        // init uri
        Uri uri = ContactsContract.Contacts.CONTENT_URI;

        // Sort by ascending
        String sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC";

        // init cursor

        Cursor cursor = getContentResolver().query(
                uri,null,null,null, sort
        );

        // check condition
        if(cursor.getCount() > 0){
            while(cursor.moveToNext()){
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
                if(phoneCursor.moveToNext()){
                    int temp3 = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    String number = phoneCursor.getString(temp3);

                    // convert (012) 345-6711 --->> 0123456711
                    String phoneNumberConverted = "";
                    for(int i = 0; i< number.length(); i++){
                        if(i == 0 || i == 4 || i == 5 || i == 9){
                            continue;
                        }
                        phoneNumberConverted+= number.charAt(i);
                    }

                    if(phoneNumberConverted.length() == 10){
                        // init contact model



                        // get user by phone number
                        RetrofitService.getInstance.getUserByPhoneNumber(phoneNumberConverted).enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(Call<User> call, Response<User> response) {
                                tempUser = response.body();
                                if(tempUser != null){
                                    if(!tempUser.get_id().equals(mUser.getUid())){
                                        Contact contact = new Contact();
                                        contact.setPhoneNumber(number);
                                        contact.setPhoneName(name);
                                        contact.setFirstName(tempUser.getFirstName());
                                        contact.setLastName(tempUser.getFirstName());
                                        contact.setId(tempUser.get_id());
                                        contact.setAvatar(tempUser.getImageURL());
                                        contactList.add(contact);
                                    }
                                }


                                contactAdapter.setData(contactList);
                                recyclerView.setAdapter(contactAdapter);

                            }

                            @Override
                            public void onFailure(Call<User> call, Throwable t) {
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
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}