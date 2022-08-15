package dev.twgroups.builders_hub.Interface;

import java.util.ArrayList;

import dev.twgroups.builders_hub.Models.Workers;

public interface onAttendanceWorkerListSelected {
    void onItemSelected(ArrayList<Workers> presentWorkersList, ArrayList<Workers> absentWorkersList, ArrayList<Workers> workersList);
}
