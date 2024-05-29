package ru.yandex.practicum.filmorate.model.film;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Genre {
   private Long id;
   @EqualsAndHashCode.Include
   private String name;
   private String description;
}
