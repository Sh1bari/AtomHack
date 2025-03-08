package ru.noxly.simulation.services;

import lombok.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.noxly.simulation.models.entities.Reservoir;
import ru.noxly.simulation.models.entities.Space;
import ru.noxly.simulation.models.models.requests.ReservoirCreateDtoReq;
import ru.noxly.simulation.models.models.requests.ReservoirUpdateDtoReq;
import ru.noxly.simulation.repositories.RepoResolver;

@Service
@RequiredArgsConstructor
public class ReservoirService {

    private final RepoResolver repoResolver;

    @Transactional
    public Reservoir createReservoir(final ReservoirCreateDtoReq request) {
        val space = repoResolver.resolve(Space.class).findById(request.getSpaceId());
        val reservoir = Reservoir.init()
                .setArea(request.getArea())
                .setSpace(space)
                .setPressure(5.0)
                .setLevel(5.0)
                .build();
        repoResolver.resolve(Reservoir.class).save(reservoir);

        return reservoir;
    }

    @Transactional
    public Reservoir updateReservoir(String id, final ReservoirUpdateDtoReq request) {
        val space = repoResolver.resolve(Space.class).findById(request.getSpaceId());
        val reservoir = repoResolver.resolve(Reservoir.class).findById(id);
        val entity = reservoir.toBuilder()
                .setArea(reservoir.getArea())
                .setSpace(space)
                .build();
        repoResolver.resolve(Reservoir.class).save(entity);

        return entity;

    }

    @Transactional
    public void deleteReservoir(String id) {
        val reservoir = repoResolver.resolve(Reservoir.class).findById(id);
        repoResolver.getReservoirRepository().delete(reservoir);
    }
}