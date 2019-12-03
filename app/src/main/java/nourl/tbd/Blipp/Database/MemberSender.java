package nourl.tbd.Blipp.Database;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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


        if (member.getMemberId() != null)
        {
            DatabaseReference here = location.child(member.getMemberId());
            here.setValue(member).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    taskDone(task.isSuccessful());
                }
            });
        }

        if (member.getMemberId() == null)
        {
        DatabaseReference here = location.push();
        here.setValue(member.withId(here.getKey())).addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                taskDone(task.isSuccessful());
            }
        });
        }
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
