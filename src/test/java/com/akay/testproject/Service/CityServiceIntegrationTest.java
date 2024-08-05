package com.akay.testproject.Service;

import com.akay.testproject.Entity.City;
import com.akay.testproject.Entity.Country;
import com.akay.testproject.Repository.CityRepository;
import com.akay.testproject.Repository.CountryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class CityServiceIntegrationTest {

    @Autowired
    public CountryRepository countryRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private CityService cityService;

    Country country;

    @BeforeEach
    void setUp() {
         country = Country.builder()
                .name("Turkey")
                .code("TR")
                .build();

        country = countryRepository.save(country);
    }

    @Test
    public void shouldCreateCityWithCity() {
        City city = City.builder()
                .name("Istanbul")
                .country(country)
                .build();

        City createdCity = cityService.createCity(city);

        assertNotNull(createdCity, "City is null");
        assertThat(createdCity.getName()).isEqualTo(city.getName());
        assertThat(createdCity.getCountry()).isEqualTo(city.getCountry());
    }

    @Test
    public void shouldUpdateCityWithIdAndCityDetails() {
        City existCity = City.builder()
                .name("Istanbul")
                .country(country)
                .build();

        cityRepository.save(existCity);

        City cityDetails = City.builder()
                .name("Ankara")
                .country(country)
                .build();

        City result = cityService.updateCity(existCity.getId(), cityDetails);

        assertNotNull(result, "City is null");
        assertThat(result.getName()).isEqualTo(cityDetails.getName());
        assertThat(result.getCountry()).isEqualTo(cityDetails.getCountry());
    }

    @Test
    public void shouldThrowExceptionWhenUpdateCityWithIdAndCityDetails() {
        Long nonExistingId = 44L;

        City city = City.builder()
                .name("Istanbul")
                .country(country)
                .build();

        assertThatThrownBy(() -> cityService.updateCity(nonExistingId, city))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Country not found with id " + nonExistingId);
    }

    @Test
    public void shouldDeleteCityWithId() {
        City city = City.builder()
                .name("Istanbul")
                .country(country)
                .build();

        cityRepository.save(city);

        cityService.deleteCity(city.getId());

        assertThat(cityRepository.findById(city.getId())).isEmpty();
    }

    @Test
    public void shouldFindAllCityWithPageable() {
        cityRepository.deleteAll();

        cityRepository.save(City.builder().name("Istanbul").country(country).build());
        cityRepository.save(City.builder().name("Ankara").country(country).build());
        cityRepository.save(City.builder().name("Izmir").country(country).build());

        Pageable pageable = PageRequest.of(0, 10);
        Page<City> cities = new PageImpl<>(cityRepository.findAll(), pageable, cityRepository.findAll().size());

        cities = cityService.findAll(pageable);

        assertThat(cities).isNotNull();
        assertThat(cities.getContent().size()).isEqualTo(3);
        assertThat(cities.getContent().get(0).getName()).isEqualTo("Istanbul");
        assertThat(cities.getContent().get(1).getName()).isEqualTo("Ankara");
        assertThat(cities.getContent().get(2).getName()).isEqualTo("Izmir");
    }


}
