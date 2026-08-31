package com.food.ordering.system.order.services.domain.valueobject;

import java.util.UUID;

public class StreetAddress {

    private final UUID id;
    private final String street;
    private final String postalCode;
    private final String city;

    public StreetAddress(UUID id, String street, String postalCode, String city) {
        this.id = id;
        this.street = street;
        this.postalCode = postalCode;
        this.city = city;
    }

    private StreetAddress(Builder builder) {
        id = builder.id;
        street = builder.street;
        postalCode = builder.postalCode;
        city = builder.city;
    }


    public UUID getId() {
        return id;
    }

    public String getStreet() {
        return street;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCity() {
        return city;
    }
    public static Builder builder() {
        return new Builder();
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof StreetAddress that)) return false;

        return getStreet().equals(that.getStreet()) && getPostalCode().equals(that.getPostalCode()) && getCity().equals(that.getCity());
    }

    @Override
    public int hashCode() {
        int result = getStreet().hashCode();
        result = 31 * result + getPostalCode().hashCode();
        result = 31 * result + getCity().hashCode();
        return result;
    }

    public static final class Builder {
        private UUID id;
        private String street;
        private String postalCode;
        private String city;

        private Builder() {
        }



        public Builder id(UUID val) {
            id = val;
            return this;
        }

        public Builder street(String val) {
            street = val;
            return this;
        }

        public Builder postalCode(String val) {
            postalCode = val;
            return this;
        }

        public Builder city(String val) {
            city = val;
            return this;
        }

        public StreetAddress build() {
            return new StreetAddress(this);
        }
    }
}
