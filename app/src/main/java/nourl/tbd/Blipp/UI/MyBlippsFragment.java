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

public class MyBlippsFragment extends Fragment implements BlipGetterCompletion {

    Spinner order;
    SwipeRefreshLayout refreshLayout;
    ListView myBlips;
    boolean didHitBottom;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.my_blipps_list, container, false);

        //Configure Blipp Ordering Drop Down
        order = v.findViewById(R.id.spinner_order_my_blipps);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(v.getContext(), R.array.blipp_order, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        order.setAdapter(adapter2);
        order.setSelection(StatePersistence.current.myBlipsSelectedOrdering, false);
        order.setOnItemSelectedListener(new OrderChanged());

        //Configure the refresh
        refreshLayout = v.findViewById(R.id.swiperefresh_my_blipps);
        refreshLayout.setOnRefreshListener(new Refresh());

        //Configure the my blips list
        myBlips = v.findViewById(R.id.list_my_blips);
        myBlips.setAdapter(new BlipListAdapter(this.getContext(), StatePersistence.current.blipsMy == null ?  new ArrayList<Blipp>() : StatePersistence.current.blipsMy));
        myBlips.setOnScrollListener(new BottomHit());

        //if there were no blipps loaded previously this will start the background task to
        if (StatePersistence.current.blipsMy == null) getBlips(null);


        return v;
    }


    private void getBlips(String blipToStartAt)
    {
        didHitBottom = false;

        refreshLayout.setEnabled(true);
        refreshLayout.setRefreshing(true);

        //TODO: Pull the blip data from the database
        if (order.getSelectedItemPosition() == 0) new BlipGetter(BlipGetter.Section.MY_BLIPS, BlipGetter.Order.MOST_RECENT, null, this, blipToStartAt, 20);

        else if (order.getSelectedItemPosition() == 1) new BlipGetter(BlipGetter.Section.MY_BLIPS, BlipGetter.Order.MOST_LIKED, null, this, blipToStartAt, 20);
    }

    @Override
    public void blipGetterGotInitialBlips(ArrayList<Blipp> results)
    {
        if (results == null) didHitBottom = true;
        StatePersistence.current.blipsMy = results;
        ((BlipListAdapter)myBlips.getAdapter()).setBlipps(results);
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void blipGetterGotAdditionalBlips(ArrayList<Blipp> results)
    {
        if (results == null) didHitBottom = true;
        StatePersistence.current.blipsMy.addAll(results);
        ((BlipListAdapter)myBlips.getAdapter()).addBlips(results);
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void blipGetterDidFail()
    {
        Toast.makeText(this.getContext(), "Error getting blips, please try again later...", Toast.LENGTH_LONG).show();
    }


    private class Refresh implements SwipeRefreshLayout.OnRefreshListener
    {
        @Override
        public void onRefresh()
        {
            getBlips(null);
        }
    }


    private class OrderChanged implements Spinner.OnItemSelectedListener
    {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l)
        {
            StatePersistence.current.myBlipsSelectedOrdering = position;
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

            if (firstVisableItem + visableItemCount == totalItemCount && totalItemCount!=0 && !didHitBottom)
            {
                getBlips(((Blipp)((BlipListAdapter)myBlips.getAdapter()).getItem(totalItemCount-1)).getId());
            }
        }
    }

}


