package ru.noxly.simulation.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.noxly.simulation.models.models.dtos.ReservoirDto;
import ru.noxly.simulation.models.models.dtos.SpaceByIdDto;
import ru.noxly.simulation.models.models.dtos.SpaceDto;
import ru.noxly.simulation.models.models.requests.SpaceCreateDtoReq;
import ru.noxly.simulation.models.models.requests.SpaceUpdateDtoReq;
import ru.noxly.simulation.redis.ReservoirPublisher;
import ru.noxly.simulation.services.SpaceService;
import ru.noxly.simulation.specifications.SpaceSpecification;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@CrossOrigin
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("")
@Tag(name = "Space API", description = "Операции с пространствами")
public class SpaceController {

	private final SpaceService spaceService;

	private final ConversionService conversionService;

	@Operation(summary = "Получить список всех пространств")
	@ApiResponses()
	@GetMapping("/spaces")
	public ResponseEntity<List<SpaceDto>> findAll() {
		val space = spaceService.findAll();
		val response = space.stream()
				.map(o -> conversionService.convert(o, SpaceDto.class))
				.toList();

		return ResponseEntity
				.status(HttpStatus.OK)
				.body(response);
	}

	@Operation(summary = "Создать новое пространство")
	@ApiResponses()
	@PostMapping("/spaces")
	public ResponseEntity<SpaceDto> createSpace(@RequestBody SpaceCreateDtoReq request) {
		val space = spaceService.createSpace(request);
		val response = conversionService.convert(space, SpaceDto.class);

		return ResponseEntity
				.status(HttpStatus.OK)
				.body(response);
	}

	@Operation(summary = "Обновить пространство")
	@ApiResponses()
	@PutMapping("/spaces/{id}")
	public ResponseEntity<SpaceDto> updateSpace(@RequestBody SpaceUpdateDtoReq request,
												@PathVariable Long id) {
		val space = spaceService.updateSpace(id, request);
		val response = conversionService.convert(space, SpaceDto.class);

		return ResponseEntity
				.status(HttpStatus.OK)
				.body(response);
	}

	@Operation(summary = "Получить информацию о пространстве (с резервуарами и трубами)")
	@ApiResponses()
	@GetMapping("/spaces/{id}")
	public ResponseEntity<SpaceByIdDto> findById(@PathVariable Long id) {
		val space = spaceService.findById(id);
		val response = conversionService.convert(space, SpaceByIdDto.class);

		return ResponseEntity
				.status(HttpStatus.OK)
				.body(response);
	}

	@Operation(summary = "Получить информацию о пространствах с пагинацией и фильтром по названию")
	@ApiResponses()
	@GetMapping("/spaces")
	public ResponseEntity<Page<SpaceDto>> findAll(@RequestParam String pattern,
												  @PageableDefault Pageable pageable) {
		val spec = Specification.where(SpaceSpecification.hasName(pattern));
		val spaces = spaceService.findByPatternAndPageable(spec, pageable);
		val response = spaces.map(space -> conversionService.convert(space, SpaceDto.class));

		return ResponseEntity
				.status(HttpStatus.OK)
				.body(response);
	}

	private final ReservoirPublisher reservoirPublisher;
	@Operation(summary = "test")
	@ApiResponses()
	@GetMapping("/test")
	public ResponseEntity<?> test(@RequestParam Long spaceId, @RequestParam Double pressure) {
		reservoirPublisher.publishUpdate(
				ReservoirDto.init()
						.setSpaceId(spaceId)
						.setId(1L)
						.setPressure(pressure)
						.setLevel(5D)
						.setArea(5D)
				.build()
		);

		return ResponseEntity
				.status(HttpStatus.OK)
				.body("gotovo");
	}
}