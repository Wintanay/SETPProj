package services;

import models.Vehicle;
import utils.FileManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VehicleService {
    private final String vehiclesFile = "files/vehicles.txt";

    public List<Vehicle> loadAll() {
        List<String> lines = FileManager.safeRead(vehiclesFile);
        List<Vehicle> out = new ArrayList<>();
        for (String l : lines) {
            if (l.trim().isEmpty()) continue;
            Vehicle v = Vehicle.fromCSV(l);
            if (v != null) out.add(v);
        }
        return out;
    }

    public void saveAll(List<Vehicle> list) throws IOException {
        List<String> lines = new ArrayList<>();
        for (Vehicle v : list) lines.add(v.toCSV());
        FileManager.overwrite(vehiclesFile, lines);
    }

    public void addVehicle(Vehicle v) throws IOException {
        FileManager.appendLine(vehiclesFile, v.toCSV());
    }

    public Vehicle findById(String id) {
        for (Vehicle v : loadAll()) if (v.getId().equals(id)) return v;
        return null;
    }

    public boolean decrementQuantity(String vehicleId) throws IOException {
        List<Vehicle> list = loadAll();
        boolean changed = false;
        for (Vehicle v : list) {
            if (v.getId().equals(vehicleId)) {
                if (v.getQuantity() <= 0) return false;
                v.setQuantity(v.getQuantity() - 1);
                changed = true;
                break;
            }
        }
        if (changed) saveAll(list);
        return changed;
    }

    public void incrementQuantity(String vehicleId) throws IOException {
        List<Vehicle> list = loadAll();
        boolean changed = false;
        for (Vehicle v : list) {
            if (v.getId().equals(vehicleId)) {
                v.setQuantity(v.getQuantity() + 1);
                changed = true;
                break;
            }
        }
        if (changed) saveAll(list);
    }
}
