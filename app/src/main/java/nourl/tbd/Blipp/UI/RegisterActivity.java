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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

import nourl.tbd.Blipp.BlippConstructs.User;
import nourl.tbd.Blipp.Database.UserSender;
import nourl.tbd.Blipp.Database.UserSenderCompletion;
import nourl.tbd.Blipp.R;

public class RegisterActivity extends AppCompatActivity {

    //Firebase auth object
    FirebaseAuth auth;
    TextInputLayout email;
    TextInputLayout password;
    TextInputLayout confirmPassword;
    TextInputLayout name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get the layout
        setContentView(R.layout.activity_register);

        //get the firebase auth object
        auth = FirebaseAuth.getInstance();

        //Set the onClickListener of the register button
        (findViewById(R.id.register_btn_submit)).setOnClickListener(new Submit());
    }


    class Submit implements MaterialButton.OnClickListener {
        @Override
        public void onClick(View view) {
            //Get all the Text from the edit texts
            final String userName = ((EditText) findViewById(R.id.register_email)).getText().toString();
            final String password = ((EditText) findViewById(R.id.register_password)).getText().toString();
            final String passwordConfirm = ((EditText) findViewById(R.id.register_password_confirm)).getText().toString();
            final String name = ((EditText) findViewById(R.id.register_name)).getText().toString();
            final String phone = ((EditText) findViewById(R.id.register_phone)).getText().toString();

            //Check to see if fields are populated
            if (userName.isEmpty() || password.isEmpty() || name.isEmpty() || phone.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Error: Please fill out all fields", Toast.LENGTH_SHORT).show();
            }
            //Make sure email is proper
            else if (!Patterns.EMAIL_ADDRESS.matcher(userName).matches()) {
                Toast.makeText(RegisterActivity.this, "Error: Please enter valid email.", Toast.LENGTH_LONG).show();
            } else if (!password.equals(passwordConfirm)) {
                Toast.makeText(RegisterActivity.this, "Error: Please ensure passwords match.", Toast.LENGTH_LONG).show();
            }
            //Make sure password is proper
            else if (!Pattern.compile("((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,20})").matcher(password).matches()) {
                Toast.makeText(RegisterActivity.this, "Error: Passwords have the following properties:\nMust contain one lower case letter.\nMust contain one uppercase letter\nMust contain one digit.\nMust have a length of 6 - 20 characters.", Toast.LENGTH_LONG).show();
            }
            //Make sure phone number is proper
            else if (!Patterns.PHONE.matcher(phone).matches()) {
                Toast.makeText(RegisterActivity.this, "Error: Please enter valid phone number.", Toast.LENGTH_SHORT).show();
            }

            //Passed all checks, create account.
            else
                auth.createUserWithEmailAndPassword(userName, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //Created new firebase user
                        if (task.isSuccessful()) {
                            new UserSender(new User(name, userName, phone), new UserSenderCompletion() {
                                @Override
                                public void userSenderDone(boolean isSuccessful) {
                                    //User is added or not added to the user table.
                                    Toast.makeText(RegisterActivity.this, isSuccessful ? "Action Complete: Blipp account confirmed." : "Action Complete With Errors: Name and phone-number not stored. Please edit your information in the app when you log in.", Toast.LENGTH_LONG).show();
                                }
                            }, getApplicationContext());

                            //Bring back to log in page
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));

                        }
                        //Failed to create new firebase user
                        else {
                            if (task.getException() == null) {
                                Toast.makeText(RegisterActivity.this, "Error: An unknown error occurred while creating the user.", Toast.LENGTH_SHORT).show();
                            } else
                                Toast.makeText(RegisterActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        }
    }
}
