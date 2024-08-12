package ru.ibs.pmp.medicalcaserepairer.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author NAnishhenko
 */
public class VersionNumberBean implements Serializable {

    private Map<Long, Boolean> versionNumberToDoneAttribute;
    private transient List<Long> unfinishedVersionNumberList;

    public VersionNumberBean() {
    }

    public VersionNumberBean(Set<Long> versionNumberList, VersionNumberBean versionNumberBean) {
        if (versionNumberBean == null) {
            versionNumberToDoneAttribute = versionNumberList.stream().collect(Collectors.toMap(el -> el, f -> false));
        } else {
            List<Long> newElements = versionNumberList.stream().filter(el -> !versionNumberBean.containsVersionNumber(el)).collect(Collectors.toList());
            versionNumberToDoneAttribute = new HashMap<>(newElements.size() + versionNumberBean.getVersionNumberToDoneAttributeSize());
            versionNumberToDoneAttribute.putAll(newElements.stream().collect(Collectors.toMap(el -> el, f -> false)));
            versionNumberToDoneAttribute.putAll(versionNumberBean.getVersionNumberToDoneAttribute());
        }
        unfinishedVersionNumberList = versionNumberToDoneAttribute.entrySet().stream().filter((Entry<Long, Boolean> entry) -> !entry.getValue()).map((Entry<Long, Boolean> entry) -> entry.getKey()).collect(Collectors.toList());
    }

    public boolean containsVersionNumber(Long versionNumber) {
        return versionNumberToDoneAttribute.containsKey(versionNumber);
    }

    public List<Long> getUnfinishedVersions() {
        return unfinishedVersionNumberList;
    }

    public void setFinished(Long versionNumber) {
        versionNumberToDoneAttribute.put(versionNumber, Boolean.TRUE);
    }

    public int getVersionNumberToDoneAttributeSize() {
        return versionNumberToDoneAttribute.size();
    }

    public Map<Long, Boolean> getVersionNumberToDoneAttribute() {
        return new HashMap<>(versionNumberToDoneAttribute);
    }

}
