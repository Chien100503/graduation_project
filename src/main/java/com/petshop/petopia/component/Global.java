package com.petshop.petopia.component;

public class Global {
    public enum ItemType {
        PET,
        PRODUCT
    }

    public enum OrderStatus {
        PENDING,
        CONFIRMED,
        CANCELLED,
        EXPIRED,
        FAILED
    }

    public enum PaymentMethod {
        PAYOS,
        COD
    }
}
