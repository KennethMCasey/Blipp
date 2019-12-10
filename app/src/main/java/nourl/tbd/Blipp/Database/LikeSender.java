package nourl.tbd.Blipp.Database;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import nourl.tbd.Blipp.BlippConstructs.Like;

public class LikeSender extends AsyncTask<Void, Void, Void> {

    FirebaseDatabase db;
    DatabaseReference location;
    Like like;
    LikeSenderCompletion completion;
    Handler uiThread;

    public LikeSender(Like like, LikeSenderCompletion completion, Context context) {
        this.like = like;
        this.completion = completion;
        db = FirebaseDatabase.getInstance("https://blipp-15ee8.firebaseio.com/");
        location = db.getReference().child("like");
        uiThread = new Handler(context.getMainLooper());
        this.execute();
    }

    @Override
    protected Void doInBackground(Void... voids) {

        DatabaseReference here = location.push();
        here.setValue(like.withId(here.getKey())).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull final Task<Void> task)
            {
                if (task.isSuccessful())
                {
                    FirebaseDatabase.getInstance().getReference().runTransaction(new Transaction.Handler() {
                        @NonNull
                        @Override
                        public Transaction.Result doTransaction(@NonNull MutableData mutableData)
                        {
                            FirebaseDatabase.getInstance().getReference().child("blip").child(like.getBlipId()).child("numOfLikes").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                {
                                    int i = dataSnapshot.getValue(int.class) == null ? 0 : dataSnapshot.getValue(int.class);

                                    FirebaseDatabase.getInstance().getReference().child("blip").child(like.getBlipId()).child("numOfLikes").setValue(i+1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            taskDone(task.isSuccessful());
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError)
                                {
                                    taskDone(false);
                                }
                            });
                            return null;
                        }

                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot)
                        {

                        }
                    });
                }
                else taskDone(false);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                taskDone(false);
            }
        });
        return null;
    }

    void taskDone(final boolean isSuccessful) {
        uiThread.post(new Runnable() {
            @Override
            public void run() {
                completion.likeSenderDone(isSuccessful);
            }
        });
    }
}
