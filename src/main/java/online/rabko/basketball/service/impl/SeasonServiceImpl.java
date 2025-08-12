package online.rabko.basketball.service.impl;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import online.rabko.basketball.entity.Season;
import online.rabko.basketball.repository.SeasonRepository;
import org.springframework.stereotype.Service;

/**
 * Service for managing {@link Season} entities.
 */
@Service
@RequiredArgsConstructor
public class SeasonServiceImpl implements online.rabko.basketball.service.SeasonService {

    private final SeasonRepository seasonRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Season> findAll() {
        return seasonRepository.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Season findById(Long id) {
        return seasonRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Season not found: " + id));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Season create(Season season) {
        validateDates(season.getStartDate(), season.getEndDate());
        if (seasonRepository.existsByNameIgnoreCase(season.getName())) {
            throw new EntityExistsException("Season already exists: " + season.getName());
        }
        if (seasonRepository.overlaps(season.getStartDate(), season.getEndDate(), null)) {
            throw new EntityExistsException("Season dates overlap with an existing season.");
        }
        return seasonRepository.save(season);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Season update(Long id, Season season) {
        Season existing = findById(id);
        validateDates(season.getStartDate(), season.getEndDate());

        if (seasonRepository.existsByNameIgnoreCase(season.getName())
            && !Objects.equals(existing.getName(), season.getName())) {
            throw new EntityExistsException("Season already exists: " + season.getName());
        }
        if (seasonRepository.overlaps(season.getStartDate(), season.getEndDate(), id)) {
            throw new EntityExistsException("Season dates overlap with an existing season.");
        }

        season.setId(id);
        return seasonRepository.save(season);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Long id) {
        if (!seasonRepository.existsById(id)) {
            throw new EntityNotFoundException("Season not found: " + id);
        }
        seasonRepository.deleteById(id);
    }

    private void validateDates(LocalDate start, LocalDate end) {
        if (Objects.isNull(start) || Objects.isNull(end)) {
            throw new IllegalArgumentException("Season startDate and endDate are required.");
        }
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("Season endDate must not be before startDate.");
        }
    }
}
