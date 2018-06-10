package com.example.eventmap.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eventmap.R;
import com.example.eventmap.models.clubCardModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

/**
 * Created by SS on 5/23/2018.
 */

public class showClubsAdapter extends ArrayAdapter<clubCardModel> {
    private Context context;
    private ArrayList<clubCardModel> clubCardModels;
    private String userId;
    private DatabaseReference mDatabase;

    public showClubsAdapter(Context context,int position, ArrayList<clubCardModel> clubModels, String userId){
        super(context, position);
        this.context = context;
        this.clubCardModels = clubModels;
        this.userId = userId;
    }

    @Override
    public int getCount() {
        return clubCardModels.size();
    }

    @Override
    public clubCardModel getItem(int i) {
        return clubCardModels.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if (view == null){
            view = View.inflate(context, R.layout.club_list_items, null);
        }

        final View viewForInner = view;
        TextView clubNameHolder = (TextView)view.findViewById(R.id.clubNameHolder);
        TextView affliationHolder = (TextView)view.findViewById(R.id.AffliationHolder);
        Button deleteClubBtn = (Button)view.findViewById(R.id.deleteClubButton);

        final clubCardModel model = clubCardModels.get(i);
        clubNameHolder.setText(model.getClubName());
        affliationHolder.setText(model.getAffliation());
        deleteClubBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Query clubQuery = mDatabase.child("clubs").orderByChild("name").equalTo(model.getClubName());
            clubQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot clubSnapshot: dataSnapshot.getChildren()) {
                        final String clubId = clubSnapshot.getKey();
                        final DataSnapshot clubSnapShotForInnerCall = clubSnapshot;
                        DatabaseReference usersClubDatabase = mDatabase.child("users").child(userId).child("clubIds");
                        usersClubDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot clubIdSnapshot: dataSnapshot.getChildren()){
                                    if (clubIdSnapshot.getValue().equals(clubId)){
                                        clubIdSnapshot.getRef().removeValue();
                                    }
                                }

                                if (model.getAffliation().equals("Owner")){
                                    clubSnapShotForInnerCall.getRef().removeValue();
                                    clubCardModels.remove(model);
                                    notifyDataSetChanged();
                                    Toast.makeText(context, model.getClubName() + " has been deleted.", Toast.LENGTH_LONG).show();
                                } else {
                                    // TODO: remove from member Arraylist
                                }


                                //viewForInner.setVisibility(viewForInner.GONE);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, "onCancelled", databaseError.toException());
                }
            });
            }
        });
        return view;
    }


}