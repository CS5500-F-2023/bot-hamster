package edu.northeastern.cs5500.starterbot.controller;

import static com.google.common.truth.Truth.assertThat;

import edu.northeastern.cs5500.starterbot.model.Battle;
import edu.northeastern.cs5500.starterbot.model.Pokemon;
import edu.northeastern.cs5500.starterbot.repository.InMemoryRepository;
import org.junit.jupiter.api.Test;

class BattleControllerTest {

    final String USER_1_ID = "trainer1";
    final String USER_2_ID = "trainer2";
    final String USER_3_ID = "trainer3";

    BattleController getBattleController() {
        PokemonController pokemonController = getPokemonController();
        PokedexController pokedexController = new PokedexController();

        TrainerController trainerController =
                getTrainerController(pokemonController, pokedexController);
        BattleController battleController = new BattleController(trainerController);
        return battleController;
    }
    BattleController getBattleController(TrainerController trainerController) {
        BattleController battleController = new BattleController(trainerController);
        return battleController;
    }

    PokemonController getPokemonController() {
        return new PokemonController(new InMemoryRepository<>());
    }

    TrainerController getTrainerController(
            PokemonController pokemonController, PokedexController pokedexController) {
        TrainerController trainerController =
                new TrainerController(
                        new InMemoryRepository<>(), pokemonController, pokedexController);

        trainerController.addPokemonToTrainer(
                USER_1_ID, pokemonController.catchPokemon(1).getId().toString());
        trainerController.addPokemonToTrainer(
                USER_2_ID, pokemonController.catchPokemon(4).getId().toString());

        return trainerController;
    }

    private Pokemon createPokemon(int pokedexNumber, int hp, int attack, int defense, int specialAttack, int specialDefense, int speed, int total) {
        return Pokemon.builder()
                .pokedexNumber(pokedexNumber)
                .hp(hp)
                .attack(attack)
                .defense(defense)
                .specialAttack(specialAttack)
                .specialDefense(specialDefense)
                .speed(speed)
                .total(total)
                .build();
    }

    @Test
    void testBattleWithSelf() {
        BattleController battleController = getBattleController();
        assertThat(battleController.battleWithSelf(USER_1_ID, USER_1_ID)).isTrue();
        assertThat(battleController.battleWithSelf(USER_1_ID, USER_2_ID)).isFalse();
    }

    @Test
    void testValidateBattleTeam() {
        PokemonController pokemonController = getPokemonController();
        TrainerController trainerController =
                getTrainerController(pokemonController, new PokedexController());
        BattleController battleController = getBattleController();

        boolean result1 = battleController.validateBattleTeam(USER_1_ID);
        boolean result2 = battleController.validateBattleTeam(USER_2_ID);
        boolean result3 = battleController.validateBattleTeam(USER_3_ID);

        assertThat(result1).isTrue();
        assertThat(result2).isTrue();
        assertThat(result3).isFalse();
    }

    @Test
    void testCreateBattle() {
        BattleController battleController = getBattleController();

        Battle battle = battleController.createBattle(USER_1_ID, USER_2_ID);

        assertThat(battle.getMyDiscordId()).isEqualTo(USER_1_ID);
        assertThat(battle.getOpponentDiscordId()).isEqualTo(USER_2_ID);

        assertThat(battle.getMyPokemonId()).isNotNull();
        assertThat(battle.getOpponentPokemonId()).isNotNull();
    }

    @Test
    void testCompareTotalNullPokemon() {
        PokemonController pokemonController = getPokemonController();
        TrainerController trainerController =
                getTrainerController(pokemonController, new PokedexController());
        BattleController battleController = getBattleController();

        Battle battle = battleController.createBattle(USER_1_ID, USER_2_ID);

        String result = battleController.compareTotal(battle);

        assertThat(battle).isNotNull();
        assertThat(result).isEqualTo("Can't retrieve Pokemon");
    }

    @Test
    void testCompareTotal() {
        PokemonController pokemonController = getPokemonController();
        TrainerController trainerController =
                getTrainerController(pokemonController, new PokedexController());
        BattleController battleController = getBattleController(trainerController);

        Pokemon myPokemon = createPokemon(1, 100, 50, 30, 40, 30, 60, 250);
        Pokemon opponentPokemon = createPokemon(2, 80, 60, 40, 30, 35, 55, 260);

        trainerController.addPokemonToTrainer(USER_1_ID, myPokemon.getId().toString());
        trainerController.addPokemonToTrainer(USER_2_ID, opponentPokemon.getId().toString());

        Battle battle = new Battle(USER_1_ID, USER_2_ID, myPokemon.getId().toString(), opponentPokemon.getId().toString());

        String result = battleController.compareTotal(battle);
        assertThat(result).isEqualTo("Can't retrieve Pokemon");
    }
}
