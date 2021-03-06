package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.AddCommand.MESSAGE_USAGE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LEVEL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PRICE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ROLE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STATUS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SUBJECT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.Set;
import java.util.stream.Stream;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.parser.exceptions.ParseException;
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
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new AddCommand object
 */
public class AddCommandParser implements Parser<AddCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the AddCommand
     * and returns an AddCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public AddCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS,
                        PREFIX_PRICE, PREFIX_SUBJECT, PREFIX_LEVEL, PREFIX_STATUS, PREFIX_ROLE, PREFIX_TAG);

        if (!arePrefixesPresent(argMultimap, PREFIX_NAME)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT + MESSAGE_USAGE, MESSAGE_USAGE));
        }

        try {
            Name name = ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME)).get();
            Phone phone = ParserUtil.parsePhone(argMultimap.getValue(PREFIX_PHONE)).orElse(new Phone(""));
            Email email = ParserUtil.parseEmail(argMultimap.getValue(PREFIX_EMAIL)).orElse(new Email(""));
            Address address = ParserUtil.parseAddress(argMultimap.getValue(PREFIX_ADDRESS)).orElse(new Address(""));
            Price price = ParserUtil.parsePrice(argMultimap.getValue(PREFIX_PRICE)).orElse(new Price(""));
            Subject subject = ParserUtil.parseSubject(argMultimap.getValue(PREFIX_SUBJECT)).orElse(new Subject(""));
            Level level = ParserUtil.parseLevel(argMultimap.getValue(PREFIX_LEVEL)).orElse(new Level(""));
            Status status = ParserUtil.parseStatus(argMultimap.getValue(PREFIX_STATUS)).orElse(new Status(""));
            Role role = ParserUtil.parseRole(argMultimap.getValue(PREFIX_ROLE)).orElse(new Role(""));
            Set<Tag> tagList = ParserUtil.parseTags(argMultimap.getAllValues(PREFIX_TAG));

            //Add required attributes to the tag list as in documentation
            //make tags only if the attribute has been entered by user
            if (!price.toString().equals("")) {
                tagList.add(new Tag(price.toString(), Tag.AllTagTypes.PRICE));
            }
            if (!subject.toString().equals("")) {
                tagList.add(new Tag(subject.toString(), Tag.AllTagTypes.SUBJECT));
            }
            if (!level.toString().equals("")) {
                tagList.add(new Tag(level.toString(), Tag.AllTagTypes.LEVEL));
            }
            if (!status.toString().equals("")) {
                tagList.add(new Tag(status.toString(), Tag.AllTagTypes.STATUS));
            }
            if (!role.toString().equals("")) {
                tagList.add(new Tag(role.toString(), Tag.AllTagTypes.ROLE));
            }

            Remark remark = new Remark("");  // default remark is empty string for newly added Person

            Person person = new Person(name, phone, email, address, price, subject, level,
                    status, role, tagList, remark);
            return new AddCommand(person);
        } catch (IllegalValueException ive) {
            throw new ParseException(ive.getMessage(), ive);
        }
    }

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

}
