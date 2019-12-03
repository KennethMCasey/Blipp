package nourl.tbd.Blipp.Database;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import nourl.tbd.Blipp.BlippConstructs.Blipp;

public class BlipDeleter extends AsyncTask<Void, Void, Void> {

    // Firebase instance variables
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mBlipDatabaseReference;

    //Blip to delete
    private Blipp blip;

    //finisher
    BlipDeleterCompletion completion;
    Handler mainThread;

    public BlipDeleter(Blipp blip, BlipDeleterCompletion completion, Context context)
    {
        this.completion = completion;
        this.blip = blip;
        this.mainThread = new Handler(context.getMainLooper());
        this.execute();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        //TODO: Write a Query that Successfully deletes the passed blipp from firebase.

        // blip to delete : class variable blip

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mBlipDatabaseReference = mFirebaseDatabase.getReference("blip").child(blip.getId());

        mBlipDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(blip.getId())){
                    mBlipDatabaseReference.removeValue();
                    taskDone(true);// pass true on success on false on failure
                }else{
                    taskDone(false);// pass true on success on false on failure
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        return null;
    }

    protected void taskDone(final boolean isSuccessful)
    {
        mainThread.post(new Runnable() {
            @Override
            public void run() {
                completion.blippDeleterDone(isSuccessful);
            }
        });
    }
}
