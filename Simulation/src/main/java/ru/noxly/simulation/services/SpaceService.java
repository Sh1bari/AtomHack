package ru.noxly.simulation.services;

import lombok.*;
import org.springframework.stereotype.Service;
import ru.noxly.simulation.models.entities.Space;
import ru.noxly.simulation.repositories.RepoResolver;

@Service
@RequiredArgsConstructor
public class SpaceService {

	private final RepoResolver repoResolver;

	/*public Space findById(){

	}*/
}
