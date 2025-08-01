package com.thiennd.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SeatTypeDTO {
    private Long id;
    private String seatTypeCode;
    @NotBlank(message = "Seat Type Name is required")
    @Size(max = 30, message = "Seat Type Name must be less than 30 characters")
    private String seatTypeName;
    private String workerMemo;
    private boolean deleted;
    private boolean isBooked;
}
