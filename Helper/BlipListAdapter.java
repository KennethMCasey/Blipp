package nourl.tbd.Blipp.Helper;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.ArrayList;

import nourl.tbd.Blipp.BlippConstructs.Blipp;
import nourl.tbd.Blipp.UI.Blip;

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
        return blipps == null ? 0 : blipps.size();
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
        Blip b;
        if (view != null)
        {
            b =  ((Blip)view).withBlip(blipps.get(i));
        }
        else b = new Blip(viewGroup.getContext()).withBlip(blipps.get(i));
        b.setLayoutParams(new ViewGroup.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, b.getLayoutHeight()));
        return b;
       // return view;
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
