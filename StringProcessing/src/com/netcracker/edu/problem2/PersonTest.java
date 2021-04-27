package com.netcracker.edu.problem2;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.InputMismatchException;

import static org.junit.jupiter.api.Assertions.*;

class PersonTest {
    private static final String name1 = "Lifantev Danil Aleksandrovich";
    private static final String name2 = "Lifantev Danil";
    private static final String name3 = "acksv b2397r2";

    @Test
    void PersonSetGetTest() {
        Person person1 = new Person(name1);
        assertEquals(name1, person1.getFullName());

        Person person2 = new Person(name2);
        assertEquals(name2, person2.getFullName());
    }

    @Test
    void PersonSetException() {
        Exception exception = assertThrows(InputMismatchException.class, () -> new Person(name3));
    }
}