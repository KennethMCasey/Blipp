package nourl.tbd.Blipp.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

import nourl.tbd.Blipp.R;
import nourl.tbd.Blipp.Helper.StatePersistence;

public class BlippContentActivity extends AppCompatActivity
{
    //TODO: Back Button should not bring the user back to the login screen, only the log out button should do that.

    //TODO: When selecting on any blip in the near me, my blips, and liked blips section we have to bring a user to a detail view fragment where they can see the blipp in greater detail and view all existing comments and likes/dislikes as well as leave there own.

    //view properties
    TabLayout tabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        //inflate layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blipp_content);

        //sets up tab layout
        //TODO: Tab Bar is glitchy when swicthing, see if can fix (might have to do with layout weight)
        //TODO: Tab Bar looks bad, see if can style
        tabLayout = findViewById(R.id.tab_bar);
        tabLayout.addOnTabSelectedListener(new TabActions());
        tabLayout.getTabAt(StatePersistence.current.tabSelected).select(); //TODO: Handle null pointer exception
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        //saves the StatePersistence if the application is stopped for whatever reason.
        StatePersistence.current.saveData();
    }

    //Tab Bar Action Listener
    class TabActions implements TabLayout.OnTabSelectedListener
    {

        //This is responsible for switching the fragment to the appropriate fragment when a Tab is selected
        //TODO: The views do not need to be deleted each time a tab is changed, see if can avoid reconstructing a view every time we switch tabs unless necessary
        @Override
        public void onTabSelected(TabLayout.Tab tab)
        {

            //saves the currenlty selected tab
            StatePersistence.current.tabSelected = tab.getPosition();

            //removes the view from the fragment place holder, note the to-do we can probably do this better
            ViewGroup temp = findViewById(R.id.fragment_place_holder);
            temp.removeAllViews();

            //prepare for fragment exchange
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            //Near Me Tab Selected
            if (tab.getPosition() == 0)
            {
                fragmentTransaction.replace(R.id.fragment_place_holder, new BlippFeedFragment());
                fragmentTransaction.addToBackStack(null);
            }

            //Community Tab Selected
            else if (tab.getPosition() == 1)
            {
                //TODO: CommunityFragment Code Here
            }

            //My Blips Tag Selected
            else if (tab.getPosition() == 2)
            {
                fragmentTransaction.replace(R.id.fragment_place_holder, new MyBlippsFragment());
                fragmentTransaction.addToBackStack(null);
            }

            //Liked Blips Selected
            else if (tab.getPosition() == 3)
            {
                fragmentTransaction.replace(R.id.fragment_place_holder, new LikedBlippsFragment());
                fragmentTransaction.addToBackStack(null);
            }

            //Options Tag Selected
            else if (tab.getPosition() == 4)
            {
                fragmentTransaction.replace(R.id.fragment_place_holder, new OptionsFragment());
                fragmentTransaction.addToBackStack(null);
            }

            //finalize the swap
            fragmentTransaction.commit();
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab)
        {
            //Un Used Method
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab)
        {
            //Un Used Method
        }
    }
}

