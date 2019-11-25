package nourl.tbd.Blipp.Database;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import nourl.tbd.Blipp.BlippConstructs.Blipp;

public class BlipDeleter extends AsyncTask<Void, Void, Void> {

    //Blip to delete
    private Blipp blip;

    //finisher
    BlipDeleterCompletion completion;
    Handler mainThread;

    public BlipDeleter(Blipp blip, BlipDeleterCompletion completion, Context context)
    {
        this.completion = completion;
        this.blip = blip;
        this.mainThread = new Handler(context.getMainLooper());
        this.execute();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        //TODO: Write a Query that Successfully deletes the passed blipp from firebase.

        // blip to delete : class variable blip
        taskDone(true);// pass true on success on false on failure
        return null;
    }

   protected void taskDone(final boolean isSuccessful)
   {
       mainThread.post(new Runnable() {
           @Override
           public void run() {
               completion.blippDeleterDone(isSuccessful);
           }
       });
   }
}
