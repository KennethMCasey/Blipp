package nourl.tbd.Blipp.Database;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;

import nourl.tbd.Blipp.BlippConstructs.Blipp;
import nourl.tbd.Blipp.BlippConstructs.Like;

public class LikeDeleter extends AsyncTask<Void, Void, Void>
{
    Like like;
    LikeDeleterCompletion completion;
    Handler uiThread;

    public LikeDeleter(Like like, LikeDeleterCompletion completion, Context context)
    {
        this.like = like;
        this.completion = completion;
        uiThread = new Handler(context.getMainLooper());
        this.execute();
    }

    @Override
    protected Void doInBackground(Void... voids)
    {
        //TODO: Delete the like on the passed blip by the current user in firebase
       taskDone(true); //pass true if success false if fail
        return null;
    }

   protected void taskDone(final boolean isSuccessful)
   {
       uiThread.post(new Runnable() {
           @Override
           public void run() {
               completion.likeDeleterDone(isSuccessful);
           }
       });
   }
}
