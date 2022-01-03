package com.emedicoz.app.referralcode.model;




import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class RefData {

    @Expose
    private String address;
    @Expose
    private String city;
    @Expose
    private String country;
    @SerializedName("created_at")
    private Object createdAt;
    @Expose
    private String email;
    @SerializedName("first_name")
    private String firstName;
    @Expose
    private String id;
    @SerializedName("last_name")
    private String lastName;
    @Expose
    private String phone;
    @SerializedName("postal_code")
    private String postalCode;
    @SerializedName("profile_image")
    private Object profileImage;
    @SerializedName("referral_by")
    private String referralBy;
    @SerializedName("referral_code")
    private String referralCode;
    @Expose
    private String state;
    @Expose
    private String status;
    @SerializedName("updated_at")
    private Object updatedAt;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Object getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Object createdAt) {
        this.createdAt = createdAt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public Object getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(Object profileImage) {
        this.profileImage = profileImage;
    }

    public String getReferralBy() {
        return referralBy;
    }

    public void setReferralBy(String referralBy) {
        this.referralBy = referralBy;
    }

    public String getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Object updatedAt) {
        this.updatedAt = updatedAt;
    }

}
