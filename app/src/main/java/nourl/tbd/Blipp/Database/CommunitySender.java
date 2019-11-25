package nourl.tbd.Blipp.Database;

import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import nourl.tbd.Blipp.BlippConstructs.Community;

public class CommunitySender extends AsyncTask<Void, Boolean, Void> {


    FirebaseDatabase db;
    DatabaseReference location;
    Community community;
    CommunitySenderCompletion completion;

    public CommunitySender(Community community, CommunitySenderCompletion completion)
    {
        this.completion = completion;
        this.community = community;
        db = FirebaseDatabase.getInstance("https://blipp-15ee8.firebaseio.com/");
        location = db.getReference().child("community");
        this.execute();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        DatabaseReference here = location.push();
        here.setValue(community.withId(here.getKey())).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                publishProgress(task.isSuccessful());
            }
        });
        return null;
    }


    @Override
    protected void onProgressUpdate(Boolean... values) {
        completion.communitySenderDone(values[0]);
    }
}


