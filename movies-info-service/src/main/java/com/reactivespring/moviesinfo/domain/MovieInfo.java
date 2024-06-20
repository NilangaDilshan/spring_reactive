package com.reactivespring.moviesinfo.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Document
@Validated
public class MovieInfo {

    @Id
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
