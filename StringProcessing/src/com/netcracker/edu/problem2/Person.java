package com.netcracker.edu.problem2;

import java.util.InputMismatchException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class stores last, first and middle names.
 * This names can be set to person by his full name in constructor {@link #Person(String)}
 * or in {@link #setFullName(String)} method.
 *
 * <li>1) First group of full name considered as last name, second group as first name and third as middle name.
 * <li>2) Full name must match to {@link #NAME_PATTERN} "([A-Z][a-z]*[a-z.]) ?([A-Z][a-z]*[a-z.])? ?([A-Z][a-z]*[a-z.])?",
 * i.e. only last name can be set or last name with first + middle name.
 * <li>3) Each name must begin with upper case latin letter, contain only lower case latin letters and
 * end with lower case letter or dot(.)
 */
public class Person {
    private String lastName;
    private String middleName;
    private String firstName;
    private static final String NAME_PATTERN =
            "([A-Z][a-z]*[a-z.]) ?([A-Z][a-z]*[a-z.])? ?([A-Z][a-z]*[a-z.])?";

    /**
     * Creates Person with specified full name, i.e. sets each part of full name to class fields.
     * @param fullName
     * @throws InputMismatchException when full name doesn't match to {@link #NAME_PATTERN}
     */
    public Person(String fullName) throws InputMismatchException {
        setFullName(fullName);
    }

    /**
     * Sets each part of full name to class fields.
     * @param fullName
     * @throws InputMismatchException when full name doesn't match to {@link #NAME_PATTERN}
     */
    public void setFullName(String fullName) throws InputMismatchException {
        Pattern p = Pattern.compile(NAME_PATTERN);
        Matcher m = p.matcher(fullName);

        if (m.matches()) {
            lastName = m.group(1);
            firstName = m.group(2);
            middleName = m.group(3);
        } else {
            throw new InputMismatchException();
        }
    }

    /**
     * Gets persons full name separated by spaces
     * @return full name
     */
    public String getFullName() {
        StringBuilder fullName = new StringBuilder();
        fullName.append(lastName);
        if (firstName != null) {
            fullName.append(' ').append(firstName);
        }
        if (middleName != null) {
            fullName.append(' ').append(middleName);
        }
        return fullName.toString();
    }
}
