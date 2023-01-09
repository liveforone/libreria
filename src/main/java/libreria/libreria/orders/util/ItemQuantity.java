package libreria.libreria.orders.util;

public class ItemQuantity {

    public static boolean isSoldOut(int remaining) {
        return remaining <= 0;
    }

    public static boolean isOverRemaining(int remaining, int orderCount) {
        return remaining - orderCount <= 0;
    }
}
