package nourl.tbd.Blipp.Database;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import nourl.tbd.Blipp.BlippConstructs.Community;
import nourl.tbd.Blipp.Helper.LocationGetter;
import nourl.tbd.Blipp.Helper.LocationGetterCompletion;

public class CommunityGetter extends AsyncTask<Void, Void, Void> {

    //where query results will be stored
    ArrayList<Community> results;
    ArrayList<Community> temp = new ArrayList<Community>();
    private Context context;

    //These variables are passed by the caller to choose what query to run
    Section section;
    Order order;

    //The completion object, already implemented.
    CommunityGetterCompletion completion;
    Handler uiThread;


    ///////////////////
    //    READ ME    //
    ///////////////////
    //This object will be created and run a query each time it is created. You must assign results to the array list of communities for your query then call taskDone() with true or false based on if your task succeed.
    //You must pull the numberOfComunitiesToPull
    //There are two cases to note, the initial query where communityToStartFrom will be null and BottomHitQueries where you will have to pull from the middle of the list.
    Community communityToStartFrom;//If this is null pull from the top, if this has a value start you list from the community directly after this community. Do not return this community in your list.
    int numberOfCommunitiesToPull;//The number of communities for you to pull
    String currentUser;//A Variable containing the current user id useful for some queries.
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



    public CommunityGetter(Section section, Order order, Community communityToStartFrom, int numberOfCommunitiesToPull, CommunityGetterCompletion completion, Context context)
    {
        this.section = section;
        this.order = order;
        this.communityToStartFrom = communityToStartFrom;
        this.numberOfCommunitiesToPull = numberOfCommunitiesToPull;
        this.currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.completion = completion;
        uiThread = new Handler(context.getMainLooper());
        this.context = context;
        this.execute();
    }


