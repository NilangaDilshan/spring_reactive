package com.reactivespring.movies.domain;

import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Validated
public class MovieInfo {

    private String movieInfoId;
    @NotBlank(message = "{movie.name.not.blank}")
    private String name;
    @NotNull
    @Positive(message = "{movie.year.not.negative}")
    private Integer year;
    @NotNull
    @NotEmpty(message = "{movie.cast.not.blank}")
    private List<String> cast;
    private LocalDate release_date;

}
