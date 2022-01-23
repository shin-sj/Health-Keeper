package ddwu.mobile.final_project.ma02_20190973;

import java.io.Serializable;

public class HospitalDto implements Serializable {

    private long _id;
    private String name;
    private String phone;
    private String address;

    public HospitalDto(long _id, String name, String phone, String address) {
        this._id = _id;
        this.name = name;
        this.phone = phone;
        this.address = address;
    }

    public HospitalDto(String name, String phone, String address) {
        this.name = name;
        this.phone = phone;
        this.address = address;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
}