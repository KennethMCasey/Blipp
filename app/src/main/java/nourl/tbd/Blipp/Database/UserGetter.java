package nourl.tbd.Blipp.Database;

import android.os.AsyncTask;

import nourl.tbd.Blipp.BlippConstructs.User;

public class UserGetter extends AsyncTask<Void, Void, User> {

    private String userID;//The ID of the user to get.
    UserGetterCompletion completion;//Completion, already handled.

    public UserGetter(String userID, UserGetterCompletion completion)
    {
        this.userID = userID;
        this.completion = completion;
        this.execute();
    }


    @Override
    protected User doInBackground(Void... voids)
    {
        //TODO: get the user object with the passed user id from firebase and return it
       //call me if failed :  cancel(true);
        return null;
    }

    @Override
    protected void onCancelled() {
        completion.userGetterFailure();
    }

    @Override
    protected void onPostExecute(User user)
    {
        completion.userGetterSuccess(user);
    }
}
