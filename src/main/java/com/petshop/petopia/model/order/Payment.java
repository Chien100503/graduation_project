package com.petshop.petopia.model.order;

import com.petshop.petopia.component.Global;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "payment")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Global.PaymentMethod paymentMethod;

    private String transactionId;
    private String transactionContent;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date paymentDate;

    @Column
    private Long orderCode;

    @OneToOne(mappedBy = "payment")
    private Order order;

    @PrePersist
    protected void onCreate() {
        paymentDate = new Date();
    }
}
