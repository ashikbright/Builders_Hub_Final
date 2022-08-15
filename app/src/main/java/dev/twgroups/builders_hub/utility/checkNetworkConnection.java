package dev.twgroups.builders_hub.utility;

import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import dev.twgroups.builders_hub.R;

public class checkNetworkConnection {
    Context context;
    AlertDialog alertDialog;

    public checkNetworkConnection(Context ct) {
        context = ct;
        checkConnection();
    }

    public void checkConnection() {
        if (!isNetworkConnected()) {
            showDialog();
        }
    }

    public boolean isNetworkConnected() {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm.getActiveNetworkInfo() != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public void showDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.check_internet_dialog, null);
        Button btnRetry = dialogView.findViewById(R.id.btnRetry);
        ProgressBar progressBar = dialogView.findViewById(R.id.checkProgress);
        builder.setView(dialogView);

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                checkConnectionAgain(alertDialog, progressBar);
            }
        });

        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();

    }

    private void checkConnectionAgain(AlertDialog alertDialog, ProgressBar progressBar) {
        if (isNetworkConnected()) {
            alertDialog.dismiss();
            Toast.makeText(context, "connection is back", Toast.LENGTH_SHORT).show();
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                progressBar.setVisibility(View.GONE);
            }
        }, 2000);

    }

}