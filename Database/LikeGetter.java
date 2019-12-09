package nourl.tbd.Blipp.Database;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import nourl.tbd.Blipp.BlippConstructs.Blipp;
import nourl.tbd.Blipp.BlippConstructs.Like;

public class LikeGetter extends AsyncTask<Void, Void, Void>
{

    Blipp blip;//The blip you are getting the array of likes for

    ArrayList<Like> results;//the array where you should store results

    //The completion handler, already implemented.
    LikeGetterCompletion completion;
    Handler uiThread;

    DatabaseReference dbr;

    public LikeGetter(Blipp blip, LikeGetterCompletion completion, Context context)
    {
        this.completion = completion;
        this.blip = blip;
        uiThread = new Handler(context.getMainLooper());
        this.execute();
    }



    @Override
    protected Void doInBackground(Void... voids)
    {
        //call task done with true or false when done
        dbr = FirebaseDatabase.getInstance().getReference();
        Query query = dbr.child("like")
                .orderByChild("blipId")
                .equalTo(blip.getId());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    results = dataSnapshot.hasChildren() ?  new ArrayList<Like>() : null;
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        results.add(snapshot.getValue(Like.class));
                    }
                    taskDone(true);
                }else
                    {
                        results = new ArrayList<>();
                    taskDone(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
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
                if (isSuccessful) completion.likeGetterSucessful(results);
                else completion.likeGetterUnsucessful();
            }
        });
    }
}
