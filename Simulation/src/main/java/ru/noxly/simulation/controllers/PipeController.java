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
import ru.noxly.simulation.models.models.dtos.PipeDto;
import ru.noxly.simulation.models.models.requests.PipeCreateDtoReq;
import ru.noxly.simulation.models.models.requests.PipeUpdateDtoReq;
import ru.noxly.simulation.models.models.socket.PipeUpdateSocketDto;
import ru.noxly.simulation.models.models.socket.SocketEntityEnum;
import ru.noxly.simulation.redis.PipePublisher;
import ru.noxly.simulation.services.PipeService;

@RestController
@RequiredArgsConstructor
@Validated
@CrossOrigin
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("")
@Tag(name = "Pipe API", description = "Операции с трубами")
public class PipeController {

    private final PipeService pipeService;

    private final ConversionService conversionService;

    private final PipePublisher pipePublisher;

    @Operation(summary = "Создать новую трубу")
    @ApiResponses()
    @PostMapping("/pipes")
    public ResponseEntity<PipeDto> createPipe(@RequestBody PipeCreateDtoReq request) {
        val pipe = pipeService.createPipe(request);
        val response = conversionService.convert(pipe, PipeDto.class);
        pipePublisher.publishUpdate(
                pipe.getSource().getSpace().getId(),
                PipeUpdateSocketDto.init()
                        .setCommand(SocketEntityEnum.CREATE)
                        .setPipe(response)
                        .build()
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @Operation(summary = "Обновить трубу")
    @ApiResponses()
    @PutMapping("/pipes/{id}")
    public ResponseEntity<PipeDto> updatePipe(@RequestBody PipeUpdateDtoReq request,
                                              @PathVariable Long id) {
        val pipe = pipeService.updatePipe(id, request);
        val response = conversionService.convert(pipe, PipeDto.class);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @Operation(summary = "Удалить трубу")
    @ApiResponses
    @DeleteMapping("/pipes/{id}")
    public ResponseEntity<String> deletePipe(@PathVariable Long id) {
        val pipe = pipeService.deletePipe(id);
        val response = conversionService.convert(pipe, PipeDto.class);
        pipePublisher.publishUpdate(
                pipe.getSource().getSpace().getId(),
                PipeUpdateSocketDto.init()
                        .setCommand(SocketEntityEnum.DELETE)
                        .setPipe(response)
                        .build()
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Труба ликвидирована рядом выстрелов из автомата АК-47" + id);
    }
}