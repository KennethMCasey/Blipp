package nourl.tbd.Blipp.Database;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import nourl.tbd.Blipp.BlippConstructs.User;

public class UserGetter extends AsyncTask<Void, Void, User> {

    private String userID;//The ID of the user to get.

    private User user; // where you should store the got user.

    //Completion, already handled.
    UserGetterCompletion completion;
    Handler uiThread;

    public UserGetter(String userID, UserGetterCompletion completion, Context context)
    {
        this.userID = userID;
        this.completion = completion;
        uiThread = new Handler(context.getMainLooper());
        this.execute();
    }


    @Override
    protected User doInBackground(Void... voids)
    {
        //TODO: get the user object with the passed user id from firebase and assign it to the user class variable
       taskDone(true); // call me when done, true if success false if fail

        return null;
    }

 void taskDone(final boolean isSuccessful)
 {
     uiThread.post(new Runnable() {
         @Override
         public void run() {
          if (isSuccessful) completion.userGetterSuccess(user);
          else completion.userGetterFailure();
         }
     });
 }
}
