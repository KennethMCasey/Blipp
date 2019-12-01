package nourl.tbd.Blipp.Database;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import nourl.tbd.Blipp.BlippConstructs.Blipp;
import nourl.tbd.Blipp.BlippConstructs.Like;

public class LikeSender extends AsyncTask<Void, Void, Void> {

    FirebaseDatabase db;
    DatabaseReference location;
    Like like;
    LikeSenderCompletion completion;
    Handler uiThread;

    public LikeSender(Like like, LikeSenderCompletion completion, Context context)
    {
        this.like = like;
        this.completion = completion;
        db = FirebaseDatabase.getInstance("https://blipp-15ee8.firebaseio.com/");
        location = db.getReference().child("like");
        uiThread = new Handler(context.getMainLooper());
        this.execute();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        FirebaseDatabase.getInstance().getReference(".info/connected").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                boolean connected = dataSnapshot.getValue(boolean.class);
                if (connected) {
                    DatabaseReference here = location.push();
                    here.setValue(like).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            taskDone(task.isSuccessful());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            taskDone(false);
                        }
                    });
                }
                else taskDone(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
                taskDone(false);
            }
        });
        return null;
    }

  void taskDone(final boolean isSuccessful)
  {
      uiThread.post(new Runnable() {
          @Override
          public void run() {
              completion.likeSenderDone(isSuccessful);
          }
      });
  }
}
