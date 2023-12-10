package edu.northeastern.cs5500.starterbot.model;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

public class BattleTest {
    final String myDiscordId = "trainer1";
    final String opponentDiscordId = "trainer2";
    final String newTrainerId = "trainer3";
    final String newopponentDiscordId = "trainer4";
    final String pokemonId = "pokemon1";
    final String opponentPokemonId = "pokemon2";
    final String newPokemonId = "pokemon3";
    final String newOpponentPokemonId = "pokemon4";

    Battle battle = new Battle("trainer1", "trainer2", "pokemon1", "pokemon2");

    @Test
    void testBattleCreation() {
        assertThat(battle.getMyDiscordId()).isEqualTo(myDiscordId);
        assertThat(battle.getOpponentDiscordId()).isEqualTo(opponentDiscordId);
        assertThat(battle.getMyPokemonId()).isEqualTo(pokemonId);
        assertThat(battle.getOpponentPokemonId()).isEqualTo(opponentPokemonId);
    }

    @Test
    void testBattleSetters() {
        battle.setMyDiscordId(newTrainerId);
        battle.setOpponentDiscordId(newopponentDiscordId);
        battle.setMyPokemonId(newPokemonId);
        battle.setOpponentPokemonId(newOpponentPokemonId);

        assertThat(battle.getMyDiscordId()).isEqualTo(newTrainerId);
        assertThat(battle.getOpponentDiscordId()).isEqualTo(newopponentDiscordId);
        assertThat(battle.getMyPokemonId()).isEqualTo(newPokemonId);
        assertThat(battle.getOpponentPokemonId()).isEqualTo(newOpponentPokemonId);
    }

    @Test
    void testSetTrainerIdNullValue() {
        try {
            battle.setMyDiscordId(null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            assertThat(e.getMessage()).isEqualTo("myDiscordId is marked non-null but is null");
        }
    }

    @Test
    void testSetopponentDiscordIdNullValue() {
        try {
            battle.setOpponentDiscordId(null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            assertThat(e.getMessage())
                    .isEqualTo("opponentDiscordId is marked non-null but is null");
        }
    }

    @Test
    void testSetPokemonIdNullValue() {
        try {
            battle.setMyPokemonId(null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            assertThat(e.getMessage()).isEqualTo("myPokemonId is marked non-null but is null");
        }
    }

    @Test
    void testSetOtherPokemonIdNullValue() {
        try {
            battle.setOpponentPokemonId(null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            assertThat(e.getMessage())
                    .isEqualTo("opponentPokemonId is marked non-null but is null");
        }
    }

    @Test
    void testEquals_comparisonToSelf() {
        assertThat(battle).isEqualTo(battle);
    }

    @Test
    void testEquals_nullComparison() {
        assertThat(battle).isNotEqualTo(null);
    }

    @Test
    void testEquals_differentDataTypes() {
        Object differentBattle = new Object();
        assertThat(battle).isNotEqualTo(differentBattle);
    }

    @Test
    void testToString() {
        String expectedString =
                "Battle(myDiscordId=trainer1, opponentDiscordId=trainer2, myPokemonId=pokemon1, opponentPokemonId=pokemon2)";
        assertEquals(expectedString, battle.toString());
    }
}
