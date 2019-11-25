package nourl.tbd.Blipp.Database;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import nourl.tbd.Blipp.BlippConstructs.Community;

public class CommunitySender extends AsyncTask<Void, Void, Void> {


    FirebaseDatabase db;
    DatabaseReference location;
    Community community;
    CommunitySenderCompletion completion;
    Handler uiThread;

    public CommunitySender(Community community, CommunitySenderCompletion completion, Context context)
    {
        this.completion = completion;
        this.community = community;
        db = FirebaseDatabase.getInstance("https://blipp-15ee8.firebaseio.com/");
        location = db.getReference().child("community");
        uiThread = new Handler(context.getMainLooper());
        this.execute();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        DatabaseReference here = location.push();
        here.setValue(community.withId(here.getKey())).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                taskDone(task.isSuccessful());
            }
        });
        return null;
    }

protected void taskDone(final boolean isSuccessful)
{
    uiThread.post(new Runnable() {
        @Override
        public void run() {
            completion.communitySenderDone(isSuccessful);
        }
    });
}

}


