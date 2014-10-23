package hu.esgott.caronboard.gl.object;

import static org.junit.Assert.assertEquals;

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
    }

}
