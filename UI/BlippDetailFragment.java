package nourl.tbd.Blipp.UI;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import nourl.tbd.Blipp.BlippConstructs.Blipp;
import nourl.tbd.Blipp.Database.BlipDeleter;
import nourl.tbd.Blipp.Database.BlipDeleterCompletion;
import nourl.tbd.Blipp.Database.BlipGetter;
import nourl.tbd.Blipp.Database.BlipGetterCompletion;
import nourl.tbd.Blipp.Database.BlipSender;
import nourl.tbd.Blipp.Database.BlipSenderCompletion;
import nourl.tbd.Blipp.Helper.BlipListAdapter;
import nourl.tbd.Blipp.Helper.LocationGetter;
import nourl.tbd.Blipp.Helper.LocationGetterCompletion;
import nourl.tbd.Blipp.R;

import static android.app.Activity.RESULT_OK;

public class BlippDetailFragment extends Fragment implements BlipGetterCompletion {

    Blipp blip;

    TextView text;
    Button toParent;

    boolean didHitBottom;

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

    String currentPhotoUrl;

    static int REQUEST_IMAGE_GET = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        fragmentSwap = (FragmentSwap) getActivity();
        fragmentSwap.postFragId(5);

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

        this.blip = new Blipp(blipLat, blipLon, blipLong, blipMed, blipShort, temp  , blipID, blipText, blipURL, blipUser, blipParent, blipCommunity, -1);}

        catch (Exception e )
        {
            Toast.makeText(getContext(), "ERROR: Could not parse time.", Toast.LENGTH_LONG).show();
            this.blip = new Blipp(blipLat, blipLon, blipLong, blipMed, blipShort, new Date() , blipID, blipText, blipURL, blipUser, blipParent, blipCommunity, 0);
        }

        //inflate the view
        View v = inflater.inflate(R.layout.blip_detail, container, false);


        //int index = container.indexOfChild(pp);
        //container.removeView(pp);
        Blip blipView = new Blip(this.getContext()).withBlip(this.blip);
        blipView.setLayoutParams( new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 5.0f));
        ((ViewGroup)v).addView(blipView, 0);

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

        getReplys(null);

        return  v;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (currentPhotoUrl != null) FirebaseStorage.getInstance().getReference().child(currentPhotoUrl).delete();
        currentPhotoUrl = null;

        if (requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK) {
            Bitmap thumbnail = data.getParcelableExtra("data");
            Uri fullPhotoUri = data.getData();
            ImageView iv = popupView.findViewById(R.id.make_blipp_photo);
            iv.setImageURI(fullPhotoUri);
            final String currentPhotoPath = FirebaseStorage.getInstance().getReference().child(UUID.randomUUID().toString()).getPath();
            FirebaseStorage.getInstance().getReference(currentPhotoPath).putFile(fullPhotoUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
                {
                    Toast.makeText(getContext(), task.isSuccessful() ? "Success: Uploaded photo" : "Error: Could not upload photo", Toast.LENGTH_LONG).show();
                    if (task.isSuccessful()) FirebaseStorage.getInstance().getReference(currentPhotoPath).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            currentPhotoUrl = uri.toString();
                            popupView.findViewById(R.id.btnSubmit).setVisibility(View.VISIBLE);
                        }
                    });
                    else
                        {popupView.findViewById(R.id.btnSubmit).setVisibility(View.VISIBLE);
                         Toast.makeText(getContext(), "Error: could not post photo, please reselect", Toast.LENGTH_SHORT).show() ;
                        }
                }
            });
        } else  popupView.findViewById(R.id.btnSubmit).setVisibility(View.VISIBLE);
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
        Toast.makeText(this.getContext(), "Error: Unable to get blips", Toast.LENGTH_SHORT).show();
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
            if (!popUpIsShowing)
            {
                LayoutInflater layoutInflater = getLayoutInflater();
                popupView = layoutInflater.inflate(R.layout.make_blipp,null);
                popupView.findViewById(R.id.check_close).setVisibility(View.GONE);
                popupView.findViewById(R.id.check_reg).setVisibility(View.GONE);
                popupView.findViewById(R.id.check_max).setVisibility(View.GONE);

                popupWindow = new PopupWindow(popupView, RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);


                Button btnClose = (Button)popupView.findViewById(R.id.btnClose);
                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupWindow.dismiss();
                        popUpIsShowing = false;
                        currentPhotoUrl = null;
                    }
                });


                final Button btnSubmit = popupView.findViewById(R.id.btnSubmit);
                btnSubmit.setOnClickListener( new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {

                        final String blipText = ((EditText)popupView.findViewById(R.id.make_blipp_text)).getText().toString();

                        if (blipText.isEmpty())
                        {
                            Toast.makeText(getContext(), "Error: Blip must contain text", Toast.LENGTH_SHORT).show();
                            return;
                        }



                        new LocationGetter(getContext(), new LocationGetterCompletion() {
                            @Override
                            public void locationGetterDidGetLocation(double latitude, double longitude)
                            {
                                new BlipSender(new Blipp(latitude, longitude, false, false, false, blipText, currentPhotoUrl ,blip.getId(), blip.getCommunity()),
                                        new BlipSenderCompletion() {
                                            @Override
                                            public void blipSenderDone(boolean isSuccessful)
                                            {
                                                if (currentPhotoUrl != null && !isSuccessful) FirebaseStorage.getInstance().getReference().child(currentPhotoUrl).delete();
                                                Toast.makeText(BlippDetailFragment.this.getContext(), isSuccessful ? "Sucess: Blip has been sent" : "Error: Blip not sent", Toast.LENGTH_SHORT).show();
                                                currentPhotoUrl = null;
                                            }
                                        }, BlippDetailFragment.this.getContext());
                            }

                            @Override
                            public void locationGetterDidFail(boolean shouldShowMessage)
                            {
                                if (currentPhotoUrl != null) FirebaseStorage.getInstance().getReference().child(currentPhotoUrl).delete();
                                currentPhotoUrl = null;
                                if (shouldShowMessage) Toast.makeText(BlippDetailFragment.this.getContext(), "Error: could not get location", Toast.LENGTH_SHORT).show();
                            }
                        });

                        popupWindow.dismiss();
                        popUpIsShowing = false;
                    }
                });

                Button btnPhoto = popupView.findViewById(R.id.make_blipp_btn_image);
                btnPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btnSubmit.setVisibility(View.INVISIBLE);
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                            startActivityForResult(intent, REQUEST_IMAGE_GET);
                        }
                    }
                });

                popupWindow.showAtLocation(BlippDetailFragment.this.getView().getRootView(), Gravity.CENTER, 0, 0);
                popupWindow.setFocusable(true);
                popupWindow.setOutsideTouchable(false);
                popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        popUpIsShowing = false;
                        if (currentPhotoUrl != null) FirebaseStorage.getInstance().getReference().child(currentPhotoUrl).delete();
                        currentPhotoUrl = null;
                    }
                });
                popupWindow.update();
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
                    b.putBoolean("blipShort", blipp.getIsShortDistance());
                    b.putBoolean("blipMed", blipp.getIsMediumDistance());
                    b.putBoolean("blipLong", blipp.getIsLongDistance());
                    BlippDetailFragment frag = new BlippDetailFragment();
                    frag.setArguments(b);
                    fragmentSwap.swap(frag, true);
                }

                @Override
                public void blipGetterGotAdditionalBlips(ArrayList<Blipp> results) {
                 }


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
             if (isSuccessful) {Toast.makeText(BlippDetailFragment.this.getContext(), "Success: Blip Deleted", Toast.LENGTH_SHORT).show();
             getActivity().onBackPressed();
             }
             else Toast.makeText(BlippDetailFragment.this.getContext(), "Error: Blip Not Deleted", Toast.LENGTH_SHORT).show();

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


    //TODO: Implement me
    /*
    private class BottomHit implements AbsListView.OnScrollListener
    {
        @Override
        public void onScrollStateChanged(AbsListView absListView, int i)
        {
            //UnUsed Method
        }

        @Override
        public void onScroll(AbsListView absListView, int firstVisableItem, int visableItemCount, int totalItemCount) {

            if (firstVisableItem + visableItemCount == totalItemCount && totalItemCount!=0  && !didHitBottom )
            {
                getReplys(((Blipp)((BlipListAdapter)replys.getAdapter()).getItem(totalItemCount-1)).getId());
            }
        }
    }
    */


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
            b.putBoolean("blipShort", blipp.getIsShortDistance());
            b.putBoolean("blipMed", blipp.getIsMediumDistance());
            b.putBoolean("blipLong", blipp.getIsLongDistance());
            BlippDetailFragment frag = new BlippDetailFragment();
            frag.setArguments(b);
            fragmentSwap.swap(frag, true);
        }
    }


    //TODO: Make popup window of larger image when tapping on image view.
    private class ImageDetailView implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {

        }
    }

}
