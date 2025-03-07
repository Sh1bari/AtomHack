package ru.noxly.simulation.models.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@ToString
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Builder(builderMethodName = "init", setterPrefix = "set", toBuilder = true)
@Table(name = "reservoirs")
public class Reservoir {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private final Long id;

	@ManyToOne
	@JoinColumn(name = "space_id", nullable = false)
	private final Space space;

	@Column(nullable = false)
	private final Double pressure;

	@Column(nullable = false)
	private final Double level;

	@Column(nullable = false)
	private final Double area;

	@OneToMany(mappedBy = "source", cascade = CascadeType.ALL, orphanRemoval = true)
	@ToString.Exclude
	private final List<Pipe> outgoingPipes;

	@OneToMany(mappedBy = "target", cascade = CascadeType.ALL, orphanRemoval = true)
	@ToString.Exclude
	private final List<Pipe> incomingPipes;
}
