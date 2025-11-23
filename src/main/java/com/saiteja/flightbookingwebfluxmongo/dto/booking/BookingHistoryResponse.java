package com.saiteja.flightbookingwebfluxmongo.dto.booking;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingHistoryResponse {
    private String pnr;
    private String summary;
}
