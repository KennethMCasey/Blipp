package nourl.tbd.Blipp.Database;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.view.Change;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

import nourl.tbd.Blipp.BlippConstructs.Blipp;
import nourl.tbd.Blipp.BlippConstructs.Community;
import nourl.tbd.Blipp.BlippConstructs.Like;
import nourl.tbd.Blipp.Helper.LocationGetter;
import nourl.tbd.Blipp.Helper.LocationGetterCompletion;
import nourl.tbd.Blipp.UI.Blip;

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
    }

    @Override
    protected Void doInBackground(Void... voids) {
        database = FirebaseDatabase.getInstance("https://blipp-15ee8.firebaseio.com/");
        ref = database.getReference("blip");

        //For single blip
        if (indivBlipID != null) {
            database.getReference().child("blip").child(indivBlipID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
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
                        //TODO: Wrire a query that will return an array of blips from firebase that are within close distance of the current user and marked as close distance ordered by most recent
                        //Note: Close distance is defined as 0Miles - 0.1Miles
                        try {
                            new LocationGetter(context, new LocationGetterCompletion() {
                                @Override
                                public void locationGetterDidGetLocation(double latitude, double longitude) {

                                    final LocationGetter.LatLongBounds latLongBounds = new LocationGetter.LatLongBounds(latitude,longitude,.1);


                                    FirebaseDatabase.getInstance("https://blipp-15ee8.firebaseio.com/").getReference().child("blip")
                                            .orderByChild("latitude")
                                            .startAt(latLongBounds.getLatMin())
                                            .endAt(latLongBounds.getLatMax())
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot snapshot) {
                                                    for (DataSnapshot childSnapshot: snapshot.getChildren()) {

                                                        final Blipp blip = childSnapshot.getValue(Blipp.class);

                                                        if (blip.getLongitude() >= latLongBounds.getLonMin() && blip.getLongitude() <= latLongBounds.getLonMax()) {
                                                            if(blip.isShortDistance()== true) {
                                                                temp.add(blip);
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


                                public void locationGetterDidFail() {
                                    taskDone(false);
                                }
                            });
                        } catch (Exception e) {
                            taskDone(false);
                        }
                    }

                    if (order.equals(Order.MOST_LIKED)) {
                        //TODO: close distance/most liked
                        //Note: Close distance is defined as 0Miles - 0.1Miles

                        try {
                            new LocationGetter(context, new LocationGetterCompletion() {
                                @Override
                                public void locationGetterDidGetLocation(final double latitude, final double longitude) {
                                    final LocationGetter.LatLongBounds latLongBounds = new LocationGetter.LatLongBounds(latitude,longitude,.1);

                                    FirebaseDatabase.getInstance("https://blipp-15ee8.firebaseio.com/").getReference().child("blip")
                                            .orderByChild("latitude")
                                            .startAt(latLongBounds.getLatMin())
                                            .endAt(latLongBounds.getLatMax())
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot snapshot) {
                                                    for (DataSnapshot childSnapshot: snapshot.getChildren()) {

                                                        final Blipp blip = childSnapshot.getValue(Blipp.class);
                                                        if (blip.getLongitude() >= latLongBounds.getLonMin() && blip.getLongitude() <= latLongBounds.getLonMax()) {
                                                            temp.add(blip);
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
                        //TODO: Wrire a query that will return an array of blips from firebase that are within regular distance of the current user and marked as regular distance ordered by most recent
                        try {
                            new LocationGetter(context, new LocationGetterCompletion() {
                                @Override
                                public void locationGetterDidGetLocation(double latitude, double longitude) {

                                    final LocationGetter.LatLongBounds latLongBounds = new LocationGetter.LatLongBounds(latitude,longitude,1);


                                    FirebaseDatabase.getInstance("https://blipp-15ee8.firebaseio.com/").getReference().child("blip")
                                            .orderByChild("latitude")
                                            .startAt(latLongBounds.getLatMin())
                                            .endAt(latLongBounds.getLatMax())
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot snapshot) {
                                                    for (DataSnapshot childSnapshot: snapshot.getChildren()) {

                                                        final Blipp blip = childSnapshot.getValue(Blipp.class);

                                                        if (blip.getLongitude() >= latLongBounds.getLonMin() && blip.getLongitude() <= latLongBounds.getLonMax()) {
                                                            if(blip.isMediumDistance()== true) {
                                                                temp.add(blip);
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


                                public void locationGetterDidFail() {
                                    taskDone(false);
                                }
                            });
                        } catch (Exception e) {
                            taskDone(false);
                        }
                    }

                    if (order.equals(Order.MOST_LIKED)) {
                        //TODO: regular distance/most liked
                        //Note: regular is defined as 0Miles - 1Miles

                        try {
                            new LocationGetter(context, new LocationGetterCompletion() {
                                @Override
                                public void locationGetterDidGetLocation(final double latitude, final double longitude) {
                                    final LocationGetter.LatLongBounds latLongBounds = new LocationGetter.LatLongBounds(latitude,longitude,1.0);

                                    FirebaseDatabase.getInstance("https://blipp-15ee8.firebaseio.com/").getReference().child("blip")
                                            .orderByChild("latitude")
                                            .startAt(latLongBounds.getLatMin())
                                            .endAt(latLongBounds.getLatMax())
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot snapshot) {
                                                    for (DataSnapshot childSnapshot: snapshot.getChildren()) {
                                                        final Blipp blip = childSnapshot.getValue(Blipp.class);
                                                        if (blip.getLongitude() >= latLongBounds.getLonMin() && blip.getLongitude() <= latLongBounds.getLonMax()) {
                                                            temp.add(blip);

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
                        //TODO: Wrire a query that will return an array of blips from firebase that are within Max distance of the current user and marked as Max distance ordered by most recent
                        //Note: MAx distance is defined as 0Miles - 10Miles

                        try {
                            new LocationGetter(context, new LocationGetterCompletion() {
                                @Override
                                public void locationGetterDidGetLocation(double latitude, double longitude) {

                                    final LocationGetter.LatLongBounds latLongBounds = new LocationGetter.LatLongBounds(latitude,longitude,10);


                                    FirebaseDatabase.getInstance("https://blipp-15ee8.firebaseio.com/").getReference().child("blip")
                                            .orderByChild("latitude")
                                            .startAt(latLongBounds.getLatMin())
                                            .endAt(latLongBounds.getLatMax())
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot snapshot) {
                                                    for (DataSnapshot childSnapshot: snapshot.getChildren()) {

                                                        final Blipp blip = childSnapshot.getValue(Blipp.class);

                                                        if (blip.getLongitude() >= latLongBounds.getLonMin() && blip.getLongitude() <= latLongBounds.getLonMax()) {
                                                            if(blip.isLongDistance()== true) {
                                                                temp.add(blip);
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


                                public void locationGetterDidFail() {
                                    taskDone(false);
                                }
                            });
                        } catch (Exception e) {
                            taskDone(false);
                        }
                    }

                    if (order.equals(Order.MOST_LIKED)) {
                        //TODO: Max distance/most liked
                        //Note: MAx distance is defined as 0Miles - 10Miles

                        try {
                            new LocationGetter(context, new LocationGetterCompletion() {
                                @Override
                                public void locationGetterDidGetLocation(final double latitude, final double longitude) {
                                    final LocationGetter.LatLongBounds latLongBounds = new LocationGetter.LatLongBounds(latitude,longitude,10.0);

                                    FirebaseDatabase.getInstance("https://blipp-15ee8.firebaseio.com/").getReference().child("blip")
                                            .orderByChild("latitude")
                                            .startAt(latLongBounds.getLatMin())
                                            .endAt(latLongBounds.getLatMax())
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot snapshot) {
                                                    for (DataSnapshot childSnapshot: snapshot.getChildren()) {
                                                        final Blipp blip = childSnapshot.getValue(Blipp.class);
                                                        if (blip.getLongitude() >= latLongBounds.getLonMin() && blip.getLongitude() <= latLongBounds.getLonMax()) {
                                                            temp.add(blip);
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
                }
            }


            if (section.equals(Section.LIKED_BLIPS)) {

                if (order.equals(Order.MOST_RECENT)) {
                    //TODO: Wrire a query that will return an array of blips from firebase that the user has previously liked (no dislikes) order by most recent

                    results.clear();
                    try {
                        Query q = FirebaseDatabase.getInstance().getReference().child("like").orderByChild("userId").equalTo(currentUser); // pull likes from currentuser
                        q.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                ArrayList<Like> likes = new ArrayList<>();
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    if (!ds.getValue(Like.class).getIsDislike())// Don't accept dislikes
                                        likes.add(ds.getValue(Like.class)); //store likes in Arraylist
                                }
                                for (Like lk : likes) {
                                    FirebaseDatabase.getInstance().getReference().child("blip").orderByChild("id").equalTo(lk.getBlipId()) // pull blipps from each like id
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                    for (DataSnapshot ds : dataSnapshot.getChildren())
                                                        results.add(ds.getValue(Blipp.class));  // add matching blipps to results
                                                    taskDone(true);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                    taskDone(false);
                                                }
                                            });
                                }
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
                    //TODO: Wrire a query that will return an array of blips from firebase that the user has previously liked (no dislikes) order by most liked
                    results.clear();
                    temp.clear();
                    try {
                        Query q = FirebaseDatabase.getInstance().getReference().child("like").orderByChild("userId").equalTo(currentUser); // pull likes from currentuser
                        q.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                ArrayList<Like> likes = new ArrayList<>();
                                for (DataSnapshot ds : dataSnapshot.getChildren())
                                    if (!ds.getValue(Like.class).getIsDislike())// Don't accept dislikes
                                        likes.add(ds.getValue(Like.class)); //store likes in Arraylist
                                for (Like lk : likes) {
                                    FirebaseDatabase.getInstance().getReference().child("blip")
                                            .orderByChild("id")
                                            .equalTo(lk.getBlipId()) // pull blipps from each like id
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    for (DataSnapshot ds : dataSnapshot.getChildren())
                                                        temp.add(ds.getValue(Blipp.class));  // add matching blipps to results
                                                }
                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                    taskDone(false);
                                                }
                                            });
                                }
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
                    //TODO: Wrire a query that will return an array of blips from firebase that the user has previously bliped, order by most recent
                    results.clear();
                    try {
                        Query q = FirebaseDatabase.getInstance().getReference().child("blip").orderByChild("userId").equalTo(currentUser);
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
                    //TODO: Wrire a query that will return an array of blips from firebase that the user has previously bliped, order by most liked

                    results.clear();
                    temp.clear();
                    try {
                        Query q = FirebaseDatabase.getInstance().getReference().child("blip").orderByChild("userId").equalTo(currentUser);
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
                        results = sortByLikes( temp);
                        taskDone(true);
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
                //TODO: Return an array of blips that are in this community ordered by most recent.
                results.clear();
                try {
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
                //TODO: Return an array of blips that are in this community ordered by most recent.

                results.clear();
                temp.clear();
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
                //TODO: Return an array of blips that are replys to the parent blip ordered by most recent
                results.clear();
                try {
                    Query q = FirebaseDatabase.getInstance().getReference().child("blip").orderByChild("parent").equalTo(parentId);
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
                //TODO: Return an array of blips that are replys to the parent blip ordered by most liked
                temp.clear();
                results.clear();
                try {
                    Query q = FirebaseDatabase.getInstance().getReference().child("blip").orderByChild("parent").equalTo(parentId);
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

        ArrayList<Blipp> finalList = new ArrayList<Blipp>();
        int[][] arr = new int[temp.size()][2];
        for(int i = 0; i < temp.size(); i++){
            arr[i][0] = temp.get(i).getNumOfLikes();
            arr[i][1] = i;
            //for (int j = 0; j <temp.size(); j++){ }
        }
        Arrays.sort(arr, new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                return Integer.compare(o2[1], o1[1]);
            }
        });
        finalList.clear();
        for (int j = 0; j <arr.length; j++){
            finalList.add(temp.get(arr[j][1]));
        }

        return finalList;
    }
}
