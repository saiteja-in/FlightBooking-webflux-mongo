package com.saiteja.flightbookingwebfluxmongo.dto.passenger;

import com.saiteja.flightbookingwebfluxmongo.model.enums.Gender;
import com.saiteja.flightbookingwebfluxmongo.model.enums.MealOption;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PassengerResponse {

    private String fullName;
    private Gender gender;
    private Integer age;
    private String seatNumber;
    private MealOption mealOption;
}
