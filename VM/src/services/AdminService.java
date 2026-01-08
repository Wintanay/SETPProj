package services;

import models.Admin;
import utils.FileManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AdminService {
    private final String adminsFile = "files/admins.txt";

    public AdminService() {
        try {
            ensureDefaultAdmin();
        } catch (IOException ignored) {
        }
    }

    public List<Admin> loadAll() {
        List<String> lines = FileManager.safeRead(adminsFile);
        List<Admin> out = new ArrayList<>();
        for (String l : lines) {
            if (l.trim().isEmpty()) continue;
            Admin a = Admin.fromCSV(l);
            if (a != null) out.add(a);
        }
        return out;
    }

    public Admin authenticate(String username, String password) {
        List<Admin> all = loadAll();
        Admin found = null;
        for (Admin a : all) {
            if (a.getUsername().equalsIgnoreCase(username)) found = a;
        }
        if (found == null) return null;

        String stored = found.getPassword() == null ? "" : found.getPassword();
        String given = password == null ? "" : password;
        String ns = stored.trim();
        String ng = given.trim();
        try {
            ns = java.text.Normalizer.normalize(ns, java.text.Normalizer.Form.NFKC);
            ng = java.text.Normalizer.normalize(ng, java.text.Normalizer.Form.NFKC);
        } catch (Exception ignored) {}
        if (ns.equals(ng)) return found;
        return null;
    }

    private void ensureDefaultAdmin() throws IOException {
        List<Admin> all = loadAll();
        if (all.isEmpty()) {
            Admin d = new Admin("A001", "admin", "admin123");
            FileManager.appendLine(adminsFile, d.toCSV());
        }
    }

    public void addAdmin(String username, String password) throws IOException {
        List<Admin> all = loadAll();
        Optional<Admin> existing = all.stream().filter(a -> a.getUsername().equals(username)).findFirst();
        if (existing.isPresent()) throw new IllegalArgumentException("username exists");
        String id = String.format("A%03d", all.size() + 1);
        Admin a = new Admin(id, username, password);
        FileManager.appendLine(adminsFile, a.toCSV());
    }

    public void updateAdmin(Admin updated) throws IOException {
        List<Admin> list = loadAll();
        List<Admin> out = new ArrayList<>();
        boolean replaced = false;
        for (Admin a : list) {
            if (a.getId().equalsIgnoreCase(updated.getId())) { out.add(updated); replaced = true; }
            else out.add(a);
        }
        if (!replaced) out.add(updated);
        List<String> lines = new ArrayList<>();
        for (Admin a : out) lines.add(a.toCSV());
        FileManager.overwrite(adminsFile, lines);
    }
}
