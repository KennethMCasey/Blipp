package nourl.tbd.Blipp.Database;

import android.os.AsyncTask;

import com.google.firebase.auth.FirebaseAuth;

import nourl.tbd.Blipp.BlippConstructs.Blipp;
import nourl.tbd.Blipp.BlippConstructs.Like;

public class LikeDeleter extends AsyncTask<Void, Boolean, Void>
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
        publishProgress(true); //pass true if success false if fail
        return null;
    }

    @Override
    protected void onProgressUpdate(Boolean... values) {
        completion.likeDeleterDone(values[0]);
    }
}
