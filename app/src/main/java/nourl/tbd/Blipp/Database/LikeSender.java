package nourl.tbd.Blipp.Database;

import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import nourl.tbd.Blipp.BlippConstructs.Blipp;
import nourl.tbd.Blipp.BlippConstructs.Like;

public class LikeSender extends AsyncTask<Void, Void, Void> {

    FirebaseDatabase db;
    DatabaseReference location;
    Like like;
    LikeSenderCompletion completion;

    public LikeSender(Like like, LikeSenderCompletion completion)
    {
        this.like = like;
        this.completion = completion;
        db = FirebaseDatabase.getInstance("https://blipp-15ee8.firebaseio.com/");
        location = db.getReference().child("like");
    }

    @Override
    protected Void doInBackground(Void... voids) {
        DatabaseReference here = location.push();
        here.setValue(like).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful()) completion.likeSenderDone(true);
                else completion.likeSenderDone(false);
            }
        });
        return null;
    }
}
