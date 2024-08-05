package com.akay.testproject.Service;


import com.akay.testproject.Entity.Country;
import com.akay.testproject.Repository.CountryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class CountryServiceIntegrationTest {

    @Autowired
    private  CountryRepository countryRepository;

    @Autowired
    private CountryService countryService;

    @Test
    public void shouldCreateCountryWithCountry() {
        Country country = Country.builder()
                .name("Turkiye")
                .code("TR")
                .build();

        Country createdCountry = countryService.createCountry(country);

        assertNotNull(createdCountry, "Country is null");
        assertThat(createdCountry.getName()).isEqualTo(country.getName());
        assertThat(createdCountry.getCode()).isEqualTo(country.getCode());
    }

    @Test
    public void shouldUpdateCountryWithIdAndCountryDetails() {
        Country country = Country.builder()
                .name("Turkiye")
                .code("TR")
                .build();

        countryRepository.save(country);

        Country updatedCountry = countryService.updateCountry(country.getId(), country);

        assertNotNull(updatedCountry, "Country is null");
        assertThat(updatedCountry.getName()).isEqualTo(country.getName());
        assertThat(updatedCountry.getCode()).isEqualTo(country.getCode());

    }

    @Test
    public void shouldThrowExceptionWhenUpdateCountryWithIdAndCountryDetails() {
        Long nonExistingId = 44L;

        Country country = Country.builder()
                .name("Turkiye")
                .code("TR")
                .build();

        assertThatThrownBy(() -> countryService.updateCountry(nonExistingId, country))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Country not found with id " + nonExistingId);
    }

    @Test
    public void shouldDeleteCountryWithId() {
        Country country = Country.builder()
                .name("Palestina")
                .code("PL")
                .build();

        countryRepository.save(country);

        countryService.deleteCountry(country.getId());

        assertThat(countryRepository.findById(country.getId())).isEmpty();
    }

    @Test
    public void shouldFindAllCategoryWithPageable() {
        countryRepository.deleteAll();

        countryRepository.save(Country.builder().name("Turkey").code("TR").build());
        countryRepository.save(Country.builder().name("Germany").code("DE").build());
        countryRepository.save(Country.builder().name("France").code("FR").build());

        List<Country> countries = countryRepository.findAll();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Country> page = new PageImpl<>(countries, pageable, countries.size());

        page = countryService.findAll(pageable);

        assertNotNull(page, "Country page should not be null");
        assertThat(page.getContent().size()).isEqualTo(3);
    }



}

