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

import nourl.tbd.Blipp.BlippConstructs.Member;

public class MemberDeleter extends AsyncTask<Void, Void, Void>
{
    private Member member;
    MemberDeleterCompletion completion;
    Handler uiThread;

    private DatabaseReference mMemberDatabaseReference;

    public MemberDeleter(Member member, MemberDeleterCompletion completion, Context context)
    {
        this.member = member;
        this.completion = completion;
        uiThread = new Handler(context.getMainLooper());
        this.execute();
    }


    @Override
    protected Void doInBackground(Void... voids) {
        mMemberDatabaseReference = FirebaseDatabase.getInstance().getReference();
        Query q = mMemberDatabaseReference.child("member").orderByChild("memberId").equalTo(member.getMemberId());
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot ds : dataSnapshot.getChildren())
                        ds.getRef().removeValue();
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


    void taskDone(final boolean isSuccessful)
    {
        uiThread.post(new Runnable() {
            @Override
            public void run() {
                completion.memberDeleterDone(isSuccessful);
            }
        });
    }
}
