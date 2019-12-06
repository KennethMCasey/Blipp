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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import nourl.tbd.Blipp.Helper.BlipListAdapter;
import nourl.tbd.Blipp.BlippConstructs.Blipp;
import nourl.tbd.Blipp.Database.BlipGetterCompletion;
import nourl.tbd.Blipp.Database.BlipGetter;
import nourl.tbd.Blipp.R;

public class LikedBlippsFragment extends Fragment implements BlipGetterCompletion {

    Spinner order;
    SwipeRefreshLayout refresh;
    ListView likedBlipps;
    FragmentSwap fragmentSwap;

    boolean didHitBottom;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        fragmentSwap = (FragmentSwap)this.getActivity();
        fragmentSwap.postFragId(3);

        View v = inflater.inflate(R.layout.liked_blipps, container, false);

        //configure drop down menu
        order = v.findViewById(R.id.spinner_order_liked);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(v.getContext(), R.array.blipp_order, R.layout.spinner_item_blip);
        adapter2.setDropDownViewResource(R.layout.spinner_item_blip);
        order.setAdapter(adapter2);
        order.setOnItemSelectedListener(new BlippOrderChanged());

        //configure refresh
        refresh = v.findViewById(R.id.swiperefresh_liked);
        refresh.setOnRefreshListener(new RefreshFeed());

        //configure blipp list
        likedBlipps = v.findViewById(R.id.list_liked);
        likedBlipps.setAdapter(new BlipListAdapter(this.getContext(),  new ArrayList<Blipp>()));
        likedBlipps.setOnScrollListener(new BottomHit());
        likedBlipps.setOnItemClickListener(new ToBlipDetail());

        //if there are no previously loaded blips this will start the background action to load them
         getBlips(null);

        return v;
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    private void getBlips(String blipToStartAt)
    {
        didHitBottom = false;

        refresh.setEnabled(true);
        refresh.setRefreshing(true);

        if (order.getSelectedItemPosition() == 0) new BlipGetter(BlipGetter.Section.LIKED_BLIPS, BlipGetter.Order.MOST_RECENT, null, this, blipToStartAt, 20, this.getContext());

        else if (order.getSelectedItemPosition() == 1) new BlipGetter(BlipGetter.Section.LIKED_BLIPS, BlipGetter.Order.MOST_LIKED, null, this, blipToStartAt, 20, this.getContext());
    }

    @Override
    public void blipGetterGotInitialBlips(ArrayList<Blipp> results)
    {
        if (results == null) didHitBottom = true;
        ((BlipListAdapter)likedBlipps.getAdapter()).setBlipps(results);
        refresh.setRefreshing(false);
    }

    @Override
    public void blipGetterGotAdditionalBlips(ArrayList<Blipp> results)
    {
        if (results == null) didHitBottom = true;
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


    class ToBlipDetail implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            Blipp blipp = (Blipp)likedBlipps.getAdapter().getItem(position);

            String time = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.ENGLISH).format(blipp.getTime());

            Bundle b = new Bundle();
            b.putString("blipID", blipp.getId());
            b.putString("blipParent", blipp.getParent());
            b.putString("blipText", blipp.getText());
            b.putString("blipUser", blipp.getUserId());
            b.putString("blipCommunity", blipp.getCommunity());
            b.putString("blipURL", blipp.getUrl() == null ? null : blipp.getUrl().toString());
            b.putDouble("blipLat", blipp.getLatitude());
            b.putDouble("blipLon", blipp.getLongitude());
            b.putString("blipTime", blipp.getTime() == null ? null : time);
            b.putBoolean("blipShort", blipp.isShortDistance());
            b.putBoolean("blipMed", blipp.isMediumDistance());
            b.putBoolean("blipLong", blipp.isLongDistance());
            BlippDetailFragment frag = new BlippDetailFragment();
            frag.setArguments(b);
            fragmentSwap.swap(frag, true);
        }
    }

    private class BlippOrderChanged implements Spinner.OnItemSelectedListener
    {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l)
        {
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

            if (firstVisableItem + visableItemCount == totalItemCount && totalItemCount!=0  && !didHitBottom )
            {
                getBlips(((Blipp)((BlipListAdapter)likedBlipps.getAdapter()).getItem(totalItemCount-1)).getId());
            }
        }
    }

}
