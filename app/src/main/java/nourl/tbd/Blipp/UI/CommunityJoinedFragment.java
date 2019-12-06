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

import nourl.tbd.Blipp.BlippConstructs.Community;
import nourl.tbd.Blipp.Database.CommunityGetter;
import nourl.tbd.Blipp.Database.CommunityGetterCompletion;
import nourl.tbd.Blipp.Helper.CommunityAdapter;
import nourl.tbd.Blipp.R;

public class CommunityJoinedFragment extends Fragment implements CommunityGetterCompletion {
    Spinner order;
    SwipeRefreshLayout refresh;
    ListView communitiesJoined;
    FragmentSwap fragmentSwap;

    boolean didHitBottom;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentSwap = (FragmentSwap) getActivity();
        fragmentSwap.postFragId(1);

        View v = inflater.inflate(R.layout.community_joined, container, false);

        //configure drop down menu
        order = v.findViewById(R.id.spinner_community_joined);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(v.getContext(), R.array.community_order, R.layout.spinner_item_blip);
        adapter2.setDropDownViewResource(R.layout.spinner_item_blip);
        order.setAdapter(adapter2);
        order.setOnItemSelectedListener(new CommunityOrderChanged());

        //configure refresh
        refresh = v.findViewById(R.id.swiperefresh_community_joined);
        refresh.setOnRefreshListener(new RefreshFeed());

        //configure blipp list
        communitiesJoined = v.findViewById(R.id.list_community_joined);
        communitiesJoined.setAdapter(new CommunityAdapter(this.getContext(),  new ArrayList<Community>() , false, false));
        communitiesJoined.setOnScrollListener(new BottomHit());
        communitiesJoined.setOnItemClickListener(new ToCommunityFeed());

        //if there are no previously loaded blips this will start the background action to load them
        getCommunity(null);

        return v;
    }

    private void getCommunity(Community communityToStartAt) {
        didHitBottom = false;

        refresh.setEnabled(true);
        refresh.setRefreshing(true);

        if (order.getSelectedItemPosition() == 0)
            new CommunityGetter(CommunityGetter.Section.JOINED, CommunityGetter.Order.ALPHABETICAL, communityToStartAt, 20, this, this.getContext());

        else if (order.getSelectedItemPosition() == 1)
            new CommunityGetter(CommunityGetter.Section.JOINED, CommunityGetter.Order.MEMBER_COUNT_LOW_TO_HIGH, communityToStartAt, 20, this, this.getContext());

        else if (order.getSelectedItemPosition() == 2)
            new CommunityGetter(CommunityGetter.Section.JOINED, CommunityGetter.Order.MEMBER_COUNT_HIGH_TO_LOW, communityToStartAt, 20, this, this.getContext());

    }


    @Override
    public void communityGetterGotInitalCommunities(ArrayList<Community> results) {
        if (results == null) didHitBottom = true;
        ((CommunityAdapter) communitiesJoined.getAdapter()).setCommunities(results);
        refresh.setRefreshing(false);
    }

    @Override
    public void communityGetterGotAditionalCommunities(ArrayList<Community> results) {
        if (results == null) didHitBottom = true;
        else {
        ((CommunityAdapter) communitiesJoined.getAdapter()).addCommunities(results);
        refresh.setRefreshing(false);}
    }

    @Override
    public void communityGetterDidFail() {
        Toast.makeText(this.getContext(), "Error getting communities, please try again later...", Toast.LENGTH_LONG).show();

    }

    private class RefreshFeed implements SwipeRefreshLayout.OnRefreshListener {
        @Override
        public void onRefresh() {
            getCommunity(null);
        }
    }


    private class CommunityOrderChanged implements Spinner.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l)
        {
            getCommunity(null);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            //UnUsed Method
        }
    }


    class ToCommunityFeed implements ListView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
        {
            Community c = (Community) communitiesJoined.getAdapter().getItem(i);

            Bundle b = new Bundle();
            b.putString("id", c.getId());
            b.putString("name", c.getName());
            b.putString("owner", c.getOwner());
            b.putDouble("lat", c.getOriginLat());
            b.putDouble("lon", c.getOriginLong());
            b.putDouble("radius", c.getRadius());
            b.putString("photo", c.getPhoto() == null ? null : c.getPhoto().toString());
            b.putBoolean("joinable", c.isJoinable());

            CommunityBlipsFragment cbf = new CommunityBlipsFragment();
            cbf.setArguments(b);
            fragmentSwap.swap(cbf, true);

        }
    }

    private class BottomHit implements AbsListView.OnScrollListener {
        @Override
        public void onScrollStateChanged(AbsListView absListView, int i) {
            //UnUsed Method
        }

        @Override
        public void onScroll(AbsListView absListView, int firstVisableItem, int visableItemCount, int totalItemCount) {
            if (firstVisableItem + visableItemCount == totalItemCount && totalItemCount != 0 && !didHitBottom) {
                getCommunity(((Community) ((CommunityAdapter) communitiesJoined.getAdapter()).getItem(totalItemCount - 1)));
            }
        }
    }
}


