package models;

public class Rental {
    private String id;
    private String customerId;
    private String vehicleId;
    private String startDate; // YYYY-MM-DD
    private String endDate;   // YYYY-MM-DD
    private double totalPrice;
    private String status; // RENTED, RETURNED

    public Rental(String id, String customerId, String vehicleId, String startDate, String endDate, double totalPrice, String status) {
        this.id = id;
        this.customerId = customerId;
        this.vehicleId = vehicleId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    public String getId() { return id; }
    public String getCustomerId() { return customerId; }
    public String getVehicleId() { return vehicleId; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public double getTotalPrice() { return totalPrice; }
    public String getStatus() { return status; }

    public String toCSV() {
        return String.join(",", id, customerId, vehicleId, startDate, endDate, String.format("%.2f", totalPrice), status);
    }

    public static Rental fromCSV(String line) {
        String[] p = line.split(",");
        if (p.length < 7) return null;
        try {
            String id = p[0].trim();
            String customerId = p[1].trim();
            String vehicleId = p[2].trim();
            String start = p[3].trim();
            String end = p[4].trim();
            double price = Double.parseDouble(p[5].trim());
            String status = p[6].trim();
            return new Rental(id, customerId, vehicleId, start, end, price, status);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return String.format("%s | %s -> %s | %s to %s | $%.2f | %s", id, customerId, vehicleId, startDate, endDate, totalPrice, status);
    }
}
