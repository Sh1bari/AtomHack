package ru.noxly.simulation.models.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.List;
import java.util.Objects;

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

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (o == null) return false;
		Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
		Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
		if (thisEffectiveClass != oEffectiveClass) return false;
		Reservoir reservoir = (Reservoir) o;
		return getId() != null && Objects.equals(getId(), reservoir.getId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
	}
}
