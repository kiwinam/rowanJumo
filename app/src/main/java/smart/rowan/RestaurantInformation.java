package smart.rowan;

/**
 * Created by charlie on 2017. 2. 7..
 */

public class RestaurantInformation {
    private String restId, restName, restAddress, restPhone;

    public RestaurantInformation(String restId, String restName, String restAddress, String restPhone) {
        this.restId = restId;
        this.restName = restName;
        this.restAddress = restAddress;
        this.restPhone = restPhone;
    }

    public void setRestId(String restId) {
        this.restId = restId;
    }

    public void setRestName(String restName) {
        this.restName = restName;
    }

    public void setRestAddress(String restAddress) {
        this.restAddress = restAddress;
    }

    public void setRestPhone(String restPhone) {
        this.restPhone = restPhone;
    }

    public String getRestId() {
        return restId;
    }

    public String getRestName() {
        return restName;
    }

    public String getRestAddress() {
        return restAddress;
    }

    public String getRestPhone() {
        return restPhone;
    }
}
