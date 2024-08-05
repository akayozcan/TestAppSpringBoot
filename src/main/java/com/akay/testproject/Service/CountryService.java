package com.akay.testproject.Service;

import com.akay.testproject.Entity.Country;
import com.akay.testproject.Repository.CountryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CountryService {

    private final CountryRepository countryRepository;

    public CountryService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    public Country createCountry(Country country) {
        return countryRepository.save(country);
    }

    public Country updateCountry(Long id, Country countryDetails) {
        Optional<Country> optionalCountry = countryRepository.findById(id);
        if (optionalCountry.isPresent()) {
            Country country = optionalCountry.get();
            country.setName(countryDetails.getName());
            country.setCode(countryDetails.getCode());
            return countryRepository.save(country);
        }
        throw new RuntimeException("Country not found with id " + id);
    }

    public void deleteCountry(Long id) {
        countryRepository.deleteById(id);
    }

    public Page<Country> findAll(Pageable pageable) {
        return countryRepository.findAll(pageable);
    }
}
