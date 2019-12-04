package nourl.tbd.Blipp.Helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import nourl.tbd.Blipp.BlippConstructs.Blipp;
import nourl.tbd.Blipp.BlippConstructs.Community;
import nourl.tbd.Blipp.BlippConstructs.Member;
import nourl.tbd.Blipp.BlippConstructs.User;
import nourl.tbd.Blipp.Database.CommunityDeleter;
import nourl.tbd.Blipp.Database.CommunityDeleterCompletion;
import nourl.tbd.Blipp.Database.MemberDeleter;
import nourl.tbd.Blipp.Database.MemberGetter;
import nourl.tbd.Blipp.Database.MemberSender;
import nourl.tbd.Blipp.Database.MemberSenderCompletion;
import nourl.tbd.Blipp.Database.UserGetter;
import nourl.tbd.Blipp.Database.UserGetterCompletion;
import nourl.tbd.Blipp.R;


public class CommunityAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Community> communities;
    private boolean hasButton;
    private boolean isDeleteButton;

    public CommunityAdapter(Context context, ArrayList<Community> communities, boolean hasButton, boolean isDeleteButton)
    {
        this.context = context;
        this.communities = communities;
        this.hasButton = hasButton;
        this.isDeleteButton = isDeleteButton;
    }

    @Override
    public int getCount()
    {
        return communities.size();
    }

    @Override
    public Object getItem(int i)
    {
        return communities.get(i);
    }

    @Override
    public long getItemId(int i)
    {
        return i;
    }


    @Override
    public View getView(final int i, View view, ViewGroup viewGroup)
    {
       final boolean isUserOwner = communities.get(i).getOwner().equals(FirebaseAuth.getInstance().getCurrentUser().getUid());

        if (view == null)
        {
            view = LayoutInflater.from(context).inflate( hasButton ? R.layout.community_with_button : R.layout.community, viewGroup, false);
        }

        Community curr = communities.get(i);

        TextView name = view.findViewById(R.id.community_name);

        ImageView img = view.findViewById(R.id.community_photo);

        if (curr.getPhoto() == null)
        {
            img.setVisibility(View.GONE);
            name.setText(curr.getName());
        }
        else
        {
            img.setVisibility(View.GONE);
            name.setText(curr.getName());

            //img.setVisibility(View.VISIBLE);
            //img.setImageBitmap(Bitmap.createBitmap(curr.));//TODO: This has not been tested, this line as well as the blip class may need to be adjusted in order to show an image
            //text.setText(curr.getText());
        }


        if (hasButton)
        {
            ((ViewGroup)view).setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);

            final Button b = view.findViewById(R.id.community_button);
            b.setText( !isDeleteButton  ? "Join" : isUserOwner ? "Delete" : "Leave");
            b.setBackgroundColor( context.getResources().getColor (isDeleteButton ? R.color.blipp_red : R.color.blipp_offset, null));
            b.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    if (isDeleteButton)
                    {
                       if (isUserOwner) new CommunityDeleter(communities.get(i), new CommunityDeleterCompletion() {
                            @Override
                            public void communityDeleterDone(boolean isSuccessful)
                            {
                                if (isSuccessful) b.setVisibility(View.INVISIBLE);
                                Toast.makeText(context, isSuccessful ? "Success: Community deleted." : "Error", Toast.LENGTH_SHORT).show();
                            }
                        }, context);
                    else
                        {
                            //TODO: Leave group here
                        }

                    }

                    else
                        {
                            Toast.makeText(context,"Here", Toast.LENGTH_SHORT).show();

                            new UserGetter(FirebaseAuth.getInstance().getCurrentUser().getUid(), new UserGetterCompletion() {
                                @Override
                                public void userGetterSuccess(User user)
                                {
                                    Toast.makeText(context,"Here2", Toast.LENGTH_SHORT).show();
                                    Member sendMe = new Member(communities.get(i).getId(), user.getName());
                                    new MemberSender(sendMe, new MemberSenderCompletion() {
                                        @Override
                                        public void memberSenderDone(boolean isSuccessful)
                                        {

                                            Toast.makeText(context, isSuccessful ? "Success: Community joined." : "Error: Could not join community", Toast.LENGTH_SHORT).show();
                                            if (isSuccessful) b.setVisibility(View.INVISIBLE);
                                        }
                                    }, context);
                                }

                                @Override
                                public void userGetterFailure()
                                {
                                    Toast.makeText(context, "Error: Could not join community", Toast.LENGTH_SHORT).show();
                                }
                            }, context);
                        }
                }
            });
        }
        return view;
    }

    public void setCommunities(ArrayList<Community> communities)
    {
        this.communities = communities;
        notifyDataSetChanged();
    }

    public  void addCommunities(ArrayList<Community> communities)
    {
        this.communities.addAll(communities);
        notifyDataSetChanged();
    }
}
