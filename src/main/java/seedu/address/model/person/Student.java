package seedu.address.model.person;

import java.util.Set;

import seedu.address.model.tag.Tag;

/**
 * Represents a Student in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */

public class Student extends Person {


    /**
     * Every field must be present and not null.
     *
     * @param name
     * @param phone
     * @param email
     * @param address
     * @param price
     * @param subject
     * @param level
     * @param status
     * @param remark
     * @param tags
     */
    public Student(Name name, Phone phone, Email email, Address address,
                   Price price, Subject subject, Level level, Status status, Set<Tag> tags, Remark remark) {
        super(name, phone, email, address, price, subject, level, status, new Role("student"), tags, remark);
    }
}
