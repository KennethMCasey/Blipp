package nourl.tbd.Blipp.Database;

import java.util.ArrayList;

import nourl.tbd.Blipp.BlippConstructs.Community;

public interface CommunityGetterCompletion
{
    void communityGetterGotInitalCommunities(ArrayList<Community> communities);
    void communityGetterGotAditionalCommunities(ArrayList<Community> communities);
    void communityGetterDidFail();
}
