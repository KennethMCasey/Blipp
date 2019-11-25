package nourl.tbd.Blipp.Database;

import android.os.AsyncTask;

import com.google.firebase.auth.FirebaseAuth;

import nourl.tbd.Blipp.BlippConstructs.Blipp;
import nourl.tbd.Blipp.BlippConstructs.Like;

public class LikeDeleter extends AsyncTask<Void, Void, Void>
{
    Like like;
    LikeDeleterCompletion completion;

    public LikeDeleter(Like like, LikeDeleterCompletion completion)
    {
       this.like = like;
        this.completion = completion;
        this.execute();
    }

    @Override
    protected Void doInBackground(Void... voids)
    {
        //TODO: Delete the like on the passed blip by the current user in firebase
        completion.likeDeleterDone(true);//call me if successful
        completion.likeDeleterDone(false);//call me if failed
        return null;
    }
}
