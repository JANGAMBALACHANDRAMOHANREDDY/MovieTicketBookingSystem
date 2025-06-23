import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MovieTicketBooking {
    static Scanner sc = new Scanner(System.in);
    static List<String> movies = new ArrayList<>(Arrays.asList("Avengers", "Inception", "Interstellar"));
    static Map<String, List<Booking>> bookings = new HashMap<>();
    static Map<String, Integer> seats = new HashMap<>();
    static final int DEFAULT_SEATS = 5;
    static final String ADMIN_PASSWORD = "admin123";

    public static void main(String[] args) {
        for (String movie : movies) {
            seats.put(movie, DEFAULT_SEATS);
        }

        System.out.println("Welcome to the Movie Ticket Booking System!");
        while (true) {
            System.out.println("\n1. View Movies\n2. Book Ticket(s)\n3. View Bookings\n4. Cancel Booking\n5. Admin Login\n6. Exit");
            int choice = getValidatedIntegerInput("Enter your choice: ", 1, 6);

            switch (choice) {
                case 1:
                    viewMovies();
                    break;
                case 2:
                    bookTickets();
                    break;
                case 3:
                    viewBookings();
                    break;
                case 4:
                    cancelBooking();
                    break;
                case 5:
                    adminLogin();
                    break;
                case 6:
                    System.out.println("Thank you for using the system!");
                    return;
            }
        }
    }

    static void viewMovies() {
        System.out.println("Available Movies (Seats Left):");
        for (int i = 0; i < movies.size(); i++) {
            String movie = movies.get(i);
            System.out.println((i + 1) + ". " + movie + " (" + seats.get(movie) + " seats left)");
        }
    }

    static void bookTickets() {
        if (movies.isEmpty()) {
            System.out.println("No movies available to book.");
            return;
        }
        viewMovies();
        int movieNum = getValidatedIntegerInput("Enter movie number to book: ", 1, movies.size());
        String movie = movies.get(movieNum - 1);

        System.out.print("Enter your name: ");
        String name = getValidatedName();

        int available = seats.get(movie);
        int numTickets = getValidatedIntegerInput(
            "How many tickets do you want to book? ", 1, available);

        bookings.putIfAbsent(name, new ArrayList<>());
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < numTickets; i++) {
            bookings.get(name).add(new Booking(movie, now));
        }
        seats.put(movie, available - numTickets);

        System.out.println("Booked " + numTickets + " ticket(s) for " + name + " to watch " + movie + "!");
    }

    static void viewBookings() {
        System.out.print("Enter your name to view bookings: ");
        String name = getValidatedName();
        List<Booking> userBookings = bookings.get(name);
        if (userBookings == null || userBookings.isEmpty()) {
            System.out.println("No bookings found for " + name + ".");
        } else {
            System.out.println("Bookings for " + name + ":");
            for (Booking b : userBookings) {
                System.out.println(" - " + b);
            }
        }
    }

    static void cancelBooking() {
        System.out.print("Enter your name to cancel booking: ");
        String name = getValidatedName();
        List<Booking> userBookings = bookings.get(name);

        if (userBookings == null || userBookings.isEmpty()) {
            System.out.println("No bookings found for " + name + ".");
            return;
        }

        System.out.println("Your bookings: ");
        for (int i = 0; i < userBookings.size(); i++) {
            System.out.println((i + 1) + ". " + userBookings.get(i));
        }

        int cancelNum = getValidatedIntegerInput(
            "Enter the number of the booking to cancel: ", 1, userBookings.size());

        Booking canceledBooking = userBookings.remove(cancelNum - 1);
        seats.put(canceledBooking.movie, seats.get(canceledBooking.movie) + 1);

        System.out.println("Booking for \"" + canceledBooking.movie + "\" canceled for " + name + ".");

        if (userBookings.isEmpty()) {
            bookings.remove(name);
        }
    }

    // --- Admin Features ---

    static void adminLogin() {
        System.out.print("Enter admin password: ");
        String pwd = sc.nextLine();
        if (!pwd.equals(ADMIN_PASSWORD)) {
            System.out.println("Incorrect password. Returning to main menu.");
            return;
        }
        while (true) {
            System.out.println("\n--- Admin Menu ---");
            System.out.println("1. Add Movie\n2. Remove Movie\n3. Reset Seats\n4. View All Bookings\n5. Exit Admin Menu");
            int adminChoice = getValidatedIntegerInput("Enter your choice: ", 1, 5);
            switch (adminChoice) {
                case 1:
                    addMovie();
                    break;
                case 2:
                    removeMovie();
                    break;
                case 3:
                    resetSeats();
                    break;
                case 4:
                    viewAllBookings();
                    break;
                case 5:
                    return;
            }
        }
    }

    static void addMovie() {
        System.out.print("Enter new movie name: ");
        String movie = sc.nextLine().trim();
        if (movie.isEmpty()) {
            System.out.println("Movie name cannot be empty.");
            return;
        }
        if (movies.contains(movie)) {
            System.out.println("Movie already exists.");
            return;
        }
        movies.add(movie);
        seats.put(movie, DEFAULT_SEATS);
        System.out.println("Movie \"" + movie + "\" added with " + DEFAULT_SEATS + " seats.");
    }

    static void removeMovie() {
        viewMovies();
        int movieNum = getValidatedIntegerInput("Enter movie number to remove: ", 1, movies.size());
        String movie = movies.remove(movieNum - 1);
        seats.remove(movie);
        // Remove all bookings for this movie
        for (List<Booking> userBookings : bookings.values()) {
            userBookings.removeIf(b -> b.movie.equals(movie));
        }
        System.out.println("Movie \"" + movie + "\" removed from the list.");
    }

    static void resetSeats() {
        viewMovies();
        int movieNum = getValidatedIntegerInput("Enter movie number to reset seats: ", 1, movies.size());
        String movie = movies.get(movieNum - 1);
        seats.put(movie, DEFAULT_SEATS);
        System.out.println("Seats for \"" + movie + "\" reset to " + DEFAULT_SEATS + ".");
    }

    static void viewAllBookings() {
        if (bookings.isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }
        System.out.println("--- All Bookings ---");
        for (String name : bookings.keySet()) {
            System.out.println(name + ":");
            for (Booking b : bookings.get(name)) {
                System.out.println(" - " + b);
            }
        }
    }

    // --- Helper Methods ---

    static int getValidatedIntegerInput(String prompt, int min, int max) {
        int input = -1;
        while (true) {
            System.out.print(prompt);
            String line = sc.nextLine();
            try {
                input = Integer.parseInt(line);
                if (input < min || input > max) {
                    System.out.println("Please enter a number between " + min + " and " + max + ".");
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
        return input;
    }

    static String getValidatedName() {
        String name;
        while (true) {
            name = sc.nextLine().trim();
            if (name.isEmpty()) {
                System.out.print("Name cannot be empty. Please enter your name: ");
            } else {
                break;
            }
        }
        return name;
    }
}

// Booking class to hold movie and booking time
class Booking {
    String movie;
    LocalDateTime time;

    Booking(String movie, LocalDateTime time) {
        this.movie = movie;
        this.time = time;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return movie + " (Booked on: " + time.format(formatter) + ")";
    }
}
