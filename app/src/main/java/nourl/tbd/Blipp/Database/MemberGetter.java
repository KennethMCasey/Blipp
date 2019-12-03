package nourl.tbd.Blipp.Database;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import nourl.tbd.Blipp.BlippConstructs.Community;
import nourl.tbd.Blipp.BlippConstructs.Member;

public class MemberGetter extends AsyncTask<Void, Void, Void> {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMemberDatabaseReference;

    //where to store query results
    ArrayList<Member> results;
    //These are variables used to decide on which query to run
    Order order;
    Section section;

    //Completion handler, already implemented
    MemberGetterCompletion completion;
    Handler uiThread;


    //////////////////
    //    READ ME   //
    /////////////////
    //You will need to pull community members by assigning the array list of members to results then calling taskDone() true or false depending on success of failure. Communities may have a lot of members. There will be two cases you should note.
    //The first case is the initial pull (memberToStartOn == null). You will pull the numberToPull number of members starting from the top.
    //The second is a secondary pull, Loading more members as the user has hit the current bottom of the memeber list. This is where you will start your return list with the memeber directly after the memeber to start on.
    Community community;//The community you will pull members from.
    Member memberToStartOn;//This will be null when doing an initial pull. If this contains a member start your return list with the member directly following the passed memberToStartOn.
    int numberToPull;//number of memebers to include in your return list.
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public MemberGetter(Community community, Order order, Section section, MemberGetterCompletion completion, Member memberToStartOn, int numberToPull, Context context) {
        this.community = community;
        this.order = order;
        this.section = section;
        this.completion = completion;
        this.memberToStartOn = memberToStartOn;
        this.numberToPull = numberToPull;
        uiThread = new Handler(context.getMainLooper());
        this.execute();
    }


    /*ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            temp.clear();
            if(dataSnapshot.exists()){
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Member m = snapshot.getValue(Member.class);
                    temp.add(m);

                    taskDone(true);
                }
            }else {
                taskDone(false);
            }
            results = temp;
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };*/

    @Override
    protected Void doInBackground(Void... voids)
    {
        mFirebaseDatabase = FirebaseDatabase.getInstance("https://blipp-15ee8.firebaseio.com/");
        mMemberDatabaseReference = mFirebaseDatabase.getReference("member");

        Query queryAA = mMemberDatabaseReference;
        Query queryAN = mMemberDatabaseReference;
        Query queryBA = mMemberDatabaseReference;
        Query queryBN = mMemberDatabaseReference;


        //assign result array list to results
        //call taskDone when done
        if (section.equals(Section.ACTIVE))
        {
            if (order.equals(Order.ALPHABETICAL))
            {
                //TODO: Get all active users in alphabetical order from the passed community

                queryAA.orderByChild("banned").equalTo("false");
                queryAA.orderByChild("displayName");
                queryAA.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            results = dataSnapshot.hasChildren() ? new ArrayList<Member>() : null;
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                Member m = snapshot.getValue(Member.class);
                                results.add(m);
                            }
                            taskDone(true);
                        }else {
                            taskDone(false);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        taskDone(false);
                    }
                });
            }

            if (order.equals(Order.NEWEST_TO_OLDEST))
            {
                //TODO: Get all active users from newest to oldest order from the passed community

                queryAN.orderByChild("banned").equalTo("false");
                queryAN.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) { ;
                        if(dataSnapshot.exists()){
                            results = dataSnapshot.hasChildren() ? new ArrayList<Member>() : null;
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                Member m = snapshot.getValue(Member.class);
                                results.add(m);
                            }
                            taskDone(true);
                        }else {
                            taskDone(false);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        taskDone(false);
                    }
                });
            }
        }

        if (section.equals(Section.BANNED))
        {
            if (order.equals(Order.ALPHABETICAL))
            {
                //TODO: Get all banned users in alphabetical order from the passed community

                queryBA.orderByChild("banned").equalTo("true");
                queryBA.orderByChild("displayName");
                queryBA.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            results = dataSnapshot.hasChildren() ? new ArrayList<Member>() : null;
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                Member m = snapshot.getValue(Member.class);
                                results.add(m);
                            }
                            taskDone(true);
                        }else {
                            taskDone(false);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        taskDone(false);
                    }
                });
            }

            if (order.equals(Order.NEWEST_TO_OLDEST))
            {
                //TODO: Get all banned users in newest to oldest order from the passed community

                queryBN.orderByChild("banned").equalTo("true");
                queryBN.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            results = dataSnapshot.hasChildren() ? new ArrayList<Member>() : null;
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                Member m = snapshot.getValue(Member.class);
                                results.add(m);
                            }
                            taskDone(true);
                        }else {
                            taskDone(false);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        taskDone(false);
                    }
                });
            }
        }

        return null;
    }



    void taskDone(final boolean isSuccessful)
    {
        uiThread.post(new Runnable() {
            @Override
            public void run()
            {
                if (isSuccessful)
                {
                    if (memberToStartOn == null) completion.memberGetterGotInitalMembers(results);
                    else completion.memberGetterGotAditionalMembers(results);
                }
                else completion.memberGetterDidFail();
            }
        });
    }

    //Inner Classes
    public static class Section {
        private int id;

        public static Section ACTIVE = new Section(0);
        public static Section BANNED = new Section(1);

        private Section(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            return obj.getClass() != Section.class ? false : this.getId() == ((Section) obj).getId();
        }

    }


    public static class Order {
        private int id;

        public static Order NEWEST_TO_OLDEST = new Order(0);
        public static Order ALPHABETICAL = new Order(1);

        private Order(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            return obj.getClass() != Order.class ? false : this.getId() == ((Order) obj).getId();
        }
    }

}
