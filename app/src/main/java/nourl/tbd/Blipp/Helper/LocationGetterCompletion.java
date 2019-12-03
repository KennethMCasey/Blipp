package nourl.tbd.Blipp.Helper;

public interface LocationGetterCompletion
{
    void locationGetterDidGetLocation(double latitude, double longitude);
    void locationGetterDidFail(boolean shouldShowMessage);
}
