package nourl.tbd.Blipp.Database;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import nourl.tbd.Blipp.BlippConstructs.Blipp;
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
        //TODO: Delete the like on the passed blip by the current user in firebase
        try {//this will only work with a like id attribute.
            FirebaseDatabase.getInstance("https://blipp-15ee8.firebaseio.com/")
                    .getReference("like")
                    .child(like.getId())
                    .removeValue();
        }
        catch (Exception e){
            taskDone(false);
            return null;
        }
        taskDone(true);// call with true if success call with false if fail
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
