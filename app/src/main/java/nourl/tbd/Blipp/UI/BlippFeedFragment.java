package nourl.tbd.Blipp.UI;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Date;

import nourl.tbd.Blipp.BlippConstructs.Community;
import nourl.tbd.Blipp.BlippConstructs.Like;
import nourl.tbd.Blipp.BlippConstructs.Member;
import nourl.tbd.Blipp.BlippConstructs.User;
import nourl.tbd.Blipp.Database.CommunitySender;
import nourl.tbd.Blipp.Database.CommunitySenderCompletion;
import nourl.tbd.Blipp.Database.LikeSender;
import nourl.tbd.Blipp.Database.LikeSenderCompletion;
import nourl.tbd.Blipp.Database.MemberSender;
import nourl.tbd.Blipp.Database.MemberSenderCompletion;
import nourl.tbd.Blipp.Database.UserSender;
import nourl.tbd.Blipp.Database.UserSenderCompletion;
import nourl.tbd.Blipp.Helper.BlipListAdapter;
import nourl.tbd.Blipp.BlippConstructs.Blipp;
import nourl.tbd.Blipp.Database.BlipGetterCompletion;
import nourl.tbd.Blipp.Database.BlipSender;
import nourl.tbd.Blipp.Database.BlipGetter;
import nourl.tbd.Blipp.Database.BlipSenderCompletion;
import nourl.tbd.Blipp.R;
import nourl.tbd.Blipp.Helper.StatePersistence;

public class BlippFeedFragment extends Fragment implements BlipGetterCompletion, BlipSenderCompletion
{
    //class properties
    private Spinner blippDistance;
    private Spinner blippOrder;
    private ListView blippFeed;
    private SwipeRefreshLayout blippRefresh;
    private FloatingActionButton fab;
    boolean didHitBottom;

    View popupView;
    PopupWindow popupWindow;
    boolean popUpIsShowing;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        //inflates the layout
        //TODO: Make the layout look nicer
        View v = inflater.inflate(R.layout.blipp_feed_list_view, container, false);

        //Configures the floating action button
        fab = v.findViewById(R.id.fab_blipp);
        fab.setOnClickListener(new MakeBlipp());

        //Configures the swipe refresh
        blippRefresh = v.findViewById(R.id.swiperefresh_feed);
        blippRefresh.setOnRefreshListener(new RefreshFeed());

