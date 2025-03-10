package ru.noxly.simulation.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.noxly.simulation.models.models.dtos.ReservoirDto;
import ru.noxly.simulation.models.models.requests.ReservoirCreateDtoReq;
import ru.noxly.simulation.models.models.requests.ReservoirUpdateDtoReq;
import ru.noxly.simulation.models.models.socket.ReservoirUpdateSocketDto;
import ru.noxly.simulation.models.models.socket.SocketEntityEnum;
import ru.noxly.simulation.redis.ReservoirPublisher;
import ru.noxly.simulation.services.ReservoirService;

@RestController
@RequiredArgsConstructor
@Validated
@CrossOrigin
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("")
@Tag(name = "Reservoir API", description = "Операции с резервуарами")
public class ReservoirController {

    private final ReservoirService reservoirService;

    private final ConversionService conversionService;

    private final ReservoirPublisher reservoirPublisher;

    @Operation(summary = "Создать новый резервуар")
    @ApiResponses()
    @PostMapping("/reservoirs")
    public ResponseEntity<ReservoirDto> createReservoir(@RequestBody ReservoirCreateDtoReq request) {
        val reservoir = reservoirService.createReservoir(request);
        val response = conversionService.convert(reservoir, ReservoirDto.class);
        reservoirPublisher.publishUpdate(
                ReservoirUpdateSocketDto.init()
                        .setSpaceId(reservoir.getSpace().getId())
                        .setCommand(SocketEntityEnum.CREATE)
                        .setReservoir(response)
                        .build()
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @Operation(summary = "Обновить резервуар")
    @ApiResponses()
    @PutMapping("/reservoirs/{id}")
    public ResponseEntity<ReservoirDto> updateReservoir(@RequestBody ReservoirUpdateDtoReq request,
                                                        @PathVariable String id) {
        val reservoir = reservoirService.updateReservoir(id, request);
        val response = conversionService.convert(reservoir, ReservoirDto.class);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @Operation(summary = "Удалить резервуар")
    @ApiResponses
    @DeleteMapping("/reservoirs/{id}")
    public ResponseEntity<String> deleteReservoir(@PathVariable String id) {
        val reservoir = reservoirService.deleteReservoir(id);
        val response = conversionService.convert(reservoir, ReservoirDto.class);
        reservoirPublisher.publishUpdate(
                ReservoirUpdateSocketDto.init()
                        .setSpaceId(reservoir.getSpace().getId())
                        .setCommand(SocketEntityEnum.DELETE)
                        .setReservoir(response)
                        .build()
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Резервуар удален на атомном уровне");
    }
}