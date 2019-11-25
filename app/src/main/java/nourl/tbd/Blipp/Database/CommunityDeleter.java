package nourl.tbd.Blipp.Database;

import android.os.AsyncTask;

import nourl.tbd.Blipp.BlippConstructs.Community;


public class CommunityDeleter extends AsyncTask<Void, Boolean, Void>
{
    private Community community;
    CommunityDeleterCompletion completion;

    public CommunityDeleter(Community community, CommunityDeleterCompletion completion)
    {
        this.community = community;
        this.completion = completion;
        this.execute();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        //TODO: Delete the passed community from firebase. Delete every trace of the community ie. all the member instances, all of its blips ect.

        publishProgress(true);//pass true isf successful false if failure

        return null;
    }


    @Override
    protected void onProgressUpdate(Boolean... values) {
        completion.communityDeleterDone(values[0]);
    }
}
