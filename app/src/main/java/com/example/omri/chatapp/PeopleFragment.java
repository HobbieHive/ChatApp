package com.example.omri.chatapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


/**
 * A simple {@link Fragment} subclass.
 */
public class PeopleFragment extends Fragment {

    public PeopleFragment(){

    }

    public static final String USERS = "users";
    private RecyclerView peopleRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private DatabaseReference ref;
    private int index;


    public static class UserViewHolder extends RecyclerView.ViewHolder {
        public TextView userName;

        public UserViewHolder(View itemView) {
            super(itemView);
            userName = (TextView)itemView.findViewById(R.id.user_name);
        }
    }
    private FirebaseRecyclerAdapter<User,UserViewHolder> firebaseRecyclerAdapter;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_people,container,false);
        getActivity().setTitle("Find People");
        final String currentUserId= FirebaseAuth.getInstance().getCurrentUser().getUid();
        peopleRecyclerView = (RecyclerView)view.findViewById(R.id.people_recycler_view);
        linearLayoutManager= new LinearLayoutManager(getActivity());
        ref= FirebaseDatabase.getInstance().getReference();

        firebaseRecyclerAdapter= new FirebaseRecyclerAdapter<User, UserViewHolder>(
                User.class,
                R.layout.user_template,
                UserViewHolder.class,
                ref.child(USERS)) {
            @Override
            protected void populateViewHolder(UserViewHolder viewHolder, final User model, final int position) {

                if( !firebaseRecyclerAdapter.getRef(position).getKey().equals(currentUserId)) {
                    viewHolder.userName.setText(model.getName());
                    viewHolder.userName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((Communicate) getActivity()).startChat(firebaseRecyclerAdapter.getRef(position).getKey(),model.getName());
                        }
                    });
                }
                else
                {
                    peopleRecyclerView.removeView(viewHolder.itemView);
                }
            }



        };
        firebaseRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver(){
            @Override
            public void onItemRangeInserted(int positionStart,int itemCount){
                super.onItemRangeInserted(positionStart,itemCount);
                int chatCount= firebaseRecyclerAdapter.getItemCount();
                int lastVisiblePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                if(lastVisiblePosition== -1 || (positionStart>=(chatCount-1) && lastVisiblePosition==(positionStart-1))){
                    peopleRecyclerView.scrollToPosition(positionStart);
                }
            }
        });

        peopleRecyclerView.setLayoutManager(linearLayoutManager);
        peopleRecyclerView.setAdapter(firebaseRecyclerAdapter);






        return view;
    }
    interface Communicate{
        void startChat(String receiverId,String receiverName);
    }
}
