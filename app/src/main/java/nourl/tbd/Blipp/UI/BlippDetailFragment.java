package nourl.tbd.Blipp.UI;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Date;

import nourl.tbd.Blipp.BlippConstructs.Blipp;
import nourl.tbd.Blipp.BlippConstructs.Like;
import nourl.tbd.Blipp.Database.BlipDeleter;
import nourl.tbd.Blipp.Database.BlipDeleterCompletion;
import nourl.tbd.Blipp.Database.BlipGetter;
import nourl.tbd.Blipp.Database.BlipGetterCompletion;
import nourl.tbd.Blipp.Database.BlipSender;
import nourl.tbd.Blipp.Database.BlipSenderCompletion;
import nourl.tbd.Blipp.Database.LikeDeleter;
import nourl.tbd.Blipp.Database.LikeDeleterCompletion;
import nourl.tbd.Blipp.Database.LikeGetter;
import nourl.tbd.Blipp.Database.LikeGetterCompletion;
import nourl.tbd.Blipp.Database.LikeSender;
import nourl.tbd.Blipp.Database.LikeSenderCompletion;
import nourl.tbd.Blipp.Helper.BlipListAdapter;
import nourl.tbd.Blipp.R;

public class BlippDetailFragment extends Fragment implements BlipGetterCompletion {

    Blipp blip;

    ImageView photo;
    TextView text;
    Button toParent;

    Button like;
    boolean didLike;

    Button dislike;
    boolean didDislike;

    TextView numLikes;
    Button delete;
    SwipeRefreshLayout refreshLayout;
    ListView replys;
    Button reply;
    Button refresh;
    Spinner order;

    View popupView;
    PopupWindow popupWindow;
    boolean popUpIsShowing;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        //inflate the view
        View v = inflater.inflate(R.layout.blip_detail, container, false);

        //configure image view
        photo = v.findViewById(R.id.blipp_detail_photo);
        //TODO: if photo url exists download here

        //configure the text
        text = v.findViewById(R.id.blipp_detail_text);
        text.setText(blip.getText());

        //Configure the toParent button
        toParent = v.findViewById(R.id.blip_Detail_to_parent);
        toParent.setVisibility(blip.getParent() == null ? View.GONE : View.VISIBLE);
        toParent.setOnClickListener(new ToParent());

        //configure like button
        like = v.findViewById(R.id.blip_detail_like);
        like.setOnClickListener(new LikeButtonsPressed());

        //configure dislike buttons
        dislike = v.findViewById(R.id.blip_detail_dislike);
        dislike.setOnClickListener(new LikeButtonsPressed());

        //configure numLikes
        numLikes = v.findViewById(R.id.blip_detail_num_likes);

