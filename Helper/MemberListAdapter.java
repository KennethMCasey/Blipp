package nourl.tbd.Blipp.Helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import nourl.tbd.Blipp.BlippConstructs.Member;
import nourl.tbd.Blipp.Database.MemberSender;
import nourl.tbd.Blipp.Database.MemberSenderCompletion;
import nourl.tbd.Blipp.R;

public class MemberListAdapter extends BaseAdapter
{
    private ArrayList<Member> members;
    private Context context;
    private boolean isOwner;
    private boolean isActive;

    public MemberListAdapter(ArrayList<Member> members, Context context, boolean isOwner, boolean isActive)
    {
        this.isActive = isActive;
        this.isOwner = isOwner;
        this.members = members;
        this.context = context;
    }

    @Override
    public int getCount() {
        return members.size();
    }

    @Override
    public Object getItem(int i) {
        return members.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup)
    {
        if (view == null) view = LayoutInflater.from(context).inflate(R.layout.member_row, viewGroup, false);


        Member currMember = members.get(i);

        TextView name = view.findViewById(R.id.member_name);
        name.setText("Name: " + currMember.getDisplayName() + "\n" + "UUID: " + currMember.getUserId());

        Button button = view.findViewById(R.id.member_button);
        button.setVisibility(isOwner ? View.VISIBLE : View.GONE);
        if (isOwner)
        {
            button.setText(isActive ? "Ban" : "Un-Ban");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    Member mem = members.get(i);
                    Member memU = new Member(mem.getCommunityId(), mem.getUserId(), !mem.isBanned(), mem.getDisplayName(), mem.getDateJoined(), mem.getMemberId());
                    new MemberSender(memU, new MemberSenderCompletion() {
                        @Override
                        public void memberSenderDone(boolean isSuccessful)
                        {
                            Toast.makeText(context, !isSuccessful ? "Error: Could not complete action." : isActive ? "Success: Member was banned" : "Success: Member is un-banned", Toast.LENGTH_SHORT).show();
                            if (isSuccessful)
                            {
                                members.remove(i);
                                notifyDataSetChanged();
                            }
                        }
                    }, context);

                }
            });


        }
        return view;
    }

    public void setMembers(ArrayList<Member> members)
    {
        this.members = members;
        notifyDataSetChanged();
    }

    public  void addMembers(ArrayList<Member> members)
    {
        this.members.addAll(members);
        notifyDataSetChanged();
    }
}
