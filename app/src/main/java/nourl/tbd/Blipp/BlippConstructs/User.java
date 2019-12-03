package nourl.tbd.Blipp.BlippConstructs;

import com.google.firebase.auth.FirebaseAuth;

public class User
{
    private String name;
    private String email;
    private String id;
    private String phoneNumber;

    //This is the constructor used when getting a new user instance.
    //Never assign id manually
    //Note: Passwords are maintained by firebase auth

    public User(){}

    public User(String name, String email, String id, String phoneNumber)
    {
        this.name = name;
        this.email = email;
        this.id = id;
        this.phoneNumber = phoneNumber;
    }

    //This is the constructor used when creating a new user instance.
    public User(String name, String email, String phoneNumber) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.id = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

   //Note: instead of using push we can create a child node using this.id as it that property defines the unique user id.


    //Getters required for firebase
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getId() {
        return id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
