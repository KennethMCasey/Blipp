package nourl.tbd.Blipp.Database;

import android.os.AsyncTask;

import nourl.tbd.Blipp.BlippConstructs.User;

public class UserDeleter extends AsyncTask<Void, Boolean, Void> {

    private User user;
    UserDeleterCompletion completion;

    public UserDeleter(User user, UserDeleterCompletion completion)
    {
        this.user = user;
        this.completion = completion;
        this.execute();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        //TODO: delete the passed user from the firebase user table
        publishProgress(true);// call with true if success call with false if fail
        return null;
    }

    @Override
    protected void onProgressUpdate(Boolean... values) {
        completion.userDeleterDone(values[0]);
    }
}
