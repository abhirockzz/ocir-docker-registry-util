
package com.oracle.ocir.util.pojo;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Repos {

    @SerializedName("revision")
    @Expose
    private Integer revision;
    @SerializedName("repos")
    @Expose
    private List<Repo> repos = null;
    @SerializedName("numImages")
    @Expose
    private Integer numImages;
    @SerializedName("numLayers")
    @Expose
    private Integer numLayers;
    @SerializedName("layerStorageBytes")
    @Expose
    private Integer layerStorageBytes;

    public Integer getRevision() {
        return revision;
    }

    public void setRevision(Integer revision) {
        this.revision = revision;
    }

    public List<Repo> getRepos() {
        return repos;
    }

    public void setRepos(List<Repo> repos) {
        this.repos = repos;
    }

    public Integer getNumImages() {
        return numImages;
    }

    public void setNumImages(Integer numImages) {
        this.numImages = numImages;
    }

    public Integer getNumLayers() {
        return numLayers;
    }

    public void setNumLayers(Integer numLayers) {
        this.numLayers = numLayers;
    }

    public Integer getLayerStorageBytes() {
        return layerStorageBytes;
    }

    public void setLayerStorageBytes(Integer layerStorageBytes) {
        this.layerStorageBytes = layerStorageBytes;
    }

}
