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

import nourl.tbd.Blipp.BlippConstructs.Blipp;
import nourl.tbd.Blipp.BlippConstructs.Community;
import nourl.tbd.Blipp.Database.CommunityGetter;
import nourl.tbd.Blipp.Database.CommunityGetterCompletion;
import nourl.tbd.Blipp.Helper.BlipListAdapter;
import nourl.tbd.Blipp.Helper.CommunityAdapter;
import nourl.tbd.Blipp.Helper.StatePersistence;
import nourl.tbd.Blipp.R;

public class CommunityJoinedFragment extends Fragment implements CommunityGetterCompletion {
    //TODO: Community Fragment. This fragment will consist of a list view that will be populated by all of the communities that the user is either a member of or an owner of. They can then select on any of those communities which will bring them to another activity which will esentially be the same as BlippFeedFragment but only with community specific Blips.


    Spinner order;
    SwipeRefreshLayout refresh;
    ListView communitiesJoined;

    boolean didHitBottom;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.community_joined, container, false);

        //configure drop down menu
        order = v.findViewById(R.id.spinner_community_joined);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(v.getContext(), R.array.community_order, R.layout.spinner_item_blip);
        adapter2.setDropDownViewResource(R.layout.spinner_item_blip);
        order.setAdapter(adapter2);
        order.setSelection(StatePersistence.current.communityJoinedOrderPosition, false);
        order.setOnItemSelectedListener(new CommunityOrderChanged());

        //configure refresh
        refresh = v.findViewById(R.id.swiperefresh_community_joined);
        refresh.setOnRefreshListener(new RefreshFeed());

        //configure blipp list
        communitiesJoined = v.findViewById(R.id.list_community_joined);
        communitiesJoined.setAdapter(new CommunityAdapter(this.getContext(), StatePersistence.current.comunityJoined == null ? new ArrayList<Community>() : StatePersistence.current.comunityJoined));
        communitiesJoined.setOnScrollListener(new BottomHit());

        //if there are no previously loaded blips this will start the background action to load them
        if (StatePersistence.current.comunityJoined == null) getCommunity(null);

        return v;
    }

    private void getCommunity(Community communityToStartAt) {
        didHitBottom = false;

        refresh.setEnabled(true);
        refresh.setRefreshing(true);

        //TODO: Pull the blip data from the database
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
        StatePersistence.current.comunityJoined = results;
        ((CommunityAdapter) communitiesJoined.getAdapter()).setCommunities(results);
        refresh.setRefreshing(false);
    }

    @Override
    public void communityGetterGotAditionalCommunities(ArrayList<Community> results) {
        if (results == null) didHitBottom = true;
        else {
        StatePersistence.current.comunityJoined.addAll(results);
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
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

            StatePersistence.current.communityJoinedOrderPosition = position;

            getCommunity(null);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            //UnUsed Method
        }
    }

    private class BottomHit implements AbsListView.OnScrollListener {
        @Override
        public void onScrollStateChanged(AbsListView absListView, int i) {
            //UnUsed Method
        }

        @Override
        public void onScroll(AbsListView absListView, int firstVisableItem, int visableItemCount, int totalItemCount) {

            /*Our Blipp Feed should only load 50 or less Blipps at a time. These will either be the 50 most recent or the 50 most liked depending on user configuration
            TODO: When our list is displaying the very last possible count of items, we should pull the next 50 items from Firebase if the user wishes to keep scrolling
             */

            if (firstVisableItem + visableItemCount == totalItemCount && totalItemCount != 0 && !didHitBottom) {
                getCommunity(((Community) ((CommunityAdapter) communitiesJoined.getAdapter()).getItem(totalItemCount - 1)));
            }
        }
    }
}


