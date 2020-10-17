package Model;

public class Report {
    private int numberOfAppts;
    private String month;

    public Report() {

    }

    public Report(int numberOfAppts, String month) {
        this.numberOfAppts = numberOfAppts;
        this.month = month;
    }

    public int getNumberOfAppts() {
        return numberOfAppts;
    }

    public void setNumberOfAppts(int numberOfAppts) {
        this.numberOfAppts = numberOfAppts;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }
}
