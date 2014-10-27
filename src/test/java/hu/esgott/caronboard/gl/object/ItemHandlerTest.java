package hu.esgott.caronboard.gl.object;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class ItemHandlerTest {

    ItemHandler itemHandler;

    @Before
    public void init() {
        itemHandler = new ItemHandler(0);
        itemHandler.addItem("1");
        itemHandler.addItem("2");
        itemHandler.addItem("3");
        itemHandler.addItem("4");
        itemHandler.addItem("5");
        itemHandler.addItem("6");
    }

    @Test
    public void previous_from_first_element_does_not_change_renderedList() {
        List<Text> beforeList = itemHandler.getRenderedItems();
        itemHandler.prev();
        List<Text> afterList = itemHandler.getRenderedItems();

        assertEquals(beforeList, afterList);
        assertListContains(afterList, "", "", "1", "2", "3");
    }

    private void assertListContains(List<Text> list, String... texts) {
        assertEquals(list.size(), texts.length);
        for (int i = 0; i < list.size(); i++) {
            if (!list.get(i).getName().equals(texts[i])) {
                fail("items at position " + i + "differs: actual<"
                        + list.get(i).getName() + "> expected <" + texts[i]
                        + ">");
            }
        }
    }

    @Test
    public void next_from_last_element_does_not_change_list() {
        itemHandler.next();
        itemHandler.next();
        itemHandler.next();
        itemHandler.next();
        itemHandler.next();

        List<Text> beforeList = itemHandler.getRenderedItems();
        itemHandler.next();
        List<Text> afterList = itemHandler.getRenderedItems();

        assertEquals(beforeList, afterList);
        assertListContains(afterList, "4", "5", "6", "", "");
    }

    @Test
    public void full_renderedList_is_correct() {
        itemHandler.next();
        itemHandler.next();
        itemHandler.next();

        List<Text> renderedList = itemHandler.getRenderedItems();
        assertListContains(renderedList, "2", "3", "4", "5", "6");
    }

    @Test
    public void clearItems_clears_list() {
        itemHandler.clearItems();
        assertListContains(itemHandler.getRenderedItems(), "", "", "", "", "");
    }

}
