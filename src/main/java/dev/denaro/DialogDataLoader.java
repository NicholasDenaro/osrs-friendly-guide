package dev.denaro;

import dev.denaro.dialog.Dialog;
import net.runelite.client.config.ConfigManager;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

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
        logger.debug("Loading Dialog data...");
        logger.debug("etag:" + this.config.etag());
        Request request = new Request.Builder()
                .get()
                .url("https://github.com/NicholasDenaro/osrs-friendly-guide-responses/releases/download/Latest/merged.toml")
                .header("IF-NONE-MATCH", this.config.etag())
                .build();

        final ConfigManager cfgMgr = this.configManager;

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                logger.error("Failed to get guide responses");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                logger.debug("Data received, code: " + response.code());
                if (response.code() == 304) {
                    logger.debug("Using cached data for etag=" + config.etag());
                }
                else if (response.code() == 200)
                {
                    String body = response.body().string();
                    if (body.startsWith("\n---")) {
                        cfgMgr.setConfiguration("friendlyGuide", "etag", response.headers().get("etag"));
                        logger.debug("Set etag=" + config.etag());
                        cfgMgr.setConfiguration("friendlyGuide", "data", body);
                    }

                } else {
                    logger.error("Error fetching dynamic responses. Status=" + response.code() + "\nUsing cache of dynamic responses");
                }

                Dialog.loadDynamicToml(config.data());
            }
        });
    }
}