        //configure delete button
        delete = v.findViewById(R.id.blip_detail_delete);
        delete.setVisibility(blip.getUserId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) ? View.VISIBLE : View.GONE);
        delete.setOnClickListener(new DeleteBlipp());

        //refreshlayout
        refreshLayout = v.findViewById(R.id.swiperefresh_blip_detail);

        //replys
        replys = v.findViewById(R.id.list_replys_blipp_detail);
        replys.setAdapter(new BlipListAdapter(this.getContext(), new ArrayList<Blipp>()));

        //reply button
        reply = v.findViewById(R.id.blip_detail_reply);
        reply.setOnClickListener(new MakeReply());

        //order
        order = v.findViewById(R.id.spinner_order_blip_detail);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this.getContext(), R.array.blipp_order, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        order.setAdapter(adapter2);
        order.setOnItemSelectedListener(new OrderSelected());

        //refresh button
        refresh = v.findViewById(R.id.blip_detail_btn_refresh);
        refresh.setOnClickListener(new ButtonRefresh());

        getLikes();
        getReplys(null);
        return  v;
    }


    void getReplys(String blipToStartAt)
    {
        refreshLayout.setEnabled(true);
        refreshLayout.setRefreshing(true);

        if (blip.getParent() == null) {refreshLayout.setRefreshing(false);
        return;}

        if (order.getSelectedItemPosition() == 0) new BlipGetter(BlipGetter.Order.MOST_RECENT, this, blipToStartAt, 20, blip.getParent(), BlippDetailFragment.this.getContext());

        if (order.getSelectedItemPosition() == 1) new BlipGetter(BlipGetter.Order.MOST_RECENT, this, blipToStartAt, 20, blip.getParent(), BlippDetailFragment.this.getContext());
    }

    @Override
    public void blipGetterGotInitialBlips(ArrayList<Blipp> results)
    {
        ((BlipListAdapter)replys.getAdapter()).setBlipps(results);
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void blipGetterGotAdditionalBlips(ArrayList<Blipp> results)
    {
        ((BlipListAdapter)replys.getAdapter()).addBlips(results);
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void blipGetterDidFail()
    {
        Toast.makeText(this.getContext(), "Error", Toast.LENGTH_SHORT).show();
    }

    private class OrderSelected implements Spinner.OnItemSelectedListener{
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            getReplys(null);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private class ButtonRefresh implements Button.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
         getLikes();
         getReplys(null);
        }
    }

    private class MakeReply implements Button.OnClickListener
    {
        @Override
        public void onClick(View view)
        {
            //TODO: Create new blip interface and functionality
            //Note: Must include option for blipp to be in either close, regular or max distance.
            //Note: Must include way of adding a photo to your blipp. Photos are optional but text is mandatory.
            //Note: Blipps are anonymous when posting, but please ensure to link the blipp with the current user when storing in the database so we can query them later such as in the My blipps section
            //Note: Please try and make the interface look as aesthetically pleasing as possible AFTER we get the core functionality working, try to make a clean interface we will go back and polish later.
            //Note: Look at the Fire base data face and see what other additional information is stored in each blip (ex: time, location), Some of this information isnt entered by the user but instead requires us to pull it from code.

            if (!popUpIsShowing)
            {
                LayoutInflater layoutInflater = getLayoutInflater();
                popupView = layoutInflater.inflate(R.layout.make_blipp,null);

                popupWindow = new PopupWindow(popupView, RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
                popupWindow.setFocusable(true);

                Button btnClose = (Button)popupView.findViewById(R.id.btnClose);

                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupWindow.dismiss();
                        popUpIsShowing = false;
                    }
                });


                Button btnSubmit = popupView.findViewById(R.id.btnSubmit);
                btnSubmit.setOnClickListener( new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        new BlipSender(new Blipp(((CheckBox) popupView.findViewById(R.id.check_max)).isChecked(), ((CheckBox) popupView.findViewById(R.id.check_reg)).isChecked(), ((CheckBox) popupView.findViewById(R.id.check_close)).isChecked(), ((EditText) popupView.findViewById(R.id.blipp_text_enter)).getText().toString(), null, blip.getId(),blip.getCommunity(), BlippDetailFragment.this.getContext()),
                                new BlipSenderCompletion() {
                                    @Override
                                    public void blipSenderDone(boolean isSuccessful) {
                                        Toast.makeText(BlippDetailFragment.this.getContext(), isSuccessful ? "Sucess" : "Error", Toast.LENGTH_SHORT).show();
                                    }
                                }, BlippDetailFragment.this.getContext());

                        popupWindow.dismiss();
                        popUpIsShowing = false;

                    }
                });

                popupWindow.showAtLocation(BlippDetailFragment.this.getView().getRootView(), Gravity.CENTER, 0, 0);

                popUpIsShowing = true;
            }
        }
    }

    private class ToParent implements Button.OnClickListener
    {

        @Override
        public void onClick(View v)
        {
        //TODO: Make Blip Getter with paramater that takes in a blip ID
        Toast.makeText(BlippDetailFragment.this.getContext(), "Complete me", Toast.LENGTH_SHORT).show();
        }
    }


    private class DeleteBlipp implements  Button.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
         new BlipDeleter(blip, new BlipDeleterCompletion() {
             @Override
             public void blippDeleterDone(boolean isSuccessful)
             {
             if (isSuccessful) BlippDetailFragment.this.getActivity().finish();
             else Toast.makeText(BlippDetailFragment.this.getContext(), "error", Toast.LENGTH_SHORT);

             }
         }, BlippDetailFragment.this.getContext());
        }
    }


    private class LikeButtonsPressed implements Button.OnClickListener
    {
        @Override
        public void onClick(View v)
        {

                if (v.equals(dislike) ? didDislike : didLike)  new LikeDeleter(new Like(blip, v.equals(dislike)), new LikeDeleterCompletion() {
                    @Override
                    public void likeDeleterDone(boolean isSuccessful)
                    {
                        Toast.makeText(BlippDetailFragment.this.getContext(), isSuccessful ? "Success, Unliked." : "Error Un-liking", Toast.LENGTH_SHORT).show();
                    }
                }, BlippDetailFragment.this.getContext());

                if (v.equals(dislike) ? !didDislike : !didLike) new LikeSender(new Like(blip, v.equals(dislike)), new LikeSenderCompletion() {
                    @Override
                    public void likeSenderDone(boolean isSuccessful)
                    {
                        Toast.makeText(BlippDetailFragment.this.getContext(), isSuccessful ? "Success, Unliked." : "Error Un-liking", Toast.LENGTH_SHORT).show();
                    }
                }, BlippDetailFragment.this.getContext());
                getLikes();
        }
    }

    void updateLikeButtons()
    {
        like.setHighlightColor( getResources().getColor( didLike ? R.color.colorPrimary : R.color.colorAccent, null));
        dislike.setHighlightColor( getResources().getColor( didDislike ? R.color.colorPrimary : R.color.colorAccent, null));
    }

    void getLikes()
    {
        new LikeGetter(this.blip, new LikeGetterCompletion() {
            @Override
            public void likeGetterSucessful(ArrayList<Like> likes)
            {
                numLikes.setText(String.valueOf(likes.size()));
               didDislike = likes.contains(new Like(true, blip.getId(), FirebaseAuth.getInstance().getCurrentUser().getUid()));
               didLike =  likes.contains(new Like(false, blip.getId(), FirebaseAuth.getInstance().getCurrentUser().getUid()));
                updateLikeButtons();
            }

            @Override
            public void likeGetterUnsucessful()
            {
                Toast.makeText(BlippDetailFragment.this.getContext(), "Error getting likes.", Toast.LENGTH_SHORT).show();
            }
        }, this.getContext());
    }



}
