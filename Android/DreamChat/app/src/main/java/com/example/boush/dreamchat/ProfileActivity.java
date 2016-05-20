package com.example.boush.dreamchat;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class ProfileActivity extends AppCompatActivity {

    private Button sendReq;
    private Button removeFriend;
    private Button sendMessage;

    private ImageView imageView;
    private TextView name;
    private TextView nickname;
    private TextView phone;
    private TextView email;

    private boolean isFriend;

    private String firstName;
    private String lastName;
    private String title;

    public ProfileActivity() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.activity_profile);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras != null) {
                firstName=extras.getString("firstName");
                lastName=extras.getString("lastName");
                title = firstName+" "+lastName;
                isFriend=extras.getBoolean("isFriend");
                Log.d("boolean friend", String.valueOf(isFriend));
            }
        }

        if (isFriend){
            setContentView(R.layout.activity_profile_friend);
            sendMessage = (Button) findViewById(R.id.sendMessage);
            removeFriend = (Button) findViewById(R.id.removeFriend);

            imageView = (ImageView) findViewById(R.id.profilePic);
            nickname = (TextView) findViewById(R.id.contactNickname);
            email = (TextView) findViewById(R.id.contactEmail);
            phone = (TextView) findViewById(R.id.contactPhone);
            name = (TextView) findViewById(R.id.contactName);

            name.setText(title);

            sendMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                    intent.putExtra("firstName", firstName);
                    intent.putExtra("lastName", lastName);
                    //intent.putExtra("message", message.getMessageText());
                    //intent.putExtra("date", message.getDate());
                    startActivity(intent);
                }
            });

            removeFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), title +" removed", Toast.LENGTH_SHORT).show();
                    //removeFriend();
                }
            });


        }
        else{
            setContentView(R.layout.activity_profile);

            imageView = (ImageView) findViewById(R.id.profilePic);
            nickname = (TextView) findViewById(R.id.contactNickname);
            email = (TextView) findViewById(R.id.contactEmail);
            phone = (TextView) findViewById(R.id.contactPhone);
            name = (TextView) findViewById(R.id.contactName);

            name.setText(title);
            sendReq = (Button) findViewById(R.id.sendMessage);

            sendReq.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "Request to "+ title +" sent", Toast.LENGTH_SHORT).show();
                    //sendRequest();
                }
            });

        }

    }
}