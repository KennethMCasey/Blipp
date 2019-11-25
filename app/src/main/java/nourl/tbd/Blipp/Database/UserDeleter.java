package nourl.tbd.Blipp.Database;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import nourl.tbd.Blipp.BlippConstructs.User;

public class UserDeleter extends AsyncTask<Void, Void, Void> {

    //user to delete
    private User user;

    //completion
    UserDeleterCompletion completion;
    Handler uiThread;

    public UserDeleter(User user, UserDeleterCompletion completion, Context context)
    {
        this.user = user;
        this.completion = completion;
        uiThread = new Handler(context.getMainLooper());
        this.execute();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        //TODO: delete the passed user from the firebase user table
       taskDone(true);// call with true if success call with false if fail
        return null;
    }

    void taskDone(final boolean isSuccessful)
    {
        uiThread.post(new Runnable() {
            @Override
            public void run() {
                completion.userDeleterDone(isSuccessful);
            }
        });
    }
}
