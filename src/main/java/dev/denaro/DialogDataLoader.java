package dev.denaro;

import dev.denaro.dialog.Dialog;
import net.runelite.client.config.ConfigManager;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DialogDataLoader
{
    private static final Logger logger = LoggerFactory.getLogger(DialogDataLoader.class);
    private OkHttpClient httpClient;
    private FriendlyGuideConfig config;
    private ConfigManager configManager;
    public DialogDataLoader(OkHttpClient httpClient, FriendlyGuideConfig config, ConfigManager configManager)
    {
        this.httpClient = httpClient;
        this.config = config;
        this.configManager = configManager;
    }

    public void Load() {
        try {
            logger.debug("Loading Dialog data...");
            logger.debug("etag:" + this.config.etag());
            Request request = new Request.Builder()
                    .get()
                    .url("https://github.com/NicholasDenaro/osrs-friendly-guide-responses/releases/download/Latest/merged.toml")
                    .header("IF-NONE-MATCH", this.config.etag())
                    .build();
            try (Response response = httpClient.newCall(request).execute();) {
                logger.debug("Data received, code: " + response.code());
                if (response.code() == 304) {
                    logger.debug("Using cached data for etag=" + config.etag());
                }
                else if (response.code() == 200)
                {
                    String body = response.body().string();
                    if (body.startsWith("\n---")) {
                        this.configManager.setConfiguration("friendlyGuide", "etag", response.headers().get("etag"));
                        logger.debug("Set etag=" + config.etag());
                        this.configManager.setConfiguration("friendlyGuide", "data", body);
                    }

                } else {
                    logger.error("Error fetching dynamic responses. Status=" + response.code() + "\nUsing cache of dynamic responses");
                }
            }

            Dialog.loadDynamicToml(config.data());

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
