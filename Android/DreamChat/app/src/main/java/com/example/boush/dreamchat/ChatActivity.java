package com.example.boush.dreamchat;

import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ChatActivity extends ListActivity {
    private String firstName;
    private String lastName;
    private String messageText;
    private String date;
    private Contact contact;

    private int conversationId;
    private int myId;
    private int recId;

    private TextView txtName;
    private EditText etxtSendMsg;
    private ImageButton send;
    private ImageButton info;
    private ImageButton sendPhoto;

    private List<Message> messagesList = new ArrayList<>();
    private MessageAdapter mAdapter;
    private Calendar c = Calendar.getInstance();
    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
    private Context context;
    private Database db;

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://10.0.2.2:8090");
        } catch (URISyntaxException e) {}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras != null) {
                if (getIntent().hasExtra(Constants.KEY_CONTACT)){
                    contact = (Contact) extras.getParcelable(Constants.KEY_CONTACT);
                    firstName = contact.getFirstName();
                    lastName = contact.getLastName();
                    recId = contact.getUserid();
                }
                else{
                    firstName=extras.getString("firstName");
                    lastName=extras.getString("lastName");
                    messageText=extras.getString("message");
                    date=extras.getString("date");
                    recId = extras.getInt("recId");
                    conversationId = extras.getInt("conversationId");
                }
            }
        }
        else{
            contact= savedInstanceState.getParcelable(Constants.KEY_CONTACT);
        }
        setContentView(R.layout.activity_chat);

        db = new Database(ChatActivity.this);
        date = sdf.format(c.getTime());

        initChat();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        myId = prefs.getInt(Constants.KEY_USERID, 0);
        context= getApplicationContext();

        mSocket.on("message", onNewMessage); //listener
        mSocket.connect();
        mSocket.emit("connect user", myId); //connect - userid
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(Constants.KEY_CONTACT,contact);
        outState.putString(Constants.KEY_NAME,firstName);
        outState.putString(Constants.KEY_SURNAME,lastName);
        outState.putInt(Constants.KEY_RECEIVER,recId);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        contact= savedInstanceState.getParcelable(Constants.KEY_CONTACT);
        firstName = savedInstanceState.getString(Constants.KEY_NAME);
        lastName = savedInstanceState.getString(Constants.KEY_SURNAME);
        recId = savedInstanceState.getInt(Constants.KEY_RECEIVER);
    }



    public void initChat(){

        txtName = (TextView) findViewById(R.id.txtName);
        etxtSendMsg = (EditText) findViewById(R.id.etxtSendMsg);

        runFetchMessages();
//        messagesList = db.getHistory(conversationId);

        mAdapter = new MessageAdapter(this, messagesList,myId);
        setListAdapter(mAdapter);


        txtName.setText(firstName+" "+lastName);

        send = (ImageButton) findViewById(R.id.btn_sendMessage);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        info = (ImageButton) findViewById(R.id.btn_Info);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "onClick: INFO BUTTON WAS CLICKED.");
            }
        });

        sendPhoto = (ImageButton) findViewById(R.id.btn_sendPhoto);
        sendPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "onClick: SEND PHOTO BUTTON WAS CLICKED.");
            }
        });
    }

    public void sendMessage(){

        String messageText = etxtSendMsg.getText().toString();

        if(!messageText.isEmpty()) {
            Message msg = new Message();
            msg.setMessageText(messageText);
            msg.setMe(true);
            msg.setRecId(recId);
            msg.setMyId(myId);
            msg.setDate(date);
            msg.setFirstName(firstName);
            msg.setLastName(lastName);
            messagesList.add(msg);

            JSONObject object = new JSONObject();
            try {
                object.put("user1", myId); //sender id
                object.put("user2", recId); //receiver id
                object.put("message", messageText);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mSocket.emit("send message", object);

            mAdapter.notifyDataSetChanged();
//            db.addMessage(msg, conversationId);
        }

        etxtSendMsg.setText("");
    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ChatActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String sender; //sender id
                    String message;
                    try {
                        sender = data.getString("sender");
                        message = data.getString("msg");
                    } catch (JSONException e) {
                        return;
                    }

                    addMessage(message);
                }
            });
        }
    };

    private void addMessage(String message) {
        Message msg = new Message();
        msg.setMessageText(message);
        msg.setMe(false);
        msg.setRecId(recId);
        msg.setMyId(myId);
        msg.setDate(date);
        msg.setFirstName(firstName);
        msg.setLastName(lastName);

        messagesList.add(msg);
        db.addMessage(msg, conversationId);
        mAdapter.notifyDataSetChanged();
    }

    public void runFetchMessages(){
        new FetchMessagesTask(new AsyncTaskCallback() {
            @Override
            public void onTaskCompleted(List result) {
                //nastavenie listu a adaptera
                Log.d("task", ((Message) result.get(1)).getMessageText());
            }

            @Override
            public void onTaskCompleted(Contact result) {

            }

            @Override
            public void onTaskCompleted(int result) {
                if (result==401){
                    Toast.makeText(context, "Error fetching messages - unauthorized access", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onTaskCompleted(String result) {

            }
        }).execute();
    }

    public class FetchMessagesTask extends AsyncTask<Void, Void, Void>{
        private AsyncTaskCallback listener;
        public FetchMessagesTask(AsyncTaskCallback listener){
            this.listener=listener;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.d("conversationid", String.valueOf(conversationId));
            new Server().getMessages(1, context, new VolleyCallback() {
                @Override
                public void onSuccess(JSONObject result) {

                }

                @Override
                public void onSuccess(JSONArray result) {
                    Log.d("Messages result", result.toString());
                    List<Message> messageL = new ParseJSON().getMessages(result, myId);
                    listener.onTaskCompleted(messageL);
                }

                @Override
                public void onSuccess(String result) {

                }

                @Override
                public void onSuccess(int result) {
                    listener.onTaskCompleted(result);
                }
            });
            return null;
        }
    }
}
