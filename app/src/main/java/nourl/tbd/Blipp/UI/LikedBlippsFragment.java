package nourl.tbd.Blipp.UI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;

import nourl.tbd.Blipp.Helper.BlipListAdapter;
import nourl.tbd.Blipp.BlippConstructs.Blipp;
import nourl.tbd.Blipp.Database.BlipGetterCompletion;
import nourl.tbd.Blipp.Database.BlipGetter;
import nourl.tbd.Blipp.R;
import nourl.tbd.Blipp.Helper.StatePersistence;

public class LikedBlippsFragment extends Fragment implements BlipGetterCompletion {

    Spinner order;
    SwipeRefreshLayout refresh;
    ListView likedBlipps;

    boolean didHitBottom;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.liked_blipps, container, false);

        //configure drop down menu
        order = v.findViewById(R.id.spinner_order_liked);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(v.getContext(), R.array.blipp_order, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        order.setAdapter(adapter2);
        order.setSelection(StatePersistence.current.likedBlipsSelectedOrdering, false);
        order.setOnItemSelectedListener(new BlippOrderChanged());

        //configure refresh
        refresh = v.findViewById(R.id.swiperefresh_liked);
        refresh.setOnRefreshListener(new RefreshFeed());

        //configure blipp list
        likedBlipps = v.findViewById(R.id.list_liked);
        likedBlipps.setAdapter(new BlipListAdapter(this.getContext(), StatePersistence.current.blipsLiked == null ? new ArrayList<Blipp>() : StatePersistence.current.blipsLiked));
        likedBlipps.setOnScrollListener(new BottomHit());

        //if there are no previously loaded blips this will start the background action to load them
        if (StatePersistence.current.blipsLiked == null) getBlips(null);

        return v;
    }

    private void getBlips(String blipToStartAt)
    {
        didHitBottom = false;

        refresh.setEnabled(true);
        refresh.setRefreshing(true);

        //TODO: Pull the blip data from the database
        if (order.getSelectedItemPosition() == 0) new BlipGetter(BlipGetter.Section.LIKED_BLIPS, BlipGetter.Order.MOST_RECENT, null, this, blipToStartAt, 20);

        else if (order.getSelectedItemPosition() == 1) new BlipGetter(BlipGetter.Section.LIKED_BLIPS, BlipGetter.Order.MOST_LIKED, null, this, blipToStartAt, 20);
    }

    @Override
    public void blipGetterGotInitialBlips(ArrayList<Blipp> results)
    {
        if (results == null) didHitBottom = true;
        StatePersistence.current.blipsLiked = results;
        ((BlipListAdapter)likedBlipps.getAdapter()).setBlipps(results);
        refresh.setRefreshing(false);
    }

    @Override
    public void blipGetterGotAdditionalBlips(ArrayList<Blipp> results)
    {
        if (results == null) didHitBottom = true;
        StatePersistence.current.blipsLiked.addAll(results);
        ((BlipListAdapter)likedBlipps.getAdapter()).addBlips(results);
        refresh.setRefreshing(false);
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


    private class BlippOrderChanged implements Spinner.OnItemSelectedListener
    {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l)
        {

            StatePersistence.current.likedBlipsSelectedOrdering = position;

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
        public void onScroll(AbsListView absListView, int firstVisableItem, int visableItemCount, int totalItemCount) {

            /*Our Blipp Feed should only load 50 or less Blipps at a time. These will either be the 50 most recent or the 50 most liked depending on user configuration
            TODO: When our list is displaying the very last possible count of items, we should pull the next 50 items from Firebase if the user wishes to keep scrolling
             */

            if (firstVisableItem + visableItemCount == totalItemCount && totalItemCount!=0  && !didHitBottom )
            {
                getBlips(((Blipp)((BlipListAdapter)likedBlipps.getAdapter()).getItem(totalItemCount-1)).getId());
            }
        }
    }

}
