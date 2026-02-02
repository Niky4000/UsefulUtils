package ru.bergstart.bergstart;

import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import ru.bergstart.bergstart.bean.ItemBean;
import ru.bergstart.bergstart.bean.SearchBean;

public class SearchTreeTest {

    @Test
    public void testBuildTree() {
        List<ItemBean> items = Arrays.asList(
                new ItemBean("1", "root",
                        Arrays.asList(new ItemBean("1.2.3", "text"),
                                new ItemBean("1.2.4", "text2" //, Arrays.asList(new ItemBean("1.2.5", "text3"))
                                )))
        );
        SearchTree searchTree = new SearchTree();
        searchTree.buildTree(items);
        SearchBean search = searchTree.search("40124");
        Assert.assertTrue(search.getResult().getName().equals("text2") && search.getKey().equals("124"));
    }
}
