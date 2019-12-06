package nourl.tbd.Blipp.BlippConstructs;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Date;

public class Blipp {

    private double latitude;
    private double longitude;
    private boolean isLongDistance;
    private boolean isMediumDistance;
    private boolean isShortDistance;
    private Date time;
    private String id;
    private String text;
    private String url;
    private String userId;
    private String parent;
    private String community;
    private int numOfLikes;



    public Blipp(){}

    //This Blip constructor will construct a blip in its entirety manually passing all information, this will probably only need to be used while loading.
    //as the id parameter will never be known on creation. The id is generated when calling push() while uploading to firebase and will be assigned in the BlipSender Class.
    //do not ever manually assign the id paramater when uplaoding to firebase always use push().
    public Blipp(double latitude, double longitude, boolean isLongDistance, boolean isMediumDistance, boolean isShortDistance, Date time, String id, String text, String url, String userId, String parent, String community, int numOfLikes) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.isLongDistance = isLongDistance;
        this.isMediumDistance = isMediumDistance;
        this.isShortDistance = isShortDistance;
        this.time = time;
        this.id = id;
        this.text = text;
        this.url = url;
        this.userId = userId;
        this.parent = parent;
        this.community = community;
        this.numOfLikes = numOfLikes;
    }

    //This constructor constructs a blip without a parent blip (not a comment) and one not in a community manually passing longitude latitude and time. This will only be used during testing and eventually deleted.
    public Blipp(double latitude, double longitude, boolean isLongDistance, boolean isMediumDistance, boolean isShortDistance, Date time, String id, String text, String url, String userId) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.isLongDistance = isLongDistance;
        this.isMediumDistance = isMediumDistance;
        this.isShortDistance = isShortDistance;
        this.time = time;
        this.id = id;
        this.text = text;
        this.url = url;
        this.userId = userId;
        parent = null;
        community = null;
    }

    //This constructor constructs a blip without a parent blip (not a comment) and one not in a community.
    public Blipp(double latitude, double longitude,boolean isLongDistance, boolean isMediumDistance, boolean isShortDistance, String text, String url) {
        this.isLongDistance = isLongDistance;
        this.isMediumDistance = isMediumDistance;
        this.isShortDistance = isShortDistance;
        this.text = text;
        this.url = url;
        this.parent = null;
        this.community = null;
        this.time = new Date();
        this.latitude = latitude;
        this.userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.longitude = longitude;
    }

    //This constructor constructs a blip that is either a comment or part of a community or both.
    public Blipp(double latitude, double longitude,boolean isLongDistance, boolean isMediumDistance, boolean isShortDistance, String text, String url, String parent, String community) {
        this.isLongDistance = isLongDistance;
        this.isMediumDistance = isMediumDistance;
        this.isShortDistance = isShortDistance;
        this.text = text;
        this.url = url;
        this.parent = parent;
        this.community = community;
        this.time = new Date();
        this.latitude = latitude;
        this.userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.longitude = longitude;
    }


    //This function is called in BlipSender to give the blip its unique id once it is generated.
    //Do not call this in anyother class besides BlipSender.
    public Blipp withId(String id)
    {
        this.id = id;
        return this;
    }

    //Geters are required to upload to firebase
    public String getId() {
        return id;
    }

    public int getNumOfLikes() {
        return numOfLikes;
    }

    public String getUrl() {
        return url;
    }

    public String getText() {
        return text;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public boolean getIsLongDistance() {
        return isLongDistance;
    }

    public boolean getIsMediumDistance() {
        return isMediumDistance;
    }

    public boolean getIsShortDistance() {
        return isShortDistance;
    }

    public Date getTime() {
        return time;
    }

    public String getUserId() {
        return userId;
    }

    public String getParent() {
        return parent;
    }

    public String getCommunity() {
        return community;
    }
}
