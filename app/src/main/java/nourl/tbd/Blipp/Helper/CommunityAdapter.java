package nourl.tbd.Blipp.Helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import nourl.tbd.Blipp.BlippConstructs.Blipp;
import nourl.tbd.Blipp.BlippConstructs.Community;
import nourl.tbd.Blipp.R;

public class CommunityAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Community> communities;

    public CommunityAdapter(Context context, ArrayList<Community> communities)
    {
        this.context = context;
        this.communities = communities;
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
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        if (view == null)
        {
            view = LayoutInflater.from(context).inflate(R.layout.community, viewGroup, false);
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
