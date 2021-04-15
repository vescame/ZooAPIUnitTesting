package org.vescm.zooapi.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.PathVariable;
import org.vescm.zooapi.dto.AnimalDto;
import org.vescm.zooapi.exception.AnimalAlreadyExistsException;
import org.vescm.zooapi.exception.AnimalNotFoundException;

import java.util.List;

@Api("Manages Animals of Your Zoo")
public interface AnimalControllerDocs {
    @ApiOperation(value = "Animal creation operation")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Success animal creation"),
            @ApiResponse(code = 400, message = "Missing required fields or wrong field range value.")
    })
    AnimalDto createBeer(AnimalDto beerDTO) throws AnimalAlreadyExistsException;

    @ApiOperation(value = "Returns an animal found by a given specie")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success animal found in the system"),
            @ApiResponse(code = 404, message = "Animal with given specie not found.")
    })
    AnimalDto findByName(@PathVariable String specie) throws AnimalNotFoundException;

    @ApiOperation(value = "Returns a list of all animals registered in the system")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of all animals registered in the system"),
    })
    List<AnimalDto> listAnimals();

    @ApiOperation(value = "Delete an animal found by a given valid Id")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Success animal deleted in the system"),
            @ApiResponse(code = 404, message = "Animal with given id not found.")
    })
    AnimalDto deleteById(@PathVariable Long id) throws AnimalNotFoundException;
}
