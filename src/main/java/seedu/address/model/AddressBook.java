package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.collections.ObservableList;
import seedu.address.model.pair.Pair;
import seedu.address.model.pair.UniquePairList;
import seedu.address.model.pair.exceptions.DuplicatePairException;
import seedu.address.model.pair.exceptions.PairNotFoundException;
import seedu.address.model.person.Person;
import seedu.address.model.person.UniquePersonList;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.person.exceptions.PersonNotFoundException;
import seedu.address.model.tag.Tag;
import seedu.address.model.tag.UniqueTagList;

/**
 * Wraps all data at the address-book level
 * Duplicates are not allowed (by .equals comparison)
 */
public class AddressBook implements ReadOnlyAddressBook {

    private final UniquePersonList persons;
    private final UniquePairList pairs;
    private final UniqueTagList tags;


    /*
     * The 'unusual' code block below is an non-static initialization block, sometimes used to avoid duplication
     * between constructors. See https://docs.oracle.com/javase/tutorial/java/javaOO/initial.html
     *
     * Note that non-static init blocks are not recommended to use. There are other ways to avoid duplication
     *   among constructors.
     */
    {
        persons = new UniquePersonList();
        pairs = new UniquePairList();
        tags = new UniqueTagList();
    }

    public AddressBook() {}

    /**
     * Creates an AddressBook using the Persons and Tags in the {@code toBeCopied}
     */
    public AddressBook(ReadOnlyAddressBook toBeCopied) {
        this();
        resetData(toBeCopied);
    }


    //// list overwrite operations

    public void setPersons(List<Person> persons) throws DuplicatePersonException {
        this.persons.setPersons(persons);
    }

    public void setPairs(List<Pair> pairs) throws DuplicatePairException {
        this.pairs.setPairs(pairs);
    }

    public void setTags(Set<Tag> tags) {
        this.tags.setTags(tags);
    }

    /**
     * Resets the existing data of this {@code AddressBook} with {@code newData}.
     */
    public void resetData(ReadOnlyAddressBook newData) {
        requireNonNull(newData);
        setTags(new HashSet<>(newData.getTagList()));
        List<Person> syncedPersonList = newData.getPersonList().stream()
                .map(this::syncWithMasterTagList)
                .collect(Collectors.toList());

        try {
            setPersons(syncedPersonList);
        } catch (DuplicatePersonException e) {
            throw new AssertionError("AddressBooks should not have duplicate persons");
        }

        List<Pair> syncedPairList = newData.getPairList().stream()
                .map(this::syncWithMasterTagList)
                .collect(Collectors.toList());

        try {
            setPairs(syncedPairList);
        } catch (DuplicatePairException e) {
            throw new AssertionError("AddressBooks should not have duplicate pairs");
        }
    }

    //// person-level operations

    /**
     * Adds a person to the address book.
     * Also checks the new person's tags and updates {@link #tags} with any new tags found,
     * and updates the Tag objects in the person to point to those in {@link #tags}.
     *
     * @throws DuplicatePersonException if an equivalent person already exists.
     */
    public void addPerson(Person p) throws DuplicatePersonException {
        Person person = syncWithMasterTagList(p);
        // TODO: the tags master list will be updated even though the below line fails.
        // This can cause the tags master list to have additional tags that are not tagged to any person
        // in the person list.
        persons.add(person);
    }

    /**
     * Replaces the given person {@code target} in the list with {@code editedPerson}.
     * {@code AddressBook}'s tag list will be updated with the tags of {@code editedPerson}.
     *
     * @throws DuplicatePersonException if updating the person's details causes the person to be equivalent to
     *      another existing person in the list.
     * @throws PersonNotFoundException if {@code target} could not be found in the list.
     *
     * @see #syncWithMasterTagList(Person)
     */
    public void updatePerson(Person target, Person editedPerson)
            throws DuplicatePersonException, PersonNotFoundException {
        requireNonNull(editedPerson);

        Person syncedEditedPerson = syncWithMasterTagList(editedPerson);
        // TODO: the tags master list will be updated even though the below line fails.
        // This can cause the tags master list to have additional tags that are not tagged to any person
        // in the person list.
        persons.setPerson(target, syncedEditedPerson);
        removeUnusedTags();
    }

    /**
     *  Updates the master tag list to include tags in {@code person} that are not in the list.
     *  @return a copy of this {@code person} such that every tag in this person points to a Tag object in the master
     *  list.
     */
    private Person syncWithMasterTagList(Person person) {
        final UniqueTagList personTags = new UniqueTagList(person.getTags());
        tags.mergeFrom(personTags);

        // Create map with values = tag object references in the master list
        // used for checking person tag references
        final Map<Tag, Tag> masterTagObjects = new HashMap<>();
        tags.forEach(tag -> masterTagObjects.put(tag, tag));

        // Rebuild the list of person tags to point to the relevant tags in the master tag list.
        final Set<Tag> correctTagReferences = new HashSet<>();
        personTags.forEach(tag -> correctTagReferences.add(masterTagObjects.get(tag)));
        return new Person(
                person.getName(), person.getPhone(), person.getEmail(), person.getAddress(),
                person.getPrice(), person.getSubject(), person.getLevel(), person.getStatus(), person.getRole(),
                correctTagReferences, person.getRemark());
    }

