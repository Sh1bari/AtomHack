package ru.noxly.simulation.services;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.noxly.simulation.models.entities.Space;
import ru.noxly.simulation.models.models.requests.SpaceCreateDtoReq;
import ru.noxly.simulation.models.models.requests.SpaceUpdateDtoReq;
import ru.noxly.simulation.repositories.RepoResolver;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SpaceService {

	private final RepoResolver repoResolver;

	public Space findById(final Long id) {
		return repoResolver.resolve(Space.class).findById(id);
	}

	public List<Space> findAll() {
		return repoResolver.resolve(Space.class).findAll(Specification.where(null));
	}

	public Page<Space> findAll(Specification<Space> spec, Pageable pageable) {
		return repoResolver.resolve(Space.class).findAll(spec, pageable);
	}

	@Transactional
	public Space createSpace(final SpaceCreateDtoReq request) {
		val space = Space.init()
				.setName(request.getName())
				.setCreateDate(OffsetDateTime.now())
				.build();
		repoResolver.resolve(Space.class).save(space);

		return space;
	}

	@Transactional
	public Space updateSpace(final Long id, final SpaceUpdateDtoReq request) {
		val space = repoResolver.resolve(Space.class).findById(id);
		val entity = space.toBuilder()
				.setName(request.getName())
				.build();
		repoResolver.resolve(Space.class).save(entity);

		return entity;
	}
}
