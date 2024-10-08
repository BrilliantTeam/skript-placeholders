package io.github.apickledwalrus.skriptplaceholders.skript.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Events;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import io.github.apickledwalrus.skriptplaceholders.skript.PlaceholderEvent;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Placeholder Result")
@Description("The value of a placeholder in a placeholder event. Can be set, reset, or deleted.")
@Examples({
	"placeholderapi placeholder with the prefix \"skriptplaceholders\":",
		"\tif the identifier is \"author\": # Placeholder is \"%skriptplaceholders_author%\"",
			"\t\tset the result to \"APickledWalrus\"",
	"mvdw placeholder named \"skriptplaceholders_author\":",
		"\t# Placeholder is \"{skriptplaceholders_author}\"",
		"\tset the result to \"APickledWalrus\""
})
@Since("1.0.0, 1.3.0 (MVdWPlaceholderAPI support), 1.6.0 (using any type support)")
@Events("Placeholder Request")
public class ExprPlaceholderResult extends SimpleExpression<String> {

	static {
		Skript.registerExpression(ExprPlaceholderResult.class, String.class, ExpressionType.SIMPLE,
				"[the] [placeholder] result"
		);
	}

	@Override
	public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
		if (!getParser().isCurrentEvent(PlaceholderEvent.class)) {
			Skript.error("'the placeholder result' can only be used in custom placeholders");
			return false;
		}
		return true;
	}

	@Override
	protected String @NotNull [] get(@NotNull Event event) {
		return new String[]{((PlaceholderEvent) event).getResult()};
	}

	@Override
	public Class<?> @NotNull [] acceptChange(ChangeMode mode) {
		switch (mode) {
			case SET:
			case DELETE:
			case RESET:
				return CollectionUtils.array(Object.class);
			default:
				//noinspection ConstantConditions - Skript annotated this wrong
				return null;
		}
	}

	@Override
	public void change(@NotNull Event event, Object @NotNull [] delta, ChangeMode mode) {
		PlaceholderEvent placeholderEvent = ((PlaceholderEvent) event);
		switch (mode) {
			case SET:
				if (delta[0] instanceof String) {
					placeholderEvent.setResult((String) delta[0]);
				} else {
					placeholderEvent.setResult(Classes.toString(delta[0]));
				}
				break;
			case DELETE:
			case RESET:
				placeholderEvent.setResult(null);
				break;
			default:
				assert false;
		}
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public @NotNull Class<? extends String> getReturnType() {
		return String.class;
	}

	@Override
	public @NotNull String toString(@Nullable Event event, boolean debug) {
		return "the placeholder result";
	}

}
