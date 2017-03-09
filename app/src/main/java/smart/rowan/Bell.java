package smart.rowan;

/**
 * Created by charlie on 2017. 3. 10..
 */

public class Bell {
    private String bellMac;
    private String bellNum;
    private String bellStatus;
    public Bell(String bellMac, String bellNum, String bellStatus){
        this.bellMac = bellMac;
        this.bellNum = bellNum;
        this.bellStatus = bellStatus;
    }

    public String getBellMac() {
        return bellMac;
    }

    public String getBellNum() {
        return bellNum;
    }

    public String getBellStatus() {
        return bellStatus;
    }

    public void setBellMac(String bellMac) {
        this.bellMac = bellMac;
    }

    public void setBellNum(String bellNum) {
        this.bellNum = bellNum;
    }

    public void setBellStatus(String bellStatus) {
        this.bellStatus = bellStatus;
    }
}
