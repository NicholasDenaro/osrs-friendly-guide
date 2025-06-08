package dev.denaro;

import com.google.inject.Provides;
import javax.inject.Inject;

import dev.denaro.dialog.Dialog;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.events.CommandExecuted;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.MenuOpened;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.chatbox.ChatboxPanelManager;
import net.runelite.client.input.MouseManager;
import net.runelite.client.menus.MenuManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import java.awt.*;
import java.util.Arrays;

@Slf4j
@PluginDescriptor(
	name = "Friendly Guide"
)
public class FriendlyGuidePlugin extends Plugin // implements MouseListener
{
	@Getter
	@Inject
	private Client client;

	@Getter
	@Inject
	private FriendlyGuideConfig config;

	@Getter
	@Inject
	private ChatMessageManager chatMessageManager;

	@Getter
	@Inject
	private ChatboxPanelManager chatboxPanelManager;

	@Inject
	private MenuManager menuManager;

	@Inject
	private MouseManager mouseManager;

	@Inject
	private ClientThread clientThread;

	private DialogBox dialog;
	private Guide guide;

	@Override
	protected void startUp() throws Exception
	{
		log.info("Friendly Guide started!");
//		mouseManager.registerMouseListener(this);
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Example stopped!");
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Example says " + config.greeting(), null);
		}
	}

	@Provides
	FriendlyGuideConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(FriendlyGuideConfig.class);
	}


	@Subscribe
	public void onCommandExecuted(CommandExecuted command)
	{
		System.out.println(command.getCommand());
		String cmd = command.getCommand();
		if (cmd.equals("guide"))
		{
			this.dialog = new DialogBox(this, Dialog.createDialogTree(this));
			this.chatboxPanelManager.openInput(this.dialog);
			System.out.println("dialog opened");
		}
		if (cmd.equals("spawn"))
		{
			this.guide = new Guide(this);
			this.guide.show();
		}
	}

	@Subscribe
	public void onMenuOpened(MenuOpened event)
	{
		if (this.guide != null)
		{
			Shape shape = Perspective.getClickbox(this.client, this.guide.getModel(), this.guide.getOrientation(), this.guide.getX(), this.guide.getY(), this.guide.getZ());
			Point point = this.client.getMouseCanvasPosition();
			if (shape != null && shape.contains(point.getX(), point.getY()))
			{
				clientThread.invokeLater(() -> {
					MenuEntry entry = this.client.getMenu().createMenuEntry(-1);
					entry.setOption("Talk to Friendly Guide");
					entry.setType(MenuAction.EXAMINE_NPC);
				});
			}
		}
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked menuOptionClicked)
	{
		if (this.guide != null)
		{
			Shape shape = Perspective.getClickbox(this.client, this.guide.getModel(), this.guide.getOrientation(), this.guide.getX(), this.guide.getY(), this.guide.getZ());
			Point point = this.client.getMouseCanvasPosition();
			if (shape != null && shape.contains(point.getX(), point.getY()))
			{
				System.out.println("clicked guide");

				this.dialog = new DialogBox(this, Dialog.createDialogTree(this));
				this.chatboxPanelManager.openInput(this.dialog);

				menuOptionClicked.consume();
			}
		}
	}
}
