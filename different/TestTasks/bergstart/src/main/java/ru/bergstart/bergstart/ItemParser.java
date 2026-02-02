package ru.bergstart.bergstart;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.List;
import ru.bergstart.bergstart.bean.ItemBean;

public class ItemParser {

    public List<ItemBean> parseItems(File file) throws IOException {
        return new ObjectMapper().readValue(file, new TypeReference<List<ItemBean>>() {
        });
    }
}
