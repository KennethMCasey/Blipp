package nourl.tbd.Blipp.Database;

import android.os.AsyncTask;

import nourl.tbd.Blipp.BlippConstructs.User;

public class UserDeleter extends AsyncTask<Void, Void, Void> {

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
        completion.userDeleterDone(true);//call if successful
        completion.userDeleterDone(false);//call if failed
        return null;
    }
}
