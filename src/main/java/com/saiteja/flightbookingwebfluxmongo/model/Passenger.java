package com.saiteja.flightbookingwebfluxmongo.model;

import com.saiteja.flightbookingwebfluxmongo.model.enums.Gender;
import com.saiteja.flightbookingwebfluxmongo.model.enums.MealOption;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Passenger {
    private String fullName;
    private Gender gender;
    private Integer age;
    private String seatNumber;
    private MealOption mealOption;

}
