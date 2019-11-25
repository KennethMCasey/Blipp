package nourl.tbd.Blipp.Database;

import android.os.AsyncTask;

import nourl.tbd.Blipp.BlippConstructs.Blipp;

public class BlipDeleter extends AsyncTask<Void, Void, Void> {

    private Blipp blip;
    BlipDeleterCompletion completion;

    public BlipDeleter(Blipp blip, BlipDeleterCompletion completion)
    {
        this.completion = completion;
        this.blip = blip;
        this.execute();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        //TODO: Write a Query that Successfully deletes the passed blipp from firebase.

        blip = blip; // <-- blip to delete : class variable
        completion.blippDeleterDone(true); /// <--- call this if successful
        completion.blippDeleterDone(false); /// <--- call this if unsuccessful
        return null;
    }
}
