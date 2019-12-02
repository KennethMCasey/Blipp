package nourl.tbd.Blipp.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import nourl.tbd.Blipp.R;

public class ResetPasswordActivity extends AppCompatActivity {

    EditText email;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //set the layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        //get the views
        email = findViewById(R.id.reset_email);
        submit = findViewById(R.id.reset_submit);

        //set the submit button functionality
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get the entered in under email
                String userEmail = email.getText().toString();

                //error out if field is empty
                if (userEmail.isEmpty()) {
                    Toast.makeText(ResetPasswordActivity.this, "Error: please fill out the email field.", Toast.LENGTH_SHORT).show();
                }

                //error out if text is not a proper email
                else if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                    Toast.makeText(ResetPasswordActivity.this, "Error: please enter valid email.", Toast.LENGTH_SHORT).show();
                }

                //passed checks, send the email
                else {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            ///show success or failure based on firebase results
                            Toast.makeText(ResetPasswordActivity.this, task.isSuccessful() ? "Action Complete: Password reset email sent." : "Error: Password reset failed.", Toast.LENGTH_SHORT).show();

                            //go back to log in screen if email sent
                            if (task.isSuccessful())
                                startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
                        }
                    });
                }
            }
        });
    }
}
