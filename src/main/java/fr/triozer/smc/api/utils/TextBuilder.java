package fr.triozer.smc.api.utils;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * @author Cédric / Triozer
 */
public class TextBuilder {

    private TextComponent textComponent;

    public TextBuilder(String string) {
        textComponent = new TextComponent(string);
    }

    public TextBuilder click(ClickEvent.Action action, String value) {
        textComponent.setClickEvent(new ClickEvent(action, value));

        return this;
    }

    public TextBuilder hove(HoverEvent.Action action, String value) {
        textComponent.setHoverEvent(new HoverEvent(action, new ComponentBuilder(value).create()));

        return this;
    }

    public TextComponent build() {
        return textComponent;
    }

}
