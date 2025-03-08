package ru.noxly.simulation.specifications;

import org.springframework.data.jpa.domain.Specification;
import ru.noxly.simulation.models.entities.Space;

public class SpaceSpecification {
    //Фильтр по имени
    public static Specification<Space> hasName(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null || name.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
                    "%" + name.toLowerCase() + "%");
        };
    }
}