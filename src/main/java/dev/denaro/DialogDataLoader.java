package dev.denaro;

import dev.denaro.dialog.Dialog;
import net.runelite.client.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class DialogDataLoader
{
    private static final Logger logger = LoggerFactory.getLogger(DialogDataLoader.class);
    private HttpClient httpClient;
    private FriendlyGuideConfig config;
    private ConfigManager configManager;
    public DialogDataLoader(HttpClient httpClient, FriendlyGuideConfig config, ConfigManager configManager)
    {
        this.httpClient = httpClient;
        this.config = config;
        this.configManager = configManager;
    }

    public void Load() {
        try {
            logger.debug("Loading Dialog data...");
            URI uri = new URI("https://github.com/NicholasDenaro/osrs-friendly-guide-responses/releases/download/Latest/merged.toml");
            HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).header("IF-NONE-MATCH", this.config.etag()).build();
            HttpResponse<String> response = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString()).join();

            logger.debug("Data received");
            if (response.statusCode() == 302)
            {
                logger.debug("Redirect");
                try
                {
                    URI redirect = new URI(response.headers().firstValue("Location").get());
                    logger.debug("Fetching contents from " + redirect.toString().split("\\?")[0]);
                    HttpRequest request2 = HttpRequest.newBuilder().GET().uri(redirect).header("IF-NONE-MATCH", this.config.etag()).build();
                    HttpResponse<String> response2 = httpClient.sendAsync(request2, HttpResponse.BodyHandlers.ofString()).join();
                    logger.debug("Response for file: " + response2.statusCode());
                    if (response2.statusCode() == 200)
                    {
                        this.configManager.setConfiguration("friendlyGuide", "etag", response2.headers().firstValue("etag").get());
                        this.configManager.setConfiguration("friendlyGuide", "data", response2.body());
                        logger.debug("Updated cache of dynamic responses");
                    }
                    else if (response2.statusCode() == 304)
                    {
                        logger.debug("Using cached data");
                    }
                    else
                    {
                        logger.error("Error fetching dynamic responses. Status=" + response2.statusCode() + "\nUsing cache of dynamic responses");
                    }

                    Dialog.loadDynamicToml(config.data());
                } catch (URISyntaxException e) {
                    logger.error(e.getMessage());
                }
            }
            else
            {
                logger.error("Error fetching dynamic responses. Status=" + response.statusCode() + "\nUsing cache of dynamic responses");
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
