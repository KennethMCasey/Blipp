package nourl.tbd.Blipp.Database;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    private Context context;

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
        this.context = context;
        this.execute();
    }

    @Override
    protected Void doInBackground(Void... voids) {

        // blip to delete : class variable blip


        FirebaseDatabase.getInstance().getReference().child("blip").child(blip.getId()).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    dataSnapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            taskDone(task.isSuccessful());
                        }
                    });
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
