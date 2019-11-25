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
import com.google.firebase.auth.FirebaseUser;

import nourl.tbd.Blipp.R;
import nourl.tbd.Blipp.Helper.StatePersistence;

public class LoginActivity extends AppCompatActivity
{

    FirebaseAuth auth;
    Button login;
    Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        auth = FirebaseAuth.getInstance();

        //Setting up View
        //TODO: Restructure activity_login layout, include fields for user name and password. Improve overall aesthetic.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //These lines set up the Login and Register Buttons
        //TODO: Make the login & register button look nice
        register = findViewById(R.id.login_btn_register);
        login = findViewById(R.id.login_btn_login);
        register.setOnClickListener(new LoginOnClick());
        login.setOnClickListener(new LoginOnClick());

        //TODO: If the Username and password that is stored in StatePersistence.current matches with one that is stored in firebase then we can skip this process entirely and automatically log the user in.
        //The point of the above to-do is to make it so the user does not have to log in again if they have yet to log out, even after someone quits the app.

        //TODO: the back button should not work after a user logs out
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) startActivity(new Intent(this, BlippContentActivity.class));
    }

    //This Class handles the button on click events
    class LoginOnClick implements Button.OnClickListener
    {
        @Override
        public void onClick(View view)
        {
            if (view.equals(register))
            {
                registerNewUser();
            }
            else if (view.equals(login))
            {
                final String userEmail = ((EditText)findViewById(R.id.login_email)).getText().toString();
                final String userPassword = ((EditText)findViewById(R.id.login_password)).getText().toString();

                if (!userEmail.isEmpty() && !userPassword.isEmpty()) loginExistingUser();
                else Toast.makeText(LoginActivity.this, "Please fill in fields", Toast.LENGTH_SHORT).show();
            }
        }
    }



    public void registerNewUser()
    {
       startActivity(new Intent(this, RegisterActivity.class));
    }


    public void loginExistingUser()
    {
        //TODO: This function should be called when the user already has an account. Upon sign in confirmation, you must pass the email of the recently validated existing user to StatePersistence.current as it will be needed for queries.
        final String userEmail = ((EditText)findViewById(R.id.login_email)).getText().toString();
        final String userPassword = ((EditText)findViewById(R.id.login_password)).getText().toString();

        auth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {

                if (task.isSuccessful())
                {
                    //Stores the validated information into StatePersistence.current
                    StatePersistence.current.loggedInUser = userEmail;
                    StatePersistence.current.password = userPassword;

                    //Moves to the BlippContent Activity, this activity will host all of the apps content through the use of fragments.
                    startActivity(new Intent(LoginActivity.this, BlippContentActivity.class));
                }
                else
                    {
                        Toast.makeText(LoginActivity.this, "Error Logging in", Toast.LENGTH_SHORT).show();
                    }


            }
        });

          }
}
