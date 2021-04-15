package org.vescm.zooapi.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import org.vescm.zooapi.builder.AnimalDtoBuilder;
import org.vescm.zooapi.dto.AnimalDto;
import org.vescm.zooapi.dto.QuantityDto;
import org.vescm.zooapi.exception.AnimalLimitExceededException;
import org.vescm.zooapi.exception.AnimalNotFoundException;
import org.vescm.zooapi.exception.AnimalNumberAlreadyZeroException;
import org.vescm.zooapi.service.AnimalService;

import java.util.Collections;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.vescm.zooapi.utils.JsonConvertionUtils.asJsonString;

@ExtendWith(MockitoExtension.class)
public class AnimalControllerTests {

    private static final String ANIMAL_API_URL_PATH = "/api/v1/animals";
    private static final long VALID_ANIMAL_ID = 1L;
    private static final long INVALID_ANIMAL_ID = 2L;
    private static final String ANIMAL_API_SUBPATH_INCREMENT_URL = "/increment";
    private static final String ANIMAL_API_SUBPATH_DECREMENT_URL = "/decrement";

    private MockMvc mockMvc;

    @Mock
    private AnimalService animalService;

    @InjectMocks
    private AnimalController animalController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(animalController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setViewResolvers((s, locale) -> new MappingJackson2JsonView())
                .build();
    }

