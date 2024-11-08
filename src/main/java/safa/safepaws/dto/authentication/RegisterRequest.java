package safa.safepaws.dto.authentication;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String username;
    private String password;
    private String email;

    private String name;
    private String surname;
    private String dni;
    private LocalDate birthdate;

    private Float coordinateX;
    private Float coordinateY;
    private String road;
    private String neighborhood;
    private String village;
    private String province;
    private String state;
    private String postcode;
    private String country;
    private String countryCode;
}