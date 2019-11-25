package nourl.tbd.Blipp.Database;

import android.os.AsyncTask;

import nourl.tbd.Blipp.BlippConstructs.Member;

public class MemberDeleter extends AsyncTask<Void, Void, Void>
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
        completion.memberDeleterDone(true);//call if sucessful
        completion.memberDeleterDone(false);//call if failed
        return null;
    }
}
