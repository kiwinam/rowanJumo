package smart.rowan;

/**
 * Created by charlie on 2017. 3. 9..
 */

public class RealTimeItem {
    private int image;
    private String name;
    private String status;

    public RealTimeItem(int image, String name, String status){
        this.image = image;
        this.name = name;
        this.status = status;
    }

    public int getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
