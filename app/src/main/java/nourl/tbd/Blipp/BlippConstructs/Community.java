package nourl.tbd.Blipp.BlippConstructs;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;

import java.net.URL;

import nourl.tbd.Blipp.Helper.LocationGetter;

public class Community {
    String id;
    String photo;
    double originLat;
    double originLong;
    double radius;
    String name;
    boolean isJoinable;
    String owner;

    public Community(){}

    //This is a constructor that manually passes all of the information to a Community object. This will only be used during testing and will eventually get deleted.
    //Manually assigning the id property is ill advised, the id will be taken care of in the CommunitySender class when push() to firebase.
    public Community(String id, String photo, double originLat, double originLong, double radius, String name, boolean isJoinable, String owner) {
        this.id = id;
        this.photo = photo;
        this.originLat = originLat;
        this.originLong = originLong;
        this.radius = radius;
        this.name = name;
        this.isJoinable = isJoinable;
        this.owner = owner;
    }

    //This will bne the constructor used to create a community
    public Community(double originLat, double originLong, String photo, double radius, String name, boolean isJoinable){
        this.photo = photo;
        this.radius = radius;
        this.name = name;
        this.isJoinable = isJoinable;
        this.originLong = originLong;
        this.originLat = originLat;
        this.owner = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.id = null;
    }

    //This function will only be used in the CommunitySender class.
    public Community withId(String id)
    {
        this.id = id;
        return this;
    }

    //getters required for firebase
    public String getId() {
        return id;
    }

    public String getPhoto() {
        return photo;
    }

    public double getOriginLat() {
        return originLat;
    }

    public double getOriginLong() {
        return originLong;
    }

    public double getRadius() {
        return radius;
    }

    public String getName() {
        return name;
    }

    public boolean isJoinable() {
        return isJoinable;
    }

    public String getOwner() {
        return owner;
    }




}



