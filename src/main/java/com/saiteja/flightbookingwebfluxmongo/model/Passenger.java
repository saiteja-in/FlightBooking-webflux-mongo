package com.saiteja.flightbookingwebfluxmongo.model;

import com.saiteja.flightbookingwebfluxmongo.model.enums.Gender;
import com.saiteja.flightbookingwebfluxmongo.model.enums.MealOption;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Passenger {
    @NotBlank(message = "passenger name is required")
    private String fullName;

    @NotNull(message = "gender is required")
    private Gender gender;

    @NotNull(message = "age is required")
    @Min(value = 1, message = "passenger age must be at least 1")
    @Max(value = 120, message = "passenger age cannot exceed 120")
    private Integer age;

    @NotBlank(message = "seat number is required")
    @Pattern(regexp = "^[0-9]{1,2}[A-F]$", message = "Invalid seat number format")
    private String seatNumber;

    @NotNull(message = "meal option is required")
    private MealOption mealOption;

}