    @Override
    protected Void doInBackground(Void... voids)
    {
        if (section.equals(Section.DISCOVER))
        {
            if (order.equals(Order.ALPHABETICAL))
            {
                try {
                    new LocationGetter(context, new LocationGetterCompletion() {
                        @Override
                        public void locationGetterDidGetLocation(final double latitude, final double longitude) {
                            final LocationGetter.LatLongBounds latLongBounds = new LocationGetter.LatLongBounds(latitude,longitude,10);

                            Log.d("logtag", "Location: " + latitude + " , " + longitude);


                            FirebaseDatabase.getInstance("https://blipp-15ee8.firebaseio.com/").getReference().child("community")
                                    .orderByChild("name")
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot snapshot) {
                                            for (DataSnapshot childSnapshot: snapshot.getChildren()) {
                                                final Community community = childSnapshot.getValue(Community.class);
                                                if (community.getOriginLong() >= latLongBounds.getLonMin() && community.getOriginLong() <= latLongBounds.getLonMax()) {
                                                    if (community.getOriginLat() >= latLongBounds.getLatMin() && community.getOriginLat() <= latLongBounds.getLatMax()) {
                                                        temp.add(community);
                                                    }
                                                }
                                                results = reverse(temp);
                                            }

                                            taskDone(true);
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            taskDone(false);
                                        }
                                    });
                        }

                        @Override
                        public void locationGetterDidFail(boolean shouldShowMessage) {
                            taskDone(false);
                        }
                    });
                } catch (Exception e) {
                    taskDone(false);
                }
            }


        }

        if (order.equals(Order.MEMBER_COUNT_LOW_TO_HIGH))
        {
            try {
                new LocationGetter(context, new LocationGetterCompletion() {
                    @Override
                    public void locationGetterDidGetLocation(final double latitude, final double longitude) {
                        final LocationGetter.LatLongBounds latLongBounds = new LocationGetter.LatLongBounds(latitude,longitude,10);

                        Log.d("logtag", "Location: " + latitude + " , " + longitude);
                        final ArrayList<Community> temp2 = new ArrayList<Community>();


                        FirebaseDatabase.getInstance("https://blipp-15ee8.firebaseio.com/").getReference().child("community")
                                .orderByChild("numMembers")
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot snapshot) {
                                        for (DataSnapshot childSnapshot: snapshot.getChildren()) {
                                            final Community community = childSnapshot.getValue(Community.class);
                                            if (community.getOriginLong() >= latLongBounds.getLonMin() && community.getOriginLong() <= latLongBounds.getLonMax()) {
                                                if (community.getOriginLat() >= latLongBounds.getLatMin() && community.getOriginLat() <= latLongBounds.getLatMax()) {
                                                    temp.add(community);
                                                }
                                            }
                                            // temp2 = temp;
                                        }
                                        for (int i = temp2.size(); i >= 0; i--) {
                                            results.add(temp.get(i));
                                        }
                                        taskDone(true);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        taskDone(false);
                                    }
                                });
                    }

                    @Override
                    public void locationGetterDidFail(boolean shouldShowMessage) {
                        taskDone(false);
                    }
                });
            } catch (Exception e) {
                taskDone(false);
            }
        }

        if (order.equals(Order.MEMBER_COUNT_HIGH_TO_LOW))
        {

            try {
                new LocationGetter(context, new LocationGetterCompletion() {
                    @Override
                    public void locationGetterDidGetLocation(final double latitude, final double longitude) {
                        final LocationGetter.LatLongBounds latLongBounds = new LocationGetter.LatLongBounds(latitude,longitude,10);

                        Log.d("logtag", "Location: " + latitude + " , " + longitude);


                        FirebaseDatabase.getInstance("https://blipp-15ee8.firebaseio.com/").getReference().child("community")
                                .orderByChild("numMembers")
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot snapshot) {
                                        for (DataSnapshot childSnapshot: snapshot.getChildren()) {
                                            final Community community = childSnapshot.getValue(Community.class);
                                            if (community.getOriginLong() >= latLongBounds.getLonMin() && community.getOriginLong() <= latLongBounds.getLonMax()) {
                                                if (community.getOriginLat() >= latLongBounds.getLatMin() && community.getOriginLat() <= latLongBounds.getLatMax()) {
                                                    temp.add(community);
                                                }
                                            }
                                        }

                                        results = temp;
                                        taskDone(true);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        taskDone(false);
                                    }
                                });
                    }

                    @Override
                    public void locationGetterDidFail(boolean shouldShowMessage) {
                        taskDone(false);
                    }
                });
            } catch (Exception e) {
                taskDone(false);
            }
        }


        if (section.equals(Section.JOINED))
        {
            if (order.equals(Order.ALPHABETICAL))
            {

                try {
                    new LocationGetter(context, new LocationGetterCompletion() {
                        @Override
                        public void locationGetterDidGetLocation(final double latitude, final double longitude) {
                            final LocationGetter.LatLongBounds latLongBounds = new LocationGetter.LatLongBounds(latitude,longitude,10);

                            Log.d("logtag", "Location: " + latitude + " , " + longitude);


                            FirebaseDatabase.getInstance("https://blipp-15ee8.firebaseio.com/").getReference().child("community")
                                    .orderByChild("name")
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot snapshot) {
                                            for (DataSnapshot childSnapshot: snapshot.getChildren()) {
                                                final Community community = childSnapshot.getValue(Community.class);
                                                if (community.isJoinable() == false) {
                                                    temp.add(community);
                                                }
                                            }
                                            results = temp;
                                            taskDone(true);
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            taskDone(false);
                                        }
                                    });
                        }

                        @Override
                        public void locationGetterDidFail(boolean shouldShowMessage) {
                            taskDone(false);
                        }
                    });
                } catch (Exception e) {
                    taskDone(false);
                }
            }
        }

        if (order.equals(Order.MEMBER_COUNT_LOW_TO_HIGH))
        {

            try {
                new LocationGetter(context, new LocationGetterCompletion() {
                    @Override
                    public void locationGetterDidGetLocation(final double latitude, final double longitude) {
                        final LocationGetter.LatLongBounds latLongBounds = new LocationGetter.LatLongBounds(latitude,longitude,10);

                        Log.d("logtag", "Location: " + latitude + " , " + longitude);


                        FirebaseDatabase.getInstance("https://blipp-15ee8.firebaseio.com/").getReference().child("community")
                                .orderByChild("numMembers")
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot snapshot) {
                                        for (DataSnapshot childSnapshot: snapshot.getChildren()) {
                                            final Community community = childSnapshot.getValue(Community.class);
                                            if (community.isJoinable() == false) {
                                                temp.add(community);
                                            }
                                        }
                                        results = reverse(temp);
                                        taskDone(true);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        taskDone(false);
                                    }
                                });
                    }

                    @Override
                    public void locationGetterDidFail(boolean shouldShowMessage) {
                        taskDone(false);
                    }

                });
            } catch (Exception e) {
                taskDone(false);
            }
        }

        if (order.equals(Order.MEMBER_COUNT_HIGH_TO_LOW))
        {

            try {
                new LocationGetter(context, new LocationGetterCompletion() {
                    @Override
                    public void locationGetterDidGetLocation(final double latitude, final double longitude) {
                        final LocationGetter.LatLongBounds latLongBounds = new LocationGetter.LatLongBounds(latitude,longitude,10);

                        Log.d("logtag", "Location: " + latitude + " , " + longitude);


                        FirebaseDatabase.getInstance("https://blipp-15ee8.firebaseio.com/").getReference().child("community")
                                .orderByChild("numMembers")
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot snapshot) {
                                        for (DataSnapshot childSnapshot: snapshot.getChildren()) {
                                            final Community community = childSnapshot.getValue(Community.class);
                                            if (community.isJoinable() == false) {
                                                temp.add(community);
                                            }
                                        }
                                        results = temp;
                                        taskDone(true);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        taskDone(false);
                                    }
                                });
                    }

                    @Override
                    public void locationGetterDidFail(boolean shouldShowMessage) {
                        taskDone(false);
                    }
                });
            } catch (Exception e) {
                taskDone(false);
            }
        }

        if (section.equals(Section.OWN))
        {
            if (order.equals(Order.ALPHABETICAL))
            {
                try {
                    new LocationGetter(context, new LocationGetterCompletion() {
                        @Override
                        public void locationGetterDidGetLocation(final double latitude, final double longitude) {
                            final LocationGetter.LatLongBounds latLongBounds = new LocationGetter.LatLongBounds(latitude,longitude,10);

                            Log.d("logtag", "Location: " + latitude + " , " + longitude);


                            FirebaseDatabase.getInstance("https://blipp-15ee8.firebaseio.com/").getReference().child("community")
                                    .orderByChild("name")
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot snapshot) {
                                            for (DataSnapshot childSnapshot: snapshot.getChildren()) {
                                                final Community community = childSnapshot.getValue(Community.class);
                                                if (community.getOwner() == currentUser) {
                                                    temp.add(community);
                                                }
                                            }
                                            results = temp;
                                            taskDone(true);
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            taskDone(false);
                                        }
                                    });
                        }

                        @Override
                        public void locationGetterDidFail(boolean shouldShowMessage) {
                            taskDone(false);
                        }

                    });
                } catch (Exception e) {
                    taskDone(false);
                }
            }

            if (order.equals(Order.MEMBER_COUNT_LOW_TO_HIGH))
            {

                try {
                    new LocationGetter(context, new LocationGetterCompletion() {
                        @Override
                        public void locationGetterDidGetLocation(final double latitude, final double longitude) {
                            final LocationGetter.LatLongBounds latLongBounds = new LocationGetter.LatLongBounds(latitude,longitude,10);

                            Log.d("logtag", "Location: " + latitude + " , " + longitude);


                            FirebaseDatabase.getInstance("https://blipp-15ee8.firebaseio.com/").getReference().child("community")
                                    .orderByChild("numMembers")
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot snapshot) {
                                            for (DataSnapshot childSnapshot: snapshot.getChildren()) {
                                                final Community community = childSnapshot.getValue(Community.class);
                                                if (community.getOwner().equals(currentUser)) {
                                                    temp.add(community);
                                                }
                                            }
                                            results = reverse(temp);
                                            taskDone(true);
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            taskDone(false);
                                        }
                                    });
                        }

                        @Override
                        public void locationGetterDidFail(boolean shouldShowMessage) {
                            taskDone(false);
                        }

                    });
                } catch (Exception e) {
                    taskDone(false);
                }
            }

            if (order.equals(Order.MEMBER_COUNT_HIGH_TO_LOW))
            {

                try {
                    new LocationGetter(context, new LocationGetterCompletion() {
                        @Override
                        public void locationGetterDidGetLocation(final double latitude, final double longitude) {
                            final LocationGetter.LatLongBounds latLongBounds = new LocationGetter.LatLongBounds(latitude,longitude,10);

                            Log.d("logtag", "Location: " + latitude + " , " + longitude);


                            FirebaseDatabase.getInstance("https://blipp-15ee8.firebaseio.com/").getReference().child("community")
                                    .orderByChild("numMembers")
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot snapshot) {
                                            for (DataSnapshot childSnapshot: snapshot.getChildren()) {
                                                final Community community = childSnapshot.getValue(Community.class);
                                                if (community.getOwner().equals(currentUser)) {
                                                    temp.add(community);
                                                }
                                            }

                                            results = temp;
                                            taskDone(true);
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            taskDone(false);
                                        }
                                    });
                        }

                        @Override
                        public void locationGetterDidFail(boolean shouldShowMessage) {
                            taskDone(false);
                        }

                    });
                } catch (Exception e) {
                    taskDone(false);
                }
            }
        }
        return null;
    }



    void taskDone(final boolean isSuccessful)
    {
        uiThread.post(new Runnable() {
            @Override
            public void run() {
                if (isSuccessful)
                {
                    if (communityToStartFrom == null) completion.communityGetterGotInitalCommunities(results);
                    else  completion.communityGetterGotAditionalCommunities(results);
                }
                else completion.communityGetterDidFail();
            }
        });
    }

    //Inner Classes
    public static class Section {
        private int id;

        public static Section JOINED = new Section(0);
        public static Section DISCOVER = new Section(1);
        public static Section OWN = new Section(2);

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

        public static Order MEMBER_COUNT_LOW_TO_HIGH = new Order(0);
        public static Order MEMBER_COUNT_HIGH_TO_LOW = new Order(1);
        public static Order ALPHABETICAL = new Order(2);


        private Order(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        @Override
        public boolean equals(@Nullable Object obj)
        {
            return obj.getClass() != Order.class ? false : this.getId() == ((Order) obj).getId();
        }
    }
    public ArrayList<Community> reverse(ArrayList<Community> temp){
        temp = new ArrayList<Community>();
        ArrayList<Community> reverse = new ArrayList<Community>();
        //int begin = 0;
        for (int end = temp.size()-1; end >= 0; end--){

            reverse.add(temp.get(end));

        }
        return reverse;
    }

}
