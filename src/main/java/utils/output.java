package utils;

import java.util.ArrayList;

public class output {
    Integer first;
    Integer second;
    public output(){

    }
    public void addpair(Integer first, Integer second){
        this.first=first;
        this.second=second;

    }

    public String toString(){
        StringBuilder sb = new StringBuilder(this.first.toString() + "," +this.second.toString());
        sb.append('\n');
        return sb.toString();
    }
}

