package smart.rowan;

/**
 * Created by charlie on 2017. 3. 10..
 */

public class Waiter {
    private String waiterMac;
    private String waiterName;
    private String waiterStatus;
    public Waiter(String waiterMac,String waiterName, String waiterStatus){
        this.waiterMac = waiterMac;
        this.waiterName = waiterName;
        this.waiterStatus = waiterStatus;
    }

    public void setWaiterMac(String waiterMac) {
        this.waiterMac = waiterMac;
    }

    public void setWaiterName(String waiterName) {
        this.waiterName = waiterName;
    }

    public void setWaiterStatus(String waiterStatus) {
        this.waiterStatus = waiterStatus;
    }

    public String getWaiterMac() {
        return waiterMac;
    }

    public String getWaiterName() {
        return waiterName;
    }

    public String getWaiterStatus() {
        return waiterStatus;
    }
}
