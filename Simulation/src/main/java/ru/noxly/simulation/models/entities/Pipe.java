package ru.noxly.simulation.models.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@ToString
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Builder(builderMethodName = "init", setterPrefix = "set", toBuilder = true)
@Table(name = "pipes")
public class Pipe {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private final Long id;

	@ManyToOne
	@JoinColumn(name = "source_id", nullable = false)
	private final Reservoir source;

	@ManyToOne
	@JoinColumn(name = "target_id", nullable = false)
	private final Reservoir target;

	@Column(nullable = false)
	private final Double diameter;
}
