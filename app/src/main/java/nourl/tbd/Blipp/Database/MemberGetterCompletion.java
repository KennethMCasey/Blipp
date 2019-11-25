package nourl.tbd.Blipp.Database;

import java.util.ArrayList;

import nourl.tbd.Blipp.BlippConstructs.Member;

public interface MemberGetterCompletion
{
    void memberGetterGotInitalMembers(ArrayList<Member> members);
    void memberGetterGotAditionalMembers(ArrayList<Member> members);
    void memberGetterDidFail();
}
