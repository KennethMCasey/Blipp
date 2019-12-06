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

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import nourl.tbd.Blipp.BlippConstructs.Community;
import nourl.tbd.Blipp.BlippConstructs.Member;
import nourl.tbd.Blipp.Database.MemberGetter;
import nourl.tbd.Blipp.Database.MemberGetterCompletion;
import nourl.tbd.Blipp.Helper.MemberListAdapter;
import nourl.tbd.Blipp.R;

public class MemberListFragment extends Fragment implements MemberGetterCompletion {
      Spinner order;
    SwipeRefreshLayout refresh;
    ListView membersList;
    FragmentSwap fragmentSwap;
    Community community;

    boolean isBanned;

    boolean didHitBottom;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentSwap = (FragmentSwap) getActivity();
        fragmentSwap.postFragId(12);

        View v = inflater.inflate(R.layout.community_joined, container, false);

        Bundle b = getArguments();

        String photoG = b.getString("photo",null);
        String nameG = b.getString("name", null);
        String idG = b.getString("id", null);
        double latG = b.getDouble("lat", 0);
        double lonG = b.getDouble("lon", 0);
        double radiusG = b.getDouble("radius", 0);
        String ownerG = b.getString("owner", null);
        boolean isJoinableG = b.getBoolean("isJoinable", false);
        isBanned =  !b.getBoolean("isActive", false);

        community = new Community(idG, photoG, latG, lonG, radiusG, nameG, isJoinableG,ownerG, 0);

        //configure drop down menu
        order = v.findViewById(R.id.spinner_community_joined);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(v.getContext(), R.array.member_order, R.layout.spinner_item_blip);
        adapter2.setDropDownViewResource(R.layout.spinner_item_blip);
        order.setAdapter(adapter2);
        order.setOnItemSelectedListener(new CommunityOrderChanged());

        //configure refresh
        refresh = v.findViewById(R.id.swiperefresh_community_joined);
        refresh.setOnRefreshListener(new RefreshFeed());

        //configure blipp list
        membersList = v.findViewById(R.id.list_community_joined);
        membersList.setAdapter(new MemberListAdapter(new ArrayList<Member>(), v.getContext(),  community.getOwner().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) ,!isBanned));
        membersList.setOnScrollListener(new BottomHit());

        //if there are no previously loaded blips this will start the background action to load them
        getMembers(null);

        return v;
    }

    private void getMembers(Member memberToStartAt) {
        didHitBottom = false;

        refresh.setEnabled(true);
        refresh.setRefreshing(true);

        if (order.getSelectedItemPosition() == 0)
        {
            if (isBanned) new MemberGetter(community, MemberGetter.Order.ALPHABETICAL, MemberGetter.Section.BANNED, this, memberToStartAt, 20, getContext());
            else new MemberGetter(community, MemberGetter.Order.ALPHABETICAL, MemberGetter.Section.ACTIVE, this, memberToStartAt, 20, getContext());
        }

        else if (order.getSelectedItemPosition() == 1)
        {
            if (isBanned) new MemberGetter(community, MemberGetter.Order.NEWEST_TO_OLDEST, MemberGetter.Section.BANNED, this, memberToStartAt, 20, getContext());
            else new MemberGetter(community, MemberGetter.Order.NEWEST_TO_OLDEST, MemberGetter.Section.ACTIVE, this, memberToStartAt, 20, getContext());
        }

    }

    @Override
    public void memberGetterGotInitalMembers(ArrayList<Member> results) {
        if (results == null) didHitBottom = true;
        ((MemberListAdapter) membersList.getAdapter()).setMembers(results);
        refresh.setRefreshing(false);
    }

    @Override
    public void memberGetterGotAditionalMembers(ArrayList<Member> results) {
        if (results == null) didHitBottom = true;
        else
            {
            ((MemberListAdapter) membersList.getAdapter()).addMembers(results);
            refresh.setRefreshing(false);
            }
    }

    @Override
    public void memberGetterDidFail() {
        Toast.makeText(this.getContext(), "Error getting members, please try again later...", Toast.LENGTH_LONG).show();
    }

    private class RefreshFeed implements SwipeRefreshLayout.OnRefreshListener {
        @Override
        public void onRefresh() {
            getMembers(null);
        }
    }


    private class CommunityOrderChanged implements Spinner.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l)
        {
            getMembers(null);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView)
        {
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

            if (firstVisableItem + visableItemCount == totalItemCount && totalItemCount != 0 && !didHitBottom) {
                getMembers(((Member) ((MemberListAdapter) membersList.getAdapter()).getItem(totalItemCount - 1)));
            }
        }
    }
}