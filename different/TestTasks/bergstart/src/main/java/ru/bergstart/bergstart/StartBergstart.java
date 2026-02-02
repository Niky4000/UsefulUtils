package ru.bergstart.bergstart;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import ru.bergstart.bergstart.bean.ItemBean;
import ru.bergstart.bergstart.bean.SearchBean;

public class StartBergstart {

    // Arguments example: -path /tmp/some.json -url https://raw.githubusercontent.com/bergstar/testcase/refs/heads/master/okved.json -phone +79268814141
    public static void main(String[] args) throws Exception {
        List<String> argList = Stream.of(args).collect(Collectors.toList());
        String filePath = getArg("-path", argList);
        String urlToDownloadFrom = getArg("-url", argList);
        String phoneNumber = getArg("-phone", argList);
        PhoneValidator phoneValidator = new PhoneValidator(phoneNumber);
        if (phoneValidator.isValid()) {
            File file = new File(filePath);
            if (!file.exists()) {
                new FileUtils().downloadFile(urlToDownloadFrom, file);
            }
            List<ItemBean> items = new ItemParser().parseItems(file);
            SearchTree searchTree = new SearchTree();
            searchTree.buildTree(items);
            SearchBean result = searchTree.search(phoneValidator.getPhone());
            System.out.println("Normalized phone number: " + phoneValidator.getPhone());
            if (result.getResult() != null) {
                System.out.println("Item code: " + result.getResult().getCode());
                System.out.println("Item name: " + result.getResult().getName());
                System.out.println("Coincidence length: " + result.getKey().length());
            } else {
                System.out.println("Nothing was found!");
            }
        } else {
            throw new PhoneInvalidException(phoneNumber);
        }
    }

    public static String getArg(String arg, List<String> argList) {
        int indexOf = argList.indexOf(arg);
        if (indexOf >= 0) {
            return argList.get(indexOf + 1);
        } else {
            return null;
        }
    }
}
