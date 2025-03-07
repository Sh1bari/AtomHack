package ru.noxly.simulation.services;

import lombok.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.noxly.simulation.models.entities.Pipe;
import ru.noxly.simulation.models.entities.Reservoir;
import ru.noxly.simulation.models.models.requests.PipeCreateDtoReq;
import ru.noxly.simulation.models.models.requests.PipeUpdateDtoReq;
import ru.noxly.simulation.repositories.RepoResolver;

@Service
@RequiredArgsConstructor
public class PipeService {

    private final RepoResolver repoResolver;

    @Transactional
    public Pipe createPipe(final PipeCreateDtoReq request) {
        val target = repoResolver.resolve(Reservoir.class).findById(request.getTargetId());
        val source = repoResolver.resolve(Reservoir.class).findById(request.getSourceId());
        val pipe = Pipe.init()
                .setSource(source)
                .setTarget(target)
                .setDiameter(request.getDiameter())
                .build();
        repoResolver.resolve(Pipe.class).save(pipe);

        return pipe;
    }


    public Pipe updatePipe(Long id, PipeUpdateDtoReq request) {
        val pipe = repoResolver.resolve(Pipe.class).findById(id);
        val target = repoResolver.resolve(Reservoir.class).findById(request.getTargetId());
        val source = repoResolver.resolve(Reservoir.class).findById(request.getSourceId());
        val entity = pipe.toBuilder()
                .setDiameter(request.getDiameter())
                .setTarget(target)
                .setSource(source)
                .build();
        repoResolver.resolve(Pipe.class).save(pipe);

        return entity;
    }

    public void deletePipe(String id) {
        val pipe = repoResolver.resolve(Pipe.class).findById(id);
        repoResolver.getPipeRepository().delete(pipe);
    }
}