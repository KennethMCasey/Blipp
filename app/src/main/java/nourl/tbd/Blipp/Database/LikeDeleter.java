package nourl.tbd.Blipp.Database;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import nourl.tbd.Blipp.BlippConstructs.Like;

public class LikeDeleter extends AsyncTask<Void, Void, Void>
{
    Like like;
    LikeDeleterCompletion completion;
    Handler uiThread;

    public LikeDeleter(Like like, LikeDeleterCompletion completion, Context context)
    {
        this.like = like;
        this.completion = completion;
        uiThread = new Handler(context.getMainLooper());
        this.execute();
    }

    @Override
    protected Void doInBackground(Void... voids)
    {

        //this will only work with a like id attribute.
            FirebaseDatabase.getInstance().getReference().child("like")
                    .child(like.getId())
                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    taskDone(task.isSuccessful());
                }
            });
        return null;
    }
        /*try { //this deletes all likes for a certain blip
            FirebaseDatabase.getInstance("https://blipp-15ee8.firebaseio.com/").getReference().child("like")
                    .orderByChild("blipId")
                    .equalTo(like.getBlipId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                childSnapshot.getRef().removeValue();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }
        catch (Exception e){
            taskDone(false);
            return null;
        }
        taskDone(true);// call with true if success call with false if fail
        return null;
    }*/

    protected void taskDone(final boolean isSuccessful)
    {
        uiThread.post(new Runnable() {
            @Override
            public void run() {
                completion.likeDeleterDone(isSuccessful);
            }
        });
    }
}
