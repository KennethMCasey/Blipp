package nourl.tbd.Blipp.Database;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import nourl.tbd.Blipp.BlippConstructs.Community;


public class CommunityDeleter extends AsyncTask<Void, Void, Void>
{
    //community to delete
    private Community community;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mCommunityDatabaseReference, mMember;
    private Context context;
    private DatabaseReference location;
    private FirebaseDatabase db;
    //completion objects
    CommunityDeleterCompletion completion;
    Handler uiThread;

    public CommunityDeleter(Community community, CommunityDeleterCompletion completion, Context context)
    {
        this.community = community;
        this.completion = completion;
        this.db = FirebaseDatabase.getInstance("https://blipp-15ee8.firebaseio.com/");
        this.location = db.getReference().child("community");
        uiThread = new Handler(context.getMainLooper());
        this.execute();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mCommunityDatabaseReference = mFirebaseDatabase.getReference("community").child(community.getId());
        mCommunityDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(community.getId())){
                    mCommunityDatabaseReference.removeValue();

                }else{
                    taskDone(false);// pass true on success on false on failure
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                taskDone(false);
            }
        });



        location = db.getReference("blipp");
        location.child("community")
                .child("name")
                .child(community.getName())
                .removeValue();

        location = db.getReference("member");
        location.child("communityId");




        taskDone(true);//pass true if successful false if failure

        return null;
    }


    protected void taskDone(boolean isSuccessful)
    {
        completion.communityDeleterDone(isSuccessful);
    }

}
