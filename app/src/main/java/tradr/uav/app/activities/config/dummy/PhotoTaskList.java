package tradr.uav.app.activities.config.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class PhotoTaskList {


    /**
     * An array of sample (dummy) items.
     */
    public List<Item> itemList = new ArrayList<Item>();


    /**
     * A map of sample (dummy) items, by ID.
     */
    public Map<String, Item> itemMap = new HashMap<String, Item>();

    private static final int COUNT = 25;

    public PhotoTaskList() {
        itemList = new ArrayList<Item>();
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createDummyItem(i));
        }
    }

    private void addItem(Item item) {
        itemList.add(item);
        itemMap.put(item.id, item);
    }

    private Item createDummyItem(int position) {
        return new Item(String.valueOf(position), "Item " + position, makeDetails(position));
    }

    private String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class Item {
        public final String id;
        public final String content;
        public final String details;

        public Item(String id, String content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
