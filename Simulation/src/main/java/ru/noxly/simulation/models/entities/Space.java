package ru.noxly.simulation.models.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Getter
@ToString
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Builder(builderMethodName = "init", setterPrefix = "set", toBuilder = true)
@Table(name = "spaces")
public class Space {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private final Long id;

	@Column(nullable = false, length = 255)
	private final String name;

	@Column(name = "create_date", columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT NOW()")
	private final OffsetDateTime createDate;

	@OneToMany(mappedBy = "space", cascade = CascadeType.ALL, orphanRemoval = true)
	@ToString.Exclude
	private final List<Reservoir> reservoirs;
}
