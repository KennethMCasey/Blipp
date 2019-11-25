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
        //TODO: Delete the passed community from firebase. Delete every trace of the community ie. all the member instances, all of its blips ect.

        taskDone(true);//pass true if successful false if failure

        return null;
    }


    protected void taskDone(boolean isSuccessful)
    {
        completion.communityDeleterDone(isSuccessful);
    }

}
