package nourl.tbd.Blipp.UI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import nourl.tbd.Blipp.BlippConstructs.Community;
import nourl.tbd.Blipp.BlippConstructs.Member;
import nourl.tbd.Blipp.Database.MemberGetter;
import nourl.tbd.Blipp.Database.MemberGetterCompletion;
import nourl.tbd.Blipp.Database.MemberSender;
import nourl.tbd.Blipp.Database.MemberSenderCompletion;
import nourl.tbd.Blipp.R;

public class ManageCommunitiesDetailFragment extends Fragment
{
    EditText name;
    Button submit;
    Button active;
    Button banned;
    Community community;
    FragmentSwap fragmentSwap;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        fragmentSwap = (FragmentSwap)getActivity();
        fragmentSwap.postFragId(10);

        Bundle b = getArguments();

        String photoG = b.getString("photo",null);
        String nameG = b.getString("name", null);
        String idG = b.getString("id", null);
        double latG = b.getDouble("lat", 0);
        double lonG = b.getDouble("lon", 0);
        double radiusG = b.getDouble("radius", 0);
        String ownerG = b.getString("owner", null);
        boolean isJoinableG = b.getBoolean("isJoinable", false);
        int numMem = b.getInt("numMem", -1);

        community = new Community(idG, photoG, latG, lonG, radiusG, nameG, isJoinableG,ownerG, numMem);

        View v = inflater.inflate(R.layout.manage_community_detail, null);

        name = v.findViewById(R.id.community_manage_detail_name);

        submit = v.findViewById(R.id.community_manage_detail_submit);
        submit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (name.getText() == null || name.getText().toString().trim().isEmpty()) Toast.makeText(getContext(), "Error: Please fill in your nick name to chanhe it", Toast.LENGTH_SHORT).show();
                else
                    {
                        if (name.getText().toString().equals("*OWNER*") && !community.getOwner().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) Toast.makeText(getContext(), "Error: You are attempting to change your name to a reserved phrase. Please select another name.", Toast.LENGTH_SHORT).show();
                        else
                            {

                                new MemberGetter(community, MemberGetter.Order.ALPHABETICAL, MemberGetter.Section.ACTIVE, new MemberGetterCompletion() {
                                    @Override
                                    public void memberGetterGotInitalMembers(ArrayList<Member> members)
                                    {
                                        for (int i = 0; i < members.size(); i++)
                                        {
                                            Member mem = members.get(i);
                                            if (mem.getUserId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                                            {
                                                Member memU = new Member(mem.getCommunityId(), mem.getUserId(), mem.isBanned(), name.getText().toString() ,mem.getDateJoined(), mem.getMemberId());

                                                new MemberSender(memU, new MemberSenderCompletion()
                                                {
                                                    @Override
                                                    public void memberSenderDone(boolean isSuccessful)
                                                    {
                                                        Toast.makeText(getContext(), isSuccessful ? "Success: Updated group member name" : "Error: Could not update name", Toast.LENGTH_SHORT).show();
                                                    }
                                                }, getContext());
                                                return;
                                            }
                                        } Toast.makeText(getContext(),  "FATAL ERROR: Could not find your member instance.", Toast.LENGTH_SHORT).show();

                                    }

                                    @Override
                                    public void memberGetterGotAditionalMembers(ArrayList<Member> members)
                                    {
                                        //unused
                                    }

                                    @Override
                                    public void memberGetterDidFail()
                                    {
                                        Toast.makeText(getContext(), "Error: Could not update name", Toast.LENGTH_SHORT).show();
                                    }
                                }, null, -1, getContext());
                            }
                    }
            }
        });

        active = v.findViewById(R.id.community_manage_detail_active_btn);
        active.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Bundle b = new Bundle();
                b.putString("photo", community.getPhoto());
                b.putString("name", community.getName());
                b.putString("id", community.getId());
                b.putDouble("lat", community.getOriginLat());
                b.putDouble("lon", community.getOriginLong());
                b.putDouble("radius", community.getRadius());
                b.putString("owner", community.getOwner());
                b.putBoolean("isJoinable", community.isJoinable());
                b.putBoolean("isActive", true);

                Fragment f = new MemberListFragment();
                f.setArguments(b);
                fragmentSwap.swap(f, true);
            }
        });


        banned = v.findViewById(R.id.community_manage_detail_banned_btn);
        banned.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Bundle b = new Bundle();
                b.putString("photo", community.getPhoto());
                b.putString("name", community.getName());
                b.putString("id", community.getId());
                b.putDouble("lat", community.getOriginLat());
                b.putDouble("lon", community.getOriginLong());
                b.putDouble("radius", community.getRadius());
                b.putString("owner", community.getOwner());
                b.putBoolean("isJoinable", community.isJoinable());
                b.putBoolean("isActive", false);

                Fragment f = new MemberListFragment();
                f.setArguments(b);
                fragmentSwap.swap(f, true);
            }
        });

        return v;
    }
}
