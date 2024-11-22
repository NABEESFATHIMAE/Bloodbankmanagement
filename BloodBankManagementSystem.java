import java.sql.*;
import java.util.Scanner;

public class BloodBankManagementSystem {
    private static final String URL = "jdbc:mysql://localhost:3306/BloodBankDB"; 
    private static final String USER = "root";  
    private static final String PASSWORD = "Nabees@18"; 
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    static class BloodBank {
        public void addDonation(String bloodGroup, int quantity) {
            String query = "INSERT INTO BloodInventory (blood_group, quantity) VALUES (?, ?)";
            try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, bloodGroup);
                stmt.setInt(2, quantity);

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Blood donation added: " + quantity + " units of " + bloodGroup);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        public void viewTotalBloodAvailable() {
            String query = "SELECT blood_group, SUM(quantity) AS total_quantity FROM BloodInventory GROUP BY blood_group";
            try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery(query);
                System.out.println("Blood Availability in the system:");
                while (rs.next()) {
                    String bloodGroup = rs.getString("blood_group");
                    int totalQuantity = rs.getInt("total_quantity");
                    System.out.println("Blood Group: " + bloodGroup + ", Total Available: " + totalQuantity + " units");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        public boolean checkBloodAvailability(String bloodGroup, int requestedQuantity) {
            String query = "SELECT SUM(quantity) AS available_quantity FROM BloodInventory WHERE blood_group = ?";
            try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, bloodGroup);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    int availableQuantity = rs.getInt("available_quantity");
                    return availableQuantity >= requestedQuantity;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }
        public void handleBloodRequest(String patientName, String patientPhone, String bloodGroup, int quantity) {
          
            if (checkBloodAvailability(bloodGroup, quantity)) {
            
                String insertRequestQuery = "INSERT INTO BloodRequests (patient_name, patient_phone, blood_group, quantity) VALUES (?, ?, ?, ?)";
                try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(insertRequestQuery)) {
                    stmt.setString(1, patientName);
                    stmt.setString(2, patientPhone);
                    stmt.setString(3, bloodGroup);
                    stmt.setInt(4, quantity);

                    int rowsAffected = stmt.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Blood request has been placed for " + patientName);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Sorry, not enough blood available for the requested quantity of " + bloodGroup);
            }
        }

        public void viewAllRequests() {
            String query = "SELECT * FROM BloodRequests";
            try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery(query);

                System.out.println("All Blood Requests:");
                while (rs.next()) {
                    String patientName = rs.getString("patient_name");
                    String patientPhone = rs.getString("patient_phone");
                    String bloodGroup = rs.getString("blood_group");
                    int quantity = rs.getInt("quantity");
                    System.out.println("Patient: " + patientName + " | Phone: " + patientPhone + " | Blood Group: " + bloodGroup + " | Quantity: " + quantity);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        BloodBank bloodBank = new BloodBank();
        while (true) {
            System.out.println("\nBlood Bank Management System");
            System.out.println("1. Add Blood Donation");
            System.out.println("2. View Total Blood Availability");
            System.out.println("3. Request Blood");
            System.out.println("4. View All Blood Requests");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();  

            switch (choice) {
                case 1:   
                    System.out.print("Enter Blood Group (e.g., A+, O-, B+): ");
                    String bloodGroup = scanner.nextLine();
                    System.out.print("Enter Donation Quantity (in units): ");
                    int quantity = scanner.nextInt();
                    bloodBank.addDonation(bloodGroup, quantity);
                    break;
                case 2:
                   bloodBank.viewTotalBloodAvailable();
                    break;

                case 3:
                    System.out.print("Enter Patient Name: ");
                    String patientName = scanner.nextLine();
                    System.out.print("Enter Patient Phone Number: ");
                    String patientPhone = scanner.nextLine();
                    System.out.print("Enter Blood Group required (e.g., A+, O-, B+): ");
                    String reqBloodGroup = scanner.nextLine();
                    System.out.print("Enter Quantity required (in units): ");
                    int reqQuantity = scanner.nextInt();
                    bloodBank.handleBloodRequest(patientName, patientPhone, reqBloodGroup, reqQuantity);
                    break;
                case 4:
                    bloodBank.viewAllRequests();
                    break;
                case 5:
                    System.out.println("Exiting the system.");
                    scanner.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
