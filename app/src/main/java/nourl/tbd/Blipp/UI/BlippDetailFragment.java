package nourl.tbd.Blipp.UI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import nourl.tbd.Blipp.BlippConstructs.Blipp;
import nourl.tbd.Blipp.R;

public class BlippDetailFragment extends Fragment {

    Blipp blip;

    ImageView photo;
    TextView text;
    Button toParent;
    Button like;
    Button dislike;
    TextView numLikes;
    Button delete;
    SwipeRefreshLayout refreshLayout;
    ListView replys;
    Button reply;
    Button refresh;

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

        //configure like button
        like = v.findViewById(R.id.blip_detail_like);

        //configure dislike buttons
        dislike = v.findViewById(R.id.blip_detail_dislike);

        //configure numLikes
        numLikes = v.findViewById(R.id.blip_detail_num_likes);

        //configure delete button
        delete = v.findViewById(R.id.blip_detail_delete);

        //refreshlayout
        refreshLayout = v.findViewById(R.id.swiperefresh_blip_detail);

        //replys
        replys = v.findViewById(R.id.list_replys_blipp_detail);

        //reply button
        reply = v.findViewById(R.id.blip_detail_reply);

        //refresh button
        refresh = v.findViewById(R.id.blip_detail_btn_refresh);

        return  v;
    }
}
