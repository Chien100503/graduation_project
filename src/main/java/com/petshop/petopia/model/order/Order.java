package com.petshop.petopia.model.order;

import com.petshop.petopia.component.Global;
import com.petshop.petopia.model.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "payment_id", referencedColumnName = "id")
    private Payment payment;

    private Date orderDate;
    private BigDecimal totalPrice;

    private String recipientName;
    private String shippingAddress;
    private String phoneNumber;

    private boolean isPaid;
    private boolean isDelivered;
    private boolean isReceived;
    private Global.OrderStatus status;

    @CreationTimestamp
    @Column(updatable = false)
    private Date createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        orderDate = new Date();
        isPaid = false;
        isDelivered = false;
        isReceived = false;
    }
}
