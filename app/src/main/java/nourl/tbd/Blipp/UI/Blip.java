package nourl.tbd.Blipp.UI;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import nourl.tbd.Blipp.BlippConstructs.Blipp;
import nourl.tbd.Blipp.BlippConstructs.Like;
import nourl.tbd.Blipp.BlippConstructs.User;
import nourl.tbd.Blipp.Database.LikeDeleter;
import nourl.tbd.Blipp.Database.LikeDeleterCompletion;
import nourl.tbd.Blipp.Database.LikeGetter;
import nourl.tbd.Blipp.Database.LikeGetterCompletion;
import nourl.tbd.Blipp.Database.LikeSender;
import nourl.tbd.Blipp.Database.LikeSenderCompletion;
import nourl.tbd.Blipp.Database.UserGetter;
import nourl.tbd.Blipp.Database.UserGetterCompletion;
import nourl.tbd.Blipp.R;

public class Blip extends LinearLayout
{
    static public int HEIGHT = 1000;

    Blipp blip;

    Button like;
    Button dislike;
    TextView text;
    ImageView image;
    TextView numLikes;
    TextView name;

    Boolean didLike;
    Boolean didDislike;

    public Blip(Context context) {
        this(context, null);
    }

    public Blip(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Blip(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public Blip(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        inflate(getContext(), R.layout.blip, this);

        like = findViewById(R.id.blip_like);
        dislike = findViewById(R.id.blip_dislike);
        text = findViewById(R.id.blip_text);
        image = findViewById(R.id.blip_photo);
        numLikes = findViewById(R.id.blip_num_likes);
        name = findViewById(R.id.blip_name);
    }


    public Blip withBlip(Blipp blip)
    {
        didLike = null;
        didDislike = null;

        this.blip = blip;

        //Configure text
        text.setText(blip.getText());

        //configure image
        if (blip.getUrl() == null){ image.setVisibility(GONE);}
        else {
            image.setVisibility(VISIBLE);
            image.setImageResource(R.mipmap.test_blimp);}

        new UserGetter(blip.getUserId(), new UserGetterCompletion() {
            @Override
            public void userGetterSuccess(User user)
            {
            name.setText(user == null ? "*UNKNOWN*" : user.getName() == null ? "*UNKNOWN*" : user.getName());
            }

            @Override
            public void userGetterFailure()
            {name.setText("*UNKNOWN*");}
        }, getContext());


        like.setOnClickListener(new LikeHandler());
        dislike.setOnClickListener(new LikeHandler());
        getLikes();
        return this;
    }

    private class LikeHandler implements Button.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
          if (v.equals(like))
          {
              if (didDislike == null || didLike == null)
              {
                  Toast.makeText(Blip.this.getContext(), "Error: Please Try Again", Toast.LENGTH_SHORT).show();
                  return;
              }

             if (!didLike)  new LikeSender(new Like(blip, false), new LikeSenderCompletion() {
            @Override
            public void likeSenderDone(boolean isSuccessful)
            {
              Toast.makeText(Blip.this.getContext(), isSuccessful ? "Action Successful." : "Error: Like not sent.", Toast.LENGTH_SHORT).show();
                if (isSuccessful) getLikes();
            }
        }, Blip.this.getContext());
             if (didLike) new LikeDeleter(new Like(blip, false), new LikeDeleterCompletion() {
                 @Override
                 public void likeDeleterDone(boolean isSuccessful)
                 {
                     Toast.makeText(Blip.this.getContext(), isSuccessful ? "Action Successful." : "Error: Like not deleted.", Toast.LENGTH_SHORT).show();
                     if (isSuccessful) getLikes();
                 }
             }, Blip.this.getContext());
          }

          if (v.equals(dislike)){

              if (!didDislike)  new LikeSender(new Like(blip, true), new LikeSenderCompletion() {
                  @Override
                  public void likeSenderDone(boolean isSuccessful)
                  {

                      Toast.makeText(Blip.this.getContext(), isSuccessful ? "Action Successful." : "Error: DisLike not sent.", Toast.LENGTH_SHORT).show();
                      if (isSuccessful) getLikes();
                  }
              }, Blip.this.getContext());


              if (didDislike) new LikeDeleter(new Like(blip, true), new LikeDeleterCompletion() {
                  @Override
                  public void likeDeleterDone(boolean isSuccessful)
                  {

                      Toast.makeText(Blip.this.getContext(), isSuccessful ? "Action Successful." : "Error: DisLike not deleted.", Toast.LENGTH_SHORT).show();
                      if (isSuccessful) getLikes();
                  }
              }, Blip.this.getContext());
          }


        }
    }

    private void getLikes()
    {
        new LikeGetter(blip, new LikeGetterCompletion() {
            @Override
            public void likeGetterSucessful(ArrayList<Like> likes)
            {
            int numUp=0;
            int numDown=0;

            for (int i = 0; i < likes.size(); i++) if (likes.get(i).isDislike()) numDown++; else numUp++;
             numLikes.setText(String.valueOf( numUp - numDown));


             didDislike = likes.contains(new Like(blip, true));
             didLike = likes.contains(new Like(blip, false));

             like.setText(didLike ? "Un-Like" : "Like");
             dislike.setText(didDislike? "Un-DisLike" : "DisLike");

             like.setEnabled(true);
             dislike.setEnabled(true);
            }

            @Override
            public void likeGetterUnsucessful() {
                Toast.makeText(getContext(), "Error getting likes", Toast.LENGTH_SHORT).show();
            }
        }, this.getContext());

    }
    public int getLayoutHeight() {return blip.getUrl() == null ? 500 : 1000;}

}







