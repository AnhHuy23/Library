package Interface;

public interface MobileValidator {
    default  public boolean isValidMobile(String phoneNumber) {
        return phoneNumber != null && phoneNumber.matches("\\d{10,11}");
    }
}
