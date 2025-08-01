package com.thiennd.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table
@Getter
@Setter
public class SeatType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 2, unique = true, nullable = false)
    private String seatTypeCode;

    @Column(length = 30, nullable = false)
    private String seatTypeName;

    @Column
    private String workerMemo;

    @Column
    private boolean deleted = false;

    @Column
    private boolean isBooked = false;

    @OneToOne(mappedBy = "seatType", cascade = CascadeType.ALL)
    private Booking booking;
}
