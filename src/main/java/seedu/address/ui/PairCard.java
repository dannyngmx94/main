package seedu.address.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import seedu.address.model.pair.Pair;
import seedu.address.model.tag.Tag;

/**
 * An UI component that displays information of a {@code Pair}.
 */
public class PairCard extends UiPart<Region> {

    public static final String[] TAG_COLOR_STYLES =
        {"teal", "red", "yellow", "blue", "orange", "brown", "green", "pink", "black", "grey"};

    private static final String FXML = "PairListCard.fxml";


    /**
     * Note: Certain keywords such as "location" and "resources" are reserved keywords in JavaFX.
     * As a consequence, UI elements' variable names cannot be set to such keywords
     * or an exception will be thrown by JavaFX during runtime.
     *
     * @see <a href="https://github.com/se-edu/addressbook-level4/issues/336">The issue on AddressBook level 4</a>
     */

    public final Pair pair;

    @FXML
    private HBox cardPane;
    @FXML
    private Label name;
    @FXML
    private Label id;
    @FXML
    private FlowPane tags;

    public PairCard(Pair pair, int displayedIndex) {
        super(FXML);
        this.pair = pair;
        id.setText(displayedIndex + ". ");
        name.setText(pair.getPairName());
        initTags(pair);
    }

    /**
     * Returns the color style for {@code tagName}'s label.
     */
    private String getTagColorStyleFor(String tagName) {
        // use hash code of tag name to generate a random color
        return TAG_COLOR_STYLES[Math.abs(tagName.hashCode()) % TAG_COLOR_STYLES.length];
    }

    /**
     * Returns the color style for {@code tag}.
     * Uses the tagType value inside the Tag to determine the colour.
     */
    private String getTagColorStyleFor(Tag tag) {
        switch (tag.tagType) {
        case SUBJECT:
            return TAG_COLOR_STYLES[0]; //subject is teal
        case LEVEL:
            return TAG_COLOR_STYLES[1]; //level is red
        case STATUS:
            return TAG_COLOR_STYLES[2]; //status is yellow
        case PRICE:
            return TAG_COLOR_STYLES[3]; //price is blue
            //fall through to default
        default:
            return TAG_COLOR_STYLES[8]; //all non-attribute are black
        }
    }

    /**
     * Creates the tag labels for {@code pair}.
     */
    private void initTags(Pair pair) {
        pair.getTags().forEach(tag -> {
            Label tagLabel = new Label(tag.tagName);
            tagLabel.getStyleClass().add(getTagColorStyleFor(tag));
            tags.getChildren().add(tagLabel);
        });
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof PairCard)) {
            return false;
        }

        // state check
        PairCard card = (PairCard) other;
        return id.getText().equals(card.id.getText())
                && pair.equals(card.pair);
    }
}
