package nourl.tbd.Blipp.Database;

import android.os.AsyncTask;

import nourl.tbd.Blipp.BlippConstructs.Community;


public class CommunityDeleter extends AsyncTask<Void, Void, Void>
{
    Community community;
    CommunityDeleterCompletion completion;

    public CommunityDeleter(Community community, CommunityDeleterCompletion completion)
    {
        this.community = community;
        this.completion = completion;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        //TODO: Delete the passed community from firebase. Delete every trace of the community ie. all the member instances, all of its blips ect.

        completion.communityDeleterDone(true);//call if sucessful
        completion.communityDeleterDone(false);//call if unsucessful

        return null;
    }
}
