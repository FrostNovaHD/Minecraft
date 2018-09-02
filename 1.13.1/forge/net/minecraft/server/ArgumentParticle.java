package net.minecraft.server;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class ArgumentParticle implements ArgumentType<ParticleParam> {
    private static final Collection<String> b = Arrays.asList("foo", "foo:bar", "particle with options");
    public static final DynamicCommandExceptionType a = new DynamicCommandExceptionType((object) -> {
        return new ChatMessage("particle.notFound", new Object[]{object});
    });

    public ArgumentParticle() {
    }

    public static ArgumentParticle a() {
        return new ArgumentParticle();
    }

    public static ParticleParam a(CommandContext<CommandListenerWrapper> commandcontext, String s) {
        return (ParticleParam)commandcontext.getArgument(s, ParticleParam.class);
    }

    public ParticleParam a(StringReader stringreader) throws CommandSyntaxException {
        return b(stringreader);
    }

    public Collection<String> getExamples() {
        return b;
    }

    public static ParticleParam b(StringReader stringreader) throws CommandSyntaxException {
        MinecraftKey minecraftkey = MinecraftKey.a(stringreader);
        Particle particle = IRegistry.PARTICLE_TYPE.get(minecraftkey);
        if (particle == null) {
            throw a.create(minecraftkey);
        } else {
            return a(stringreader, particle);
        }
    }

    private static <T extends ParticleParam> T a(StringReader stringreader, Particle<T> particle) throws CommandSyntaxException {
        return (T)particle.f().b(particle, stringreader);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder suggestionsbuilder) {
        return ICompletionProvider.a(IRegistry.PARTICLE_TYPE.keySet(), suggestionsbuilder);
    }

    // $FF: synthetic method
    public Object parse(StringReader stringreader) throws CommandSyntaxException {
        return this.a(stringreader);
    }
}