package ru.noxly.simulation.repositories;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import ru.noxly.simulation.models.entities.Pipe;
import ru.noxly.simulation.models.entities.Reservoir;
import ru.noxly.simulation.models.entities.Space;
import ru.sh1bari.resolver.RepoResolverHelper;
import ru.sh1bari.resolver.RepositoryWrapper;

import java.util.HashMap;
import java.util.Map;

@Service
@Getter
@RequiredArgsConstructor
public class RepoResolver {

    //Repositories
    private final PipeRepository pipeRepository;
    private final ReservoirRepository reservoirRepository;
    private final SpaceRepository spaceRepository;

    @PostConstruct
    private void init() {
        resolver.put(Pipe.class, pipeRepository);
        resolver.put(Reservoir.class, reservoirRepository);
        resolver.put(Space.class, spaceRepository);
    }

    private final Map<Class<?>, JpaRepository<?, ?>> resolver = new HashMap<>();
    private final RepoResolverHelper repoResolverHelper;

    public <T, ID> RepositoryWrapper<T, ID> resolve(Class<T> entityClass) {
        return repoResolverHelper.resolve(entityClass, resolver);
    }
}
