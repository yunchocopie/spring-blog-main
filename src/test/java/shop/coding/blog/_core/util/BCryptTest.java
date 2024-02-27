package shop.coding.blog._core.util;

import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

public class BCryptTest {

    @Test
    public void login_test() {

    }

    @Test
    public void gensalt_test() {
        String salt = BCrypt.gensalt();
        System.out.println(salt);

    }
    @Test //$2a$10$5U79dgC.oHVb0RoFrB23IuWOZYHcwF0kWA7EUwh4hiTigjeRfq0TO
    public void hashpw_test() {
        String rawPassword = "1234";
        String encPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
        System.out.println(encPassword);
    }
}
