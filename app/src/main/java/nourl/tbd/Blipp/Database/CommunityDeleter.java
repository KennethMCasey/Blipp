package nourl.tbd.Blipp.Database;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import nourl.tbd.Blipp.BlippConstructs.Community;


public class CommunityDeleter extends AsyncTask<Void, Void, Void>
{
    //community to delete
    private Community community;

    //completion objects
    CommunityDeleterCompletion completion;
    Handler uiThread;

    public CommunityDeleter(Community community, CommunityDeleterCompletion completion, Context context)
    {
        this.community = community;
        this.completion = completion;
        uiThread = new Handler(context.getMainLooper());
        this.execute();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        taskDone(true);//pass true if successful false if failure

        return null;
    }


    protected void taskDone(final boolean isSuccessful)
    {
        uiThread.post(new Runnable() {
            @Override
            public void run()
            {
                completion.communityDeleterDone(isSuccessful);
            }
        });

    }

}
