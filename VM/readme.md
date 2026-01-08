🚗 Vehicle Renting System (Java CLI Project)
📌 Project Overview

The Vehicle Renting System is a command-line-based Java application designed to manage the renting of vehicles such as cars, bikes, and trucks.
The system allows customers to rent and return vehicles, while administrators manage vehicles and view rental records.

This project demonstrates Object-Oriented Programming (OOP) principles, custom exception handling, and file handling for persistent data storage without using a database.

🎯 Objectives

Apply core Java OOP concepts in a real-world scenario

Implement exception handling for system reliability

Use file handling to store and retrieve data permanently

Build a clean and user-friendly command-line interface

🧠 Concepts Used

Abstraction

Inheritance

Polymorphism

Encapsulation

Custom Exceptions

File I/O (Text Files)

Collections (ArrayList)

🧩 System Features
👑 Admin Functions

Add new vehicles

View all vehicles

View rental history

View total income generated

Manage vehicle availability

🙋 Customer Functions

Register as a customer

View available vehicles

Rent a vehicle

Return a rented vehicle

View personal rental history

🚙 Vehicle Types Supported

Car

Bike

Truck

Each vehicle type has its own rental pricing logic implemented using polymorphism.

⚠️ Exception Handling

The system uses both built-in and custom exceptions to prevent runtime errors and invalid operations.

Custom Exceptions:

VehicleNotAvailableException

InvalidRentalPeriodException

Handled scenarios include:

Renting unavailable vehicles

Invalid rental duration

Invalid menu input

💾 File Handling & Data Persistence

Data is stored using text files to ensure persistence even after program termination.

Files Used:

vehicles.txt – stores vehicle details

customers.txt – stores customer information

rentals.txt – stores rental records

All file operations are handled through a dedicated utility class to ensure clean code and reusability.

📂 Project Structure
vehicle_rental_system/
 ├── models/
 │    ├── Vehicle.java
 │    ├── Car.java
 │    ├── Bike.java
 │    ├── Truck.java
 │    ├── Customer.java
 │    └── Rental.java
 ├── services/
 │    ├── VehicleService.java
 │    ├── CustomerService.java
 │    └── RentalService.java
 ├── exceptions/
 │    ├── VehicleNotAvailableException.java
 │    └── InvalidRentalPeriodException.java
 ├── utils/
 │    └── FileManager.java
 ├── files/
 │    ├── vehicles.txt
 │    ├── customers.txt
 │    └── rentals.txt
 └── Main.java

▶️ How to Run the Project

Install Java JDK 8 or higher

Clone or download the project

Open the project in any Java IDE (IntelliJ, Eclipse, VS Code) or use the command line

Build & run (simple, without a build tool):

```bash
javac -d out src/**/*.java    # compile (adjust path to your sources)
java -cp out Main            # run the CLI app
```

If you prefer Maven or Gradle, add a minimal `pom.xml` or `build.gradle` (ask me and I can scaffold one).

Quick start (IDE): open `Main.java`, run the file, then follow the on-screen menu.

---

**File formats (text file schemas)**

Provide the following plain-text/CSV-style formats in the `files/` folder so the system can parse them predictably.

- `vehicles.txt` — one vehicle per line:

	`vehicleId,type,make,model,year,ratePerDay,available`

	Example:

	`V001,Car,Toyota,Corolla,2019,45.0,true`

- `customers.txt` — one customer per line:

	`customerId,name,email,phone`

	Example:

	`C001,Jane Doe,jane@example.com,555-1234`

- `rentals.txt` — one rental record per line:

	`rentalId,customerId,vehicleId,startDate,endDate,totalPrice,status`

	Dates should use ISO format `YYYY-MM-DD`. Example:

	`R001,C001,V001,2024-12-01,2024-12-04,135.0,RETURNED`

Documenting these schemas in the README helps others create valid sample files quickly.

---

**Sample data (small set for quick manual testing)**

Place these lines in the corresponding files inside the `files/` folder to exercise the app right away.

- `files/vehicles.txt`:

	V001,Car,Toyota,Corolla,2019,45.0,true

	V002,Bike,Yamaha,FZ,2020,20.0,true

	V003,Truck,Ford,F-150,2018,80.0,true

- `files/customers.txt`:

	C001,Jane Doe,jane@example.com,555-1234

- `files/rentals.txt` (empty or with a header-less sample):

	(start empty to let the app write records)

---

**Custom exceptions (documented)**

- `VehicleNotAvailableException` — thrown when attempting to rent a vehicle marked unavailable.
- `InvalidRentalPeriodException` — thrown when end date is before start date or duration is invalid.

List thrown exceptions and handling behaviour so reviewers and maintainers understand edge cases.

---

**Testing recommendations**

- Add a small test module using JUnit 5 to validate pricing logic and rental period validation.
- Example test targets: `RentalService.calculatePrice()`, `VehicleService.toggleAvailability()`, and exception paths.

Run tests with Maven/Gradle or from the IDE; ask me and I can scaffold basic JUnit tests.

---

**Class responsibilities (brief)**

- `models/*`: domain objects (`Vehicle`, `Car`, `Bike`, `Truck`, `Customer`, `Rental`).
- `services/*`: core business logic and in-memory orchestration (`VehicleService`, `CustomerService`, `RentalService`).
- `utils/FileManager`: file read/write and parsing helpers.
- `exceptions/*`: custom exception types.

---

**Project enhancements & checklist**

- [ ] Add example `files/*.txt` (done above — copy into `files/`).
- [ ] Add `pom.xml` or `build.gradle` for reproducible builds.
- [ ] Add JUnit tests for core logic.
- [ ] Add `LICENSE` and `CONTRIBUTING.md` for open reuse.
- [ ] Optionally: add simple CLI credentials for admin/customer or migrate persistence to a lightweight DB.

---

📈 Future Enhancements

📈 Future Enhancements

User authentication (Admin & Customer login)

Search and filter vehicles

File encryption for security

Migration to database (MySQL)

🧾 Conclusion

This project successfully demonstrates a real-world Java application using OOP principles, robust exception handling, and file-based data persistence.
It is suitable for academic submission, Java practice, and portfolio demonstration.