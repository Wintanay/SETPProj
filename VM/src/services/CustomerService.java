package services;

import models.Customer;
import utils.FileManager;

import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CustomerService {
    private final String customersFile = "files/customers.txt";

    public List<Customer> loadAll() {
        List<String> lines = FileManager.safeRead(customersFile);
        
        List<Customer> out = new ArrayList<>();
        for (String l : lines) {
            if (l.trim().isEmpty()) continue;
            Customer c = Customer.fromCSV(l);
            if (c != null) out.add(c);
        }
        return out;
    }

    public void saveAll(List<Customer> list) throws IOException {
        List<String> lines = new ArrayList<>();
        for (Customer c : list) lines.add(c.toCSV());
        FileManager.overwrite(customersFile, lines);
    }

    public Customer register(String name, String email, String phone, String password) throws IOException {
        List<Customer> existing = loadAll();
        Optional<Customer> found = existing.stream().filter(c -> c.getEmail().equalsIgnoreCase(email)).findFirst();
        if (found.isPresent()) {
            throw new IllegalArgumentException("Email already registered: " + email);
        }
        String id = generateId(existing.size());
        Customer c = new Customer(id, name, email, phone, password);
        FileManager.appendLine(customersFile, c.toCSV());
        return c;
    }

    private String generateId(int count) {
        return String.format("C%03d", count + 1);
    }

    public Customer authenticate(String email, String password) {
        List<Customer> existing = loadAll();
        // prefer the most recent entry for an email in case of duplicates
        Customer found = null;
        for (Customer c : existing) {
            if (c.getEmail().equalsIgnoreCase(email)) found = c;
        }
        if (found == null) return null;

        String stored = found.getPassword() == null ? "" : found.getPassword();
        String given = password == null ? "" : password;

        // normalize and trim (preserve internal spaces)
        String ns = stored.trim();
        String ng = given.trim();
        try {
            ns = Normalizer.normalize(ns, Normalizer.Form.NFKC);
            ng = Normalizer.normalize(ng, Normalizer.Form.NFKC);
        } catch (Exception ignored) {}

        // exact comparison after normalization
        boolean ok = ns.equals(ng);

        return ok ? found : null;
    }

    public Customer findById(String id) {
        if (id == null) return null;
        List<Customer> existing = loadAll();
        for (Customer c : existing) {
            if (id.equalsIgnoreCase(c.getId())) return c;
        }
        return null;
    }

    public void updateCustomer(Customer updated) throws IOException {
        List<Customer> list = loadAll();
        List<Customer> out = new ArrayList<>();
        boolean replaced = false;
        for (Customer c : list) {
            if (c.getId().equalsIgnoreCase(updated.getId())) { out.add(updated); replaced = true; }
            else out.add(c);
        }
        if (!replaced) out.add(updated);
        saveAll(out);
    }

    public void incrementCompletedRentals(String customerId) throws IOException {
        List<Customer> list = loadAll();
        boolean changed = false;
        for (Customer c : list) {
            if (c.getId().equalsIgnoreCase(customerId)) {
                int v = c.getCompletedRentals() + 1;
                c.setCompletedRentals(v);
                // assign badge
                if (v >= 25) c.setBadge("SILVER");
                else if (v >= 10) c.setBadge("BRONZE");
                else c.setBadge("");
                changed = true;
                break;
            }
        }
        if (changed) saveAll(list);
    }

    public int allowedConcurrentRentals(Customer c) {
        if (c == null) return 1;
        String b = c.getBadge();
        if ("SILVER".equalsIgnoreCase(b)) return 3;
        if ("BRONZE".equalsIgnoreCase(b)) return 2;
        return 1;
    }
}
