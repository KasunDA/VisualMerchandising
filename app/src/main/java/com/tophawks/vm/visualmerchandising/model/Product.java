package com.tophawks.vm.visualmerchandising.model;

import java.io.Serializable;


public class Product implements Serializable{

    private String productId;
    private String storeId;
    private String productName, productColor, productSpecification, imageUrl, storeName;
    private float wholeSalePrice, retailPrice, originalPrice, discountPrice;
    private int productQuantity;
    private int productPopularity;
    private String category, brandName;
    public Product() {
    }

    public Product(String productId, String storeId, String productName, String storeName, String productColor, String productSpecification, String imageUrl, float wholeSalePrice, float retailPrice, float originalPrice, float discountPrice, int productQuantity, String category, String brandName, int productPopularity) {
        this.productId = productId;
        this.storeId = storeId;
        this.productPopularity = productPopularity;
        this.productName = productName;
        this.storeName = storeName;
        this.productColor = productColor;
        this.productSpecification = productSpecification;
        this.imageUrl = imageUrl;
        this.wholeSalePrice = wholeSalePrice;
        this.retailPrice = retailPrice;
        this.originalPrice = originalPrice;
        this.discountPrice = discountPrice;
        this.productQuantity = productQuantity;
        this.category = category;
        this.brandName = brandName;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public int getProductPopularity() {
        return productPopularity;
    }

    public void setProductPopularity(int productPopularity) {
        this.productPopularity = productPopularity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductColor() {
        return productColor;
    }

    public void setProductColor(String productColor) {
        this.productColor = productColor;
    }

    public String getProductSpecification() {
        return productSpecification;
    }

    public void setProductSpecification(String productSpecification) {
        this.productSpecification = productSpecification;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public float getWholeSalePrice() {
        return wholeSalePrice;
    }

    public void setWholeSalePrice(float wholeSalePrice) {
        this.wholeSalePrice = wholeSalePrice;
    }

    public float getRetailPrice() {
        return retailPrice;
    }

    public void setRetailPrice(float retailPrice) {
        this.retailPrice = retailPrice;
    }

    public float getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(float originalPrice) {
        this.originalPrice = originalPrice;
    }

    public float getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(float discountPrice) {
        this.discountPrice = discountPrice;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }


}
