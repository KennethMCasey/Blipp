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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import nourl.tbd.Blipp.BlippConstructs.Blipp;
import nourl.tbd.Blipp.BlippConstructs.Community;
import nourl.tbd.Blipp.BlippConstructs.Member;
import nourl.tbd.Blipp.BlippConstructs.User;
import nourl.tbd.Blipp.Database.CommunityDeleter;
import nourl.tbd.Blipp.Database.CommunityDeleterCompletion;
import nourl.tbd.Blipp.Database.MemberDeleter;
import nourl.tbd.Blipp.Database.MemberDeleterCompletion;
import nourl.tbd.Blipp.Database.MemberGetter;
import nourl.tbd.Blipp.Database.MemberGetterCompletion;
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

    public CommunityAdapter(Context context, ArrayList<Community> communities, boolean hasButton, boolean isDeleteButton) {
        this.context = context;
        this.communities = communities;
        this.hasButton = hasButton;
        this.isDeleteButton = isDeleteButton;
    }

    @Override
    public int getCount() {
        return communities.size();
    }

    @Override
    public Object getItem(int i) {
        return communities.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final boolean isUserOwner = communities.get(i).getOwner().equals(FirebaseAuth.getInstance().getCurrentUser().getUid());

        if (view == null) {
            view = LayoutInflater.from(context).inflate(hasButton ? R.layout.community_with_button : R.layout.community, viewGroup, false);
        }

        Community curr = communities.get(i);

        TextView name = view.findViewById(R.id.community_name);
        name.setText(curr.getName());

        ImageView img = view.findViewById(R.id.community_photo);

        if (curr.getPhoto() == null) {
            img.setVisibility(View.GONE);
        } else {
            img.setVisibility(View.VISIBLE);
            Picasso.get().load(communities.get(i).getPhoto().equals("http://fake.com/") ? "https://firebasestorage.googleapis.com/v0/b/blipp-15ee8.appspot.com/o/blipp.png?alt=media&token=06f2b24e-0905-469e-b092-b73f466edfc3" : communities.get(i).getPhoto()).into(img);

        }


        if (hasButton) {
            ((ViewGroup) view).setDescendantFocusability(isDeleteButton ? ViewGroup.FOCUS_BLOCK_DESCENDANTS : ViewGroup.FOCUS_BEFORE_DESCENDANTS);

            final Button b = view.findViewById(R.id.community_button);
            b.setVisibility(hasButton ? View.VISIBLE : View.GONE);

            b.setText(!isDeleteButton ? "Join" : isUserOwner ? "Delete" : "Leave");
            b.setBackgroundColor(context.getResources().getColor(isDeleteButton ? R.color.blipp_red : R.color.blipp_offset, null));
            b.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isDeleteButton) {
                        if (isUserOwner)
                            new CommunityDeleter(communities.get(i), new CommunityDeleterCompletion() {
                                @Override
                                public void communityDeleterDone(boolean isSuccessful) {
                                    if (isSuccessful)
                                    {
                                        new MemberGetter(communities.get(i), MemberGetter.Order.ALPHABETICAL, MemberGetter.Section.ACTIVE, new MemberGetterCompletion() {
                                            @Override
                                            public void memberGetterGotInitalMembers(ArrayList<Member> members)
                                            {
                                                for (int i = 0; i < members.size(); i++) new MemberDeleter(members.get(i), new MemberDeleterCompletion() {
                                                    @Override
                                                    public void memberDeleterDone(boolean isSuccessful)
                                                    {
                                                    }
                                                }, context);
                                            }

                                            @Override
                                            public void memberGetterGotAditionalMembers(ArrayList<Member> members)
                                            {
                                            }

                                            @Override
                                            public void memberGetterDidFail()
                                            {

                                            }
                                        }, null, -1, context);
                                        communities.remove(i);
                                        notifyDataSetChanged();
                                    }
                                    Toast.makeText(context, isSuccessful ? "Success: Community deleted." : "Error", Toast.LENGTH_SHORT).show();
                                }
                            }, context);
                        else {
                            new MemberGetter(communities.get(i), MemberGetter.Order.ALPHABETICAL, MemberGetter.Section.ACTIVE, new MemberGetterCompletion() {
                                @Override
                                public void memberGetterGotInitalMembers(final ArrayList<Member> members) {
                                    for (int i = 0; i < members.size(); i++) {
                                        Member mem = members.get(i);
                                        if (mem.getUserId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                            final int index = i;
                                            new MemberDeleter(mem, new MemberDeleterCompletion() {
                                                @Override
                                                public void memberDeleterDone(boolean isSuccessful) {
                                                    Toast.makeText(context, isSuccessful ? "Success: You are no longer a member" : "Error: Could not leave group", Toast.LENGTH_SHORT).show();
                                                    if (isSuccessful) {members.remove(index);
                                                    notifyDataSetChanged();
                                                    }
                                                }
                                            }, context);
                                            return;
                                        }
                                    } Toast.makeText(context,  "FATAL ERROR: Could not find your member instance.", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void memberGetterGotAditionalMembers(ArrayList<Member> members) {

                                }

                                @Override
                                public void memberGetterDidFail() {

                                }
                            }, null, -1, context);
                        }

                    } else {
                        new UserGetter(FirebaseAuth.getInstance().getCurrentUser().getUid(), new UserGetterCompletion() {
                            @Override
                            public void userGetterSuccess(User user) {
                                Member sendMe = new Member(communities.get(i).getId(), user.getName());
                                new MemberSender(sendMe, new MemberSenderCompletion() {
                                    @Override
                                    public void memberSenderDone(boolean isSuccessful) {

                                        Toast.makeText(context, isSuccessful ? "Success: Community joined." : "Error: Could not join community", Toast.LENGTH_SHORT).show();
                                        if (isSuccessful) {
                                            communities.remove(i);
                                            notifyDataSetChanged();
                                        }
                                    }
                                }, context);
                            }

                            @Override
                            public void userGetterFailure() {
                                Toast.makeText(context, "Error: Could not join community", Toast.LENGTH_SHORT).show();
                            }
                        }, context);
                    }
                }
            });
        }
        return view;
    }

    public void setCommunities(ArrayList<Community> communities) {
        this.communities = communities;
        notifyDataSetChanged();
    }

    public void addCommunities(ArrayList<Community> communities) {
        this.communities.addAll(communities);
        notifyDataSetChanged();
    }
}
