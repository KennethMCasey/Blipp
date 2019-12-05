package nourl.tbd.Blipp.BlippConstructs;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Date;

public class Member
{
    String communityId;
    String userId;
    boolean isBanned;
    String displayName;
    Date dateJoined;
    String memberId;

    public Member() {}

    //This is the constructor that would be used for loading a Member. It manually passes in all information needed.
    public Member(String communityId, String userId, boolean isBanned, String displayName, Date dateJoined ) {
        this.communityId = communityId;
        this.userId = userId;
        this.isBanned = isBanned;
        this.displayName = displayName;
        this.dateJoined = dateJoined;
        this.memberId = null;
    }

    //This is a constructor that would be useful for creating a new member_row instance (first joining a community)
    public Member(String communityId, String displayName) {
        this.communityId = communityId;
        this.displayName = displayName;
        this.userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.isBanned = false;
        dateJoined = new Date();
        this.memberId = null;
    }

    public Member withId(String id)
    {
        this.memberId = id;
        return  this;
    }

    //Note: We still use push to create a new instance but we do not store the unique id inside the object as there is no benifit.


    //getters required for firebase
    public String getCommunityId() {
        return communityId;
    }

    public String getUserId() {
        return userId;
    }

    public String getMemberId() {return memberId;}

    public boolean isBanned() {
        return isBanned;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Date getDateJoined() {
        return dateJoined;
    }
}
