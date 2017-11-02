package eco_driving;

/**
 * Created by Farhan on 8/11/2016.
 */
public class SummaryCons {
    public String getAvg_penalty() {
        return avg_penalty;
    }

    public void setAvg_penalty(String avg_penalty) {
        this.avg_penalty = avg_penalty;
    }

    public String getPenalties_number() {
        return penalties_number;
    }

    public void setPenalties_number(String penalties_number) {
        this.penalties_number = penalties_number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getMileage() {
        return mileage;
    }

    public void setMileage(String mileage) {
        this.mileage = mileage;
    }

    private  String avg_penalty;
    private  String penalties_number;
    private  String name;
    private  String rating;
    private  String mileage;
}
