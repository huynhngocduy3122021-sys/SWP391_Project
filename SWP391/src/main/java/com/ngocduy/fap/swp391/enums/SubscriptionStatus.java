package com.ngocduy.fap.swp391.enums;

public enum SubscriptionStatus {
    ACTIVE("Đang hoạt động"),
    EXPIRED("Đã hết hạn"),
    CANCELLED("Đã hủy");

    private final String vietnameseName;

    SubscriptionStatus(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }

    public String getVietnameseName() {
        return vietnameseName;
    }

    public boolean canUse() {
        return this == ACTIVE;
    }
}
