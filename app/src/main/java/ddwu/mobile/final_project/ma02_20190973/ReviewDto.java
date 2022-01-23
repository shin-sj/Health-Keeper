package ddwu.mobile.final_project.ma02_20190973;

import java.io.Serializable;

public class ReviewDto implements Serializable {
    private long _id;
    private String title;
    private String hospital;
    private String date;
    private String symptom;
    private String contents;

    public ReviewDto(long _id, String title, String hospital, String date, String symptom, String contents, int img) {
        this._id = _id;
        this.title = title;
        this.hospital = hospital;
        this.date = date;
        this.symptom = symptom;
        this.contents = contents;
    }

    public ReviewDto(String title, String hospital, String date, String symptom, String contents, int img) {
        this.title = title;
        this.hospital = hospital;
        this.date = date;
        this.symptom = symptom;
        this.contents = contents;
    }


    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSymptom() {
        return symptom;
    }

    public void setSymptom(String symptom) {
        this.symptom = symptom;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

}