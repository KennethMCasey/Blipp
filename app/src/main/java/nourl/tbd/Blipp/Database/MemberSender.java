package nourl.tbd.Blipp.Database;

import android.os.AsyncTask;

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


    public MemberSender(Member member, MemberSenderCompletion completion) {
        this.member = member;
        this.completion = completion;
        db = FirebaseDatabase.getInstance("https://blipp-15ee8.firebaseio.com/");
        location = db.getReference().child("member");
        this.execute();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        DatabaseReference here = location.push();
        here.setValue(member).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                publishProgress(task.isSuccessful());
            }
        });
        return null;
    }

    @Override
    protected void onProgressUpdate(Boolean... values) {
        completion.memberSenderDone(values[0]);
    }
}
