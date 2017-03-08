package smart.rowan.chatting;


public class ChatData {
    private String userName;
    private String userName2;
    private String message;
    private String sendTime;
    private String times;
    private String myName;

    public ChatData(String userName, String userName2, String message, String sendTime, String times, String myName) {
        this.userName = userName;
        this.userName2 = userName2;
        this.message = message;
        this.sendTime = sendTime;
        this.times = times;
        this.myName = myName;
    }

    public ChatData(){
    }

    public String getUserName() {
        return userName;
    }

    public String getMessage() {
        return message;
    }

    public String getUserName2() {
        return userName2;
    }

    public String getSendTime() {
        return sendTime;
    }

    public String getTimes() {
        return this.times;
    }

    public String getMyName() {
        return this.myName;
    }
}
