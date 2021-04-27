package com.netcracker.edu.problem5;

import com.netcracker.edu.problem2.Person;
import org.junit.jupiter.api.Test;

import java.util.InputMismatchException;

import static org.junit.jupiter.api.Assertions.*;

class PhoneNumberTest {
    @Test
    void getPhone() {
        PhoneNumber p = new PhoneNumber("+79175655655");
        assertEquals("+7917-565-5655", p.getPhone());

        p.setPhone("+104289652211");
        assertEquals("+10428-965-2211", p.getPhone());

        p.setPhone("89367252334");
        assertEquals("+7936-725-2334", p.getPhone());
    }

    @Test
    void getPhoneException() {
        // too large number
        PhoneNumber phoneNumber = new PhoneNumber("+1023183761257639286457");
        Exception exception = assertThrows(InputMismatchException.class, () -> phoneNumber.getPhone());

        // number wasn't set
        phoneNumber.setPhone(null);
        exception = assertThrows(IllegalStateException.class, () -> phoneNumber.getPhone());
    }
}