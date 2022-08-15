package dev.twgroups.builders_hub.Models;

import com.google.firebase.database.Exclude;

public class OutPayment {
    String amtPaid;
    String descriptionOut;
    String DateOUT;
    String c_type;
    private String key;

    public OutPayment() {
    }

    public OutPayment(String amtPaid, String descriptionOut, String dateOUT, String c_type) {
        this.amtPaid = amtPaid;
        this.descriptionOut = descriptionOut;
        DateOUT = dateOUT;
        this.c_type = c_type;
    }

    public String getAmtPaid() {
        return amtPaid;
    }

    public String getDescriptionOut() {
        return descriptionOut;
    }

    public String getDateOUT() {
        return DateOUT;
    }

    public String getC_type() {
        return c_type;
    }

    public void setAmtPaid(String amtPaid) {
        this.amtPaid = amtPaid;
    }

    public void setDescriptionOut(String descriptionOut) {
        this.descriptionOut = descriptionOut;
    }

    public void setDateOUT(String dateOUT) {
        DateOUT = dateOUT;
    }

    public void setC_type(String c_type) {
        this.c_type = c_type;
    }

    @Exclude
    public String getKey() {
        return key;
    }

    @Exclude
    public void setKey(String key) {
        this.key = key;
    }
}
