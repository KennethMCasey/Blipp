package nourl.tbd.Blipp.UI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

import nourl.tbd.Blipp.BlippConstructs.User;
import nourl.tbd.Blipp.Database.UserDeleter;
import nourl.tbd.Blipp.Database.UserDeleterCompletion;
import nourl.tbd.Blipp.Database.UserGetter;
import nourl.tbd.Blipp.Database.UserGetterCompletion;
import nourl.tbd.Blipp.Database.UserSender;
import nourl.tbd.Blipp.Database.UserSenderCompletion;
import nourl.tbd.Blipp.R;

public class ChangeInfoFragment extends Fragment
{
    FirebaseAuth auth;
    TextView info;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        auth = FirebaseAuth.getInstance();

       final View v = inflater.inflate(R.layout.change_user_info, null);

        info = (v.findViewById(R.id.change_id));
        displayInfo();



        v.findViewById(R.id.change_btn_submit).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //Get all the Text from the edit texts
                final String userName = ((EditText) v.findViewById(R.id.change_email)).getText().toString();
                final String passwordCurrent = ((EditText) v.findViewById(R.id.change_current_password)).getText().toString();
                final String passwordNew = ((EditText) v.findViewById(R.id.change_new_password)).getText().toString();
                final String name = ((EditText) v.findViewById(R.id.change_name)).getText().toString();
                final String phone = ((EditText) v.findViewById(R.id.change_phone)).getText().toString();

                if (!passwordCurrent.isEmpty()) {

                    new UserGetter(auth.getCurrentUser().getUid(), new UserGetterCompletion() {
                        @Override
                        public void userGetterSuccess(final User user)
                        {

                            auth.getCurrentUser().reauthenticate(EmailAuthProvider.getCredential(auth.getCurrentUser().getEmail(), passwordCurrent)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    String newEmail = null;
                                    String newName = null;
                                    String newPhone = null;
                                    boolean didErrorPre = false;


                                    if (!task.isSuccessful()) Toast.makeText(getContext(), "Error: Can not authenticate changes", Toast.LENGTH_LONG).show();
                                    else
                                    {

                                        if (!userName.isEmpty())
                                        {
                                            if (!Patterns.EMAIL_ADDRESS.matcher(userName).matches())
                                            {
                                                Toast.makeText(ChangeInfoFragment.this.getContext(), "Error: Please enter valid email.", Toast.LENGTH_SHORT).show();
                                                didErrorPre = true;
                                            }
                                            else
                                            {
                                                auth.getCurrentUser().updateEmail(userName);
                                                newEmail = userName;
                                                Toast.makeText(ChangeInfoFragment.this.getContext(), "Success: Email updated", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        if (!passwordNew.isEmpty())
                                        {
                                            if (!Pattern.compile("((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,20})").matcher(passwordCurrent).matches())
                                            {
                                                Toast.makeText(ChangeInfoFragment.this.getContext(), "Error: Passwords have the following properties:\nMust contain one lower case letter.\nMust contain one uppercase letter\nMust contain one digit.\nMust have a length of 6 - 20 characters.", Toast.LENGTH_LONG).show();
                                                didErrorPre = true;
                                            }
                                            else
                                            {
                                                auth.getCurrentUser().updatePassword(passwordNew);
                                            }
                                        }
                                        if (!name.isEmpty())
                                        {
                                            newName = name;
                                        }
                                        if (!phone.isEmpty())
                                        {
                                            if (!Patterns.PHONE.matcher(phone).matches())
                                            {
                                                Toast.makeText(ChangeInfoFragment.this.getContext(), "Error: Please enter valid phone number.", Toast.LENGTH_SHORT).show();
                                                didErrorPre = true;
                                            }
                                            else
                                            {
                                                newPhone = phone;
                                            }
                                        }


                                        final boolean didError = didErrorPre;
                                        new UserSender(new User(newName == null ? user.getName() : newName, newEmail == null ? user.getEmail() : newEmail, newPhone == null ? user.getPhoneNumber() : newPhone), new UserSenderCompletion() {
                                            @Override
                                            public void userSenderDone(boolean isSuccessful)
                                            {
                                                Toast.makeText(ChangeInfoFragment.this.getContext(), !isSuccessful ?  "Error: Update failed" : didError ? "Prccess Complete with errors" : "Success: Information updated" , Toast.LENGTH_SHORT).show();
                                                if (isSuccessful) displayInfo();
                                            }
                                        }, getContext());
                                    }
                                }
                            });
                        }

                        @Override
                        public void userGetterFailure() {

                        }
                    }, getContext());
                }
                else {Toast.makeText(ChangeInfoFragment.this.getContext(), "Error: Must fill in current password to change info.", Toast.LENGTH_SHORT).show();}
            }
        });

        v.findViewById(R.id.change_btn_delete).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                final String passwordCurrent = ((EditText) v.findViewById(R.id.change_current_password)).getText().toString();
                if (!passwordCurrent.isEmpty()) {


                    auth.getCurrentUser().reauthenticate(EmailAuthProvider.getCredential(auth.getCurrentUser().getEmail(), passwordCurrent)).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful())Toast.makeText(ChangeInfoFragment.this.getContext(), "Error: Could not validate password.", Toast.LENGTH_SHORT).show();
                            else auth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (!task.isSuccessful())
                                        Toast.makeText(ChangeInfoFragment.this.getContext(), "Error: Can not delete user", Toast.LENGTH_SHORT).show();
                                    else {
                                        startActivity(new Intent(getContext(), LoginActivity.class));
                                        new UserGetter(auth.getCurrentUser().getUid(), new UserGetterCompletion() {
                                            @Override
                                            public void userGetterSuccess(User user)
                                            {
                                             new UserDeleter(user, new UserDeleterCompletion() {
                                                 @Override
                                                 public void userDeleterDone(boolean isSuccessful)
                                                 {
                                                     Toast.makeText(ChangeInfoFragment.this.getContext(),  isSuccessful ? "Success: User deleted" : "User deleted with errors", Toast.LENGTH_SHORT).show();
                                                 }
                                             }, getContext());
                                            }
                                            @Override
                                            public void userGetterFailure()
                                            {
                                                Toast.makeText(ChangeInfoFragment.this.getContext(), "User deleted with errors", Toast.LENGTH_SHORT).show();
                                            }
                                        }, getContext());
                                    }
                                }
                            });
                        }
                    });
                }else {Toast.makeText(ChangeInfoFragment.this.getContext(), "Error: Must fill in current password to delete account.", Toast.LENGTH_SHORT).show();}
            }
        });
    return  v;
    }


    void displayInfo()
    {
        new UserGetter(auth.getCurrentUser().getUid(), new UserGetterCompletion() {
            @Override
            public void userGetterSuccess(User user)
            {
                if (user == null) info.setText("Error: can not get info.");
                else info.setText(
                "User ID: " + auth.getCurrentUser().getUid() + "\n" +
                        "Name: " + user.getName() + "\n" +
                        "Email: " + user.getEmail() + "\n" +
                        "Phone: " + user.getPhoneNumber() + "\n"
                );
            }

            @Override
            public void userGetterFailure()
            {
                info.setText("Error: can not get info.");
            }
        }, getContext());
    }
}
