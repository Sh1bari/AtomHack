package ru.noxly.simulation.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.*;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.noxly.simulation.models.models.dtos.SpaceByIdDto;
import ru.noxly.simulation.services.SpaceService;

@RestController
@RequiredArgsConstructor
@Validated
@CrossOrigin
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("")
@Tag(name = "Space API", description = "")
public class SpaceController {

	private final SpaceService spaceService;

	private final ConversionService conversionService;

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

	/*@Operation(summary = "Создать новое пространство")
	@ApiResponses()
	@PostMapping("/spaces")
	public ResponseEntity<SpaceByIdDto> createSpace() {
		val space = spaceService.findById(id);
		val response = conversionService.convert(space, SpaceByIdDto.class);

		return ResponseEntity
				.status(HttpStatus.OK)
				.body(response);
	}*/
}
