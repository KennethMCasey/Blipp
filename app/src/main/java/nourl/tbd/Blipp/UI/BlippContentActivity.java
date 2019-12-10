package nourl.tbd.Blipp.UI;

import androidx.annotation.NonNull;
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

public class BlippContentActivity extends AppCompatActivity implements FragmentSwap
{
    //view properties
    TabLayout tabLayout;
    Fragment currFrag;
    int fragID;


    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() >0)
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //inflate layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blipp_content);

        //sets up tab layout
        tabLayout = findViewById(R.id.tab_bar);
        tabLayout.addOnTabSelectedListener(new TabActions());
        fragID =  savedInstanceState == null ? 0 : savedInstanceState.getInt("frag id", 1);
        tabFragmentSwap(fragID);

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt("frag id", fragID);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        //saves the StatePersistence if the application is stopped for whatever reason.
    }



    @Override
    protected void onResume() {
        super.onResume();
        //if the user is logged out bring them back to the log in screem
        if (FirebaseAuth.getInstance().getCurrentUser() == null) startActivity(new Intent(this.getBaseContext(), LoginActivity.class));
    }

    //This method is called from inside a fragment to swap with another fragment
    @Override
    public void swap(Fragment fragment, boolean addToBackstack) {
        ViewGroup temp = findViewById(R.id.fragment_place_holder);
        temp.removeAllViews();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_place_holder, fragment);
        if (addToBackstack) fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void postFragId(int id)
    {
        fragID = id;
    }


    void tabFragmentSwap(int tab)
    {
        while (getSupportFragmentManager().getBackStackEntryCount() > 0){
            getSupportFragmentManager().popBackStackImmediate();
        }

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

        }

        //Community Tab Selected
        else if (tab == 1)
        {
            fragmentTransaction.replace(R.id.fragment_place_holder, new CommunityJoinedFragment());
        }

        //My Blips Tag Selected
        else if (tab == 2)
        {
            fragmentTransaction.replace(R.id.fragment_place_holder, new MyBlippsFragment());
        }

        //Liked Blips Selected
        else if (tab == 3)
        {
            fragmentTransaction.replace(R.id.fragment_place_holder, new LikedBlippsFragment());
        }

        //Options Tag Selected
        else if (tab == 4)
        {
            fragmentTransaction.replace(R.id.fragment_place_holder, new OptionsFragment());
        }

        //Blip Detail Frag Selected
        else if (tab == 5)
        {
            fragmentTransaction.replace(R.id.fragment_place_holder, new BlippDetailFragment());
        }

        //Change info Frag Selected
        else if (tab == 6)
        {
            fragmentTransaction.replace(R.id.fragment_place_holder, new ChangeInfoFragment());
        }

        else if (tab == 7)
        {
            fragmentTransaction.replace(R.id.fragment_place_holder, new CommunityBlipsFragment());
        }

        else if (tab == 8)
        {
            fragmentTransaction.replace(R.id.fragment_place_holder, new JoinCommunityFragment());
        }

        else if (tab == 9)
        {
            fragmentTransaction.replace(R.id.fragment_place_holder, new MakeCommunityFragment());
        }

        else if (tab == 10)
        {
            fragmentTransaction.replace(R.id.fragment_place_holder, new ManageCommunitiesDetailFragment());
        }

        else if (tab == 11)
        {
            fragmentTransaction.replace(R.id.fragment_place_holder, new ManageCommunitiesSelectFragment());
        }

        else if (tab == 12)
        {
            fragmentTransaction.replace(R.id.fragment_place_holder, new MemberListFragment());
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
            fragID = tab.getPosition();
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

