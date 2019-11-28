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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

import nourl.tbd.Blipp.BlippConstructs.User;
import nourl.tbd.Blipp.Database.UserGetter;
import nourl.tbd.Blipp.Database.UserGetterCompletion;
import nourl.tbd.Blipp.R;
import nourl.tbd.Blipp.Helper.StatePersistence;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth auth;
    Button login;
    Button register;
    Button reset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //The Firebase authentication object
        auth = FirebaseAuth.getInstance();

        //Setting up View
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //These lines set up the Login and Register Buttons
        register = findViewById(R.id.login_btn_register);
        login = findViewById(R.id.login_btn_login);
        reset = findViewById(R.id.login_btn_reset);
        register.setOnClickListener(new LoginOnClick());
        login.setOnClickListener(new LoginOnClick());
        reset.setOnClickListener(new LoginOnClick());
    }

    @Override
    protected void onStart() {
        super.onStart();

        //If the user is currently logged in they do not need to log in again. This line will bring them to the app.
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) startActivity(new Intent(this, BlippContentActivity.class));
    }

    //This Class handles the button on click events
    class LoginOnClick implements Button.OnClickListener {
        @Override
        public void onClick(View view) {
            //If the register button is hit bring the user to the register activity.
            if (view.equals(register))
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));

            else if (view.equals(login)) {
                //Check to see if input is valid, if it is we will attempt to log in the user.
                if (isValidInput()) loginUser();
            } else if (view.equals(reset))
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
        }
    }

    //This handles the valid input logic
    private boolean isValidInput() {
        //get the username and password from the Edit Texts
        final String userEmail = ((EditText) findViewById(R.id.login_email)).getText().toString();
        final String userPassword = ((EditText) findViewById(R.id.login_password)).getText().toString();

        //Do not accept empty fields
        if (userEmail.isEmpty() || userPassword.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Error: Please fill in the email and password fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        //Do not accept improperly formatted email addresses
        else if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            Toast.makeText(LoginActivity.this, "Error: Please enter in a vaid email address", Toast.LENGTH_SHORT).show();
            return false;
        }

        //Do not accept improperly formatted passwords
        else if (!Pattern.compile("((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,20})").matcher(userPassword).matches()) {
            Toast.makeText(LoginActivity.this, "Error: Passwords must have the following properties:\nOne lower case letter.\nOne uppercase letter\nOne digit.\nLength of 6 - 20 characters.", Toast.LENGTH_LONG).show();
            return false;
        }
        //You have met all the checks if you are here
        else return true;
    }

    public void loginUser() {
        //Get the text from the Edit text
        final String userEmail = ((EditText) findViewById(R.id.login_email)).getText().toString();
        final String userPassword = ((EditText) findViewById(R.id.login_password)).getText().toString();

        //Sign in using the firebase auth
        auth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //If we logged in with firebase
                if (task.isSuccessful()) {
                    //Get the user Id from the firebase result
                    String userID = auth.getCurrentUser() == null ? null : auth.getCurrentUser().getUid();
                    //Error out if null user
                    if (userID == null) {
                        Toast.makeText(LoginActivity.this, "FATAL ERROR: LOGIN REPORTED SUCCESSFUL BUT CAN NOT DETERMINE CURRENT USER.", Toast.LENGTH_LONG).show();
                        auth.signOut();
                        return;
                    }
                    //Get the user information from the user table if user not null
                    new UserGetter(userID, new UserGetterCompletion() {

                        //Got the user we will log in
                        @Override
                        public void userGetterSuccess(User user) {
                            //Gets the users name
                            String userName = user == null ? "*unknown*" : user.getName() == null ? "*unknown*" : user.getName();

                            //Stores the validated information into StatePersistence.current
                            StatePersistence.current.loggedInUser = userEmail;
                            StatePersistence.current.password = userPassword;

                            //Moves to the BlippContent Activity, this activity will host all of the apps content through the use of fragments.
                            startActivity(new Intent(LoginActivity.this, BlippContentActivity.class));

                            //Display nice welcome message
                            Toast.makeText(LoginActivity.this, "Hello " + userName + ", Welcome To Blipp!", Toast.LENGTH_SHORT).show();
                        }

                        //Did not get the user we will not log in as we have an issue pulling from firebase.
                        @Override
                        public void userGetterFailure() {
                            Toast.makeText(LoginActivity.this, "Error: Log in failed. Ensure your credentials are correct.", Toast.LENGTH_SHORT).show();
                            auth.signOut();
                        }
                    }, LoginActivity.this);
                } else {
                    Toast.makeText(LoginActivity.this, "Error: Log in failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
