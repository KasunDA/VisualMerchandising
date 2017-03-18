package com.tophawks.vm.visualmerchandising.model;

import java.io.Serializable;

/**
 * Created by Sanidhya on 18-Mar-17.
 */

public class Store implements Serializable {

    private String storeId;
    private String name;
    private String owner;
    private String shopAddress;
    private String godownAddress;
    private long capacity;
    private long spaceAvailable;
    private String storePic;
    private String stateAddress;
    private String cityAddress;

    public Store() {
    }

    public Store(String storeId, String name, String owner, String shopAddress, String godownAddress, long capacity, long spaceAvailable, String storePic, String stateAddress, String cityAddress) {
        this.storeId = storeId;
        this.name = name;
        this.owner = owner;
        this.shopAddress = shopAddress;
        this.godownAddress = godownAddress;
        this.capacity = capacity;
        this.spaceAvailable = spaceAvailable;
        this.storePic = storePic;
        this.stateAddress = stateAddress;
        this.cityAddress = cityAddress;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getShopAddress() {
        return shopAddress;
    }

    public void setShopAddress(String shopAddress) {
        this.shopAddress = shopAddress;
    }

    public String getGodownAddress() {
        return godownAddress;
    }

    public void setGodownAddress(String godownAddress) {
        this.godownAddress = godownAddress;
    }

    public long getCapacity() {
        return capacity;
    }

    public void setCapacity(long capacity) {
        this.capacity = capacity;
    }

    public long getSpaceAvailable() {
        return spaceAvailable;
    }

    public void setSpaceAvailable(long spaceAvailable) {
        this.spaceAvailable = spaceAvailable;
    }

    public String getStorePic() {
        return storePic;
    }

    public void setStorePic(String storePic) {
        this.storePic = storePic;
    }

    public String getStateAddress() {
        return stateAddress;
    }

    public void setStateAddress(String stateAddress) {
        this.stateAddress = stateAddress;
    }

    public String getCityAddress() {
        return cityAddress;
    }

    public void setCityAddress(String cityAddress) {
        this.cityAddress = cityAddress;
    }
}
