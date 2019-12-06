package nourl.tbd.Blipp.Database;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import nourl.tbd.Blipp.BlippConstructs.Blipp;
import nourl.tbd.Blipp.BlippConstructs.Like;

public class LikeGetter extends AsyncTask<Void, Void, Void>
{

    Blipp blip;//The blip you are getting the array of likes for

    ArrayList<Like> results;//the array where you should store results

    //The completion handler, already implemented.
    LikeGetterCompletion completion;
    Handler uiThread;


    public LikeGetter(Blipp blip, LikeGetterCompletion completion, Context context)
    {
        this.completion = completion;
        this.blip = blip;
        uiThread = new Handler(context.getMainLooper());
        this.execute();
    }



    @Override
    protected Void doInBackground(Void... voids)
    {
        //Test code delete me
        ArrayList<Like> temp = new ArrayList<>();
        for (int i = 0; i < (((int) (Math.random() * 10)) + 1); i++) temp.add(new Like(false, "Fake ID", FirebaseAuth.getInstance().getCurrentUser().getUid()));
        results = temp;
        taskDone(true);

        return null;
    }

    void taskDone(final boolean isSuccessful)
    {
        uiThread.post(new Runnable() {
            @Override
            public void run() {
            if (isSuccessful) completion.likeGetterSucessful(results);
            else completion.likeGetterUnsucessful();
            }
        });
    }
}