        //Configures the blipp distance selector
        blippDistance = v.findViewById(R.id.spinner_feed);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(v.getContext(), R.array.blipp_distances, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        blippDistance.setAdapter(adapter);
        blippDistance.setSelection(StatePersistence.current.nearMeSelectedRadius, false);
        blippDistance.setOnItemSelectedListener(new BlippSpinnerChanged());


        //Configures the blipp order selector
        blippOrder = v.findViewById(R.id.spinner_order);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this.getContext(), R.array.blipp_order, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        blippOrder.setAdapter(adapter2);
        blippOrder.setSelection(StatePersistence.current.nearMeSelectedOrdering,false);
        blippOrder.setOnItemSelectedListener(new BlippSpinnerChanged());

        //Configures the blipp feed list
        blippFeed = v.findViewById(R.id.list_feed);
        blippFeed.setAdapter(new BlipListAdapter(this.getContext(), StatePersistence.current.blipsFeed == null ? new ArrayList<Blipp>() : StatePersistence.current.blipsFeed));
        blippFeed.setOnScrollListener(new BottomHit());

        //if there were no previously loaded blipps this will start the background task to get the new blipps
        if (StatePersistence.current.blipsFeed == null) getBlips(null);


        //set the touch listener for if the user clicks outside the make blip window
        v.setOnTouchListener(new AnythingTouched());
        blippOrder.setOnTouchListener(new AnythingTouched());
        blippDistance.setOnTouchListener(new AnythingTouched());

        return v;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (popUpIsShowing)
        {
            popupWindow.dismiss();
            popUpIsShowing = false;
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (popUpIsShowing)
        {
            popupWindow.dismiss();
            popUpIsShowing = false;
        }
    }


    private void getBlips(String blipIdToStartAt)
    {
        didHitBottom = false;

    //TODO: populate the inital blips with the first 50 blips that meet the user specifications from firebase. each of the different if statement sections will require different queries. (6 total)

        blippRefresh.setEnabled(true);
        blippRefresh.setRefreshing(true);

    //Order By Most Recent
    if (blippOrder.getSelectedItemPosition() == 0)
    {

        //Close Distance
        if (blippDistance.getSelectedItemPosition() == 0) new BlipGetter(BlipGetter.Section.FEED, BlipGetter.Order.MOST_RECENT, BlipGetter.Distance.CLOSE, this , blipIdToStartAt, 20).execute();

        //Regular Distance
        else if (blippDistance.getSelectedItemPosition() == 1) new BlipGetter(BlipGetter.Section.FEED, BlipGetter.Order.MOST_RECENT, BlipGetter.Distance.REGULAR, this , blipIdToStartAt, 20).execute();

        //Max Distance
        else if (blippDistance.getSelectedItemPosition() == 2) new BlipGetter(BlipGetter.Section.FEED, BlipGetter.Order.MOST_RECENT, BlipGetter.Distance.MAX, this , blipIdToStartAt, 20).execute();

    }

    //Order By Most Liked
    else if (blippOrder.getSelectedItemPosition() == 1)
    {
        //Close Distance
        if (blippDistance.getSelectedItemPosition() == 0) new BlipGetter(BlipGetter.Section.FEED, BlipGetter.Order.MOST_LIKED, BlipGetter.Distance.CLOSE, this , blipIdToStartAt, 20).execute();

        //Regular Distance
        else if (blippDistance.getSelectedItemPosition() == 1) new BlipGetter(BlipGetter.Section.FEED, BlipGetter.Order.MOST_LIKED, BlipGetter.Distance.REGULAR, this , blipIdToStartAt, 20).execute();

        //Max Distance
        else if (blippDistance.getSelectedItemPosition() == 2) new BlipGetter(BlipGetter.Section.FEED, BlipGetter.Order.MOST_LIKED, BlipGetter.Distance.MAX, this , blipIdToStartAt, 20).execute();
    }
}

    @Override
    public void blipGetterGotInitialBlips(ArrayList<Blipp> results)
    {
        if (results == null) didHitBottom = true;
        StatePersistence.current.blipsFeed = results;
        ((BlipListAdapter)blippFeed.getAdapter()).setBlipps(results);
        blippRefresh.setRefreshing(false);
    }

    @Override
    public void blipGetterGotAdditionalBlips(ArrayList<Blipp> results)
    {
        if (results == null) didHitBottom = true;
        StatePersistence.current.blipsFeed.addAll(results);
        ((BlipListAdapter)blippFeed.getAdapter()).addBlips(results);
        blippRefresh.setRefreshing(false);
    }

    @Override
    public void blipGetterDidFail()
    {
        Toast.makeText(this.getContext(), "Error getting blips, please try again later...", Toast.LENGTH_LONG).show();
    }

    private class RefreshFeed implements SwipeRefreshLayout.OnRefreshListener
    {
        @Override
        public void onRefresh()
        {
            getBlips(null);
        }
    }


    class BlippSpinnerChanged implements Spinner.OnItemSelectedListener
    {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l)
        {
            StatePersistence.current.nearMeSelectedRadius =  view.equals(blippDistance) ? position : StatePersistence.current.nearMeSelectedRadius;

            StatePersistence.current.nearMeSelectedOrdering = view.equals(blippOrder) ?  position : StatePersistence.current.nearMeSelectedOrdering;


            blippRefresh.setRefreshing(true);
            getBlips(null);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView)
        {
        //UnUsed Method
        }
    }

    private class BottomHit implements AbsListView.OnScrollListener
    {
        @Override
        public void onScrollStateChanged(AbsListView absListView, int i)
        {
            //UnUsed Method
        }

