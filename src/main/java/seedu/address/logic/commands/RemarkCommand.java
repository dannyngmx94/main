package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REMARK;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Level;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Price;
import seedu.address.model.person.Remark;
import seedu.address.model.person.Role;
import seedu.address.model.person.Status;
import seedu.address.model.person.Subject;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.person.exceptions.PersonNotFoundException;
import seedu.address.model.tag.Tag;

/**
 * Adds a remark to person to the address book.
 */
public class RemarkCommand extends UndoableCommand {

    public static final String COMMAND_WORD = "remark";
    public static final String COMMAND_WORD_ALIAS = "rm";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Adds a remark to person identified by the index number used in the last person listing. "
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1 " + PREFIX_REMARK;

    public static final String MESSAGE_REMARK_PERSON_SUCCESS = "Added Remark to %1$s: " + "%2$s";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the address book.";

    private final Index targetIndex;
    private Remark newRemark;

    private Person personToEdit;
    private Person editedPerson;

    public RemarkCommand(Index targetIndex, Remark newRemark) {
        requireNonNull(targetIndex);
        requireNonNull(newRemark);

        this.targetIndex = targetIndex;
        this.newRemark = newRemark;
    }

    @Override
    public CommandResult executeUndoableCommand() throws CommandException {
        try {
            model.updatePerson(personToEdit, editedPerson);
        } catch (DuplicatePersonException dpe) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        } catch (PersonNotFoundException pnfe) {
            throw new AssertionError("The target person cannot be missing");
        }
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_REMARK_PERSON_SUCCESS,
                                editedPerson.getName(), editedPerson.getRemark()));
    }

    @Override
    protected void preprocessUndoableCommand() throws CommandException {
        List<Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        personToEdit = lastShownList.get(targetIndex.getZeroBased());
        editedPerson = createPersonWithNewRemark(personToEdit, newRemark);
    }

    /**
     * Creates and returns a {@code Person} with the details of {@code personToEdit}.
     */
    private static Person createPersonWithNewRemark(Person personToEdit, Remark newRemark) {
        assert personToEdit != null;

        Name name = personToEdit.getName();
        Phone phone = personToEdit.getPhone();
        Email email = personToEdit.getEmail();
        Address address = personToEdit.getAddress();
        Price price = personToEdit.getPrice();
        Subject subject = personToEdit.getSubject();
        Level level = personToEdit.getLevel();
        Status status = personToEdit.getStatus();
        Role role = personToEdit.getRole();

        Set<Tag> updatedTags = personToEdit.getTags();

        //create a new modifiable set of tags
        Set<Tag> attributeTags = new HashSet<>(updatedTags);

        attributeTags.add(new Tag(personToEdit.getPrice().toString(), Tag.AllTagTypes.PRICE));
        attributeTags.add(new Tag(personToEdit.getLevel().toString(), Tag.AllTagTypes.LEVEL));
        attributeTags.add(new Tag(personToEdit.getSubject().toString(), Tag.AllTagTypes.SUBJECT));
        attributeTags.add(new Tag(personToEdit.getStatus().toString(), Tag.AllTagTypes.STATUS));
        attributeTags.add(new Tag(personToEdit.getRole().toString(), Tag.AllTagTypes.ROLE));

        return new Person(name, phone, email, address, price, subject, level, status, role, attributeTags, newRemark);
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof RemarkCommand // instanceof handles nulls
                && this.targetIndex.equals(((RemarkCommand) other).targetIndex)
                && this.newRemark.equals(((RemarkCommand) other).newRemark));
    }
}
