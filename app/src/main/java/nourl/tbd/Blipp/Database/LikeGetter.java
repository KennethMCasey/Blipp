package nourl.tbd.Blipp.Database;

import android.os.AsyncTask;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import nourl.tbd.Blipp.BlippConstructs.Blipp;
import nourl.tbd.Blipp.BlippConstructs.Like;

public class LikeGetter extends AsyncTask<Void, Void, ArrayList<Like>>
{

    Blipp blip;//The blip you are getting the array of likes for
    LikeGetterCompletion completion;//The completion handler, already implemented.


    public LikeGetter(Blipp blip, LikeGetterCompletion completion)
    {
        this.completion = completion;
        this.blip = blip;
        this.execute();
    }



    @Override
    protected ArrayList<Like> doInBackground(Void... voids)
    {
        //TODO: Write firebase query that will return the ArrayList of ALL Likes on The Blip
        //return like array list if successful
        //call cancel(true); if failed

        //Test code delete me
        ArrayList<Like> temp = new ArrayList<>();
        for (int i = 0; i < (((int) (Math.random() * 10)) + 1); i++) temp.add(new Like(false, "Fake ID", FirebaseAuth.getInstance().getCurrentUser().getUid()));
        return temp;
    }

    @Override
    protected void onPostExecute(ArrayList<Like> results)
    {
        completion.likeGetterSucessful(results);
    }

    @Override
    protected void onCancelled()
    {
        completion.likeGetterUnsucessful();
    }


    protected class Results
    {
        int numOfLikes;
        boolean didCurrentUserLike;
        boolean didCurrentUserDislike;

        public Results(int numOfLikes, boolean didCurrentUserLike, boolean didCurrentUserDislike) {
            this.numOfLikes = numOfLikes;
            this.didCurrentUserLike = didCurrentUserLike;
            this.didCurrentUserDislike = didCurrentUserDislike;
        }
    }
}
