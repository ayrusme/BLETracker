package teamfour.bluetoothtracker;

/**
 * Created by Surya Raman on 25-03-2017.
 */

public class userData
{
    public String IMEI;
    public String BeaconID;
    public String registerNum;

    public userData()
    {
        //Does nothing, default constructor
    }

    public userData(String imei, String dbResult,String regNum)
    {
        this.IMEI = imei;
        this.BeaconID = dbResult;
        this.registerNum = regNum;
    }
}
