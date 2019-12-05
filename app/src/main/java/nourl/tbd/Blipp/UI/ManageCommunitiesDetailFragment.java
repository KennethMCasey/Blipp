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
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import nourl.tbd.Blipp.BlippConstructs.Community;
import nourl.tbd.Blipp.Database.MemberGetter;
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

        Bundle b = getArguments();

        String photoG = b.getString("photo",null);
        String nameG = b.getString("name", null);
        String idG = b.getString("id", null);
        double latG = b.getDouble("lat", 0);
        double lonG = b.getDouble("lon", 0);
        double radiusG = b.getDouble("radius", 0);
        String ownerG = b.getString("owner", null);
        boolean isJoinableG = b.getBoolean("isJoinable", false);

        community = new Community(idG, photoG, latG, lonG, radiusG, nameG, isJoinableG,ownerG);

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
                                //TODO: change member_row name
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
