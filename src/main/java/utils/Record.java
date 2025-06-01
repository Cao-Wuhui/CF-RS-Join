package utils;

import java.util.ArrayList;

public class Record {
    public ArrayList<Integer> tokens;
    public int recordid;
    public Record(int id){
        this.tokens = new ArrayList<>();
        this.recordid = id;
    }
}

