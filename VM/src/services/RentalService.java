package services;

import models.Rental;
import utils.FileManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RentalService {
    private final String rentalsFile = "files/rentals.txt";

    public List<Rental> loadAll() {
        List<String> lines = FileManager.safeRead(rentalsFile);
        List<Rental> out = new ArrayList<>();
        for (String l : lines) {
            if (l.trim().isEmpty()) continue;
            Rental r = Rental.fromCSV(l);
            if (r != null) out.add(r);
        }
        return out;
    }

    public void addRental(Rental r) throws IOException {
        // Before writing rental, decrement vehicle quantity
        services.VehicleService vs = new VehicleService();
        boolean ok = vs.decrementQuantity(r.getVehicleId());
        if (!ok) throw new IllegalStateException("No vehicles available for id: " + r.getVehicleId());
        FileManager.appendLine(rentalsFile, r.toCSV());
    }

    public void returnRental(String rentalId) throws IOException {
        List<Rental> list = loadAll();
        boolean changed = false;
        for (Rental r : list) {
            if (r.getId().equals(rentalId) && "RENTED".equalsIgnoreCase(r.getStatus())) {
                // mark returned
                Rental updated = new Rental(r.getId(), r.getCustomerId(), r.getVehicleId(), r.getStartDate(), r.getEndDate(), r.getTotalPrice(), "RETURNED");
                // replace
                int idx = list.indexOf(r);
                list.set(idx, updated);
                // increment vehicle quantity
                new VehicleService().incrementQuantity(r.getVehicleId());
                // increment customer's completed count and maybe badge
                new services.CustomerService().incrementCompletedRentals(r.getCustomerId());
                changed = true;
                break;
            }
        }
        if (changed) {
            List<String> lines = new ArrayList<>();
            for (Rental rr : list) lines.add(rr.toCSV());
            FileManager.overwrite(rentalsFile, lines);
        }
    }

    public double totalIncome() {
        double sum = 0.0;
        for (Rental r : loadAll()) sum += r.getTotalPrice();
        return sum;
    }
}
