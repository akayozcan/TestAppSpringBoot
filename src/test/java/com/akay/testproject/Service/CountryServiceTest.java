package com.akay.testproject.Service;

import com.akay.testproject.Entity.Country;
import com.akay.testproject.Repository.CountryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CountryServiceTest {
    @Mock
    private CountryRepository countryRepository;
    @InjectMocks
    private CountryService countryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        countryService = new CountryService(countryRepository);
    }

    @Test
    public void shouldCreateCountryWithCountry() {
        // Arrange
        Country country= Country.builder()
                .name("Turkiye")
                .code("TR")
                .build();
        // Act
        Mockito.when(countryRepository.save(country)).thenReturn(country);
        Country result = countryService.createCountry(country);
        // Assert
        assertNotNull(result,"Country is null");
        assertEquals(country.getName(), result.getName(), "Name is not equal");
        assertEquals(country.getCode(), result.getCode(), "Code is not equal");
        verify(countryRepository, times(1)).save(country);
    }

    @Test
    public void shouldUpdateCountryWithIdAndCountryDetails() {
        //Arrange
        Country existingCountry = Country.builder()
                .name("Turkiye")
                .code("TR")
                .build();

        Country countryDetails = Country.builder()
                .name("Palestina")
                .code("PL")
                .build();

        // Act
        Mockito.when(countryRepository.findById(existingCountry.getId())).thenReturn(Optional.of(existingCountry));
        Mockito.when(countryRepository.save(existingCountry)).thenReturn(existingCountry);

        Country result = countryService.updateCountry(existingCountry.getId(), countryDetails);

        // Assert
        assertNotNull(result, "Updated country should not be null");
        assertEquals(countryDetails.getName(), result.getName(), "Country name should be updated");
        assertEquals(countryDetails.getCode(), result.getCode(), "Country code should be updated");

        verify(countryRepository, times(1)).findById(existingCountry.getId());
        verify(countryRepository, times(1)).save(existingCountry);

    }

    @Test
    public void shouldRuntimeExceptionIfCountryNotFoundWhenUpdateCountry() {
        Long notExistedId = 44L;

        Country country = Country.builder()
                .name("Turkiye")
                .code("TR")
                .build();

        Mockito.when(countryRepository.findById(country.getId())).thenReturn(Optional.empty());
        assertThatThrownBy(() ->
                countryService.updateCountry(country.getId(),country))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("The Id " + country.getId() + " unavailable.");

        verify(countryRepository, times(1)).findById(notExistedId);
    }

    @Test
    public void shouldDeleteCountryWithId() {
        // Arrange
        Long id = 1L;
        // Act
        countryService.deleteCountry(id);
        // Assert
        verify(countryRepository, times(1)).deleteById(id);
    }


    @Test
    public void shouldReturnAllCountriesWithPageable() {

        Page<Country> countryPage;
        List<Country> countryList = new ArrayList<>();
        countryList.add(Country.builder().id(1L).name("Turkey").code("TR").build());
        countryList.add(Country.builder().id(2L).name("United States").code("US").build());
        countryList.add(Country.builder().id(3L).name("Canada").code("CA").build());

        Pageable pageable = PageRequest.of(0, 10);
        countryPage = new PageImpl<>(countryList, pageable, countryList.size());
        // Arrange
        Mockito.when(countryRepository.findAll(pageable)).thenReturn(countryPage);

        // Act
        Page<Country> result = countryService.findAll(pageable);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(countryList.size(), result.getTotalElements(), "Total elements should match");
        assertEquals(countryList, result.getContent(), "Content should match the expected list");

        verify(countryRepository, times(1)).findAll(pageable);
    }

    @AfterEach
    void tearDown() {
        countryRepository.deleteAll();

    }
}