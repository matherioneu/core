package eu.matherion.core.shared.currency;

import cz.maku.mommons.ef.annotation.AttributeConvert;
import cz.maku.mommons.ef.annotation.Entity;
import eu.matherion.core.shared.player.MatherionPlayer;
import eu.matherion.core.shared.player.PlayerEntityTypeConverter;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity(name = "core_currencies", repositoryClass = CurrencyRepository.class)
public class CurrencyData {

    @AttributeConvert(converter = CurrencyEntityTypeConverter.class)
    private Currency currency;
    //@AttributeConvert(converter = PlayerEntityTypeConverter.class)
    private String player;
    private double amount;
}
