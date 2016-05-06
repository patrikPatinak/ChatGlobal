package com.example.boush.dreamchat;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

public class ChatActivity extends AppCompatActivity {

    ViewPager pager;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        pager = (ViewPager) findViewById(R.id.view_pager);
        tabLayout= (TabLayout) findViewById(R.id.tab_layout);

        final TabLayout.Tab me = tabLayout.newTab();
        final TabLayout.Tab conversations = tabLayout.newTab();
        final TabLayout.Tab friends = tabLayout.newTab();

        me.setText(getString(R.string.tab_profile));
        conversations.setText((getString(R.string.tab_conversations)));
        friends.setText((getString(R.string.tab_friends)));

        tabLayout.addTab(me, 0);
        tabLayout.addTab(conversations, 1);
        tabLayout.addTab(friends, 2);

        FragmentManager manager=getSupportFragmentManager();
        com.example.boush.dreamchat.PagerAdapter adapter = new com.example.boush.dreamchat.PagerAdapter(manager) ;
        pager.setAdapter(adapter);

        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }
}