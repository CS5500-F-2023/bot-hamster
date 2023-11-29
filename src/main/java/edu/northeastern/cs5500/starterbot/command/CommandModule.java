package edu.northeastern.cs5500.starterbot.command;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;

@Module
public class CommandModule {

    @Provides
    @IntoMap
    @StringKey(SayCommand.NAME)
    public SlashCommandHandler provideSayCommand(SayCommand sayCommand) {
        return sayCommand;
    }

    @Provides
    @IntoMap
    @StringKey(PreferredNameCommand.NAME)
    public SlashCommandHandler providePreferredNameCommand(
            PreferredNameCommand preferredNameCommand) {
        return preferredNameCommand;
    }

    @Provides
    @IntoMap
    @StringKey(FailureCommand.NAME)
    public SlashCommandHandler provideFailureCommand(FailureCommand failureCommand) {
        return failureCommand;
    }

    @Provides
    @IntoMap
    @StringKey(ButtonCommand.NAME)
    public SlashCommandHandler provideButtonCommand(ButtonCommand buttonCommand) {
        return buttonCommand;
    }

    @Provides
    @IntoMap
    @StringKey(ButtonCommand.NAME)
    public ButtonHandler provideButtonCommandClickHandler(ButtonCommand buttonCommand) {
        return buttonCommand;
    }

    @Provides
    @IntoMap
    @StringKey(DropdownCommand.NAME)
    public SlashCommandHandler provideDropdownCommand(DropdownCommand dropdownCommand) {
        return dropdownCommand;
    }

    @Provides
    @IntoMap
    @StringKey(DropdownCommand.NAME)
    public StringSelectHandler provideDropdownCommandMenuHandler(DropdownCommand dropdownCommand) {
        return dropdownCommand;
    }

    @Provides
    @IntoMap
    @StringKey(CatchCommand.NAME)
    public SlashCommandHandler provideCatchCommand(CatchCommand command) {
        return command;
    }

    @Provides
    @IntoMap
    @StringKey(CatchCommand.NAME)
    public ButtonHandler provideCatchCommandClickHandler(CatchCommand command) {
        return command;
    }

    @Provides
    @IntoMap
    @StringKey(HealCommand.NAME)
    public SlashCommandHandler provideHealCommand(HealCommand command) {
        return command;
    }

    @Provides
    @IntoMap
    @StringKey(HealCommand.NAME)
    public StringSelectHandler provideHealCommandMenuHandler(HealCommand dropdownCommand) {
        return dropdownCommand;
    }

    @Provides
    @IntoMap
    @StringKey(SearchCommand.NAME)
    public SlashCommandHandler provideSearchCommand(SearchCommand command) {
        return command;
    }

    @Provides
    @IntoMap
    @StringKey(HomeCommand.NAME)
    public SlashCommandHandler provideHomeCommand(HomeCommand command) {
        return command;
    }

    @Provides
    @IntoMap
    @StringKey(HomeCommand.NAME)
    public ButtonHandler provideHomeCommandprovideHomeButtonHandler(HomeCommand command) {
        return command;
    }

    @Provides
    @IntoMap
    @StringKey(TradeCommand.NAME)
    public SlashCommandHandler provideTradeCommand(TradeCommand command) {
        return command;
    }

    @Provides
    @IntoMap
    @StringKey(TradeCommand.NAME)
    public StringSelectHandler provideTradeCommandMenuHandler(TradeCommand dropdownCommand) {
        return dropdownCommand;
    }

    @Provides
    @IntoMap
    @StringKey(RulesCommand.NAME)
    public SlashCommandHandler provideRulesCommand(RulesCommand command) {
        return command;
    }

    @Provides
    @IntoMap
    @StringKey(SellCommand.NAME)
    public SlashCommandHandler provideSellCommand(SellCommand command) {
        return command;
    }

    @Provides
    @IntoMap
    @StringKey(SellCommand.NAME)
    public StringSelectHandler provideSellCommandMenuHandler(SellCommand dropdownCommand) {
        return dropdownCommand;
    }

    @Provides
    @IntoMap
    @StringKey(ShopCommand.NAME)
    public SlashCommandHandler provideShopCommand(ShopCommand command) {
        return command;
    }

    @Provides
    @IntoMap
    @StringKey(ShopCommand.NAME)
    public StringSelectHandler provideShopCommandMenuHandler(ShopCommand command) {
        return command;
    }

    @Provides
    @IntoMap
    @StringKey(ShopCommand.NAME)
    public ButtonHandler provideShopCommandClickHandler(ShopCommand command) {
        return command;
    }
}
