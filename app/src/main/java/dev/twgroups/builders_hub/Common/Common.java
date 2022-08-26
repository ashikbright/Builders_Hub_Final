package dev.twgroups.builders_hub.Common;

import android.net.Uri;

import dev.twgroups.builders_hub.Models.User;

public class Common {

    public static User CurrentUser;

    public static Uri userProfileImage;

    public static String adminToken = "dICWAWxtRqStbFxzJLyGJp:APA91bGzJfve01EE19uehsBAN8-118uQtHqZ9ulS2T3XJmyjVdjrzKAafRL17vnY_uia7afNBUdm3IyvaeXG4IfEISo4wqw5yiDNIlxmRS-uR8raknbFw_9CuGleUXGEo4ZyVC0rYc3X";

    public static String CURRENT_ORDER_STATUS;

    public static String projectID;


    public static boolean userDeleted = false;

    public static String checkStatus(String status_code) {

        String st = "Pending";

        if (status_code.equals("0")) {
            st = "Pending";
        } else if (status_code.equals("1")) {
            st = "Accepted";
        } else if (status_code.equals("2")) {
            st = "Cancelled";
        }
//        else if (status_code.equals("3")) {
//            st = "Completed";
//        } else if (status_code.equals("4")) {
//            st = "Order placed";
//        }

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


