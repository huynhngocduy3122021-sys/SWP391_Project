package com.ngocduy.fap.swp391.enums;

public enum OrderStatus {
    PENDING("Đang chờ xử lý"),
    PAID("Đã thanh toán"),
    CONFIRMED("Đã xác nhận"),
    COMPLETED("Hoàn thành"),
    CANCELLED("Đã hủy");

    private final String vietnameseName;

    OrderStatus(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }

    public String getVietnameseName() {
        return vietnameseName;
    }
}
