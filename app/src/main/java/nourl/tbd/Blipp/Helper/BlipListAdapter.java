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
import nourl.tbd.Blipp.R;

public class BlipListAdapter extends BaseAdapter
{
    private Context context;
    private ArrayList<Blipp> blipps;

    public BlipListAdapter(Context context, ArrayList<Blipp> blipps)
    {
        this.context = context;
        this.blipps = blipps;
    }

    @Override
    public int getCount()
    {
        return blipps.size();
    }

    @Override
    public Object getItem(int i)
    {
        return blipps.get(i);
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
            view = LayoutInflater.from(context).inflate(R.layout.blip, viewGroup, false);
        }

        Blipp curr = blipps.get(i);

        TextView text = view.findViewById(R.id.blipp_text);

        ImageView img = view.findViewById(R.id.blipp_photo);

        if (curr.getUrl() == null)
        {
            img.setVisibility(View.GONE);
            text.setText(curr.getText());
        }
        else
            {
                img.setVisibility(View.GONE);
                text.setText(curr.getText());

                //img.setVisibility(View.VISIBLE);
                //img.setImageBitmap(Bitmap.createBitmap(curr.));//TODO: This has not been tested, this line as well as the blip class may need to be adjusted in order to show an image
                //text.setText(curr.getText());
            }

        return view;
    }

    public void setBlipps(ArrayList<Blipp> blipps)
    {
        this.blipps = blipps;
        notifyDataSetChanged();
    }

    public  void addBlips(ArrayList<Blipp> blipps)
    {
        this.blipps.addAll(blipps);
        notifyDataSetChanged();
    }
}
