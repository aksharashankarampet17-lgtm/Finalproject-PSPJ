
import java.util.*;
import java.nio.file.*;
import java.io.IOException;
import java.util.function.*;
import java.util.stream.*;

interface Billable {
    double calculateTotal();
}

class MenuItem implements Comparable<MenuItem> {
    private int id;
    private String name;
    private double price;
    private int categoryCode;

    public MenuItem(int id, String name, double price) {
        this(id, name, price, 0); 
    }

    public MenuItem(int id, String name, double price, int categoryCode) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.categoryCode = categoryCode;
    }
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public double getPrice() {
        return price;
    }
    public int getCategoryCode() {
        return categoryCode;
    }
    @Override
    public int compareTo(MenuItem other) {
        return Double.compare(this.price, other.price);
    }
    @Override
    public String toString() {
        return id + ". " + name + " - Rs. " + price;
    }
}
class OrderItem extends MenuItem {
    private int quantity;

    public OrderItem(MenuItem item, int quantity) {
        super(item.getId(), item.getName(), item.getPrice(),
              item.getCategoryCode());
        this.quantity = quantity;
    }
    public int getQuantity() {
        return quantity;
    }
    public double getLineTotal() {
        return getPrice() * quantity;
    }
    @Override
    public String toString() {
        return getName() + " x " + quantity + " = Rs. "
                + String.format("%.2f", getLineTotal());
    }
}
abstract class OrderBase {
    protected String customerName;

    public OrderBase(String customerName) {
        this.customerName = customerName;
    }
    public abstract void printBill();
}
class RestaurantOrder extends OrderBase implements Billable {
    private ArrayList<OrderItem> items = new ArrayList<>();

    public RestaurantOrder(String customerName) {
        super(customerName);
    }

    public void addItem(MenuItem item, int quantity) {
        items.add(new OrderItem(item, quantity));
    }

    public ArrayList<OrderItem> getItems() {
        return items;
    }
    public double calculateTotal(double discountRate) {
        return calculateTotal() * (1 - discountRate);
    }

    @Override
    public double calculateTotal() {
        double total = 0;

        for (OrderItem item : items) {
            total += item.getLineTotal();
        }

        return total;
    }
    @Override
    public void printBill() {
        System.out.println("\n========== RESTAURANT BILL ==========");
        System.out.println("Customer: " + customerName);
        System.out.println("-------------------------------------");
        for (OrderItem item : items) {
            System.out.println(item);
        }
        double subtotal = calculateTotal();
        double tax = subtotal * 0.05;
        double grandTotal = subtotal + tax;
        System.out.printf("-------------------------------------%n");
        System.out.printf("Subtotal : Rs. %.2f%n", subtotal);
        System.out.printf("Tax (5%%) : Rs. %.2f%n", tax);
        System.out.printf("Total    : Rs. %.2f%n", grandTotal);
        System.out.println("=====================================");
    }
}
public class RestaurantManagementSystem {
    static Scanner sc = new Scanner(System.in);
    static final double TAX_RATE = 0.05;
    
    static ArrayList<MenuItem> menu = new ArrayList<>();
    static HashMap<Integer, MenuItem> menuMap = new HashMap<>();
    static HashSet<String> uniqueNames = new HashSet<>();
 static final String FILE_NAME = "restaurant_orders.txt";

