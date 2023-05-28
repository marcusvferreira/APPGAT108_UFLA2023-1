package android.marcusvferreira.appgat108.model;

public class Local {
    private double lat, longi; // latitude e longitude

    // talvez uma var chamada distancia


    public Local(double lat, double longi) {
        this.lat = lat;
        this.longi = longi;
    }

    public double getLat() {
        return lat;
    }

    public double getLongi() {
        return longi;
    }

}
