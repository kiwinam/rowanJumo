package smart.rowan.etc;

public class MyData {
    private final String restName;
    private final String restAddress;
    private final String restPhone;
    private final String firstName;
    private final String lastName;
    private final String phone;
    private final String birthday;
    private final String email;
    private final String address;
    private final String startDate;
    private final String endDate;
    private final String myFullName;

    public MyData(String restName, String restAddress, String restPhone, String firstName, String lastName, String phone,
                  String birthday, String email, String address, String startDate, String endDate, String myFullName) {
        this.restName = restName;
        this.restAddress = restAddress;
        this.restPhone = restPhone;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.birthday = birthday;
        this.email = email;
        this.address = address;
        this.startDate = startDate;
        this.endDate = endDate;
        this.myFullName = myFullName;
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

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhone() {
        return phone;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getMyFullName() {
        return myFullName;
    }
}
