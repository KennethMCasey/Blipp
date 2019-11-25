package nourl.tbd.Blipp.Database;

import android.os.AsyncTask;

import nourl.tbd.Blipp.BlippConstructs.Member;

public class MemberDeleter extends AsyncTask<Void, Boolean, Void>
{
    private Member member;
    MemberDeleterCompletion completion;

    public MemberDeleter(Member member, MemberDeleterCompletion completion)
    {
        this.member = member;
        this.completion = completion;
        this.execute();
    }


    @Override
    protected Void doInBackground(Void... voids) {
        //TODO: write function that will delete the current member from firebase
       publishProgress(true); //call with true if success call with false if fail
        return null;
    }


    @Override
    protected void onProgressUpdate(Boolean... values) {
        completion.memberDeleterDone(values[0]);
    }
}
