package nourl.tbd.Blipp.UI;

import android.content.res.Resources;
import android.os.Bundle;
import android.text.style.LineHeightSpan;
import android.text.style.TtsSpan;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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
import nourl.tbd.Blipp.Helper.LocationGetter;
import nourl.tbd.Blipp.Helper.LocationGetterCompletion;
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

    boolean didHitBottom;

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

    View emptyView;

    FragmentSwap fragmentSwap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        fragmentSwap = (FragmentSwap) getActivity();

        Bundle b = getArguments();

       String blipID = b.getString("blipID", null);
       String blipParent = b.getString("blipParent", null);
       String blipText = b.getString("blipText", null);
        String blipUser = b.getString("blipUser", null);
        String blipCommunity = b.getString("blipCommunity", null);
        String blipURL = b.getString("blipURL", null);
        double blipLat =b.getDouble("blipLat", -1);
        double blipLon = b.getDouble("blipLon", -1);
        String blipTime = b.getString("blipTime", null);
        boolean blipShort = b.getBoolean("blipShort", false);
        boolean blipMed = b.getBoolean("blipMed", false);
        boolean blipLong = b.getBoolean("blipLong", false);

        try {Date temp = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.ENGLISH).parse(blipTime);

        this.blip = new Blipp(blipLat, blipLon, blipLong, blipMed, blipShort, temp  , blipID, blipText, blipURL, blipUser, blipParent, blipCommunity);}
        catch (Exception e ){    }




        //inflate the view
        View v = inflater.inflate(R.layout.blip_detail, container, false);


        //int index = container.indexOfChild(pp);
        //container.removeView(pp);
        Blip blipView = new Blip(this.getContext()).withBlip(this.blip);
        blipView.setLayoutParams( new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 5.0f));
        ((ViewGroup)v).addView(blipView, 0);



        //configure image view
        //photo = v.findViewById(R.id.blipp_detail_photo);
        //TODO: if photo url exists download here

        //Configure the toParent button
       toParent = v.findViewById(R.id.blip_detail_to_parent);
        toParent.setVisibility(blip.getParent() == null ? View.GONE : View.VISIBLE);
        toParent.setOnClickListener(new ToParent());




        //configure delete button
       delete = v.findViewById(R.id.blip_detail_delete);
        delete.setVisibility(blip.getUserId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) ? View.VISIBLE : View.GONE);
        delete.setOnClickListener(new DeleteBlipp());

        //refreshlayout
        refreshLayout = v.findViewById(R.id.swiperefresh_blip_detail);
        refreshLayout.setOnRefreshListener(new RefreshFeed());

        //replys
        replys = v.findViewById(R.id.list_replys_blipp_detail);
        replys.setEmptyView(v.findViewById(R.id.empty_view));
        replys.setAdapter(new BlipListAdapter(this.getContext(), new ArrayList<Blipp>()));
        replys.setOnScrollListener(new BottomHit());
        replys.setOnItemClickListener(new ToBlipDetail());

        //empty View
        emptyView = v.findViewById(R.id.blip_detail_empty);


        //reply button
        reply = v.findViewById(R.id.blip_detail_reply);
        reply.setOnClickListener(new MakeReply());

        //order
        order = v.findViewById(R.id.spinner_order_blip_detail);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this.getContext(), R.array.blipp_order, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        order.setAdapter(adapter2);
        order.setOnItemSelectedListener(new OrderSelected());

        //getActivity().getSupportFragmentManager().backStackk

        getReplys(null);
        return  v;
    }




    void getReplys(String blipToStartAt)
    {
        refreshLayout.setEnabled(true);
        refreshLayout.setRefreshing(true);


        if (order.getSelectedItemPosition() == 0) new BlipGetter(BlipGetter.Order.MOST_RECENT, this, blipToStartAt, 20, blip.getId(), BlippDetailFragment.this.getContext());

        if (order.getSelectedItemPosition() == 1) new BlipGetter(BlipGetter.Order.MOST_LIKED, this, blipToStartAt, 20, blip.getId(), BlippDetailFragment.this.getContext());
    }

    @Override
    public void blipGetterGotInitialBlips(ArrayList<Blipp> results)
    {
        emptyView.setVisibility(results == null || results.size() == 0 ? View.VISIBLE : View.GONE);
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
        emptyView.setVisibility(View.GONE);
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
                popupView.findViewById(R.id.check_close).setVisibility(View.GONE);
                popupView.findViewById(R.id.check_reg).setVisibility(View.GONE);
                popupView.findViewById(R.id.check_max).setVisibility(View.GONE);

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


                        new LocationGetter(getContext(), new LocationGetterCompletion() {
                            @Override
                            public void locationGetterDidGetLocation(double latitude, double longitude)
                            {
                                new BlipSender(new Blipp(latitude, longitude, false, false, false, ((EditText) popupView.findViewById(R.id.make_blipp_text)).getText().toString(), null ,blip.getId(), null),
                                        new BlipSenderCompletion() {
                                            @Override
                                            public void blipSenderDone(boolean isSuccessful) {
                                                Toast.makeText(BlippDetailFragment.this.getContext(), isSuccessful ? "Sucess" : "Error", Toast.LENGTH_SHORT).show();
                                            }
                                        }, BlippDetailFragment.this.getContext());
                            }

                            @Override
                            public void locationGetterDidFail(boolean shouldShowMessage)
                            {
                                if (shouldShowMessage) Toast.makeText(BlippDetailFragment.this.getContext(), "Error: could not get location", Toast.LENGTH_SHORT).show();
                            }
                        });

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

            new BlipGetter(blip.getParent(), new BlipGetterCompletion() {
                @Override
                public void blipGetterGotInitialBlips(ArrayList<Blipp> results)
                {
                    Blipp blipp = results.get(0);
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

                @Override
                public void blipGetterGotAdditionalBlips(ArrayList<Blipp> results) { }

                @Override
                public void blipGetterDidFail()
                {
                    Toast.makeText(BlippDetailFragment.this.getContext(), "Error: Could not get parent.", Toast.LENGTH_SHORT).show();
                }
            }, getContext());
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
             else Toast.makeText(BlippDetailFragment.this.getContext(), "Error: Blip Not Deleted", Toast.LENGTH_SHORT);

             }
         }, BlippDetailFragment.this.getContext());
        }
    }

    private class RefreshFeed implements SwipeRefreshLayout.OnRefreshListener
    {
        @Override
        public void onRefresh()
        {
            getReplys(null);
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

            /*Our Blipp Feed should only load 50 or less Blipps at a time. These will either be the 50 most recent or the 50 most liked depending on user configuration
            TODO: When our list is displaying the very last possible count of items, we should pull the next 50 items from Firebase if the user wishes to keep scrolling
             */

            if (firstVisableItem + visableItemCount == totalItemCount && totalItemCount!=0  && !didHitBottom )
            {
                getReplys(((Blipp)((BlipListAdapter)replys.getAdapter()).getItem(totalItemCount-1)).getId());
            }
        }
    }

    class ToBlipDetail implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {


            Blipp blipp = (Blipp)replys.getAdapter().getItem(position);

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


    private class ImageDetailView implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {

        }
    }

}
