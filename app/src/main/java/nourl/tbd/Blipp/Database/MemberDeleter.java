package nourl.tbd.Blipp.Database;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import nourl.tbd.Blipp.BlippConstructs.Member;

public class MemberDeleter extends AsyncTask<Void, Void, Void>
{
    private Member member;
    MemberDeleterCompletion completion;
    Handler uiThread;

    public MemberDeleter(Member member, MemberDeleterCompletion completion, Context context)
    {
        this.member = member;
        this.completion = completion;
        uiThread = new Handler(context.getMainLooper());
        this.execute();
    }


    @Override
    protected Void doInBackground(Void... voids) {
         taskDone(true); //call with true if success call with false if fail

        return null;
    }


    void taskDone(final boolean isSuccessful)
    {
        uiThread.post(new Runnable() {
            @Override
            public void run() {
                completion.memberDeleterDone(isSuccessful);
            }
        });
    }
}
