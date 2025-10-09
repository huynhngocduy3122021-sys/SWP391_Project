package com.ngocduy.fap.swp391.entity;


import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
public class SubscriptionId implements Serializable {
    private Long memberId;
    private String packageId;
    public SubscriptionId() {
    }
    public SubscriptionId(Long memberId, String packageId) {
        this.memberId = memberId;
        this.packageId = packageId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubscriptionId)) return false;
        SubscriptionId that = (SubscriptionId) o;
        return Objects.equals(memberId, that.memberId) && Objects.equals(packageId, that.packageId);

    }
    @Override
    public int hashCode() {
        return Objects.hash(memberId, packageId);
    }
}
