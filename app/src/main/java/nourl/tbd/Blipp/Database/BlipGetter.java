package nourl.tbd.Blipp.Database;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import nourl.tbd.Blipp.BlippConstructs.Blipp;
import nourl.tbd.Blipp.BlippConstructs.Like;
import nourl.tbd.Blipp.Helper.LocationGetter;
import nourl.tbd.Blipp.Helper.LocationGetterCompletion;

public class BlipGetter extends AsyncTask<Void, Void, Void> {

    //where you should store your results
    ArrayList<Blipp> results;
    ArrayList<Blipp> temp = new ArrayList<Blipp>();
    ArrayList<Blipp> temp2 = new ArrayList<Blipp>();
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private Context context;
    private String indivBlipID;

    //these instance variables are used to determine which query to run, these will not be used in queries.
    Section section;
    Order order;
    Distance distance;

    //The completion object, already implemented.
    BlipGetterCompletion completion;
    LocationGetterCompletion locationGetterCompletion;
    Handler uiThread;

    ////////////////////
    ////   READ ME    //
    /////////////////////
    //**********************
    //these instance variables are used to assist you in your queries

    String blipToStartAt;
    //if blipToStartAt is null pull from the top of the list, otherwise start the pull from the next blip after this blipp id. This will be null when initially populating
    //the blip list but you will not pull every blip all at once (see numberOfBlipsToPull)
    //A blip getter will be created every time the user reaches the bottom of the list and we need to pull more blips. When the user hits the bottom
    //The blip getter will be created with the blipToStartAt parameter being the last one that was successfully loaded. When this is the case pull
    //the numberOfBlips passed starting from the blip directly after the blip with the id of blipToStartAt.
    //Do NOT include blipToStartAt in your return list

    int numberOfBlipsToPull;
    //the number of blips you should return from your query, if there are not enough return whatever number of blips you pulled, return null if there are zero blips pulled.

    String currentUser;
    //this has the current user UID that will be needed for certain queries such as the My Blips queries.

    String communityId;
    //this is the community id to pull blips from. This will be null for any other query besides community queries.

    String parentId;
    //this is the parent blipp, this will be null for any other query that isn't a reply query.

    //if your pull is successful assign the pulled blips to the class variable results and call taskDone(true). Pass null if 0 blips result from your query.
    //if your pull fails (no internet, google cloud unreachable ect.) call taskDone(false);

    /////////////////////////////////////////////////////////////////////////////////////////////


    //Use this constructor to get blips that are not in a community and dont have a parent
    public BlipGetter(Section section, Order order, Distance distance, BlipGetterCompletion completion, String blipIdToStartAt, int numberOfBlipsToPull, Context context) {
        this.blipToStartAt = blipIdToStartAt;
        this.numberOfBlipsToPull = numberOfBlipsToPull;
        this.section = section;
        this.order = order;
        this.distance = distance;
        this.completion = completion;
        this.currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        communityId = null;
        parentId = null;
        this.uiThread = new Handler(context.getMainLooper());
        this.execute();
        this.context = context;
    }


    //Use this constructor to get blips that are in a community
    public BlipGetter(Order order, BlipGetterCompletion completion, String communityId, String blipToStartAt, int numberOfBlipsToPull, Context context) {
        this.order = order;
        this.completion = completion;
        this.communityId = communityId;
        this.blipToStartAt = blipToStartAt;
        this.numberOfBlipsToPull = numberOfBlipsToPull;
        this.distance = null;
        this.section = null;
        this.parentId = null;
        this.currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.uiThread = new Handler(context.getMainLooper());
        this.execute();
    }

    //use this constructor to get blips that are replys
    public BlipGetter(Order order, BlipGetterCompletion completion, String blipToStartAt, int numberOfBlipsToPull, String parentId, Context context) {
        this.order = order;
        this.completion = completion;
        this.blipToStartAt = blipToStartAt;
        this.numberOfBlipsToPull = numberOfBlipsToPull;
        this.parentId = parentId;
        this.distance = null;
        this.section = null;
        this.communityId = null;
        this.currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.uiThread = new Handler(context.getMainLooper());
        this.execute();
    }

