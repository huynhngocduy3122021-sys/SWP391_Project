package com.ngocduy.fap.swp391.enums;

public enum PaymentStatus {
    INITIATED("Đã khởi tạo"),
    PENDING ("Chờ thanh toán"),
    PAID("Đã thanh toán"),
    FAILED("Thanh toán thất bại"),
    REFUNDED("Đã hoàn tiền"),
    DUPLICATE ("Lập đơn hàng");

    private final String vietnameseName;

    PaymentStatus(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }

    public String getVietnameseName() {
        return vietnameseName;
    }
}
