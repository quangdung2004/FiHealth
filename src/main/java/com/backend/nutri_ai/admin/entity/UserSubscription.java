package com.backend.nutri_ai.admin.entity;

import com.backend.nutri_ai.auth.entity.AppUser;
import com.backend.nutri_ai.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name="user_subscription", indexes = {
        @Index(name="idx_sub_user", columnList="user_id")
})
@Getter @Setter
public class UserSubscription extends BaseEntity {

    @ManyToOne(fetch=FetchType.LAZY) //gọi tới đâu load tới đó là lazy
    @JoinColumn(name="user_id", nullable=false, columnDefinition="BINARY(16)")
    private AppUser user;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="plan_id", nullable=false, columnDefinition="BINARY(16)")
    private SubscriptionPlan plan;

    @Column(nullable=false)
    private Instant startAt;

    @Column(nullable=false)
    private Instant endAt;

    @Column(nullable=false)
    private Boolean autoRenew = false;
}
