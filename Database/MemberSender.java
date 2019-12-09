package nourl.tbd.Blipp.Database;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import nourl.tbd.Blipp.BlippConstructs.Member;

public class MemberSender extends AsyncTask<Void, Boolean, Void> {

    FirebaseDatabase db;
    DatabaseReference location;
    Member member;
    MemberSenderCompletion completion;
    Handler uiThread;

    public MemberSender(Member member, MemberSenderCompletion completion, Context context) {
        this.member = member;
        this.completion = completion;
        db = FirebaseDatabase.getInstance("https://blipp-15ee8.firebaseio.com/");
        location = db.getReference().child("member");
        this.uiThread = new Handler(context.getMainLooper());
        this.execute();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        DatabaseReference here = location.push();
        here.setValue(member).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful())
                {
                    FirebaseDatabase.getInstance().getReference().runTransaction(new Transaction.Handler() {
                        @NonNull
                        @Override
                        public Transaction.Result doTransaction(@NonNull MutableData mutableData)
                        {
                            FirebaseDatabase.getInstance().getReference().child("community").child(member.getCommunityId()).child("numMembers").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                {
                                    int i = dataSnapshot.getValue(int.class) == null ? 0 : dataSnapshot.getValue(int.class);

                                    FirebaseDatabase.getInstance().getReference().child("community").child(member.getCommunityId()).child("numMembers").setValue(i+1).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                else taskDone(task.isSuccessful());
            }
        });
        return null;
    }

    void taskDone(final boolean isSuccessful)
    {
        uiThread.post(new Runnable() {
            @Override
            public void run() {
                completion.memberSenderDone(isSuccessful);
            }
        });
    }
}
