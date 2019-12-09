package nourl.tbd.Blipp.Database;

import java.util.ArrayList;

import nourl.tbd.Blipp.BlippConstructs.Blipp;

public interface BlipGetterCompletion
{
    void blipGetterGotInitialBlips(ArrayList<Blipp> results);
    void blipGetterGotAdditionalBlips(ArrayList<Blipp> results);
    void blipGetterDidFail();
}
