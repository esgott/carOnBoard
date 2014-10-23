package hu.esgott.caronboard.gl.object;

import java.util.ArrayList;
import java.util.List;

public class ItemHandler {

    private static final Text EMTPY_ITEM = new Text("", 0);

    private final List<Text> menuItems = new ArrayList<>();
    private final List<Text> renderedItems = new ArrayList<>(5);
    private final int size;
    private int currentItem = 0;

    public ItemHandler(final int fontSize) {
        size = fontSize;
        for (int i = 0; i < 5; i++) {
            renderedItems.add(EMTPY_ITEM);
        }
    }

    public void addItem(String text) {
        menuItems.add(new Text(text, size));
        refreshRenderedList();
    }

    private void refreshRenderedList() {
        for (int i = 0; i < 5; i++) {
            renderedItems.set(i, getRelative(i - 2));
        }
    }

    private Text getRelative(int offset) {
        try {
            return menuItems.get(currentItem + offset);
        } catch (IndexOutOfBoundsException e) {
            return EMTPY_ITEM;
        }
    }

    public void clearItems() {
        menuItems.clear();
        currentItem = 0;
        refreshRenderedList();
    }

    public List<Text> getRenderedItems() {
        return renderedItems;
    }

    public Text next() {
        if (currentItem < menuItems.size() - 1) {
            currentItem++;
            refreshRenderedList();
        }
        return renderedItems.get(renderedItems.size() - 1);
    }

    public Text prev() {
        if (currentItem > 0) {
            currentItem--;
            refreshRenderedList();
        }
        return renderedItems.get(0);
    }
}