    //use this to get an individual blip by id
    public BlipGetter(String indivBlipID, BlipGetterCompletion completion, Context context)
    {
        this.indivBlipID = indivBlipID;
        this.completion = completion;
        this.context = context;
        this.uiThread = new Handler(context.getMainLooper());
        this.execute();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        database = FirebaseDatabase.getInstance("https://blipp-15ee8.firebaseio.com/");
        ref = database.getReference("blip");

        //For single blip
        if (indivBlipID != null) {
            FirebaseDatabase.getInstance().getReference().child("blip").child(indivBlipID).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if (!dataSnapshot.exists()) {taskDone(false); return;}
                    results = new ArrayList<>();
                     results.add(dataSnapshot.getValue(Blipp.class));
                     taskDone(true);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    taskDone(false);
                }
            });
            return null;
        }


        //Queries for blips that are not in a community and do not have a parent
        if (communityId == null && parentId == null && section!= null && order != null) {

            if (section.equals(Section.FEED)) {
                if (distance.equals(Distance.CLOSE)) {

                    if (order.equals(Order.MOST_RECENT)) {
                        //Note: Close distance is defined as 0Miles - 0.1Miles
                        try {
                            new LocationGetter(context, new LocationGetterCompletion() {
                                @Override
                                public void locationGetterDidGetLocation(double latitude, double longitude) {

                                    temp = new ArrayList<>();

                                    final LocationGetter.LatLongBounds latLongBounds = new LocationGetter.LatLongBounds(latitude,longitude,.1);


                                    FirebaseDatabase.getInstance("https://blipp-15ee8.firebaseio.com/").getReference().child("blip")
                                            .orderByChild("latitude")
                                            .startAt(latLongBounds.getLatMin())
                                            .endAt(latLongBounds.getLatMax())
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot snapshot) {
                                                    for (DataSnapshot childSnapshot: snapshot.getChildren()) {

                                                        final Blipp blip = childSnapshot.getValue(Blipp.class);

                                                        if (blip.getLongitude() >= latLongBounds.getLonMin() && blip.getLongitude() <= latLongBounds.getLonMax()) {
                                                            if(blip.getIsShortDistance()== true && blip.getParent() == null && blip.getCommunity() == null) {
                                                                temp.add(blip);
                                                            }
                                                        }
                                                    }
                                                    results = temp;
                                                   if (results != null) Collections.reverse(results);
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


                                public void locationGetterDidFail() {
                                    taskDone(false);
                                }
                            });
                        } catch (Exception e) {
                            taskDone(false);
                        }
                    }

                    if (order.equals(Order.MOST_LIKED)) {
                        //Note: Close distance is defined as 0Miles - 0.1Miles

                        try {
                            new LocationGetter(context, new LocationGetterCompletion() {
                                @Override
                                public void locationGetterDidGetLocation(final double latitude, final double longitude) {
                                    temp = new ArrayList<>();
                                    final LocationGetter.LatLongBounds latLongBounds = new LocationGetter.LatLongBounds(latitude,longitude,.1);

                                    FirebaseDatabase.getInstance("https://blipp-15ee8.firebaseio.com/").getReference().child("blip")
                                            .orderByChild("latitude")
                                            .startAt(latLongBounds.getLatMin())
                                            .endAt(latLongBounds.getLatMax())
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot snapshot) {
                                                    for (DataSnapshot childSnapshot: snapshot.getChildren()) {

                                                        final Blipp blip = childSnapshot.getValue(Blipp.class);
                                                        if (blip.getLongitude() >= latLongBounds.getLonMin() && blip.getLongitude() <= latLongBounds.getLonMax()) {
                                                           if (blip.getCommunity() == null && blip.getParent() == null && blip.getIsShortDistance())   temp.add(blip);
                                                        }
                                                    }
                                                    //ArrayList
                                                    results = sortByLikes(temp);
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

                    }//end of sort by likes

                }

                if (distance.equals(Distance.REGULAR)) {
                    if (order.equals(Order.MOST_RECENT)) {
                        try {
                            new LocationGetter(context, new LocationGetterCompletion() {
                                @Override
                                public void locationGetterDidGetLocation(double latitude, double longitude) {

                                    temp = new ArrayList<>();

                                    final LocationGetter.LatLongBounds latLongBounds = new LocationGetter.LatLongBounds(latitude,longitude,1);


                                    FirebaseDatabase.getInstance("https://blipp-15ee8.firebaseio.com/").getReference().child("blip")
                                            .orderByChild("latitude")
                                            .startAt(latLongBounds.getLatMin())
                                            .endAt(latLongBounds.getLatMax())
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot snapshot) {
                                                    for (DataSnapshot childSnapshot: snapshot.getChildren()) {

                                                        final Blipp blip = childSnapshot.getValue(Blipp.class);

                                                        if (blip.getLongitude() >= latLongBounds.getLonMin() && blip.getLongitude() <= latLongBounds.getLonMax()) {
                                                            if(blip.getIsMediumDistance()== true && blip.getParent() == null && blip.getCommunity() == null) {
                                                                temp.add(blip);
                                                            }
                                                        }
                                                    }
                                                   if (results != null) Collections.reverse(results);
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


                                public void locationGetterDidFail() {
                                    taskDone(false);
                                }
                            });
                        } catch (Exception e) {
                            taskDone(false);
                        }
                    }

                    if (order.equals(Order.MOST_LIKED)) {
                        //Note: regular is defined as 0Miles - 1Miles

                        try {
                            new LocationGetter(context, new LocationGetterCompletion() {
                                @Override
                                public void locationGetterDidGetLocation(final double latitude, final double longitude) {
                                    temp = new ArrayList<>();

                                    final LocationGetter.LatLongBounds latLongBounds = new LocationGetter.LatLongBounds(latitude,longitude,1.0);

                                    FirebaseDatabase.getInstance("https://blipp-15ee8.firebaseio.com/").getReference().child("blip")
                                            .orderByChild("latitude")
                                            .startAt(latLongBounds.getLatMin())
                                            .endAt(latLongBounds.getLatMax())
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot snapshot) {
                                                    for (DataSnapshot childSnapshot: snapshot.getChildren()) {
                                                        final Blipp blip = childSnapshot.getValue(Blipp.class);
                                                        if (blip.getLongitude() >= latLongBounds.getLonMin() && blip.getLongitude() <= latLongBounds.getLonMax()) {
                                                           if (blip.getParent() == null && blip.getCommunity() == null && blip.getIsMediumDistance())  temp.add(blip);
                                                        }
                                                    }
                                                    //ArrayList
                                                    results = sortByLikes(temp);
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

                if (distance.equals(Distance.MAX)) {
                    if (order.equals(Order.MOST_RECENT)) {
                        //Note: MAx distance is defined as 0Miles - 10Miles

                        try {
                            new LocationGetter(context, new LocationGetterCompletion() {
                                @Override
                                public void locationGetterDidGetLocation(double latitude, double longitude) {
                                    temp = new ArrayList<>();

                                    final LocationGetter.LatLongBounds latLongBounds = new LocationGetter.LatLongBounds(latitude,longitude,10);


                                    FirebaseDatabase.getInstance("https://blipp-15ee8.firebaseio.com/").getReference().child("blip")
                                            .orderByChild("latitude")
                                            .startAt(latLongBounds.getLatMin())
                                            .endAt(latLongBounds.getLatMax())
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot snapshot) {
                                                    for (DataSnapshot childSnapshot: snapshot.getChildren()) {

                                                        final Blipp blip = childSnapshot.getValue(Blipp.class);

                                                        if (blip.getLongitude() >= latLongBounds.getLonMin() && blip.getLongitude() <= latLongBounds.getLonMax()) {
                                                            if(blip.getIsLongDistance()== true && blip.getParent() == null && blip.getCommunity() == null) {
                                                                temp.add(blip);
                                                            }
                                                        }
                                                    }
                                                    results = temp;
                                                   if (results != null) Collections.reverse(results);
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


                                public void locationGetterDidFail() {
                                    taskDone(false);
                                }
                            });
                        } catch (Exception e) {
                            taskDone(false);
                        }
                        return null;
                    }

                    if (order.equals(Order.MOST_LIKED)) {
                        //Note: MAx distance is defined as 0Miles - 10Miles

                        try {
                            new LocationGetter(context, new LocationGetterCompletion() {
                                @Override
                                public void locationGetterDidGetLocation(final double latitude, final double longitude) {

                                    temp = new ArrayList<>();

                                    final LocationGetter.LatLongBounds latLongBounds = new LocationGetter.LatLongBounds(latitude,longitude,10.0);

                                    FirebaseDatabase.getInstance("https://blipp-15ee8.firebaseio.com/").getReference().child("blip")
                                            .orderByChild("latitude")
                                            .startAt(latLongBounds.getLatMin())
                                            .endAt(latLongBounds.getLatMax())
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot snapshot) {
                                                    for (DataSnapshot childSnapshot: snapshot.getChildren()) {
                                                        final Blipp blip = childSnapshot.getValue(Blipp.class);
                                                        if (blip.getLongitude() >= latLongBounds.getLonMin() && blip.getLongitude() <= latLongBounds.getLonMax()) {
                                                            if (blip.getParent() == null && blip.getCommunity() == null && blip.getIsLongDistance()) temp.add(blip);
                                                        }
                                                    }
                                                    //ArrayList
                                                    //Log.d("logtag", "outside loop  temp.size = " + temp.size());
                                                    results = sortByLikes(temp);
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
                    return null;
                }
            }


            if (section.equals(Section.LIKED_BLIPS)) {

                if (order.equals(Order.MOST_RECENT)) {
                    try {
                        FirebaseDatabase.getInstance().getReference().child("like").orderByChild("userId").equalTo(currentUser).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                ArrayList<Like> likes = new ArrayList<>();
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    if (!ds.getValue(Like.class).getIsDislike())// Don't accept dislikes
                                        likes.add(ds.getValue(Like.class)); //store likes in Arraylist
                                }
                                results = new ArrayList<>();
                                for (Like lk : likes) {
                                    FirebaseDatabase.getInstance().getReference().child("blip").orderByChild("id").equalTo(lk.getBlipId()) // pull blipps from each like id
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                        results.add(ds.getValue(Blipp.class));  }// add matching blipps to results
                                                       if (results != null) Collections.reverse(results);
                                                    taskDone(true);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                    taskDone(false);
                                                }
                                            });
                                } if (likes.size() == 0) taskDone(true);
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                taskDone(false);
                            }
                        });
                    } catch (Exception e){
                        taskDone(false);
                    }
                    return null;
                }

                if (order.equals(Order.MOST_LIKED)) {
                    try {
                        FirebaseDatabase.getInstance().getReference().child("like").orderByChild("userId").equalTo(currentUser).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                ArrayList<Like> likes = new ArrayList<>();
                                for (DataSnapshot ds : dataSnapshot.getChildren()){
                                    if (!ds.getValue(Like.class).getIsDislike())// Don't accept dislikes
                                        likes.add(ds.getValue(Like.class)); }//store likes in Arraylist
                                temp = new ArrayList<>();
                                for (Like lk : likes) {
                                    FirebaseDatabase.getInstance().getReference().child("blip")
                                            .orderByChild("id")
                                            .equalTo(lk.getBlipId()) // pull blipps from each like id
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                                                        temp.add(ds.getValue(Blipp.class));  }// add matching blipps to results
                                                    results = sortByLikes(temp);
                                                    taskDone(true);
                                                }
                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                    taskDone(false);
                                                }
                                            });
                                }
                                if (likes.size() == 0) taskDone(true);
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                taskDone(false);
                            }
                        });
                        results = sortByLikes(temp);
                    } catch (Exception e){
                        taskDone(false);
                    }
                    return null;
                }
            }

            if (section.equals(Section.MY_BLIPS)) {
                if (order.equals(Order.MOST_RECENT)) {
                    try {
                        FirebaseDatabase.getInstance().getReference().child("blip").orderByChild("userId").equalTo(currentUser).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                results = new ArrayList<>();
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    results.add(ds.getValue(Blipp.class));
                                }
                                if (results != null) Collections.reverse(results);
                                taskDone(true);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                taskDone(false);
                            }
                        });
                    }catch (Exception e){
                        taskDone(false);
                    }
                    return null;
                }

                if (order.equals(Order.MOST_LIKED)) {
                    temp = new ArrayList<>();
                    try {
                        FirebaseDatabase.getInstance().getReference().child("blip").orderByChild("userId").equalTo(currentUser).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    temp.add(ds.getValue(Blipp.class));
                                }
                                results = sortByLikes( temp);
                                taskDone(true);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                taskDone(false);
                            }
                        });

                    }catch (Exception e){
                        taskDone(false);
                    }
                    return null;
                }
            }
        }

        //Community queries
        if (communityId != null) {
            if (order.equals(Order.MOST_RECENT))
            {
                try {
                    results = new ArrayList<>();
                    Query q = FirebaseDatabase.getInstance().getReference().child("blip").orderByChild("community").equalTo(communityId);
                    q.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                results.add(ds.getValue(Blipp.class));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            taskDone(false);
                        }
                    });
                    taskDone(true);
                }catch (Exception e){
                    taskDone(false);
                }
                return null;
            }

            if (order.equals(Order.MOST_LIKED)) {

                results = new ArrayList<>();
                temp = new ArrayList<>();
                try {
                    Query q = FirebaseDatabase.getInstance().getReference().child("blip").orderByChild("community").equalTo(communityId);
                    q.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                temp.add(ds.getValue(Blipp.class));
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            taskDone(false);
                        }
                    });
                    results = sortByLikes(temp);
                    taskDone(true);
                }catch (Exception e){
                    taskDone(false);
                }
                return null;
            }
        }

        //reply queries
        if (parentId != null) {
            if (order.equals(Order.MOST_RECENT)) {
                results = new ArrayList<>();
                try {
                    Query q = FirebaseDatabase.getInstance().getReference().child("blip").orderByChild("parent").equalTo(parentId);
                    q.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                results.add(ds.getValue(Blipp.class));

                            }
                            taskDone(true);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            taskDone(false);
                        }
                    });

                }catch (Exception e){
                    taskDone(false);
                }
                return null;
            }

            if (order.equals(Order.MOST_LIKED)) {
                temp = new ArrayList<>();
                results = new ArrayList<>();
                try {
                    Query q = FirebaseDatabase.getInstance().getReference().child("blip").orderByChild("parent").equalTo(parentId);
                    q.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                temp.add(ds.getValue(Blipp.class));
                            }
                            results = sortByLikes(temp);
                            taskDone(true);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            taskDone(false);
                        }
                    });

                }catch (Exception e){
                    taskDone(false);
                }
                return null;
            }

        }
        return null;
    }

    protected void taskDone(final boolean isSuccessful)
    {
        uiThread.post(new Runnable() {
            @Override
            public void run() {
                if (isSuccessful)
                {
                    if (blipToStartAt == null) completion.blipGetterGotInitialBlips(results);
                    else completion.blipGetterGotAdditionalBlips(results);
                }
                else  completion.blipGetterDidFail();
            }
        });
    }


    //Inner Classes
    public static class Section {
        private int id;

        public static Section FEED = new Section(0);
        public static Section MY_BLIPS = new Section(1);
        public static Section LIKED_BLIPS = new Section(2);

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

    public static class Distance {
        private int id;

        public static Distance CLOSE = new Distance(0);
        public static Distance REGULAR = new Distance(1);
        public static Distance MAX = new Distance(2);

        private Distance(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            return obj.getClass() != Distance.class ? false : this.getId() == ((Distance) obj).getId();
        }
    }


    public static class Order {
        private int id;

        public static Order MOST_RECENT = new Order(0);
        public static Order MOST_LIKED = new Order(1);

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
    public static ArrayList<Blipp> sortByLikes(ArrayList<Blipp> temp) {
        Collections.sort(temp);
        return temp;
    }
}
