package dev.twgroups.builders_hub.WorkerModule;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import dev.twgroups.builders_hub.R;

public class WorkerDetailsHome extends AppCompatActivity {

    ImageButton imgBtnLabour, imgBtnMistri, imgBtnTiles, imgBtnPaint, imgBtnFurniture, imgBtnPlumber, imgBtnWelder, imgBtnElectrician;
    private ImageView backButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_details_home);


        imgBtnLabour = findViewById(R.id.imgbtn_labour);
        imgBtnMistri = findViewById(R.id.imgbtn_mistri);
        imgBtnTiles = findViewById(R.id.imgbtn_tiles);
        imgBtnPaint = findViewById(R.id.imgbtn_painter);
        imgBtnFurniture = findViewById(R.id.imgbtn_furniture);
        imgBtnPlumber = findViewById(R.id.imgbtn_plumber);
        imgBtnWelder = findViewById(R.id.imgbtn_plumber2);
        imgBtnElectrician = findViewById(R.id.imgbtn_plumber3);
        backButton = findViewById(R.id.btn_back_workers_registration);


        imgBtnLabour.setOnClickListener(v -> confirmOrder(0));

        imgBtnMistri.setOnClickListener(v -> confirmOrder(1));

        imgBtnTiles.setOnClickListener(v -> confirmOrder(2));

        imgBtnPaint.setOnClickListener(v -> confirmOrder(3));

        imgBtnFurniture.setOnClickListener(v -> confirmOrder(4));

        imgBtnPlumber.setOnClickListener(v -> confirmOrder(5));

        imgBtnWelder.setOnClickListener(v -> confirmOrder(6));

        imgBtnElectrician.setOnClickListener(v -> confirmOrder(7));

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }


    private void confirmOrder(int selectedItem) {
        Intent intent = new Intent(WorkerDetailsHome.this, WorkersDetails.class);
        intent.putExtra("itemSelected", selectedItem);
        startActivity(intent);
    }
}