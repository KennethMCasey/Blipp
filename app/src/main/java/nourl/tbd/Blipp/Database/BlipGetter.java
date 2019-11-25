package nourl.tbd.Blipp.Database;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

import nourl.tbd.Blipp.BlippConstructs.Blipp;

public class BlipGetter extends AsyncTask<Void, Void, Void> {

    //where you should store your results
    ArrayList<Blipp> results;

    //these instance variables are used to determine which query to run, these will not be used in queries.
    Section section;
    Order order;
    Distance distance;

    //The completion object, already implemented.
    BlipGetterCompletion completion;
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

    @Override
    protected Void doInBackground(Void... voids) {

        //Queries for blips that are not in a community and do not have a parent
        if (communityId == null && parentId == null) {

            if (section.equals(Section.FEED)) {
                if (distance.equals(Distance.CLOSE)) {

                    if (order.equals(Order.MOST_RECENT)) {
                        //TODO: Wrire a query that will return an array of blips from firebase that are within close distance of the current user and marked as close distance ordered by most recent
                        //Note: Close distance is defined as 0Miles - 0.1Miles

                        //Test Code: Delete me.
                        ArrayList<Blipp> temp = new ArrayList<Blipp>();
                        try {
                            for (int i = 0; i < (((int) (Math.random() * 10)) + 1); i++)
                                temp.add(new Blipp(0.0, 0.0, false, false, true, new Date(), "fake id", "Most Recent - Close Distance", new URL("http://fake.com/"), "fake id"));
                        } catch (Exception e) {
                        }
                        results = temp;
                        taskDone(true);
                    }

                    if (order.equals(Order.MOST_LIKED)) {
                        //TODO: Wrire a query that will return an array of blips from firebase that are within close distance of the current user and marked as close distance ordered by most liked
                        //Note: Close distance is defined as 0Miles - 0.1Miles

                        //Test Code: Delete me.
                        ArrayList<Blipp> temp = new ArrayList<Blipp>();
                        try {
                            for (int i = 0; i < (((int) (Math.random() * 10)) + 1); i++)
                                temp.add(new Blipp(0.0, 0.0, false, false, true, new Date(), "fake id", "Most Liked - Close Distance", new URL("http://fake.com/"), "fake id"));
                        } catch (Exception e) {
                        }
                        results = temp;
                        taskDone(true);
                    }

                }

                if (distance.equals(Distance.REGULAR)) {
                    if (order.equals(Order.MOST_RECENT)) {
                        //TODO: Wrire a query that will return an array of blips from firebase that are within regular distance of the current user and marked as regular distance ordered by most recent
                        //Note: regular is defined as 0Miles - 1Miles

                        //Test Code: Delete me.
                        ArrayList<Blipp> temp = new ArrayList<Blipp>();
                        try {
                            for (int i = 0; i < (((int) (Math.random() * 10)) + 1); i++)
                                temp.add(new Blipp(0.0, 0.0, false, false, true, new Date(), "fake id", "Most Recent - Regular Distance", new URL("http://fake.com/"), "fake id"));
                        } catch (Exception e) {
                        }
                        results = temp;
                        taskDone(true);
                    }

                    if (order.equals(Order.MOST_LIKED)) {
                        //TODO: Wrire a query that will return an array of blips from firebase that are within regular distance of the current user and marked as regular distance ordered by most liked
                        //Note: regular is defined as 0Miles - 1Miles

                        //Test Code: Delete me.
                        ArrayList<Blipp> temp = new ArrayList<Blipp>();
                        try {
                            for (int i = 0; i < (((int) (Math.random() * 10)) + 1); i++)
                                temp.add(new Blipp(0.0, 0.0, false, false, true, new Date(), "fake id", "Most Liked - Regular Distance", new URL("http://fake.com/"), "fake id"));
                        } catch (Exception e) {
                        }
                        results = temp;
                        taskDone(true);
                    }
                }

                if (distance.equals(Distance.MAX)) {
                    if (order.equals(Order.MOST_RECENT)) {
                        //TODO: Wrire a query that will return an array of blips from firebase that are within Max distance of the current user and marked as Max distance ordered by most recent
                        //Note: MAx distance is defined as 0Miles - 10Miles

                        //Test Code: Delete me.
                        ArrayList<Blipp> temp = new ArrayList<Blipp>();
                        try {
                            for (int i = 0; i < (((int) (Math.random() * 10)) + 1); i++)
                                temp.add(new Blipp(0.0, 0.0, false, false, true, new Date(), "fake id", "Most Recent - Max Distance", new URL("http://fake.com/"), "fake id"));
                        } catch (Exception e) {
                        }
                        results = temp;
                        taskDone(true);
                    }

                    if (order.equals(Order.MOST_LIKED)) {
                        //TODO: Wrire a query that will return an array of blips from firebase that are within Max distance of the current user and marked as Max distance ordered by most liked
                        //Note: MAx distance is defined as 0Miles - 10Miles

                        //Test Code: Delete me.
                        ArrayList<Blipp> temp = new ArrayList<Blipp>();
                        try {
                            for (int i = 0; i < (((int) (Math.random() * 10)) + 1); i++)
                                temp.add(new Blipp(0.0, 0.0, false, false, true, new Date(), "fake id", "Most Liked - Max Distance", new URL("http://fake.com/"), "fake id"));
                        } catch (Exception e) {
                        }
                        results = temp;
                        taskDone(true);
                    }
                }
            }


            if (section.equals(Section.LIKED_BLIPS)) {

                if (order.equals(Order.MOST_RECENT)) {
                    //TODO: Wrire a query that will return an array of blips from firebase that the user has previously liked (no dislikes) order by most recent

                    //Test Code: Delete me.
                    ArrayList<Blipp> temp = new ArrayList<Blipp>();
                    try {
                        for (int i = 0; i < (((int) (Math.random() * 10)) + 1); i++)
                            temp.add(new Blipp(0.0, 0.0, false, false, true, new Date(), "fake id", "Most Recent", new URL("http://fake.com/"), "fake id"));
                    } catch (Exception e) {
                    }
                    results = temp;
                    taskDone(true);
                }

                if (order.equals(Order.MOST_LIKED)) {
                    //TODO: Wrire a query that will return an array of blips from firebase that the user has previously liked (no dislikes) order by most liked

                    //Test Code: Delete me.
                    ArrayList<Blipp> temp = new ArrayList<Blipp>();
                    try {
                        for (int i = 0; i < (((int) (Math.random() * 10)) + 1); i++)
                            temp.add(new Blipp(0.0, 0.0, false, false, true, new Date(), "fake id", "Most Liked", new URL("http://fake.com/"), "fake id"));
                    } catch (Exception e) {
                    }
                    results = temp;
                    taskDone(true);
                }
            }

            if (section.equals(Section.MY_BLIPS)) {
                if (order.equals(Order.MOST_RECENT)) {
                    //TODO: Wrire a query that will return an array of blips from firebase that the user has previously bliped, order by most recent

                    //Test Code: Delete me.
                    ArrayList<Blipp> temp = new ArrayList<Blipp>();
                    try {
                        for (int i = 0; i < (((int) (Math.random() * 10)) + 1); i++)
                            temp.add(new Blipp(0.0, 0.0, false, false, true, new Date(), "fake id", "Most Recent", new URL("http://fake.com/"), "fake id"));
                    } catch (Exception e) {
                    }
                    results = temp;
                    taskDone(true);
                }

                if (order.equals(Order.MOST_LIKED)) {
                    //TODO: Wrire a query that will return an array of blips from firebase that the user has previously bliped, order by most liked

                    //Test Code: Delete me.
                    ArrayList<Blipp> temp = new ArrayList<Blipp>();
                    try {
                        for (int i = 0; i < (((int) (Math.random() * 10)) + 1); i++)
                            temp.add(new Blipp(0.0, 0.0, false, false, true, new Date(), "fake id", "Most Liked", new URL("http://fake.com/"), "fake id"));
                    } catch (Exception e) {
                    }
                    results = temp;
                    taskDone(true);
                }
            }
        }

        //Community queries
        if (communityId != null) {
            if (order.equals(Order.MOST_RECENT))
            {
                //TODO: Return an array of blips that are in this community ordered by most recent.

                //Test Code: Delete me.
                ArrayList<Blipp> temp = new ArrayList<Blipp>();
                try {
                    for (int i = 0; i < (((int) (Math.random() * 10)) + 1); i++)
                        temp.add(new Blipp(0.0, 0.0, false, false, true, new Date(), "fake id", "Most Recent", new URL("http://fake.com/"), "fake id"));
                } catch (Exception e) {
                }
                results = temp;
                taskDone(true);
            }

            if (order.equals(Order.MOST_LIKED)) {
                //TODO: Return an array of blips that are in this community ordered by most recent.

                //Test Code: Delete me.
                ArrayList<Blipp> temp = new ArrayList<Blipp>();
                try {
                    for (int i = 0; i < (((int) (Math.random() * 10)) + 1); i++)
                        temp.add(new Blipp(0.0, 0.0, false, false, true, new Date(), "fake id", "Most Liked", new URL("http://fake.com/"), "fake id"));
                } catch (Exception e) {
                }
                results = temp;
                taskDone(true);
            }

        }

        //reply queries
        if (parentId != null) {
            if (order.equals(Order.MOST_RECENT)) {
                //TODO: Return an array of blips that are replys to the parent blip ordered by most recent
                //Test Code: Delete me.
                ArrayList<Blipp> temp = new ArrayList<Blipp>();
                try {
                    for (int i = 0; i < (((int) (Math.random() * 10)) + 1); i++)
                        temp.add(new Blipp(0.0, 0.0, false, false, true, new Date(), "fake id", "Most Liked", new URL("http://fake.com/"), "fake id"));
                } catch (Exception e) {
                }
                results = temp;
                taskDone(true);

            }

            if (order.equals(Order.MOST_LIKED)) {
                //TODO: Return an array of blips that are replys to the parent blip ordered by most liked
                //Test Code: Delete me.
                ArrayList<Blipp> temp = new ArrayList<Blipp>();
                try {
                    for (int i = 0; i < (((int) (Math.random() * 10)) + 1); i++)
                        temp.add(new Blipp(0.0, 0.0, false, false, true, new Date(), "fake id", "Most Liked", new URL("http://fake.com/"), "fake id"));
                } catch (Exception e) {
                }
                results = temp;
                taskDone(true);
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
}
