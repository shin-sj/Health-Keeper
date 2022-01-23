package ddwu.mobile.final_project.ma02_20190973;

import java.io.Serializable;

public class NaverLocalDto implements Serializable {
    //로컬에서 '가게명, 전화번호, 분류, 도로명주소'를 담은 dto
    private  int _id;
    private String title;
    private String telephone;
    private String category;
    private String roadAddress;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getRoadAddress() {
        return roadAddress;
    }

    public void setRoadAddress(String roadAddress) {
        this.roadAddress = roadAddress;
    }

    public String toString() {return "제목)" + getTitle() + "\n전화번호) " + getTelephone() +"\n분류) " + getCategory() +"\n도로명주소)" + getRoadAddress();}
}