    @Test
    void whenPOSTIsCalledThenAAnimalIsCreated() throws Exception {
        // given
        AnimalDto animalDto = AnimalDtoBuilder.builder().build().toAnimalDto();

        // when
        when(animalService.createAnimal(animalDto)).thenReturn(animalDto);

        // then
        mockMvc.perform(post(ANIMAL_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(animalDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.specie", is(animalDto.getSpecie())))
                .andExpect(jsonPath("$.kingdom", is(animalDto.getKingdom().toString())))
                .andExpect(jsonPath("$.phylum", is(animalDto.getPhylum().toString())));
    }

    @Test
    void whenPOSTIsCalledWithoutRequiredFieldThenAnErrorIsReturned() throws Exception {
        // given
        AnimalDto animalDto = AnimalDtoBuilder.builder().build().toAnimalDto();
        animalDto.setKingdom(null);

        // then
        mockMvc.perform(post(ANIMAL_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(animalDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenGETIsCalledWithValidNameThenOkStatusIsReturned() throws Exception {
        // given
        AnimalDto animalDto = AnimalDtoBuilder.builder().build().toAnimalDto();

        //when
        when(animalService.getBySpecie(animalDto.getSpecie())).thenReturn(animalDto);

        // then
        mockMvc.perform(MockMvcRequestBuilders.get(ANIMAL_API_URL_PATH + "/" + animalDto.getSpecie())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.specie", is(animalDto.getSpecie())))
                .andExpect(jsonPath("$.kingdom", is(animalDto.getKingdom().toString())))
                .andExpect(jsonPath("$.phylum", is(animalDto.getPhylum().toString())));
    }

    @Test
    void whenGETIsCalledWithoutRegisteredNameThenNotFoundStatusIsReturned() throws Exception {
        // given
        AnimalDto animalDto = AnimalDtoBuilder.builder().build().toAnimalDto();

        //when
        when(animalService.getBySpecie(animalDto.getSpecie())).thenThrow(AnimalNotFoundException.class);

        // then
        mockMvc.perform(MockMvcRequestBuilders.get(ANIMAL_API_URL_PATH + "/" + animalDto.getSpecie())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenGETListWithAnimalsIsCalledThenOkStatusIsReturned() throws Exception {
        // given
        AnimalDto animalDto = AnimalDtoBuilder.builder().build().toAnimalDto();

        //when
        when(animalService.listAll()).thenReturn(Collections.singletonList(animalDto));

        // then
        mockMvc.perform(MockMvcRequestBuilders.get(ANIMAL_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].specie", is(animalDto.getSpecie())))
                .andExpect(jsonPath("$[0].kingdom", is(animalDto.getKingdom().toString())))
                .andExpect(jsonPath("$[0].phylum", is(animalDto.getPhylum().toString())));
    }

    @Test
    void whenGETListWithoutAnimalsIsCalledThenOkStatusIsReturned() throws Exception {
        // given
        AnimalDto animalDto = AnimalDtoBuilder.builder().build().toAnimalDto();

        //when
        when(animalService.listAll()).thenReturn(Collections.singletonList(animalDto));

        // then
        mockMvc.perform(MockMvcRequestBuilders.get(ANIMAL_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void whenDELETEIsCalledWithValidIdThenNoContentStatusIsReturned() throws Exception {
        // given
        AnimalDto animalDto = AnimalDtoBuilder.builder().build().toAnimalDto();

        //when
        doNothing().when(animalService).deleteById(animalDto.getId());

        // then
        mockMvc.perform(MockMvcRequestBuilders.delete(ANIMAL_API_URL_PATH + "/" + animalDto.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void whenDELETEIsCalledWithInvalidIdThenNotFoundStatusIsReturned() throws Exception {
        //when
        doThrow(AnimalNotFoundException.class).when(animalService).deleteById(INVALID_ANIMAL_ID);

        // then
        mockMvc.perform(MockMvcRequestBuilders.delete(ANIMAL_API_URL_PATH + "/" + INVALID_ANIMAL_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenPATCHIsCalledToIncrementDiscountThenOKstatusIsReturned() throws Exception {
        QuantityDto quantityDTO = QuantityDto.builder()
                .quantity(10)
                .build();

        AnimalDto animalDto = AnimalDtoBuilder.builder().build().toAnimalDto();
        animalDto.setQuantity(animalDto.getQuantity() + quantityDTO.getQuantity());

        when(animalService.increaseAnimalNumber(VALID_ANIMAL_ID, quantityDTO.getQuantity())).thenReturn(animalDto);

        mockMvc.perform(MockMvcRequestBuilders.patch(ANIMAL_API_URL_PATH + "/" + VALID_ANIMAL_ID + ANIMAL_API_SUBPATH_INCREMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDTO))).andExpect(status().isOk())
                .andExpect(jsonPath("$.specie", is(animalDto.getSpecie())))
                .andExpect(jsonPath("$.kingdom", is(animalDto.getKingdom().toString())))
                .andExpect(jsonPath("$.phylum", is(animalDto.getPhylum().toString())))
                .andExpect(jsonPath("$.quantity", is(animalDto.getQuantity())));
    }

    @Test
    void whenPATCHIsCalledToIncrementGreaterThanMaxThenBadRequestStatusIsReturned() throws Exception {
        QuantityDto quantityDTO = QuantityDto.builder()
                .quantity(9)
                .build();

        AnimalDto animalDto = AnimalDtoBuilder.builder().build().toAnimalDto();
        animalDto.setQuantity(animalDto.getQuantity() + quantityDTO.getQuantity());

        when(animalService.increaseAnimalNumber(VALID_ANIMAL_ID, quantityDTO.getQuantity())).thenThrow(AnimalLimitExceededException.class);

        mockMvc.perform(MockMvcRequestBuilders.patch(ANIMAL_API_URL_PATH + "/" + VALID_ANIMAL_ID + ANIMAL_API_SUBPATH_INCREMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDTO)))
                .andExpect(status().isBadRequest());

    }

    @Test
    void whenPATCHIsCalledWithInvalidAnimalIdToIncrementThenNotFoundStatusIsReturned() throws Exception {
        QuantityDto quantityDTO = QuantityDto.builder()
                .quantity(1)
                .build();

        when(animalService.increaseAnimalNumber(INVALID_ANIMAL_ID, quantityDTO.getQuantity())).thenThrow(AnimalNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.patch(ANIMAL_API_URL_PATH + "/" + INVALID_ANIMAL_ID + ANIMAL_API_SUBPATH_INCREMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenPATCHIsCalledToDecrementDiscountThenOKstatusIsReturned() throws Exception {
        QuantityDto quantityDTO = QuantityDto.builder()
                .quantity(5)
                .build();

        AnimalDto animalDto = AnimalDtoBuilder.builder().build().toAnimalDto();
        animalDto.setQuantity(animalDto.getQuantity() + quantityDTO.getQuantity());

        when(animalService.decreaseAnimalNumber(VALID_ANIMAL_ID, quantityDTO.getQuantity())).thenReturn(animalDto);

        mockMvc.perform(MockMvcRequestBuilders.patch(ANIMAL_API_URL_PATH + "/" + VALID_ANIMAL_ID + ANIMAL_API_SUBPATH_DECREMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDTO))).andExpect(status().isOk())
                .andExpect(jsonPath("$.specie", is(animalDto.getSpecie())))
                .andExpect(jsonPath("$.kingdom", is(animalDto.getKingdom().toString())))
                .andExpect(jsonPath("$.phylum", is(animalDto.getPhylum().toString())))
                .andExpect(jsonPath("$.quantity", is(animalDto.getQuantity())));
    }

    @Test
    void whenPATCHIsCalledToDecrementLowerThanZeroThenBadRequestStatusIsReturned() throws Exception {
        QuantityDto quantityDTO = QuantityDto.builder()
                .quantity(10)
                .build();

        AnimalDto animalDto = AnimalDtoBuilder.builder().build().toAnimalDto();
        animalDto.setQuantity(animalDto.getQuantity() + quantityDTO.getQuantity());

        when(animalService.decreaseAnimalNumber(VALID_ANIMAL_ID, quantityDTO.getQuantity()))
                .thenThrow(AnimalNumberAlreadyZeroException.class);

        mockMvc.perform(MockMvcRequestBuilders.patch(ANIMAL_API_URL_PATH + "/" + VALID_ANIMAL_ID + ANIMAL_API_SUBPATH_DECREMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenPATCHIsCalledWithInvalidAnimalIdToDecrementThenNotFoundStatusIsReturned() throws Exception {
        QuantityDto quantityDTO = QuantityDto.builder()
                .quantity(5)
                .build();

        when(animalService.decreaseAnimalNumber(INVALID_ANIMAL_ID, quantityDTO.getQuantity()))
                .thenThrow(AnimalNotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders.patch(ANIMAL_API_URL_PATH + "/" + INVALID_ANIMAL_ID + ANIMAL_API_SUBPATH_DECREMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDTO)))
                .andExpect(status().isNotFound());
    }
}
