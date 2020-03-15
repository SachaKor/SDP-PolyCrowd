package ch.epfl.polycrowd.logic;

import androidx.annotation.NonNull;

import com.google.type.Date;
import com.google.type.TimeOfDay;

public class TimeSlot {

    public Date start, end;

    public TimeSlot(@NonNull TimeOfDay start,@NonNull TimeOfDay end){
        //if (start.getHours()>end.getHours()) return null;

    }

}
