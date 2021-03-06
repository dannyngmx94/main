package seedu.address.model;

import java.util.function.Predicate;

import javafx.collections.ObservableList;
import seedu.address.model.pair.Pair;
import seedu.address.model.person.Person;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.person.exceptions.PersonNotFoundException;
import seedu.address.model.tag.Tag;

/**
 * The API of the Model component.
 */
public interface Model {
    /** {@code Predicate} that always evaluate to true */
    Predicate<Person> PREDICATE_SHOW_ALL_PERSONS = unused -> true;

    /** {@code Predicate} that always evaluate to true */
    Predicate<Pair> PREDICATE_SHOW_ALL_PAIRS = unused -> true;

    /** {@code Predicate} that filter all person with student tag */
    Predicate<Person> PREDICATE_SHOW_ALL_STUDENTS = person -> person.getTags().contains(new Tag("student"));

    /** {@code Predicate} that filter all person with student tag */
    Predicate<Person> PREDICATE_SHOW_ALL_TUTORS = person -> person.getTags().contains(new Tag("tutor"));

    /** Clears existing backing model and replaces with the provided new data. */
    void resetData(ReadOnlyAddressBook newData);

    /** Returns the AddressBook */
    ReadOnlyAddressBook getAddressBook();

    /** Deletes the given person. */
    void deletePerson(Person target) throws PersonNotFoundException;

    /** Adds the given person */
    void addPerson(Person person) throws DuplicatePersonException;

    /**
     * Replaces the given person {@code target} with {@code editedPerson}.
     *
     * @throws DuplicatePersonException if updating the person's details causes the person to be equivalent to
     *      another existing person in the list.
     * @throws PersonNotFoundException if {@code target} could not be found in the list.
     */
    void updatePerson(Person target, Person editedPerson)
            throws DuplicatePersonException, PersonNotFoundException;

    /** Returns an unmodifiable view of the filtered person list */
    ObservableList<Person> getFilteredPersonList();

    /**
     * Updates the filter of the filtered person list to filter by the given {@code predicate}.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredPersonList(Predicate<Person> predicate);


    /** Returns an unmodifiable view of the filtered pair list */
    ObservableList<Pair> getFilteredPairList();

    /**
     * Updates the filter of the filtered pair list to filter by the given {@code predicate}.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredPairList(Predicate<seedu.address.model.pair.Pair> predicate);

    /**
     * Delete a tag from the addressbook
     * @param tag
     * @throws PersonNotFoundException
     */
    void deleteTag (Tag tag)throws PersonNotFoundException;
}
