package net.silthus.mcgamelib.event;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import lombok.Getter;
import lombok.NonNull;
import net.silthus.mcgamelib.Game;
import net.silthus.mcgamelib.GameMode;
import net.silthus.mcgamelib.TestBase;
import net.silthus.mcgamelib.event.filters.Playing;
import net.silthus.mcgamelib.event.filters.Spectating;
import net.silthus.mcgamelib.events.JoinGameEvent;
import net.silthus.mcgamelib.events.JoinedGameEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class GameEventHandlerTests extends TestBase {

    private final ArgumentCaptor<JoinGameEvent> joinGameEvent = ArgumentCaptor.forClass(JoinGameEvent.class);
    private final ArgumentCaptor<JoinedGameEvent> joinedGameEvent = ArgumentCaptor.forClass(JoinedGameEvent.class);

    private GameEventHandler eventHandler;
    private Game game;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        eventHandler = new GameEventHandler(plugin);
        game = new Game(GameMode.builder().build());
    }

    @Test
    void create() {
        assertThat(eventHandler.getPlugin())
                .isEqualTo(plugin);
    }

    @Test
    void getListeners_isEmpty() {
        assertThat(eventHandler.getListeners())
                .isEmpty();
    }

    @Test
    void getListeners_isImmutable() {
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> eventHandler.getListeners().add(GameEventListener.empty()));
    }

    @Test
    void callEvent_withIgnoredFilters_callsJoinGameEvent() {

        IgnoreFilterEventListener listener = spy(new IgnoreFilterEventListener());
        eventHandler.registerEvents(game, listener);

        game.join(server.addPlayer());

        verify(listener).onJoinGameEvent(any());
    }

    @Test
    void callNativeBukkitEvent_andFilter_ifGameEvent() {

        NativeBukkitGameEventListener listener = spy(new NativeBukkitGameEventListener());
        eventHandler.registerEvents(game, listener);

        PlayerMock player = server.addPlayer();
        verify(listener, never()).onPlayerJoin(any());

        game.join(player);
        server.addPlayer(player);
        verify(listener).onPlayerJoin(any());
    }

    @Test
    void catchAnyEventException() {

        ErrorListener listener = spy(new ErrorListener());
        doAnswer(invocation -> {throw new RuntimeException();}).when(listener).onPlayerJoin(any());
        eventHandler.registerEvents(game, listener);

        assertThatCode(() -> callEvent(new PlayerJoinEvent(new PlayerMock(server, "test"), "")))
                .doesNotThrowAnyException();
        verify(listener).onPlayerJoin(any());
    }

    @Test
    void nonPlayerEvents_areFired() {

        NonPlayerEventListener listener = spy(new NonPlayerEventListener());
        eventHandler.registerEvents(game, listener);

        callEvent(new ServerLoadEvent(ServerLoadEvent.LoadType.RELOAD));

        verify(listener).onServerStart(any());
    }

    @Test
    void playerFilter_catchesIllegalAccessSilently() {

        ErrorListener listener = spy(new ErrorListener());
        doAnswer(invocation -> {throw new IllegalAccessError();}).when(listener).onPlayerJoin(any());
        eventHandler.registerEvents(game, listener);

        assertThatCode(() -> server.addPlayer())
                .doesNotThrowAnyException();

        verify(listener).onPlayerJoin(any());
    }

    @Test
    void customEvent_cannotGetPlayer_playerFilterIsIgnored() {

        CustomPlayerEventListener listener = spy(new CustomPlayerEventListener());
        eventHandler.registerEvents(game, listener);

        callEvent(new CustomPlayerEvent());

        verify(listener).onCustomEvent(any());
    }

    @Nested
    @DisplayName("with single event")
    class SingleEvents {


        private SingleEventListener listener;

        @BeforeEach
        void setUp() {
            listener = spy(new SingleEventListener());
            eventHandler.registerEvents(game, listener);
        }

        @Test
        void registerEvents_registersListener() {

            assertThat(eventHandler.getListeners())
                    .hasSize(1)
                    .first()
                    .isInstanceOf(GameEventListener.class)
                    .extracting(
                            GameEventListener::listener,
                            GameEventListener::game,
                            GameEventListener::eventClass
                    ).contains(
                            listener,
                            game,
                            JoinedGameEvent.class
                    );
            assertThat(eventHandler.getListeners().get(0).filters())
                    .hasSize(1)
                    .allMatch(gameEventFilter -> gameEventFilter instanceof Playing);
        }

        @Test
        void eventIsFired_afterRegister() {

            game.join(server.addPlayer());

            verify(listener).onJoinedGameEvent(joinedGameEvent.capture());
            verify(listener).onPlayerJoin(any());
        }
        @Test
        void registerEvents_registersBukkitListener() {

            assertThat(HandlerList.getRegisteredListeners(plugin))
                    .anyMatch(registeredListener -> registeredListener.getListener().equals(listener));
        }
    }
    @Nested
    @DisplayName("with multiple event listeners")
    class MultiEvents {

        private MultiEventListener listener;

        @BeforeEach
        void setUp() {
            listener = spy(new MultiEventListener());
            eventHandler.registerEvents(game, listener);
        }

        @Test
        void registerEvents_registersAllListeners() {

            assertThat(eventHandler.getListeners())
                    .hasSize(3);
        }
        @Test
        void bothEvents_fired() {

            game.join(server.addPlayer());

            verify(listener).onJoinedGameEvent(joinedGameEvent.capture());
            verify(listener).onJoinGameEvent(joinGameEvent.capture());
            verify(listener).onPlayerJoin(any());
        }

    }
    @Nested
    @DisplayName("with multiple listeners")
    class MultipleListeners {

        private SingleEventListener singleListener;
        private MultiEventListener multiListener;

        @BeforeEach
        void setUp() {
            singleListener = spy(new SingleEventListener());
            multiListener = spy(new MultiEventListener());
            eventHandler.registerEvents(game, singleListener);
            eventHandler.registerEvents(game, multiListener);
        }

        @Test
        void registerEvents_registersAllListeners() {

            assertThat(eventHandler.getListeners())
                    .hasSize(4);
        }

        @Test
        void allEventsFiredOnce() {

            PlayerMock player = server.addPlayer();
            game.join(player);

            verify(singleListener).onJoinedGameEvent(any());
            verify(multiListener).onJoinedGameEvent(any());
            verify(multiListener).onJoinedGameEventTwo(any());
            verify(multiListener).onJoinGameEvent(any());
            verify(singleListener).onPlayerJoin(any());
        }

    }
    @Nested
    @DisplayName("with multiple games")
    class WithMultipleGames {

        private Game game1;
        private Game game2;
        private SingleEventListener listenerGame1;

        private SingleEventListener listenerGame2;

        @BeforeEach
        void setUp() {

            game1 = new Game(GameMode.builder().build());
            game2 = new Game(GameMode.builder().build());
            listenerGame1 = spy(new SingleEventListener());
            listenerGame2 = spy(new SingleEventListener());
            eventHandler.registerEvents(game1, listenerGame1);
            eventHandler.registerEvents(game2, listenerGame2);
        }
        @Test
        void callEvent_filtersByPlayers_inGame() {

            PlayerMock player = server.addPlayer();
            game1.join(player);

            verify(listenerGame1).onJoinedGameEvent(joinedGameEvent.capture());
            assertThat(joinedGameEvent.getValue().getPlayer()).isEqualTo(player);

            verify(listenerGame2, never()).onJoinedGameEvent(any());
        }

    }

    static class SingleEventListener implements Listener {

        @GameEvent
        public void onJoinedGameEvent(JoinedGameEvent event) {

        }

        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent event) {

        }
    }

    static class MultiEventListener implements Listener {

        @GameEvent(ignoreFilters = true)
        public void onJoinGameEvent(JoinGameEvent event) {

        }

        @GameEvent
        public void onJoinedGameEvent(JoinedGameEvent event) {

        }

        @GameEvent
        public void onJoinedGameEventTwo(JoinedGameEvent event) {

        }

        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent event) {

        }
    }

    static class IgnoreFilterEventListener implements Listener {

        @GameEvent(ignoreFilters = true)
        public void onJoinGameEvent(JoinGameEvent event) {

        }
    }

    static class NativeBukkitGameEventListener implements Listener {

        @GameEvent
        public void onPlayerJoin(PlayerJoinEvent event) {

        }
    }

    static class ErrorListener implements Listener {

        @GameEvent(ignoreFilters = true)
        public void onPlayerJoin(PlayerJoinEvent event) {

        }
    }

    static class NonPlayerEventListener implements Listener {

        @GameEvent
        public void onServerStart(ServerLoadEvent event) {

        }
    }

    static class CustomPlayerEventListener implements Listener {

        @GameEvent
        public void onCustomEvent(CustomPlayerEvent event) {

        }
    }

    static class CustomPlayerEvent extends Event {

        private final Player player = new PlayerMock(null, "test");

        public Player getPlayer() throws IllegalAccessException {
            throw new IllegalAccessException("");
        }

        @Getter
        private static final HandlerList handlerList = new HandlerList();

        @Override
        @NonNull
        public HandlerList getHandlers() {
            return handlerList;
        }
    }

    @Nested
    @DisplayName("with custom filter")
    class WithCustomFilter {

        private CustomFilterListener listener;

        @BeforeEach
        void setUp() {
            listener = spy(new CustomFilterListener());
            eventHandler.registerEvents(game, listener);
        }

        @Test
        void should_apply_customFilter() {

            callEvent(new JoinedGameEvent(game, server.addPlayer()));

            verify(listener).onJoinedGame(any());
        }

        @Test
        void should_onlyApplyToSpectators() {

            PlayerMock player = server.addPlayer();
            game.spectate(player);
            BlockPlaceEvent event = player.simulateBlockPlace(Material.STONE, new Location(server.addSimpleWorld("test"), 1, 2, 3));

            assertThat(event)
                    .isNotNull()
                    .extracting(BlockPlaceEvent::isCancelled)
                    .isEqualTo(true);
            verify(listener).onPlaceBlock(event);
        }

        @Test
        void shouldCatchErrorsAtFilterInstantiation() {

            callEvent(new ServerLoadEvent(ServerLoadEvent.LoadType.RELOAD));

            verify(listener).onErrorFilter(any());
        }
    }

    static class CustomFilterListener implements Listener {

        @GameEvent(filters = {CustomFilter.class})
        public void onJoinedGame(JoinedGameEvent event) {

        }

        @GameEvent(filters = {Spectating.class})
        public void onPlaceBlock(BlockPlaceEvent event) {
            event.setCancelled(true);
        }

        @GameEvent(filters = {ErrorFilter.class})
        public void onErrorFilter(ServerLoadEvent event) {

        }
    }

    public static class CustomFilter implements GameEventFilter {

        @Override
        public boolean test(GameEventListener listener, Event event) {
            return true;
        }
    }

    private static class ErrorFilter implements GameEventFilter {
        private ErrorFilter() {
        }

        @Override
        public boolean test(GameEventListener listener, Event event) {
            return false;
        }
    }
}