    public static void main(String[] args) {

        loadMenu();
        int totalMenuItems = menu.size();
        double averagePrice = calculateAveragePrice();
        int roundedAverage = (int) averagePrice;

        System.out.println("=====================================");
        System.out.println("     RESTAURANT MANAGEMENT SYSTEM");
        System.out.println("=====================================");
        System.out.println("Menu items: " + totalMenuItems);
        System.out.println("Average price (rounded): Rs. "
                + roundedAverage);

        int choice;

        do {
            System.out.println("\n1. Display Menu");
            System.out.println("2. Place Order");
            System.out.println("3. Search Menu Item");
            System.out.println("4. Sort Menu by Price");
            System.out.println("5. Show Collection and Stream Demo");
            System.out.println("6. Show Array and Matrix Demo");
            System.out.println("7. Show Previous Orders");
            System.out.println("8. Exit");
            System.out.print("Enter your choice: ");

            choice = readInt();
            switch (choice) {
                case 1:
                    displayMenu();
                    break;
                case 2:
                    placeOrder();
                    break;
                case 3:
                    searchItem();
                    break;
                case 4:
                    sortMenu();
                    break;
                case 5:
                    collectionDemo();
                    break;
                case 6:
                    arrayAndMatrixDemo();
                    break;
                case 7:
                    readOrders();
                    break;
                case 8:
                    System.out.println("Thank you for visiting!");
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }

        } while (choice != 8);

        sc.close();
    }
    static void loadMenu() {
        menu.add(new MenuItem(1, "Burger", 120, 1));
        menu.add(new MenuItem(2, "Pizza", 250, 1));
        menu.add(new MenuItem(3, "Sandwich", 100, 1));
        menu.add(new MenuItem(4, "Coffee", 60, 2));
        menu.add(new MenuItem(5, "Ice Cream", 80, 2));

        for (MenuItem item : menu) {
            menuMap.put(item.getId(), item);
            uniqueNames.add(item.getName());
        }
    }
    static void displayMenu() {
        System.out.println("\n----------- MENU -----------");
        System.out.printf("%-5s %-15s %10s%n",
                "ID", "Item", "Price");

        for (MenuItem item : menu) {
            System.out.printf("%-5d %-15s Rs. %7.2f%n",
                    item.getId(), item.getName(), item.getPrice());
        }
    }
    static int readInt() {
        while (!sc.hasNextInt()) {
            System.out.print("Enter a valid whole number: ");
            sc.next();
        }
        return sc.nextInt();
    }
    static void placeOrder() {
        sc.nextLine(); 

        System.out.print("Enter customer name: ");
        String customerName = sc.nextLine().trim();

        if (customerName.isEmpty()) {
            customerName = "Guest";
        }
        RestaurantOrder order = new RestaurantOrder(customerName);

        int id;
        do {
            displayMenu();
            System.out.print("Enter item ID (0 to finish): ");
            id = readInt();
            if (id == 0) {
                break;
            }
            MenuItem selected = menuMap.get(id);
            if (selected == null) {
                System.out.println("Item not found.");
                continue;
            }
            System.out.print("Enter quantity: ");
            int quantity = readInt();

            if (quantity <= 0) {
                System.out.println("Quantity must be positive.");
                continue;
            }
            order.addItem(selected, quantity);
            System.out.println("Item added successfully.");

        } while (true);
        if (order.getItems().isEmpty()) {
            System.out.println("No items ordered.");
            return;
        }

        order.printBill();
        System.out.printf("Total after 10%% discount: Rs. %.2f%n",
                order.calculateTotal(0.10));

        saveOrder(order);
    }
    static void searchItem() {
        System.out.print("Enter item name to search: ");
        sc.nextLine();
        String search = sc.nextLine().trim();
        boolean found = false;

        for (MenuItem item : menu) {
            if (item.getName().equalsIgnoreCase(search)) {
                System.out.println("Found: " + item);
                found = true;
                break;
            }
        }
        if (!found) {
            System.out.println("Item not found.");
        }
    }
    static void sortMenu() {
        Collections.sort(menu);

        System.out.println("\nMenu sorted by price:");
        displayMenu();
    }
    static void collectionDemo() {
        System.out.println("\n--- COLLECTIONS AND STREAMS ---");
        Predicate<MenuItem> expensive =
                item -> item.getPrice() >= 100;

        System.out.println("Items costing Rs. 100 or more:");

        menu.stream()
                .filter(expensive)
                .forEach(System.out::println); 
        Function<MenuItem, String> itemName =
                item -> item.getName().toUpperCase();

        List<String> names = menu.stream()
                .map(itemName)
                .collect(Collectors.toList());

        System.out.println("Uppercase names: " + names);

        Map<Integer, Long> categoryCount = menu.stream()
                .collect(Collectors.groupingBy(
                        MenuItem::getCategoryCode,
                        Collectors.counting()));

        System.out.println("Items per category: " + categoryCount);
        Supplier<String> message =
                () -> "Stream demonstration completed.";

        System.out.println(message.get());

        System.out.println("Unique menu names: " + uniqueNames);
        System.out.println("Items in map: " + menuMap.size());
        String joinedNames = menu.stream()
                .map(MenuItem::getName)
                .collect(Collectors.joining(", "));

        System.out.println("All items: " + joinedNames);
    }
    static void arrayAndMatrixDemo() {
        int[] prices = new int[menu.size()];

        for (int i = 0; i < menu.size(); i++) {
            prices[i] = (int) menu.get(i).getPrice();
        }

        System.out.println("\n--- ARRAY DEMO ---");
        System.out.println("Prices: " + Arrays.toString(prices));

        Arrays.sort(prices);
        System.out.println("Sorted prices: " + Arrays.toString(prices));

        int target = 100;
        int position = Arrays.binarySearch(prices, target);

        System.out.println("Search for Rs. 100: " + position);
        System.out.println("Total of prices: " + recursiveSum(prices, 0));
        System.out.printf("Average price: %.2f%n",
                (double) recursiveSum(prices, 0) / prices.length);

        int[][] sales = {
            {2, 3},
            {4, 1}
        };
        int[][] nextSales = {
            {1, 2},
            {2, 3}
        };

        System.out.println("Matrix addition result:");

        for (int i = 0; i < sales.length; i++) {
            for (int j = 0; j < sales[i].length; j++) {
                System.out.print(
                        (sales[i][j] + nextSales[i][j]) + " ");
            }
            System.out.println();
        }
        int a = 5;
        int b = 3;
        System.out.println("Bitwise AND (5 & 3): " + (a & b));
    }
    static int recursiveSum(int[] values, int index) {
        if (index >= values.length) {
            return 0;
        }
        return values[index] + recursiveSum(values, index + 1);
    }
    static double calculateAveragePrice() {
        if (menu.isEmpty()) {
            return 0;
        }
        double total = 0;
        for (MenuItem item : menu) {
            total += item.getPrice();
        }
        return total / menu.size();
    }
    static void saveOrder(RestaurantOrder order) {
        try {
            List<String> lines = new ArrayList<>();

            lines.add("Customer: " + order.customerName);
            lines.add("Items:");

            for (OrderItem item : order.getItems()) {
                lines.add(item.getName() + ","
                        + item.getQuantity() + ","
                        + item.getLineTotal());
            }
            lines.add("Total: " + order.calculateTotal());
            lines.add("-------------------------");

            Path path = Paths.get(FILE_NAME);

            // try-with-resources closes the writer automatically
            try (java.io.BufferedWriter writer =
                    Files.newBufferedWriter(
                            path,
                            StandardOpenOption.CREATE,
                            StandardOpenOption.APPEND)) {

                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                }
            }
            System.out.println("Order saved to " + FILE_NAME);

        } catch (IOException e) {
            System.out.println("Could not save order: " + e.getMessage());
        }
    }
    static void readOrders() {
        Path path = Paths.get(FILE_NAME);

        try {
            if (!Files.exists(path)) {
                System.out.println("No previous orders found.");
                return;
            }
            System.out.println("\n--- PREVIOUS ORDERS ---");
            try (Stream<String> lines = Files.lines(path)) {
                lines.forEach(System.out::println);
            }

        } catch (IOException e) {
            System.out.println("Could not read orders: " + e.getMessage());
        }
    }
}