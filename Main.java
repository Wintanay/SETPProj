import models.Customer;
import models.Admin;
import services.CustomerService;
import services.AdminService;
import exceptions.AuthenticationException;

import java.io.IOException;
import java.util.Scanner;
import java.util.List;
import java.time.Year;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import services.VehicleService;
import services.RentalService;

public class Main {
    private static final CustomerService customerService = new CustomerService();
    private static final AdminService adminService = new AdminService();
    private static final VehicleService vehicleService = new VehicleService();
    private static final RentalService rentalService = new RentalService();
    // ANSI color codes for CLI highlighting (works in terminals that support ANSI)
    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String CYAN = "\u001B[36m";
    private static final String RED = "\u001B[31m";
    private static final String MAGENTA = "\u001B[35m";
    private static final boolean ANSI_SUPPORTED = detectAnsiSupport();

    public static void main(String[] args) {
        
        Scanner scanner = new Scanner(System.in);
        while (true) {
            printMainMenu();
            String choice = readLine(scanner);
            if (choice == null) {
                System.out.println("Input closed. Exiting.");
                return;
            }
            try {
                switch (choice) {
                    case "1": adminLogin(scanner); break;
                    case "2": customerLogin(scanner); break;
                    case "3": registerCustomer(scanner); break;
                    case "4": System.out.println("Goodbye."); System.exit(0); break;
                    default: System.out.println("Invalid choice. Try again.");
                }
            } catch (IOException ioe) {
                System.out.println("IO error: " + ioe.getMessage());
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void printMainMenu() {
        System.out.println(GREEN + BOLD + "=== Vehicle Renting System ===" + RESET);
        System.out.println(YELLOW + "1) Admin Login" + RESET);
        System.out.println(YELLOW + "2) Customer Login" + RESET);
        System.out.println(YELLOW + "3) Register as Customer" + RESET);
        System.out.println(YELLOW + "4) Exit" + RESET);
        System.out.print(CYAN + "Choose: " + RESET);
    }

    private static void clearScreen() {
        try {
            if (ANSI_SUPPORTED) {
                // ANSI clear screen
                System.out.print("\u001B[H\u001B[2J");
                System.out.flush();
                return;
            }
            // Fallback for Windows: try executing cls via cmd
            if (isWindows()) {
                try {
                    new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
                    return;
                } catch (Exception ignored) {
                    // continue to newline fallback
                }
            }
            // fallback: print several newlines
            for (int i = 0; i < 50; i++) System.out.println();
        } catch (Exception ignored) {
            for (int i = 0; i < 50; i++) System.out.println();
        }
    }

    private static void showBreadcrumb(String pageName) {
        if (ANSI_SUPPORTED) {
            System.out.println(BOLD + CYAN + "== " + pageName + " ==" + RESET);
        } else {
            System.out.println("== " + pageName + " ==");
        }
    }

    private static void transitionTo(String pageName) {
        // Clear, show a small loading animation, then clear and show breadcrumb
        clearScreen();
        String loading = "Opening " + pageName;
        System.out.print(loading);
        for (int i = 0; i < 3; i++) {
            try {
                Thread.sleep(180);
            } catch (InterruptedException ignored) {}
            System.out.print(".");
        }
        System.out.println();
        try {
            Thread.sleep(120);
        } catch (InterruptedException ignored) {}
        clearScreen();
    }

    private static boolean isWindows() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("win");
    }

    private static boolean detectAnsiSupport() {
        try {
            // Non-Windows systems generally support ANSI
            if (!isWindows()) return true;
            // On Windows, check for environment hints (Windows Terminal or ANSICON)
            String ansicon = System.getenv("ANSICON");
            if (ansicon != null && !ansicon.isEmpty()) return true;
            String wt = System.getenv("WT_SESSION");
            if (wt != null && !wt.isEmpty()) return true;
            String term = System.getenv("TERM");
            if (term != null && term.toLowerCase().contains("xterm")) return true;
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private static void adminLogin(Scanner scanner) {
        while (true) {
            transitionTo("Admin Login");
            System.out.print(CYAN + "Admin username (or type 'back' to return): " + RESET);
            String username = readLine(scanner);
            if (username == null || username.equalsIgnoreCase("back") || username.isEmpty()) {
                transitionTo("Main Menu");
                return;
            }
            System.out.print(CYAN + "Admin password: " + RESET);
            String pw = readLine(scanner);
            if (pw == null) return;
            Admin a = adminService.authenticate(username, pw);
            if (a == null) {
                System.out.println(RED + "Invalid admin credentials — try again." + RESET);
                continue;
            }
            System.out.println(GREEN + "Admin login successful." + RESET);
            adminMenu(scanner);
            return;
        }
    }

    private static void adminMenu(Scanner scanner) {
        while (true) {
            transitionTo("Admin Menu");
            System.out.println(MAGENTA + "--- Admin Menu ---" + RESET);
            System.out.println(YELLOW + "1) View all vehicles" + RESET);
            System.out.println(YELLOW + "2) Add vehicle" + RESET);
            System.out.println(YELLOW + "3) Edit vehicle" + RESET);
            System.out.println(YELLOW + "4) View rental history" + RESET);
            System.out.println(YELLOW + "5) View total income" + RESET);
            System.out.println(YELLOW + "6) Profile" + RESET);
            System.out.println(YELLOW + "7) Back" + RESET);
            System.out.print(CYAN + "Choose: " + RESET);
            String c = readLine(scanner);
            if (c == null) return;
            if ("6".equals(c)) {
                transitionTo("Main Menu");
                return;
            }
            if ("1".equals(c)) {
                clearScreen();
                showBreadcrumb("All Vehicles");
                for (models.Vehicle v : vehicleService.loadAll()) System.out.println(v.toString());
                System.out.println();
                System.out.print(CYAN + "Press Enter to return..." + RESET);
                readLine(scanner);
                continue;
            }
            if ("2".equals(c)) {
                adminAddVehicle(scanner);
                continue;
            }
            if ("3".equals(c)) {
                adminEditVehicle(scanner);
                continue;
            }
            if ("4".equals(c)) {
                clearScreen();
                showBreadcrumb("Rental History");
                for (models.Rental r : rentalService.loadAll()) {
                    models.Customer cust = customerService.findById(r.getCustomerId());
                    models.Vehicle veh = vehicleService.findById(r.getVehicleId());
                    String custName = cust == null ? r.getCustomerId() : cust.getName() + " (" + cust.getEmail() + ")";
                    String vehDesc = veh == null ? r.getVehicleId() : veh.getType() + " " + veh.getMake() + " " + veh.getModel();
                    System.out.println(String.format("%s | %s | %s | %s to %s | $%.2f | %s", r.getId(), custName, vehDesc, r.getStartDate(), r.getEndDate(), r.getTotalPrice(), r.getStatus()));
                }
                System.out.println();
                System.out.print(CYAN + "Press Enter to return..." + RESET);
                readLine(scanner);
                continue;
            }
            if ("5".equals(c)) {
                clearScreen();
                showBreadcrumb("Total Income");
                double total = rentalService.totalIncome();
                System.out.println(GREEN + String.format("Total income: $%.2f", total) + RESET);
                System.out.println();
                System.out.print(CYAN + "Press Enter to return..." + RESET);
                readLine(scanner);
                continue;
            }
            if ("6".equals(c)) {
                // Admin Profile
                adminProfile(scanner);
                continue;
            }
            if ("7".equals(c)) {
                transitionTo("Main Menu");
                return;
            }
            System.out.println(MAGENTA + "Unknown option." + RESET);
        }
    }

    private static void adminProfile(Scanner scanner) {
        transitionTo("Admin Profile");
        try {
            // show first admin (current simplistic approach)
            List<models.Admin> all = adminService.loadAll();
            if (all.isEmpty()) {
                System.out.println(RED + "No admin account found." + RESET);
                return;
            }
            models.Admin a = all.get(0);
            System.out.println(YELLOW + "Admin ID: " + a.getId() + RESET);
            System.out.println(YELLOW + "Username: " + a.getUsername() + RESET);
            System.out.println();
            System.out.print(CYAN + "New username (leave blank to keep): " + RESET);
            String nu = readLine(scanner);
            if (nu == null) return;
            if (!nu.trim().isEmpty()) a = new models.Admin(a.getId(), nu.trim(), a.getPassword());
            System.out.print(CYAN + "New password (leave blank to keep): " + RESET);
            String np = readLine(scanner);
            if (np == null) return;
            if (!np.trim().isEmpty()) a = new models.Admin(a.getId(), a.getUsername(), np.trim());
            adminService.updateAdmin(a);
            System.out.println(GREEN + "Admin profile updated." + RESET);
            try { Thread.sleep(400); } catch (InterruptedException ignored) {}
        } catch (Exception e) {
            System.out.println(RED + "Failed to update admin profile: " + e.getMessage() + RESET);
        }
    }

    private static void adminAddVehicle(Scanner scanner) {
        transitionTo("Add Vehicle");
        try {
            String type;
            while (true) {
                System.out.print(CYAN + "Type (Car/Bike/Truck) — enter one of: Car, Bike, Truck: " + RESET);
                type = readLine(scanner);
                if (type == null) return;
                if (type.equalsIgnoreCase("car") || type.equalsIgnoreCase("bike") || type.equalsIgnoreCase("truck")) {
                    type = Character.toUpperCase(type.charAt(0)) + type.substring(1).toLowerCase();
                    break;
                }
                System.out.println(RED + "Invalid type — enter Car, Bike, or Truck." + RESET);
            }

            String make;
            while (true) {
                System.out.print(CYAN + "Make (manufacturer) — e.g. Toyota: " + RESET);
                make = readLine(scanner);
                if (make == null) return;
                if (!make.trim().isEmpty()) break;
                System.out.println(RED + "Make cannot be empty." + RESET);
            }

            String model;
            while (true) {
                System.out.print(CYAN + "Model — e.g. Corolla: " + RESET);
                model = readLine(scanner);
                if (model == null) return;
                if (!model.trim().isEmpty()) break;
                System.out.println(RED + "Model cannot be empty." + RESET);
            }

            int year;
            int current = Year.now().getValue();
            while (true) {
                System.out.print(CYAN + "Year (4 digits) between 1900 and " + current + " — e.g. 2019: " + RESET);
                String yearS = readLine(scanner);
                if (yearS == null) return;
                try {
                    year = Integer.parseInt(yearS.trim());
                    if (year < 1900 || year > current) {
                        System.out.println(RED + "Year must be between 1900 and " + current + "." + RESET);
                        continue;
                    }
                    break;
                } catch (NumberFormatException nfe) {
                    System.out.println(RED + "Invalid year — enter a 4-digit number like 2019." + RESET);
                }
            }

            double rate;
            while (true) {
                System.out.print(CYAN + "Rate per day (positive) — e.g. 45.00: " + RESET);
                String rateS = readLine(scanner);
                if (rateS == null) return;
                try {
                    rate = Double.parseDouble(rateS.trim());
                    if (rate <= 0) {
                        System.out.println(RED + "Rate must be positive." + RESET);
                        continue;
                    }
                    break;
                } catch (NumberFormatException nfe) {
                    System.out.println(RED + "Invalid rate — enter a number like 45 or 45.00." + RESET);
                }
            }

            boolean av;
            while (true) {
                System.out.print(CYAN + "Available? (yes/no) — e.g. yes: " + RESET);
                String avS = readLine(scanner);
                if (avS == null) return;
                if (avS.equalsIgnoreCase("yes") || avS.equalsIgnoreCase("y") || avS.equalsIgnoreCase("true")) { av = true; break; }
                if (avS.equalsIgnoreCase("no") || avS.equalsIgnoreCase("n") || avS.equalsIgnoreCase("false")) { av = false; break; }
                System.out.println(RED + "Enter 'yes' or 'no'." + RESET);
            }

            int quantity;
            while (true) {
                System.out.print(CYAN + "Quantity (number of units, integer >= 0): " + RESET);
                String qS = readLine(scanner);
                if (qS == null) return;
                try {
                    quantity = Integer.parseInt(qS.trim());
                    if (quantity < 0) { System.out.println(RED + "Quantity cannot be negative." + RESET); continue; }
                    break;
                } catch (NumberFormatException nfe) {
                    System.out.println(RED + "Invalid quantity — enter an integer like 3." + RESET);
                }
            }

            int count = vehicleService.loadAll().size();
            String id = String.format("V%03d", count + 1);
            models.Vehicle v = new models.Vehicle(id, type, make.trim(), model.trim(), year, rate, av, quantity);
            vehicleService.addVehicle(v);
            System.out.println(GREEN + "Vehicle added: " + v.toString() + RESET);
            try { Thread.sleep(600); } catch (InterruptedException ignored) {}
            transitionTo("Admin Menu");
        } catch (Exception e) {
            System.out.println(RED + "Failed to add vehicle: " + e.getMessage() + RESET);
            try { Thread.sleep(400); } catch (InterruptedException ignored) {}
        }
    }

    private static void adminEditVehicle(Scanner scanner) {
        transitionTo("Edit Vehicle");
        try {
            List<models.Vehicle> list = vehicleService.loadAll();
            if (list.isEmpty()) {
                System.out.println(MAGENTA + "No vehicles available." + RESET);
                try { Thread.sleep(600); } catch (InterruptedException ignored) {}
                return;
            }
            for (models.Vehicle v : list) System.out.println(v.toString());
            models.Vehicle sel = null;
            while (true) {
                System.out.print(CYAN + "Enter vehicle ID to edit (or 'back' to return): " + RESET);
                String id = readLine(scanner);
                if (id == null) return;
                if (id.equalsIgnoreCase("back")) return;
                sel = vehicleService.findById(id.trim());
                if (sel == null) {
                    System.out.println(RED + "Vehicle not found — try again or type 'back' to cancel." + RESET);
                    try { Thread.sleep(300); } catch (InterruptedException ignored) {}
                    continue;
                }
                break;
            }

            // Type
            while (true) {
                System.out.print(CYAN + "Type (Car/Bike/Truck, leave blank to keep) — current: " + sel.getType() + ": " + RESET);
                String type = readLine(scanner);
                if (type == null) return;
                if (type.trim().isEmpty()) break;
                if (type.equalsIgnoreCase("car") || type.equalsIgnoreCase("bike") || type.equalsIgnoreCase("truck")) {
                    sel = new models.Vehicle(sel.getId(), Character.toUpperCase(type.charAt(0)) + type.substring(1).toLowerCase(), sel.getMake(), sel.getModel(), sel.getYear(), sel.getRatePerDay(), sel.isAvailable(), sel.getQuantity());
                    break;
                }
                System.out.println(RED + "Invalid type — enter Car, Bike, or Truck, or leave blank to keep." + RESET);
            }

            // Make
            while (true) {
                System.out.print(CYAN + "Make (leave blank to keep) — current: " + sel.getMake() + ": " + RESET);
                String make = readLine(scanner);
                if (make == null) return;
                if (make.trim().isEmpty()) break;
                sel = new models.Vehicle(sel.getId(), sel.getType(), make.trim(), sel.getModel(), sel.getYear(), sel.getRatePerDay(), sel.isAvailable(), sel.getQuantity());
                break;
            }

            // Model
            while (true) {
                System.out.print(CYAN + "Model (leave blank to keep) — current: " + sel.getModel() + ": " + RESET);
                String model = readLine(scanner);
                if (model == null) return;
                if (model.trim().isEmpty()) break;
                sel = new models.Vehicle(sel.getId(), sel.getType(), sel.getMake(), model.trim(), sel.getYear(), sel.getRatePerDay(), sel.isAvailable(), sel.getQuantity());
                break;
            }

            // Year
            int year = sel.getYear();
            int current = Year.now().getValue();
            while (true) {
                System.out.print(CYAN + "Year (1900-" + current + ", leave blank to keep) — current: " + sel.getYear() + ": " + RESET);
                String yearS = readLine(scanner);
                if (yearS == null) return;
                if (yearS.trim().isEmpty()) break;
                try {
                    int ny = Integer.parseInt(yearS.trim());
                    if (ny < 1900 || ny > current) {
                        System.out.println(RED + "Year must be between 1900 and " + current + "." + RESET);
                        continue;
                    }
                    year = ny;
                    break;
                } catch (NumberFormatException nfe) {
                    System.out.println(RED + "Invalid year — enter a 4-digit number like 2019, or leave blank to keep current." + RESET);
                }
            }

            // Rate
            double rate = sel.getRatePerDay();
            while (true) {
                System.out.print(CYAN + "Rate per day (positive, leave blank to keep) — current: " + sel.getRatePerDay() + ": " + RESET);
                String rateS = readLine(scanner);
                if (rateS == null) return;
                if (rateS.trim().isEmpty()) break;
                try {
                    double nr = Double.parseDouble(rateS.trim());
                    if (nr <= 0) { System.out.println(RED + "Rate must be positive." + RESET); continue; }
                    rate = nr;
                    break;
                } catch (NumberFormatException nfe) {
                    System.out.println(RED + "Invalid rate — enter a number like 45 or 45.00, or leave blank to keep current." + RESET);
                }
            }

            // Availability
            boolean av = sel.isAvailable();
            while (true) {
                System.out.print(CYAN + "Available? (yes/no, leave blank to keep) — current: " + (sel.isAvailable() ? "yes" : "no") + ": " + RESET);
                String avS = readLine(scanner);
                if (avS == null) return;
                if (avS.trim().isEmpty()) break;
                if (avS.equalsIgnoreCase("yes") || avS.equalsIgnoreCase("y") || avS.equalsIgnoreCase("true")) { av = true; break; }
                if (avS.equalsIgnoreCase("no") || avS.equalsIgnoreCase("n") || avS.equalsIgnoreCase("false")) { av = false; break; }
                System.out.println(RED + "Enter 'yes' or 'no', or leave blank to keep current." + RESET);
            }

            // build new vehicle and save
            // Quantity
            int quantity = sel.getQuantity();
            while (true) {
                System.out.print(CYAN + "Quantity (leave blank to keep) — current: " + sel.getQuantity() + ": " + RESET);
                String qS = readLine(scanner);
                if (qS == null) return;
                if (qS.trim().isEmpty()) break;
                try {
                    int nq = Integer.parseInt(qS.trim());
                    if (nq < 0) { System.out.println(RED + "Quantity cannot be negative." + RESET); continue; }
                    quantity = nq;
                    break;
                } catch (NumberFormatException nfe) {
                    System.out.println(RED + "Invalid quantity — enter an integer like 3, or leave blank to keep current." + RESET);
                }
            }

            models.Vehicle updated = new models.Vehicle(sel.getId(), sel.getType(), sel.getMake(), sel.getModel(), year, rate, av, quantity);
            List<models.Vehicle> updatedList = new java.util.ArrayList<>();
            for (models.Vehicle v : list) {
                if (v.getId().equals(updated.getId())) updatedList.add(updated);
                else updatedList.add(v);
            }
            vehicleService.saveAll(updatedList);
            System.out.println(GREEN + "Vehicle updated." + RESET);
            try { Thread.sleep(600); } catch (InterruptedException ignored) {}
        } catch (Exception e) {
            System.out.println(RED + "Failed to edit vehicle: " + e.getMessage() + RESET);
            try { Thread.sleep(400); } catch (InterruptedException ignored) {}
        }
    }

    private static void customerLogin(Scanner scanner) {
        while (true) {
            transitionTo("Customer Login");
            
            System.out.print(CYAN + "Email (or type 'back' to return): " + RESET);
            String email = readLine(scanner);
            if (email == null || email.equalsIgnoreCase("back") || email.isEmpty()) {
                transitionTo("Main Menu");
                return;
            }

            // Keep the email and prompt for password until correct or user cancels
            while (true) {
                System.out.print(CYAN + "Password (or type 'back' to change email): " + RESET);
                String pw = readLine(scanner);
                if (pw == null) return;
                if (pw.equalsIgnoreCase("back")) break; // go back to re-enter email
                Customer c = customerService.authenticate(email, pw);
                if (c == null) {
                    System.out.println(RED + "Invalid password — try again or type 'back' to change email." + RESET);
                    continue; // prompt password again, keep email
                }
                System.out.println(GREEN + "Login successful. Welcome, " + c.getName() + "!" + RESET);
                customerMenu(scanner, c);
                return;
            }
            // loop continues to allow entering a new email
        }
    }

    private static void customerMenu(Scanner scanner, Customer customer) {
        while (true) {
            transitionTo("Customer Menu - " + customer.getName());
            System.out.println(MAGENTA + "--- Customer Menu (" + customer.getName() + ") ---" + RESET);
            System.out.println(YELLOW + "1) View available vehicles" + RESET);
            System.out.println(YELLOW + "2) Rent a vehicle" + RESET);
            System.out.println(YELLOW + "3) Return vehicle" + RESET);
            System.out.println(YELLOW + "4) View my rentals" + RESET);
            System.out.println(YELLOW + "5) Profile" + RESET);
            System.out.println(YELLOW + "6) Logout" + RESET);
            System.out.print(CYAN + "Choose: " + RESET);
            String c = readLine(scanner);
            if (c == null) return;
            if ("6".equals(c)) {
                transitionTo("Main Menu");
                return;
            }

            if ("1".equals(c)) {
                clearScreen();
                showBreadcrumb("Available Vehicles");
                for (models.Vehicle v : vehicleService.loadAll()) {
                    if (v.getQuantity() > 0) System.out.println(v.toString());
                }
                System.out.println();
                System.out.print(CYAN + "Press Enter to return..." + RESET);
                readLine(scanner);
                continue;
            }

            if ("2".equals(c)) {
                // Rent a vehicle
                while (true) {
                    clearScreen();
                    showBreadcrumb("Rent a Vehicle");
                    for (models.Vehicle v : vehicleService.loadAll()) if (v.getQuantity() > 0) System.out.println(v.toString());
                    System.out.print(CYAN + "Enter vehicle ID to rent (or 'back'): " + RESET);
                    String vid = readLine(scanner);
                    if (vid == null) return;
                    if (vid.equalsIgnoreCase("back")) break;
                    models.Vehicle veh = vehicleService.findById(vid.trim());
                    if (veh == null) { System.out.println(RED + "Vehicle not found." + RESET); try { Thread.sleep(300); } catch (InterruptedException ignored) {} continue; }
                    if (veh.getQuantity() <= 0) { System.out.println(RED + "Selected vehicle has no available units." + RESET); try { Thread.sleep(300); } catch (InterruptedException ignored) {} continue; }

                    // check concurrent rental allowance
                    int active = 0;
                    for (models.Rental rr : rentalService.loadAll()) if (rr.getCustomerId().equals(customer.getId()) && "RENTED".equalsIgnoreCase(rr.getStatus())) active++;
                    int allowed = customerService.allowedConcurrentRentals(customer);
                    if (active >= allowed) {
                        System.out.println(RED + String.format("You have %d active rental(s). Your current badge allows %d concurrent rental(s). Return existing rentals to proceed.", active, allowed) + RESET);
                        System.out.print(CYAN + "Press Enter to return to the menu..." + RESET);
                        readLine(scanner);
                        break;
                    }

                    // dates
                    LocalDate start = null;
                    LocalDate end = null;
                    while (true) {
                        System.out.print(CYAN + "Start date (YYYY-MM-DD or YYYY-M-D, e.g. 2018-05-05 or 2018-5-5) or 'back': " + RESET);
                        String s = readLine(scanner);
                        if (s == null) return;
                        if (s.equalsIgnoreCase("back")) break;
                        LocalDate parsed = tryParseDate(s.trim());
                        if (parsed == null) { System.out.println(RED + "Invalid date format. Use YYYY-MM-DD or YYYY-M-D." + RESET); continue; }
                        start = parsed; break;
                    }
                    if (start == null) break; // user escaped
                    while (true) {
                        System.out.print(CYAN + "End date (YYYY-MM-DD or YYYY-M-D, e.g. 2018-05-07 or 2018-5-7) or 'back': " + RESET);
                        String eS = readLine(scanner);
                        if (eS == null) return;
                        if (eS.equalsIgnoreCase("back")) break;
                        LocalDate parsedE = tryParseDate(eS.trim());
                        if (parsedE == null) { System.out.println(RED + "Invalid date format. Use YYYY-MM-DD or YYYY-M-D." + RESET); continue; }
                        end = parsedE; break;
                    }
                    if (end == null) break;
                    if (end.isBefore(start)) { System.out.println(RED + "End date cannot be before start date." + RESET); try { Thread.sleep(300); } catch (InterruptedException ignored) {} continue; }
                    long days = ChronoUnit.DAYS.between(start, end) + 1;
                    double total = days * veh.getRatePerDay();
                    System.out.println(YELLOW + String.format("Total: $%.2f for %d days", total, days) + RESET);
                    System.out.print(CYAN + "Proceed to rent? (yes/no): " + RESET);
                    String confirm = readLine(scanner);
                    if (confirm == null) return;
                    if (!confirm.equalsIgnoreCase("yes") && !confirm.equalsIgnoreCase("y")) { System.out.println(MAGENTA + "Cancelled." + RESET); try { Thread.sleep(300); } catch (InterruptedException ignored) {} break; }

                    // create rental
                    try {
                        int count = rentalService.loadAll().size();
                        String rid = String.format("R%03d", count + 1);
                        models.Rental r = new models.Rental(rid, customer.getId(), veh.getId(), start.toString(), end.toString(), total, "RENTED");
                        rentalService.addRental(r);
                        System.out.println(GREEN + "Rental created: " + r.toString() + RESET);
                        try { Thread.sleep(600); } catch (InterruptedException ignored) {}
                        break;
                    } catch (IllegalStateException ise) {
                        System.out.println(RED + "Unable to rent: " + ise.getMessage() + RESET);
                        try { Thread.sleep(400); } catch (InterruptedException ignored) {}
                        break;
                    } catch (Exception ioe) {
                        System.out.println(RED + "Failed to create rental: " + ioe.getMessage() + RESET);
                        try { Thread.sleep(400); } catch (InterruptedException ignored) {}
                        break;
                    }
                }
                continue;
            }

            if ("3".equals(c)) {
                // Return vehicle
                clearScreen();
                showBreadcrumb("Return Vehicle");
                List<models.Rental> myR = new java.util.ArrayList<>();
                for (models.Rental r : rentalService.loadAll()) if (r.getCustomerId().equals(customer.getId()) && "RENTED".equalsIgnoreCase(r.getStatus())) myR.add(r);
                if (myR.isEmpty()) {
                    System.out.println(MAGENTA + "You have no rented vehicles." + RESET);
                    System.out.print(CYAN + "Press Enter to return..." + RESET);
                    readLine(scanner);
                    continue;
                }
                for (models.Rental r : myR) System.out.println(r.toString());
                System.out.print(CYAN + "Enter rental ID to return (or 'back'): " + RESET);
                String rid = readLine(scanner);
                if (rid == null) return;
                if (rid.equalsIgnoreCase("back")) continue;
                boolean found = false;
                for (models.Rental r : myR) if (r.getId().equals(rid.trim())) { found = true; break; }
                if (!found) { System.out.println(RED + "Rental not found." + RESET); try { Thread.sleep(300); } catch (InterruptedException ignored) {} continue; }
                try {
                    rentalService.returnRental(rid.trim());
                    System.out.println(GREEN + "Rental returned." + RESET);
                    try { Thread.sleep(500); } catch (InterruptedException ignored) {}
                } catch (Exception e) {
                    System.out.println(RED + "Failed to return rental: " + e.getMessage() + RESET);
                }
                continue;
            }

            if ("4".equals(c)) {
                clearScreen();
                showBreadcrumb("My Rentals");
                for (models.Rental r : rentalService.loadAll()) {
                    if (r.getCustomerId().equals(customer.getId())) System.out.println(r.toString());
                }
                System.out.println();
                System.out.print(CYAN + "Press Enter to return..." + RESET);
                readLine(scanner);
                continue;
            }

            if ("5".equals(c)) {
                // Profile submenu (view / edit / back)
                customerProfile(scanner, customer);
                continue;
            }

            System.out.println(MAGENTA + "Unknown option." + RESET);
        }
    }

    private static void registerCustomer(Scanner scanner) throws IOException {
        transitionTo("Register Customer");

        // Name
        String name;
        while (true) {
            System.out.print(CYAN + "Full name (or type 'back' to return): " + RESET);
            name = readLine(scanner);
            if (name == null || name.equalsIgnoreCase("back")) {
                transitionTo("Main Menu");
                return;
            }
            if (name.trim().isEmpty() || name.matches(".*\\d.*")) {
                System.out.println(RED + "Invalid name — do not include numbers and cannot be empty." + RESET);
                try { Thread.sleep(400); } catch (InterruptedException ignored) {}
                continue;
            }
            break;
        }

        // Email
        String email;
        while (true) {
            System.out.print(CYAN + "Email: " + RESET);
            email = readLine(scanner);
            if (email == null || email.equalsIgnoreCase("back")) {
                transitionTo("Main Menu");
                return;
            }
            if (!email.matches("^.+@.+\\..+$")) {
                System.out.println(RED + "Invalid email format." + RESET);
                try { Thread.sleep(300); } catch (InterruptedException ignored) {}
                continue;
            }
            break;
        }

        // Phone
        String phoneDigits;
        while (true) {
            System.out.print(CYAN + "Phone (enter 8 digits, will be stored as +2519XXXXXXXX): " + RESET);
            String phone = readLine(scanner);
            if (phone == null || phone.equalsIgnoreCase("back")) {
                transitionTo("Main Menu");
                return;
            }
            phoneDigits = phone.replaceAll("\\D", "");
            if (phoneDigits.length() != 8) {
                System.out.println(RED + "Invalid phone number — enter exactly 8 digits (e.g. 12345678)." + RESET);
                try { Thread.sleep(300); } catch (InterruptedException ignored) {}
                continue;
            }
            break;
        }

        // Password (only re-prompt password on error)
        String pw;
        while (true) {
            System.out.print(CYAN + "Password (min 4, max 8 chars): " + RESET);
            pw = readLine(scanner);
            if (pw == null || pw.equalsIgnoreCase("back")) {
                transitionTo("Main Menu");
                return;
            }
            if (pw.length() < 4) {
                System.out.println(RED + "Password too short (min 4)." + RESET);
                try { Thread.sleep(300); } catch (InterruptedException ignored) {}
                continue;
            }
            if (pw.length() > 8) {
                System.out.println(RED + "Password too long (max 8)." + RESET);
                try { Thread.sleep(300); } catch (InterruptedException ignored) {}
                continue;
            }
            break;
        }

        String phoneFinal = "+2519" + phoneDigits;
        while (true) {
            try {
                Customer c = customerService.register(name.trim(), email.trim(), phoneFinal, pw);
                System.out.println(GREEN + "Registered OK. Your customer ID: " + c.getId() + RESET);
                try { Thread.sleep(600); } catch (InterruptedException ignored) {}
                transitionTo("Main Menu");
                return;
            } catch (IllegalArgumentException iae) {
                String msg = iae.getMessage() == null ? "" : iae.getMessage();
                System.out.println(RED + "Registration failed: " + msg + RESET);
                try { Thread.sleep(400); } catch (InterruptedException ignored) {}
                // If it's an email conflict, re-prompt only the email field
                if (msg.toLowerCase().contains("email")) {
                    while (true) {
                        System.out.print(CYAN + "Email (different one) or type 'back' to cancel: " + RESET);
                        email = readLine(scanner);
                        if (email == null || email.equalsIgnoreCase("back")) {
                            transitionTo("Main Menu");
                            return;
                        }
                        if (!email.matches("^.+@.+\\..+$")) {
                            System.out.println(RED + "Invalid email format." + RESET);
                            try { Thread.sleep(300); } catch (InterruptedException ignored) {}
                            continue;
                        }
                        break; // valid new email, try registration again
                    }
                    continue; // attempt register again with new email
                }
                // otherwise, give up and return to main menu
                transitionTo("Main Menu");
                return;
            }
        }
    }

    private static void customerProfile(Scanner scanner, Customer customer) {
        transitionTo("My Profile");
        try {
            Customer c = customerService.findById(customer.getId());
            if (c == null) { System.out.println(RED + "Customer not found." + RESET); return; }

            // Recompute completed rentals from rental history (covers existing returned rentals)
            int completed = 0;
            for (models.Rental rr : rentalService.loadAll()) {
                if (rr.getCustomerId().equals(c.getId()) && "RETURNED".equalsIgnoreCase(rr.getStatus())) completed++;
            }
            c.setCompletedRentals(completed);
            // set badge according to same rules as CustomerService.incrementCompletedRentals
            if (completed >= 25) c.setBadge("SILVER"); else if (completed >= 10) c.setBadge("BRONZE"); else c.setBadge("");
            // persist the updated counts so file reflects history
            try { customerService.updateCustomer(c); } catch (Exception ignored) {}

            System.out.println(YELLOW + "ID: " + c.getId() + RESET);
            System.out.println(YELLOW + "Name: " + c.getName() + RESET);
            System.out.println(YELLOW + "Email: " + c.getEmail() + RESET);
            System.out.println(YELLOW + "Phone: " + c.getPhone() + RESET);
            System.out.println(YELLOW + "Completed rentals: " + c.getCompletedRentals() + RESET);
            System.out.println(YELLOW + "Badge: " + (c.getBadge().isEmpty() ? "None" : c.getBadge()) + RESET);
            System.out.println();
            System.out.print(CYAN + "Edit name (leave blank to keep): " + RESET);
            String nn = readLine(scanner);
            if (nn == null) return;
            if (!nn.trim().isEmpty()) c.setName(nn.trim());
            while (true) {
                System.out.print(CYAN + "Edit email (leave blank to keep): " + RESET);
                String ne = readLine(scanner);
                if (ne == null) return;
                if (ne.trim().isEmpty()) break;
                if (!ne.matches("^.+@.+\\..+$")) { System.out.println(RED + "Invalid email." + RESET); continue; }
                c.setEmail(ne.trim());
                break;
            }
            while (true) {
                System.out.print(CYAN + "Edit phone (8 digits, leave blank to keep): " + RESET);
                String np = readLine(scanner);
                if (np == null) return;
                if (np.trim().isEmpty()) break;
                String digits = np.replaceAll("\\D", "");
                if (digits.length() != 8) { System.out.println(RED + "Enter exactly 8 digits." + RESET); continue; }
                c.setPhone("+2519" + digits);
                break;
            }
            System.out.print(CYAN + "Edit password (min4 max8, leave blank to keep): " + RESET);
            String pw = readLine(scanner);
            if (pw == null) return;
            if (!pw.trim().isEmpty()) {
                if (pw.length() < 4 || pw.length() > 8) {
                    System.out.println(RED + "Invalid password length; changes not saved." + RESET);
                } else {
                    c.setPassword(pw);
                }
            }
            customerService.updateCustomer(c);
            System.out.println(GREEN + "Profile updated." + RESET);
            try { Thread.sleep(400); } catch (InterruptedException ignored) {}
        } catch (Exception e) {
            System.out.println(RED + "Failed to update profile: " + e.getMessage() + RESET);
        }
    }

    private static void viewCustomerProfile(Scanner scanner, Customer customer) {
        transitionTo("My Profile");
        try {
            Customer c = customerService.findById(customer.getId());
            if (c == null) { System.out.println(RED + "Customer not found." + RESET); return; }
            // compute completed rentals without persisting
            int completed = 0;
            for (models.Rental rr : rentalService.loadAll()) if (rr.getCustomerId().equals(c.getId()) && "RETURNED".equalsIgnoreCase(rr.getStatus())) completed++;
            System.out.println(YELLOW + "ID: " + c.getId() + RESET);
            System.out.println(YELLOW + "Name: " + c.getName() + RESET);
            System.out.println(YELLOW + "Email: " + c.getEmail() + RESET);
            System.out.println(YELLOW + "Phone: " + c.getPhone() + RESET);
            System.out.println(YELLOW + "Completed rentals: " + completed + RESET);
            System.out.println(YELLOW + "Badge: " + (c.getBadge().isEmpty() ? "None" : c.getBadge()) + RESET);
            System.out.println();
            System.out.print(CYAN + "Press Enter to return..." + RESET);
            readLine(scanner);
        } catch (Exception e) {
            System.out.println(RED + "Failed to show profile: " + e.getMessage() + RESET);
        }
    }

    private static void editCustomerProfile(Scanner scanner, Customer customer) {
        // reuse existing profile editor
        customerProfile(scanner, customer);
    }

    private static String readLine(Scanner scanner) {
        try {
            if (!scanner.hasNextLine()) return null;
            return scanner.nextLine().trim();
        } catch (Exception e) {
            return null;
        }
    }

    private static LocalDate tryParseDate(String s) {
        if (s == null) return null;
        try {
            return LocalDate.parse(s);
        } catch (Exception ignored) {}
        java.time.format.DateTimeFormatter f1 = java.time.format.DateTimeFormatter.ofPattern("yyyy-M-d");
        try {
            return LocalDate.parse(s, f1);
        } catch (Exception ignored) {}
        java.time.format.DateTimeFormatter f2 = java.time.format.DateTimeFormatter.ofPattern("yyyy/M/d");
        try {
            return LocalDate.parse(s.replace('-', '/'), f2);
        } catch (Exception ignored) {}
        return null;
    }
}
