package utils;

public class CandidateData {
    public int count;
    public int minoverlap;
    public int size;

    public CandidateData(int count, int minoverlap, int size){
        this.count = count;
        this.minoverlap = minoverlap;
        this.size = size;
    }
    public void reset(){
        this.count = 0;
        this.size = 0;
    }
}