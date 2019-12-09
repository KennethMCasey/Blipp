package nourl.tbd.Blipp.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

import nourl.tbd.Blipp.R;

public class OptionsFragment extends Fragment {
    ListView settings;
    Button signOut;
    Button test;
    FragmentSwap fragmentSwap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        fragmentSwap = (FragmentSwap)getActivity();
        fragmentSwap.postFragId(4);

        View v = inflater.inflate(R.layout.settings_fragment, container, false);

        //Configure Settings List
        settings = v.findViewById(R.id.list_settings);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(v.getContext(), R.array.blipp_options, R.layout.options_row);
        adapter2.setDropDownViewResource(R.layout.options_row);
        settings.setAdapter(adapter2);
        settings.setOnItemClickListener(new SettingsItemSelected());

        //configures sign out button
        signOut = v.findViewById(R.id.btn_sign_out);
        signOut.setOnClickListener(new SignOut());

        return v;
    }


    private class SignOut implements Button.OnClickListener
    {
        @Override
        public void onClick(View view)
        {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(OptionsFragment.this.getContext(), LoginActivity.class));
        }
    }


    private class SettingsItemSelected implements AdapterView.OnItemClickListener
    {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
        {
            if (i == 0) fragmentSwap.swap(new ChangeInfoFragment(), true);
            if (i == 1) fragmentSwap.swap(new MakeCommunityFragment(), true);
            if (i == 2) fragmentSwap.swap(new JoinCommunityFragment(), true);
            if (i == 3) fragmentSwap.swap(new ManageCommunitiesSelectFragment(), true);
        }
    }

}
