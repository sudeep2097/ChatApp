package com.example.whtas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;




import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;


public class Main2Activity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private TabAccessorAdapter myTabsAccessorAdapter;

    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private String currentUserID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


                mAuth = FirebaseAuth.getInstance();
                currentUser = mAuth.getCurrentUser();
                currentUserID = mAuth.getCurrentUser().getUid();
                RootRef = FirebaseDatabase.getInstance().getReference();


              mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
               setSupportActionBar(mToolbar);
                getSupportActionBar().setTitle("");


               myViewPager = (ViewPager) findViewById(R.id.main_tabs_pager);
                myTabsAccessorAdapter = new TabAccessorAdapter(getSupportFragmentManager());
                myViewPager.setAdapter(myTabsAccessorAdapter);


                myTabLayout = (TabLayout) findViewById(R.id.main_tabs);
                myTabLayout.setupWithViewPager(myViewPager);
            }


            @Override
            protected void onStart()
            {
                super.onStart();

                if (currentUser == null)
                {
                    SendUserToLoginActivity();
                }
                else
                {
                    updateUserStatus("online");

                    VerifyUserExistance();
                }
            }


            @Override
            protected void onStop()
            {
                super.onStop();

                if (currentUser != null)
                {
                    updateUserStatus("offline");
                }
            }



            @Override
            protected void onDestroy()
            {
                super.onDestroy();

                if (currentUser != null)
                {
                    updateUserStatus("offline");
                }
            }



            private void VerifyUserExistance()
            {
                String currentUserID = mAuth.getCurrentUser().getUid();

                RootRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if ((dataSnapshot.child("name").exists()))
                        {
                            //Toast.makeText(Main2Activity.this, "Welcome", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            SendUserToSettingsActivity();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }



            @Override
            public boolean onCreateOptionsMenu(Menu menu)
            {
                super.onCreateOptionsMenu(menu);

                getMenuInflater().inflate(R.menu.options_menu, menu);

                return true;
            }


            @Override
            public boolean onOptionsItemSelected(MenuItem item)
            {
                super.onOptionsItemSelected(item);

                if (item.getItemId() == R.id.main_logout_options)
                {
                    updateUserStatus("offline");
                    mAuth.signOut();
                    SendUserToLoginActivity();
                }
                if (item.getItemId() == R.id.main_settings_options)
                {
                    SendUserToSettingsActivity();
                }
                if (item.getItemId() == R.id.main_create_groups_options)
                {
                    RequestNewGroup();
                }
                if (item.getItemId() == R.id.main_find_friends_options)
                {
                    SendUserToFindFriendsActivity();
                }

                return true;
            }


            private void RequestNewGroup()
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(Main2Activity.this, R.style.AlertDialog);
                builder.setTitle("Enter Group Name :");

                final EditText groupNameField = new EditText(Main2Activity.this);
                groupNameField.setHint("group");
                builder.setView(groupNameField);

                builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        String groupName = groupNameField.getText().toString();

                        if (TextUtils.isEmpty(groupName))
                        {
                            Toast.makeText(Main2Activity.this, "Please write Group Name...", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            CreateNewGroup(groupName);
                        }
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        dialogInterface.cancel();
                    }
                });

                builder.show();
            }



            private void CreateNewGroup(final String groupName)
            {
                RootRef.child("Groups").child(groupName).setValue("")
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if (task.isSuccessful())
                                {
                                    Toast.makeText(Main2Activity.this, groupName + " group is Created Successfully...", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }



            private void SendUserToLoginActivity()
            {
                Intent loginIntent = new Intent(Main2Activity.this, LoginActivity.class);
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(loginIntent);

            }

            private void SendUserToSettingsActivity()
            {
                Intent settingsIntent = new Intent(Main2Activity.this, SettingActivity.class);
                startActivity(settingsIntent);
            }


            private void SendUserToFindFriendsActivity()
            {
                Intent findFriendsIntent = new Intent(Main2Activity.this, FindFriendsActivity.class);
                startActivity(findFriendsIntent);
            }



            private void updateUserStatus(String state)
            {
                String saveCurrentTime, saveCurrentDate;

                Calendar calendar = Calendar.getInstance();

                SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
                saveCurrentDate = currentDate.format(calendar.getTime());

                SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
                saveCurrentTime = currentTime.format(calendar.getTime());

                HashMap<String, Object> onlineStateMap = new HashMap<>();
                onlineStateMap.put("time", saveCurrentTime);
                onlineStateMap.put("date", saveCurrentDate);
                onlineStateMap.put("state", state);

                RootRef.child("Users").child(currentUserID).child("userState")
                        .updateChildren(onlineStateMap);

            }
        }
