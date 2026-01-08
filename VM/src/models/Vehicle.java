package models;

public class Vehicle {
    private String id;
    private String type; // Car/Bike/Truck
    private String make;
    private String model;
    private int year;
    private double ratePerDay;
    private boolean available;
    private int quantity; // number of units available

    public Vehicle(String id, String type, String make, String model, int year, double ratePerDay, boolean available, int quantity) {
        this.id = id;
        this.type = type;
        this.make = make;
        this.model = model;
        this.year = year;
        this.ratePerDay = ratePerDay;
        this.available = available;
        this.quantity = quantity;
    }

    public String getId() { return id; }
    public String getType() { return type; }
    public String getMake() { return make; }
    public String getModel() { return model; }
    public int getYear() { return year; }
    public double getRatePerDay() { return ratePerDay; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean a) { this.available = a; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int q) { this.quantity = q; this.available = q > 0; }

    public String toCSV() {
        return String.format("%s,%s,%s,%s,%d,%.2f,%b,%d", id, type, make, model, year, ratePerDay, available, quantity);
    }

    public static Vehicle fromCSV(String line) {
        String[] p = line.split(",");
        // backwards compatible: old format had 7 fields, new includes quantity as 8th
        if (p.length < 7) return null;
        try {
            String id = p[0].trim();
            String type = p[1].trim();
            String make = p[2].trim();
            String model = p[3].trim();
            int year = Integer.parseInt(p[4].trim());
            double rate = Double.parseDouble(p[5].trim());
            boolean av = Boolean.parseBoolean(p[6].trim());
            int qty = 1;
            if (p.length >= 8) {
                try { qty = Integer.parseInt(p[7].trim()); } catch (Exception ignored) { qty = 1; }
            }
            return new Vehicle(id, type, make, model, year, rate, av, qty);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return String.format("%s | %s %s %s (%d) - $%.2f/day - %s - Qty:%d", id, type, make, model, year, ratePerDay, (available?"Available":"Unavailable"), quantity);
    }
}
