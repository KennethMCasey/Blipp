package nourl.tbd.Blipp.Database;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import nourl.tbd.Blipp.BlippConstructs.Community;

public class CommunityGetter extends AsyncTask<Void, Void, Void> {

    //where query results will be stored
    ArrayList<Community> results;

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
        this.execute();
    }


    @Override
    protected Void doInBackground(Void... voids)
    {
        if (section.equals(Section.DISCOVER))
        {
            if (order.equals(Order.ALPHABETICAL))
            {
                //TODO: pull communities that are within range of the current user, order by alphabetical order.

                //test code delete me
                ArrayList<Community> temp = new ArrayList<>();
                for (int i = 0; i < (((int) (Math.random() * 10)) + 1); i++) temp.add(new Community("fake id", null, 0.0, 0.0, 0.0, "fake name", true, FirebaseAuth.getInstance().getCurrentUser().getUid()));
                results = temp;
                taskDone(true);
            }

            if (order.equals(Order.MEMBER_COUNT_LOW_TO_HIGH))
            {
                //TODO: pull communities that are within range of the current user, order by member count low to high.


                //test code delete me
                ArrayList<Community> temp = new ArrayList<>();
                for (int i = 0; i < (((int) (Math.random() * 10)) + 1); i++) temp.add(new Community("fake id", null, 0.0, 0.0, 0.0, "fake name", true, FirebaseAuth.getInstance().getCurrentUser().getUid()));
                results = temp;
                taskDone(true);
            }

            if (order.equals(Order.MEMBER_COUNT_HIGH_TO_LOW))
            {
                //TODO: pull communities that are within range of the current user, order by member count high to low.


                //test code delete me
                ArrayList<Community> temp = new ArrayList<>();
                for (int i = 0; i < (((int) (Math.random() * 10)) + 1); i++) temp.add(new Community("fake id", null, 0.0, 0.0, 0.0, "fake name", true, FirebaseAuth.getInstance().getCurrentUser().getUid()));
                results = temp;
                taskDone(true);
            }
        }

        if (section.equals(Section.JOINED))
        {
            if (order.equals(Order.ALPHABETICAL))
            {
                //TODO: pull communities that the user has joined in alphabetical order


                //test code delete me
                ArrayList<Community> temp = new ArrayList<>();
                for (int i = 0; i < (((int) (Math.random() * 10)) + 1); i++) temp.add(new Community("fake id", null, 0.0, 0.0, 0.0, "fake name", true, FirebaseAuth.getInstance().getCurrentUser().getUid()));
                results = temp;
                taskDone(true);
            }

            if (order.equals(Order.MEMBER_COUNT_LOW_TO_HIGH))
            {
                //TODO: pull communities that the user has joined ordered by member count low to high


                //test code delete me
                ArrayList<Community> temp = new ArrayList<>();
                for (int i = 0; i < (((int) (Math.random() * 10)) + 1); i++) temp.add(new Community("fake id", null, 0.0, 0.0, 0.0, "fake name", true, FirebaseAuth.getInstance().getCurrentUser().getUid()));
                results = temp;
                taskDone(true);
            }

            if (order.equals(Order.MEMBER_COUNT_HIGH_TO_LOW))
            {
                //TODO: pull communities that the user has joined ordered by member count high to low


                //test code delete me
                ArrayList<Community> temp = new ArrayList<>();
                for (int i = 0; i < (((int) (Math.random() * 10)) + 1); i++) temp.add(new Community("fake id", null, 0.0, 0.0, 0.0, "fake name", true, FirebaseAuth.getInstance().getCurrentUser().getUid()));
                results = temp;
                taskDone(true);
            }
        }

        if (section.equals(Section.OWN))
        {
            if (order.equals(Order.ALPHABETICAL))
            {
                //TODO: pull all the communities that the user owns in alphabetical order


                //test code delete me
                ArrayList<Community> temp = new ArrayList<>();
                for (int i = 0; i < (((int) (Math.random() * 10)) + 1); i++) temp.add(new Community("fake id", null, 0.0, 0.0, 0.0, "fake name", true, FirebaseAuth.getInstance().getCurrentUser().getUid()));
                results = temp;
                taskDone(true);;
            }

            if (order.equals(Order.MEMBER_COUNT_LOW_TO_HIGH))
            {
                //TODO: pull all the communities that the user owns order by member count low to high


                //test code delete me
                ArrayList<Community> temp = new ArrayList<>();
                for (int i = 0; i < (((int) (Math.random() * 10)) + 1); i++) temp.add(new Community("fake id", null, 0.0, 0.0, 0.0, "fake name", true, FirebaseAuth.getInstance().getCurrentUser().getUid()));
                results = temp;
                taskDone(true);
            }

            if (order.equals(Order.MEMBER_COUNT_HIGH_TO_LOW))
            {
                //TODO: pull all the communities that the user owns order by member count high to low


                //test code delete me
                ArrayList<Community> temp = new ArrayList<>();
                for (int i = 0; i < (((int) (Math.random() * 10)) + 1); i++) temp.add(new Community("fake id", null, 0.0, 0.0, 0.0, "fake name", true, FirebaseAuth.getInstance().getCurrentUser().getUid()));
                results = temp;
                taskDone(true);
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

}
