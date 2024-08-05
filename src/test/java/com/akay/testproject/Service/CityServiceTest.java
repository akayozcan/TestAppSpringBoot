package com.akay.testproject.Service;

import com.akay.testproject.Entity.City;
import com.akay.testproject.Entity.Country;
import com.akay.testproject.Repository.CityRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CityServiceTest {

    @Mock
    private CityRepository cityRepository;

    @InjectMocks
    private CityService cityService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cityService = new CityService(cityRepository);
    }

    @Test
    void shouldCreateCityWithCity() {
        Country country = Country.builder()
                .name("Turkey")
                .code("TR")
                .build();

        City newCity = City.builder()
                .name("Istanbul")
                .country(country)
                .build();

        Mockito.when(cityRepository.save(newCity)).thenReturn(newCity);

        City result = cityService.createCity(newCity);

        assertNotNull(result, "City is null");
        assertEquals(newCity.getName(), result.getName(), "Name is not equal");
        assertEquals(newCity.getCountry(), result.getCountry(), "Country is not equal");

        Mockito.verify(cityRepository, Mockito.times(1)).save(newCity);
    }

    @Test
    public void shouldUpdateCityWithIdAndCityDetails() {
        //Arrange
        Country country = Country.builder()
                .name("Turkey")
                .code("TR")
                .build();

        City existCity = City.builder()
                .id(1L)
                .name("Istanbul")
                .country(country)
                .build();

        City cityDetails = City.builder()
                .name("Ankara")
                .country(country)
                .build();

        // Act
        Mockito.when(cityRepository.findById(existCity.getId())).thenReturn(Optional.of(existCity));

        Mockito.when(cityRepository.save(existCity)).thenReturn(existCity);

        City returnCity = cityService.updateCity(existCity.getId(), cityDetails);

        // Assert
        assertNotNull(returnCity, "Updated city should not be null");
        assertEquals(cityDetails.getName(), returnCity.getName(), "City name should be updated");

        Mockito.verify(cityRepository, Mockito.times(1)).findById(existCity.getId());
        Mockito.verify(cityRepository, Mockito.times(1)).save(existCity);
    }

    @Test
    void shouldRuntimeExceptionIfCityNotFoundWhenUpdateCity() {
        Long notExistedId = 44L;

        City city = City.builder()
                .name("Istanbul")
                .build();

        Mockito.when(cityRepository.findById(notExistedId)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                cityService.updateCity(city.getId(),city))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("The Id " + notExistedId + " unavailable.");

        Mockito.verify(cityRepository, Mockito.times(1)).findById(notExistedId);
    }

    @Test
    public void shouldDeleteCityWithId() {
        Long cityId = 1L;

        cityService.deleteCity(cityId);

        Mockito.verify(cityRepository, Mockito.times(1)).deleteById(cityId);
    }

    @Test
    public void shouldFindAllCitiesWithPageable() {
        Country country = Country.builder()
                .name("Turkey")
                .code("TR")
                .build();

        List<City> cities = List.of(
                City.builder().name("Istanbul").country(country).build(),
                City.builder().name("Ankara").country(country).build(),
                City.builder().name("Malatya").country(country).build()
        );

        Pageable pageable  = PageRequest.of(0, 10);
        Page<City> cityPage = new PageImpl<>(cities, pageable, cities.size());

        Mockito.when(cityRepository.findAll(pageable)).thenReturn(cityPage);

        Page<City> result = cityService.findAll(pageable);

        assertNotNull(result, "City page should not be null");
        assertEquals(cities.size(), result.getContent().size(), "City size should be equal");

        Mockito.verify(cityRepository, Mockito.times(1)).findAll(pageable);


    }




    @AfterEach
    void tearDown() {

    }
}