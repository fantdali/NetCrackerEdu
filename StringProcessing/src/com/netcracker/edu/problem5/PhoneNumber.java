package com.netcracker.edu.problem5;

import java.util.InputMismatchException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * CLass gets a string representation of
 * phone number in one of two possible string formats:
 * </li> +<Country code><10 digits number>, for example “+79175655655” or "+104289652211"
 * or
 * </li> 8<Number 10 digits> for Russia, for example "89175655655"
 * and converts the resulting string to the format:
 * +<Country Code><Three Digits>-<Three Digits>-<Four Digits>
 * NOTE: Country code code considered to be not larger than 4 digits, as it is in real life.
 */
public class PhoneNumber {
    private String phone;
    private final Pattern p1 = Pattern.compile("^\\+(\\d{1,4})(\\d{3})(\\d{3})(\\d{4})$");
    private final Pattern p2 = Pattern.compile("^8(\\d{3})(\\d{3})(\\d{4})$");

    /**
     * Default constructor.
     */
    public PhoneNumber() {}

    /**
     * Constructor specifying phone number, but not analyzing it.
     * @param phone
     */
    public PhoneNumber(String phone){
        setPhone(phone);
    }

    /**
     * Sets phone number, but not analyzing it.
     * @param phone
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Gets phone number in format +<Country Code><Three Digits>-<Three Digits>-<Four Digits>
     * @return phone number
     * @throws IllegalStateException if phone wasn't set by {@link #setPhone(String)} or {@link #PhoneNumber(String)}.
     * @throws InputMismatchException if phone format was incorrect.
     */
    public String getPhone() throws IllegalStateException, InputMismatchException {
        if (phone == null) {
            throw new IllegalStateException();
        }

        Matcher m = p1.matcher(phone);
        StringBuilder phoneBuilder = new StringBuilder().append('+');

        if (m.matches()) {
            phoneBuilder = phoneBuilder.append(m.group(1)).append(m.group(2)).append('-');
            return phoneBuilder.append(m.group(3)).append('-').append(m.group(4)).toString();
        }

        m = p2.matcher(phone);
        if (m.matches()) {
            phoneBuilder = phoneBuilder.append('7').append(m.group(1)).append('-');
            return phoneBuilder.append(m.group(2)).append('-').append(m.group(3)).toString();

        }

        throw new InputMismatchException();
    }
}
