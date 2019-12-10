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
import nourl.tbd.Blipp.BlippConstructs.User;

public class UserGetter extends AsyncTask<Void, Void, User> {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMemberDatabaseReference;

    private String userID;//The ID of the user to get.

    private User user; // where you should store the got user.

    //Completion, already handled.
    UserGetterCompletion completion;
    Handler uiThread;

    public UserGetter(String userID, UserGetterCompletion completion, Context context)
    {
        this.userID = userID;
        this.completion = completion;
        uiThread = new Handler(context.getMainLooper());
        this.execute();
    }


    @Override
    protected User doInBackground(Void... voids)
    {
        mFirebaseDatabase = FirebaseDatabase.getInstance("https://blipp-15ee8.firebaseio.com/");
        mMemberDatabaseReference = mFirebaseDatabase.getReference("user");

        Query query = mMemberDatabaseReference;
        query.orderByChild("id").equals(userID);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        user = snapshot.getValue(User.class);
                    }
                    taskDone(true);
                }else {
                    taskDone(false);
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
                if (isSuccessful) completion.userGetterSuccess(user);
                else completion.userGetterFailure();
            }
        });
    }
}
