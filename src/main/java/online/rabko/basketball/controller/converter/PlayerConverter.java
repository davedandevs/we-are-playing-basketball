package online.rabko.basketball.controller.converter;

import java.util.Objects;
import online.rabko.basketball.entity.Player;
import org.springframework.stereotype.Component;

/**
 * Converts between {@link Player} entity and {@link online.rabko.model.Player} DTO.
 */
@Component
public class PlayerConverter extends TwoWayConverter<Player, online.rabko.model.Player> {

    @Override
    public online.rabko.model.Player convert(Player source) {
        return new online.rabko.model.Player()
            .id(source.getId())
            .teamId(Objects.nonNull(source.getTeam()) ? source.getTeam().getId() : null)
            .firstName(source.getFirstName())
            .lastName(source.getLastName())
            .position(source.getPosition())
            .age(source.getAge())
            .height(source.getHeight())
            .weight(source.getWeight());
    }

    @Override
    public Player convertBack(online.rabko.model.Player dto) {
        return Player.builder()
            .id(dto.getId())
            .firstName(dto.getFirstName())
            .lastName(dto.getLastName())
            .position(dto.getPosition())
            .age(dto.getAge())
            .height(dto.getHeight())
            .weight(dto.getWeight())
            .build();
    }
}
