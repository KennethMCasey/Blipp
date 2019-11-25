package nourl.tbd.Blipp.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import nourl.tbd.Blipp.BlippConstructs.User;
import nourl.tbd.Blipp.Database.UserSender;
import nourl.tbd.Blipp.Database.UserSenderCompletion;
import nourl.tbd.Blipp.R;

public class RegisterActivity extends AppCompatActivity {


    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();

        ((Button)findViewById(R.id.register_btn_submit)).setOnClickListener(new Submit());

    }


    class Submit implements Button.OnClickListener
    {
        @Override
        public void onClick(View view)
        {
            final String userName = ((EditText)findViewById(R.id.register_email)).getText().toString();
            final String password = ((EditText)findViewById(R.id.register_password)).getText().toString();
            final String name = ((EditText)findViewById(R.id.register_name)).getText().toString();
            final String phone = ((EditText)findViewById(R.id.register_phone)).getText().toString();
            auth.createUserWithEmailAndPassword(userName, password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
            {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    if (task.isSuccessful())
                    {
                        new UserSender(new User(name, userName, phone), new UserSenderCompletion() {
                            @Override
                            public void userSenderDone(boolean isSuccessful) {
                                Toast.makeText(RegisterActivity.this, isSuccessful ? "Action sucessful" : "Error", Toast.LENGTH_SHORT).show();
                            }
                        }, getApplicationContext());

                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));

                    }

                    else
                        {
                            Toast.makeText(RegisterActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                }
            });
        }
    }

}
