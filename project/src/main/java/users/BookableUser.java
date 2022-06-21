package users;

/**
 * Interface for users with the ability to make bookings
 */
public interface BookableUser {
    void makeBooking();
    String getSiteId();
    String getBookingUserId();
}