        @Override
        public void onScroll(AbsListView absListView, int firstVisableItem, int visableItemCount, int totalItemCount)
        {

            /*Our Blipp Feed should only load 50 or less Blipps at a time. These will either be the 50 most recent or the 50 most liked depending on user configuration
            TODO: When our list is displaying the very last possible count of items, we should pull the next 50 items from Firebase if the user wishes to keep scrolling
             */

            if (firstVisableItem + visableItemCount == totalItemCount && totalItemCount!=0) getBlips(((Blipp)((BlipListAdapter)blippFeed.getAdapter()).getItem(totalItemCount-1)).getId());

        }
    }


    //Floating Action Button Related Classes

    class MakeBlipp implements FloatingActionButton.OnClickListener
    {
        @Override
        public void onClick(View view)
        {
            //TODO: Create new blip interface and functionality
            //Note: Must include option for blipp to be in either close, regular or max distance.
            //Note: Must include way of adding a photo to your blipp. Photos are optional but text is mandatory.
            //Note: Blipps are anonymous when posting, but please ensure to link the blipp with the current user when storing in the database so we can query them later such as in the My blipps section
            //Note: Please try and make the interface look as aesthetically pleasing as possible AFTER we get the core functionality working, try to make a clean interface we will go back and polish later.
            //Note: Look at the Fire base data face and see what other additional information is stored in each blip (ex: time, location), Some of this information isnt entered by the user but instead requires us to pull it from code.

            if (!popUpIsShowing)
            {
            LayoutInflater layoutInflater = getLayoutInflater();
            popupView = layoutInflater.inflate(R.layout.make_blipp,null);

            popupWindow = new PopupWindow(popupView, RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
            popupWindow.setFocusable(true);

            Button btnClose = (Button)popupView.findViewById(R.id.btnClose);

            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popupWindow.dismiss();
                    popUpIsShowing = false;
                }
            });


            Button btnSubmit = popupView.findViewById(R.id.btnSubmit);
            btnSubmit.setOnClickListener( new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    new BlipSender(new Blipp(Double.parseDouble(((EditText) popupView.findViewById(R.id.blipp_spoof_lat)).getText().toString()), Double.parseDouble(((EditText) popupView.findViewById(R.id.blipp_spoof_lon)).getText().toString()), ((CheckBox) popupView.findViewById(R.id.check_max)).isChecked(), ((CheckBox) popupView.findViewById(R.id.check_reg)).isChecked(), ((CheckBox) popupView.findViewById(R.id.check_close)).isChecked(), new Date(), null, ((EditText) popupView.findViewById(R.id.blipp_text_enter)).getText().toString(), null, FirebaseAuth.getInstance().getCurrentUser().getUid()),
                            new BlipSenderCompletion() {
                                @Override
                                public void blipSenderDone(boolean isSuccessful) {
                                    Toast.makeText(BlippFeedFragment.this.getContext(), isSuccessful ? "Sucess" : "Error", Toast.LENGTH_SHORT).show();
                                }
                            }).execute();

                    popupWindow.dismiss();
                    popUpIsShowing = false;

                }
            });

                popupWindow.showAtLocation(BlippFeedFragment.this.getView().getRootView(), Gravity.CENTER, 0, 0);

                popUpIsShowing = true;
            }
        }
    }


    @Override
    public void blipSenderDone(boolean isSucessful)
    {
        if (isSucessful)
        {
            Toast.makeText(BlippFeedFragment.this.getContext(), "Blipp was sent sucessfully", Toast.LENGTH_SHORT).show();
            popupWindow.dismiss();
            popUpIsShowing = false;
        }

        else
            {
                Toast.makeText(BlippFeedFragment.this.getContext(), "Error Sending Blip", Toast.LENGTH_SHORT).show();
            }
    }


    class AnythingTouched implements View.OnTouchListener
    {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent)
        {
            if (popUpIsShowing)
            {
                popupWindow.dismiss();
                popUpIsShowing = false;
            }
            return false;
        }
    }

}
