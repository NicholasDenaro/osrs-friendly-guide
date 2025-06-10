package dev.denaro;

import com.google.inject.Provides;
import javax.inject.Inject;

import com.google.inject.name.Named;
import dev.denaro.dialog.Dialog;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.events.*;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.chatbox.ChatboxPanelManager;
import net.runelite.client.menus.MenuManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;

import java.awt.*;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

@Slf4j
@PluginDescriptor(
	name = "Friendly Guide"
)
public class FriendlyGuidePlugin extends Plugin
{
	@Getter
	@Inject
	private Client client;

	@Getter
	@Inject
	private FriendlyGuideConfig config;

	@Inject
	private ConfigManager configManager;

	@Inject
	private OverlayManager overlayManager;

	@Getter
	@Inject
	private ChatMessageManager chatMessageManager;

	@Getter
	@Inject
	private ChatboxPanelManager chatboxPanelManager;

	@Inject
	private MenuManager menuManager;

	@Inject
	private TooltipManager tooltipManager;

	@Inject
	private ClientThread clientThread;

	private FriendlyGuideOverlay overlay;

	@Inject
	@Named("developerMode") boolean developerMode;

	private final HttpClient httpClient = HttpClient.newHttpClient();

	@Override
	protected void startUp()
	{
		log.info("Friendly Guide started!");

		this.overlayManager.add(overlay = new FriendlyGuideOverlay());

		Guide.firstLoad(this, this.clientThread);

		new Thread(() -> {
            try {
				System.out.println("Loading cooks assistant.yml");
                URI uri = new URI("https://github.com/NicholasDenaro/osrs-friendly-guide-responses/releases/download/Latest/merged.yml");
				HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).header("IF-NONE-MATCH", this.config.etag()).build();
				CompletableFuture<HttpResponse<String>> future = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
				future.thenAccept(response ->
				{
					if (response.statusCode() == 302)
					{
                        try {
                            URI redirect = new URI(response.headers().firstValue("Location").get());
							System.out.println("Fetching contents from " + redirect.toString().split("\\?")[0]);
							HttpRequest request2 = HttpRequest.newBuilder().GET().uri(redirect).header("IF-NONE-MATCH", this.config.etag()).build();
							CompletableFuture<HttpResponse<String>> future2 = httpClient.sendAsync(request2, HttpResponse.BodyHandlers.ofString());
							future2.thenAccept(response2 ->
							{
								System.out.println("Response for file: " + response2.statusCode());
								if (response2.statusCode() == 200)
								{
									this.configManager.setConfiguration("friendlyGuide", "etag", response2.headers().firstValue("etag").get());
									this.configManager.setConfiguration("friendlyGuide", "data", response2.body());
									System.out.println("Updated cache of dynamic responses");
								}
								else if (response2.statusCode() == 304)
								{
									System.out.println("Using cached data");
								}
								else
								{
									System.err.println("Error fetching dynamic responses. Status=" + response2.statusCode() + "\nUsing cache of dynamic responses");
								}
								Dialog.loadDynamicYaml(config.data());
							});
                        } catch (URISyntaxException e) {
                            throw new RuntimeException(e);
                        }
					}
					else
					{
						System.err.println("Error fetching dynamic responses. Status=" + response.statusCode() + "\nUsing cache of dynamic responses");
					}
				});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Friendly Guide stopped!");

		Guide.unload(this.clientThread);
	}

	public void setIntroduced()
	{
		this.configManager.setConfiguration("friendlyGuide", "introduction", false);
	}

	@Subscribe
	public void onClientTick(ClientTick tick)
	{
		Guide.tickAll();

		if (client.isMenuOpen())
		{
			return;
		}

		for (Guide guide : Guide.allGuides())
		{
			Shape shape = Perspective.getClickbox(this.client, guide.getModel(), guide.getOrientation(), guide.getX(), guide.getY(), guide.getZ());
			Point point = this.client.getMouseCanvasPosition();
			if (shape != null && shape.contains(point.getX(), point.getY()))
			{
				MenuEntry entry = this.client.getMenu().createMenuEntry(-1);
				entry.setOption("Talk to Friendly Guide");
				entry.setType(MenuAction.CANCEL);
				Arrays.stream(this.client.getMenu().getMenuEntries()).filter(e -> e.getOption().equalsIgnoreCase("walk here")).findFirst().get().setDeprioritized(true);

			}
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
		if (developerMode)
		{
			System.out.println(command.getCommand());
			String cmd = command.getCommand();
			if (cmd.equals("guide"))
			{
				this.chatboxPanelManager.openInput(new DialogBox(this, Dialog.createDialogTree(this)));
			}
		}
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked menuOptionClicked)
	{
		if (menuOptionClicked.getMenuOption().equalsIgnoreCase("Talk to Friendly Guide"))
		{
			this.chatboxPanelManager.openInput(new DialogBox(this, Dialog.createDialogTree(this)));

			Point point = this.client.getMouseCanvasPosition();
			this.overlay.setClick(point);
		}
		else
		{
			if (menuOptionClicked.getMenuAction() != MenuAction.CANCEL && this.chatboxPanelManager.getCurrentInput() instanceof DialogBox)
			{
				this.chatboxPanelManager.close();
			}
		}
	}
}
