package com.saiteja.flightbookingwebfluxmongo.dto.passenger;

import com.saiteja.flightbookingwebfluxmongo.model.enums.Gender;
import com.saiteja.flightbookingwebfluxmongo.model.enums.MealOption;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PassengerRequest {

    @NotBlank
    private String fullName;

    @NotNull
    private Gender gender;

    @Min(1) @Max(120)
    private Integer age;

    @NotBlank
    private String seatNumber;

    @NotNull
    private MealOption mealOption;
}
