package nourl.tbd.Blipp.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import nourl.tbd.Blipp.R;
import nourl.tbd.Blipp.Helper.StatePersistence;

public class BlippContentActivity extends AppCompatActivity implements FragmentSwap
{


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
        tabLayout = findViewById(R.id.tab_bar);
        tabLayout.addOnTabSelectedListener(new TabActions());
        tabLayout.setScrollPosition(StatePersistence.current.tabSelected, 0, true);
        //TODO: Handle null pointer exception
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        //saves the StatePersistence if the application is stopped for whatever reason.
        StatePersistence.current.saveData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //if the user is logged out bring them back to the log in screem
        if (FirebaseAuth.getInstance().getCurrentUser() == null) startActivity(new Intent(this.getBaseContext(), LoginActivity.class));
    }

    //This method is called from inside a fragment to swap with another fragment
    @Override
    public void swap(Fragment fragment) {
        ViewGroup temp = findViewById(R.id.fragment_place_holder);
        temp.removeAllViews();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_place_holder, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    void tabFragmentSwap(int tab)
    {
        //saves the currenlty selected tab
        StatePersistence.current.tabSelected = tab;

        //removes the view from the fragment place holder, note the to-do we can probably do this better
        ViewGroup temp = findViewById(R.id.fragment_place_holder);
        temp.removeAllViews();

        //prepare for fragment exchange
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        //Near Me Tab Selected
        if (tab == 0)
        {
            fragmentTransaction.replace(R.id.fragment_place_holder, new BlippFeedFragment());
            fragmentTransaction.addToBackStack(null);
        }

        //Community Tab Selected
        else if (tab == 1)
        {
            fragmentTransaction.replace(R.id.fragment_place_holder, new CommunityJoinedFragment());
            fragmentTransaction.addToBackStack(null);
        }

        //My Blips Tag Selected
        else if (tab == 2)
        {
            fragmentTransaction.replace(R.id.fragment_place_holder, new MyBlippsFragment());
            fragmentTransaction.addToBackStack(null);
        }

        //Liked Blips Selected
        else if (tab == 3)
        {
            fragmentTransaction.replace(R.id.fragment_place_holder, new LikedBlippsFragment());
            fragmentTransaction.addToBackStack(null);
        }

        //Options Tag Selected
        else if (tab == 4)
        {
            fragmentTransaction.replace(R.id.fragment_place_holder, new OptionsFragment());
            fragmentTransaction.addToBackStack(null);
        }

        //finalize the swap
        fragmentTransaction.commit();
    }


    //Tab Bar Action Listener
    class TabActions implements TabLayout.OnTabSelectedListener
    {

        //This is responsible for switching the fragment to the appropriate fragment when a Tab is selected
        @Override
        public void onTabSelected(TabLayout.Tab tab)
        {
            tabFragmentSwap(tab.getPosition());
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

