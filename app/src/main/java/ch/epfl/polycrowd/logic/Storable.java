package ch.epfl.polycrowd.logic;

import java.util.HashMap;
import java.util.Map;

public abstract class  Storable {

    /**
     * returns the relevant data to store in the database
     * @return
     */
    public abstract Map<String, Object> getRawData();

}
