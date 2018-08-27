
package com.oracle.ocir.util.pojo;

import java.util.List;

public class RepoTagsList {
    private String name;
    private List<String> tags;

    public RepoTagsList() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
    
    
}
