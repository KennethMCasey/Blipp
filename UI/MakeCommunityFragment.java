package nourl.tbd.Blipp.UI;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.net.URI;
import java.text.DecimalFormat;
import java.util.UUID;

import nourl.tbd.Blipp.BlippConstructs.Community;
import nourl.tbd.Blipp.BlippConstructs.Member;
import nourl.tbd.Blipp.Database.CommunitySender;
import nourl.tbd.Blipp.Database.CommunitySenderCompletion;
import nourl.tbd.Blipp.Database.MemberSender;
import nourl.tbd.Blipp.Database.MemberSenderCompletion;
import nourl.tbd.Blipp.Helper.LocationGetter;
import nourl.tbd.Blipp.Helper.LocationGetterCompletion;
import nourl.tbd.Blipp.R;

import static android.app.Activity.RESULT_OK;

public class MakeCommunityFragment extends Fragment {

    static final int REQUEST_IMAGE_GET = 1;

    EditText communityName;
    Button addPhoto;
    ImageView imageView;
    TextView numMiles;
    SeekBar milesSelector;
    Button btnSubmit;
    String currentPhotoUrl;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.make_community, null);

        ((FragmentSwap)getActivity()).postFragId(9);

        communityName = v.findViewById(R.id.make_community_name);

        addPhoto = v.findViewById(R.id.make_community_add_photo);
        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_IMAGE_GET);
                }
            }
        });

        imageView = v.findViewById(R.id.make_community_photo);

        milesSelector = v.findViewById(R.id.make_community_miles_selector);
        milesSelector.setMax(99);
        milesSelector.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {

                double numberMiles = ((milesSelector.getProgress()+1) * 0.1);
                numberMiles = Double.parseDouble(new DecimalFormat("###.#").format(numberMiles));

                numMiles.setText(numberMiles + " square mile discoverability");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        numMiles = v.findViewById(R.id.make_community_miles_display);
        double numberMiles = ((milesSelector.getProgress()+1) * 0.1);
        numberMiles = Double.parseDouble(new DecimalFormat("###.#").format(numberMiles));
        numMiles.setText(numberMiles + " square mile discoverability");

        btnSubmit = v.findViewById(R.id.make_community_btn_submit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (communityName.getText() == null || communityName.getText().toString().trim().isEmpty()) Toast.makeText(getContext(), "Error: Please enter valid community name.", Toast.LENGTH_SHORT).show();
                else
                    {

                        final double numberMiles = Double.parseDouble(new DecimalFormat("###.#").format(((milesSelector.getProgress()+1) * 0.1)));
                        final String name = communityName.getText().toString();
                        new LocationGetter(getContext(), new LocationGetterCompletion() {
                            @Override
                            public void locationGetterDidGetLocation(double latitude, double longitude)
                            {

                                if (currentPhotoUrl == null) currentPhotoUrl = FirebaseStorage.getInstance().getReference("blipp.png").getDownloadUrl().toString();

                                final Community sendMe = new Community(latitude, longitude, currentPhotoUrl, numberMiles, name,true);

                                new CommunitySender(sendMe, new CommunitySenderCompletion() {
                                    @Override
                                    public void communitySenderDone(boolean isSuccessful, Community communitySent)
                                    {
                                        Member owner = new Member(communitySent.getId(), "*OWNER*");
                                       if (isSuccessful) new MemberSender(owner, new MemberSenderCompletion() {
                                            @Override
                                            public void memberSenderDone(boolean isSuccessful)
                                            {
                                                Toast.makeText(getContext(), isSuccessful ? "Success: Community is live." : "FATAL ERROR: Community constructed but you are not a member_row.", Toast.LENGTH_SHORT).show();
                                                if (isSuccessful) getActivity().onBackPressed();
                                            }
                                        }, getContext());
                                        else Toast.makeText(getContext(), "Error: Community could not be constructed", Toast.LENGTH_SHORT).show();

                                    }
                                }, getContext());
                            }

                            @Override
                            public void locationGetterDidFail(boolean shouldShowMessage) {
                                if (shouldShowMessage) Toast.makeText(getContext(), "Error: Could not get current location", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
            }
        });
        return v;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK) {
            Uri fullPhotoUri = data.getData();
            imageView.setImageURI(fullPhotoUri);
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
                        }
                    });
                }
            });
        }
    }
}