package nourl.tbd.Blipp.UI;

import androidx.fragment.app.Fragment;

public interface FragmentSwap
{
    void swap(Fragment fragment, boolean addToBackstack);
    void postFragId(int id);
}

