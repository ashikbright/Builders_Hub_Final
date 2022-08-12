package dev.afnan.builders_hub.Common;

import android.net.Uri;
import android.util.Log;

import dev.afnan.builders_hub.Models.User;

public class Common {

    public static User CurrentUser;

    public static Uri userProfileImage;

    public static int orderCount;

    public static String CURRENT_ORDER_STATUS;

    public static String projectID;

    public static boolean userDeleted = false;

    public static String checkStatus(String status_code) {

        String st = "N/A";

        if (status_code.equals("0")) {
            st = "Pending";
        } else if (status_code.equals("1")) {
            st = "Accepted";
        } else if (status_code.equals("2")) {
            st = "Cancelled";
        } else if (status_code.equals("3")) {
            st = "Completed";
        } else if (status_code.equals("4")) {
            st = "Order placed";
        }

        Log.d("statusCheck", "conditions not met , status is: " + st);
        CURRENT_ORDER_STATUS = st;

        return CURRENT_ORDER_STATUS;
    }

    public static String getSelectedWorkerType(int selectedItem) {
        String selectedItemName = "";

        switch (selectedItem) {
            case 0:
                selectedItemName = "Labour";
                break;
            case 1:
                selectedItemName = "Mistri";
                break;
            case 2:
                selectedItemName = "Tiles/Marble Mistri";
                break;
            case 3:
                selectedItemName = "Painter";
                break;
            case 4:
                selectedItemName = "Furniture Works";
                break;
            case 5:
                selectedItemName = "Plumber";
                break;
            case 6:
                selectedItemName = "Welder";
                break;
            case 7:
                selectedItemName = "Electrician";
                break;
        }


        return selectedItemName;
    }

    public static String getCurrentOrderStatus() {
        return CURRENT_ORDER_STATUS;
    }

    public static void setCurrentOrderStatus(String currentOrderStatus) {
        CURRENT_ORDER_STATUS = currentOrderStatus;
    }
}


