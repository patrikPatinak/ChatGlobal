package com.example.boush.dreamchat;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {


    public FriendsFragment() {
        // Required empty public constructor
    }

    Context context;
    private List<Friend> friendList = new ArrayList<>();
    private RecyclerView recyclerView;
    private FriendsAdapter mAdapter;
    private SearchView search;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        context = getActivity();

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_friends);

        mAdapter = new FriendsAdapter(friendList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));

        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(context, recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Friend friend = friendList.get(position);
                Toast.makeText(context, friend.getTitle() + " is selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        prepareFriendData();

        search = (SearchView) view.findViewById(R.id.searchView);
        search.setOnQueryTextListener(listener); // call the QuerytextListner.

        return view;
    }

    SearchView.OnQueryTextListener listener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextChange(String query) {
            query = query.toLowerCase();

            final List<Friend> filteredList = new ArrayList<>();

            for (int i = 0; i < friendList.size(); i++) {

                final String text1 = friendList.get(i).getNickname().toLowerCase();
                final String text2 = friendList.get(i).getFirstName().toLowerCase();
                final String text3 = friendList.get(i).getLastName().toLowerCase();
                if (text1.contains(query) || text2.contains(query) || text3.contains(query)) {

                    filteredList.add(friendList.get(i));
                }
            }

            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            mAdapter = new FriendsAdapter(filteredList);
            recyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();  // data set changed
            return true;

        }
        public boolean onQueryTextSubmit(String query) {
            return false;
        }
    };

    private void prepareFriendData() {
        Friend friend = new Friend("Iba", "Meliško", "Meliško", "11.5.2016");
        friendList.add(friend);

        friend = new Friend("Patrik", "Patinák", "Patres", "12.5.2016");
        friendList.add(friend);

        friend = new Friend("Martin", "Tarhanič", "Matolator", "10.5.2016");
        friendList.add(friend);

        friend = new Friend("Monika", "Jaššová", "monikka", "12.5.2016");
        friendList.add(friend);

        friend = new Friend("Michal", "Borovský", "Michaljevič", "12.5.2016");
        friendList.add(friend);

        friend = new Friend("Matúš", "Kokoška", "DreamTeam", "11.5.2016");
        friendList.add(friend);

        friend = new Friend("Roman", "Klimčík", "Global Logic", "10.4.2016");
        friendList.add(friend);

        friend = new Friend("X", "Y", "Slovensko", "12.5.2016");
        friendList.add(friend);

        friend = new Friend("Meno", "Priezvisko", "Nick", "10.5.2016");
        friendList.add(friend);

        mAdapter.notifyDataSetChanged();
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private FriendsFragment.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final FriendsFragment.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

}
