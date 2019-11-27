package nourl.tbd.Blipp.Helper;

//We use this class to save the state of the app to a shared preferences file.
//We load the data from this shared preferences file each time the app is opened or if part of our app gets destroyed

import java.util.ArrayList;

import nourl.tbd.Blipp.BlippConstructs.Blipp;
import nourl.tbd.Blipp.BlippConstructs.Community;

public class StatePersistence
{

    static public StatePersistence current = new StatePersistence();

    public String loggedInUser;

    public String password;

    public int tabSelected;

    public int nearMeSelectedRadius;

    public int nearMeSelectedOrdering;

    public int myBlipsSelectedOrdering;

    public int likedBlipsSelectedOrdering;

    public ArrayList<Blipp> blipsFeed;

    public ArrayList<Blipp> blipsLiked;

    public ArrayList<Blipp> blipsMy;

    public ArrayList<Community> comunityJoined;

    public int communityJoinedOrderPosition;


    private StatePersistence()
    {
    //TODO: Load all of this data from an encrypted shared preferences file
    //check if there is any data to load
    //if there is data to load, load the data then end code.
    //if there is no data to load then...
    this.tabSelected = 0;
    this.loggedInUser = null;
    this.password = null;
    this.nearMeSelectedRadius = 0;
    this.nearMeSelectedOrdering = 0;
    this.myBlipsSelectedOrdering = 0;
    this.likedBlipsSelectedOrdering = 0;
    this.communityJoinedOrderPosition = 0;
    this.blipsFeed = null;
    this.blipsLiked = null;
    this.blipsMy = null;
    this.comunityJoined = null;
    }

    public void saveData()
    {
    //TODO: Save all the data in this class to an encrypted shared preferences file
    }

   public static void clearData()
    {
        //TODO: Clear the encrypted shared prefrences file as the user is logging out and we no longer need to care about previous states
        StatePersistence.current = new StatePersistence();
    }

}