    /**
     *  Updates the master tag list to include tags in {@code pair} that are not in the list.
     *  @return a copy of this {@code pair} such that every tag in this pair points to a Tag object in the master
     *  list.
     */
    private Pair syncWithMasterTagList(Pair pair) {
        final UniqueTagList pairTags = new UniqueTagList(pair.getTags());
        tags.mergeFrom(pairTags);

        // Create map with values = tag object references in the master list
        // used for checking pair tag references
        final Map<Tag, Tag> masterTagObjects = new HashMap<>();
        tags.forEach(tag -> masterTagObjects.put(tag, tag));

        // Rebuild the list of pair tags to point to the relevant tags in the master tag list.
        final Set<Tag> correctTagReferences = new HashSet<>();
        pairTags.forEach(tag -> correctTagReferences.add(masterTagObjects.get(tag)));
        return new Pair(
                pair.getStudentName(), pair.getTutorName(), pair.getSubject(), pair.getLevel(),
                pair.getPrice(), correctTagReferences);
    }


    /**
     * Removes {@code key} from this {@code AddressBook}.
     * @throws PersonNotFoundException if the {@code key} is not in this {@code AddressBook}.
     */
    public boolean removePerson(Person key) throws PersonNotFoundException {
        if (persons.remove(key)) {
            return true;
        } else {
            throw new PersonNotFoundException();
        }
    }


    //// pair-level operations

    /**
     * Adds a pair to the address book.
     * Also checks the new pair's tags and updates {@link #tags} with any new tags found,
     * and updates the Tag objects in the pair to point to those in {@link #tags}.
     *
     * @throws seedu.address.model.pair.exceptions.DuplicatePairException if an equivalent pair already exists.
     */

    public void addPair(Pair p) throws DuplicatePairException {
        //  Pair pair = syncWithMasterTagList(p);
        // TODO: the tags master list will be updated even though the below line fails.
        // This can cause the tags master list to have additional tags that are not tagged to any pair
        // in the pair list.
        pairs.add(p);
    }

    /**
     * Replaces the given pair {@code target} in the list with {@code editedPair}.
     * {@code AddressBook}'s tag list will be updated with the tags of {@code editedPair}.
     *
     * @throws seedu.address.model.pair.exceptions.DuplicatePairException if updating the pair's details
     * causes the pair to be equivalent to another existing pair in the list.
     * @throws seedu.address.model.pair.exceptions.PairNotFoundException if {@code target} could not be found
     * in the list.
     *
     * @see #syncWithMasterTagList(Pair)
     */
    public void updatePair(Pair target, Pair editedPair)
            throws DuplicatePairException, PairNotFoundException {
        requireNonNull(editedPair);

        Pair syncedEditedPair = syncWithMasterTagList(editedPair);
        // TODO: the tags master list will be updated even though the below line fails.
        // This can cause the tags master list to have additional tags that are not tagged to any pair
        // in the pair list.
        pairs.setPair(target, syncedEditedPair);
        removeUnusedTags();
    }



    /**
     * Removes {@code key} from this {@code AddressBook}.
     * @throws seedu.address.model.pair.exceptions.PairNotFoundException if the {@code key} is not in this
     * {@code AddressBook}.gr
     */
    /*
    public boolean removePair(Pair key) throws PairNotFoundException {
        if (pairs.remove(key)) {
            return true;
        } else {
            throw new PairNotFoundException();
        }
    }
    */

    //// tag-level operations

    public void addTag(Tag t) throws UniqueTagList.DuplicateTagException {
        tags.add(t);
    }

    //// util methods

    @Override
    public String toString() {
        return persons.asObservableList().size() + " persons, " + tags.asObservableList().size() +  " tags";
        // TODO: refine later
    }

    @Override
    public ObservableList<Person> getPersonList() {
        return persons.asObservableList();
    }

    @Override
    public ObservableList<seedu.address.model.pair.Pair> getPairList() {
        return pairs.asObservableList();
    }

    @Override
    public ObservableList<Tag> getTagList() {
        return tags.asObservableList();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof AddressBook // instanceof handles nulls
                && this.persons.equals(((AddressBook) other).persons)
                && this.tags.equalsOrderInsensitive(((AddressBook) other).tags));
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(persons, tags);
    }

    /**
     * Removes {@code tag} from this {@code AddressBook}.
     * @throws PersonNotFoundException if the {@code key} is not in this {@code AddressBook}.
     */

    public void removeTag(Tag tag) throws PersonNotFoundException {
        for (Person person : persons) {
            removeTagFromPerson(tag, person);
        }
    }

    /**
     *
     * Removes {@code tag} from {@code person} in this {@code AddressBook}.
     * @throws PersonNotFoundException if the {@code person} is not in this {@code AddressBook}.
     * @@author yamgent
     * Reused from https://github.com/se-edu/
     * addressbook-level4/pull/790/commits/48ba8e95de5d7eae883504d40e6795c857dae3c2
     */
    private void removeTagFromPerson(Tag tag, Person person) throws PersonNotFoundException {
        Set<Tag> updatedTags = new HashSet<>(person.getTags());
        if (!updatedTags.remove(tag)) {
            return;
        }
        Person updatedPerson = new Person (person.getName(), person.getPhone(),
                person.getEmail(), person.getAddress(), person.getPrice(),
               person.getSubject(), person.getLevel(), person.getStatus(), person.getRole(),
                updatedTags, person.getRemark());
        try {
            updatePerson(person, updatedPerson);
        } catch (DuplicatePersonException dupe) {
            throw new AssertionError("Modifying a person's tags only should not result in a duplicate. "
                     + "See Person#equals(Object).");
        }
    }

    /**
     *
     * Removes unsed {@code tag} from this {@code AddressBook}.
     * @@author yamgent
     * Reused from https://github.com/se-edu/
     * addressbook-level4/pull/790/commits/48ba8e95de5d7eae883504d40e6795c857dae3c2
     */
    private void removeUnusedTags() {
        Set<Tag> tagsInPersons = persons.asObservableList().stream()
                           .map(Person::getTags)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
        tags.setTags(tagsInPersons);
    }
}
