package dev.denaro;

import dev.denaro.dialog.Dialog;
import dev.denaro.dialog.DialogMessage;
import dev.denaro.dialog.DialogOption;
import net.runelite.api.*;
import net.runelite.api.widgets.*;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.game.chatbox.ChatboxInput;

import java.awt.*;

public class DialogBox extends ChatboxInput
{

    private final int X_OFFSET = 11;
    private final int Y_OFFSET = 10;

    private FriendlyGuidePlugin plugin;
    private Dialog dialog;
    public DialogBox(FriendlyGuidePlugin plugin, Dialog dialog)
    {
        this.plugin = plugin;
        this.dialog = dialog;
    }

    @Override
    public void open()
    {
        Widget chatbox = plugin.getChatboxPanelManager().getContainerWidget();

        this.constructDialog(chatbox, this.dialog);

//        this.plugin.getChatMessageManager().queue(
//                QueuedMessage.builder()
//                        .type(ChatMessageType.DIALOG)
//                        .runeLiteFormattedMessage("Hello World?")
//                        .build());
    }

    private void constructDialog(Widget chatbox, Dialog dialog)
    {
        if (dialog instanceof DialogMessage)
        {
            DialogMessage dialogMessage = (DialogMessage) dialog;

            if (dialogMessage.speaker == DialogMessage.DialogSpeaker.Guide)
            {
                this.constructFace(chatbox, new int[]{52, 82});
            }
            else
            {
                this.constructPlayerFace(chatbox);
            }

            int xOffset = dialogMessage.speaker == DialogMessage.DialogSpeaker.Guide ? 0 : (5 - 96);

            Widget talker = chatbox.createChild(-1, WidgetType.TEXT);
            talker.setText(dialogMessage.speaker == DialogMessage.DialogSpeaker.Guide ? "Friendly Guide" : plugin.getClient().getLocalPlayer().getName());
            talker.setTextColor(0x800000);
            talker.setFontId(FontID.QUILL_8);
            talker.setOriginalX(X_OFFSET + 96 + xOffset);
            talker.setOriginalY(Y_OFFSET + 0);
            talker.setOriginalWidth(380);
            talker.setOriginalHeight(17);
            talker.setXTextAlignment(WidgetTextAlignment.CENTER);
            talker.setYTextAlignment(WidgetTextAlignment.CENTER);
            talker.revalidate();

            Widget text = chatbox.createChild(-1, WidgetType.TEXT);
            text.setText(dialogMessage.message);
            text.setTextColor(0x000000);
            text.setFontId(FontID.QUILL_8);
            text.setOriginalX(X_OFFSET + 96 + xOffset);
            text.setOriginalY(Y_OFFSET + 16);
            text.setOriginalWidth(380);
            text.setOriginalHeight(67);
            text.setXTextAlignment(WidgetTextAlignment.CENTER);
            text.setYTextAlignment(WidgetTextAlignment.CENTER);
            text.revalidate();

            Widget cont = chatbox.createChild(-1, WidgetType.TEXT);
            cont.setText("Click here to continue");
            cont.setTextColor(Color.BLUE.getRGB());
            cont.setFontId(FontID.QUILL_8);
            cont.setOriginalX(X_OFFSET + 96 + xOffset);
            cont.setOriginalY(Y_OFFSET + 80);
            cont.setOriginalWidth(380);
            cont.setOriginalHeight(17);
            cont.setXTextAlignment(WidgetTextAlignment.CENTER);
            cont.setYTextAlignment(WidgetTextAlignment.TOP);
            cont.setAction(0, "Continue");
            cont.setOnOpListener((JavaScriptCallback) event -> this.nextDialog());
            cont.setOnMouseOverListener((JavaScriptCallback) ev -> cont.setTextColor(Color.WHITE.getRGB()));
            cont.setOnMouseLeaveListener((JavaScriptCallback) ev -> cont.setTextColor(Color.BLUE.getRGB()));
            cont.setOnKeyListener((JavaScriptCallback) ev -> this.keyTyped(ev));
            cont.setHasListener(true);
            cont.revalidate();
        }
        else if (dialog instanceof DialogOption)
        {
            DialogOption dialogOption = (DialogOption) dialog;

            int yPos = 0;

            Widget talker = chatbox.createChild(-1, WidgetType.TEXT);
            talker.setText("Select an option");
            talker.setTextColor(0x800000);
            talker.setFontId(FontID.QUILL_8);
            talker.setOriginalX(X_OFFSET + 0);
            talker.setOriginalY(Y_OFFSET + yPos);
            talker.setOriginalWidth(479);
            talker.setOriginalHeight(20);
            talker.setXTextAlignment(WidgetTextAlignment.CENTER);
            talker.setYTextAlignment(WidgetTextAlignment.CENTER);
            talker.revalidate();
            yPos += 20;

            for (int i = 0; i < dialogOption.options.length; i++)
            {
                int finalI = i + 1;
                DialogOption.Option message = dialogOption.options[i];
                Widget text = chatbox.createChild(-1, WidgetType.TEXT);
                text.setText(finalI + ". " + message.text);
                text.setTextColor(Color.BLACK.getRGB());
                text.setFontId(FontID.QUILL_8);
                text.setOriginalX(X_OFFSET + 0);
                text.setOriginalY(Y_OFFSET + yPos);
                text.setOriginalWidth(479);
                text.setOriginalHeight(16);
                text.setXTextAlignment(WidgetTextAlignment.CENTER);
                text.setYTextAlignment(WidgetTextAlignment.LEFT);
                text.setAction(0, message.text);
                text.setOnOpListener((JavaScriptCallback) event -> {
                    this.chooseOption(finalI - 1);
                });
                text.setOnMouseOverListener((JavaScriptCallback) ev -> text.setTextColor(Color.WHITE.getRGB()));
                text.setOnMouseLeaveListener((JavaScriptCallback) ev -> text.setTextColor(Color.BLACK.getRGB()));
                text.setOnKeyListener((JavaScriptCallback) ev -> {
                    if ((String.valueOf((char)ev.getTypedKeyChar())).equals("" + finalI)) {
                        this.chooseOption(finalI - 1);
                    }
                });
                text.setHasListener(true);
                text.revalidate();

                yPos += 16;
            }

        }
    }

    private void constructFace(Widget chatbox, int[] modelIds)
    {
        for (int modelId : modelIds) {
            Widget face = chatbox.createChild(-1, WidgetType.MODEL);
            face.setModelId(modelId);
            face.setRotationZ((int)((360 - 20.0) / 360 * 2047));
            face.setOriginalX(X_OFFSET + 35);
            face.setOriginalY(Y_OFFSET + 43);
            face.setOriginalWidth(50);
            face.setOriginalHeight(50);
            face.setModelZoom(800);
            face.setAnimationId(568);
            face.revalidate();
        }
    }

    private void constructPlayerFace(Widget chatbox)
    {
        Widget face = chatbox.createChild(-1, WidgetType.MODEL);
        face.setModelId(0);
        face.setModelType(3);
        face.setRotationZ((int)(20.0 / 360 * 2047));
        face.setXPositionMode(WidgetPositionMode.ABSOLUTE_RIGHT);
        face.setOriginalX(X_OFFSET + 35);
        face.setOriginalY(Y_OFFSET + 43);
        face.setOriginalWidth(50);
        face.setOriginalHeight(50);
        face.setModelZoom(800);
        face.setAnimationId(568);
        face.revalidate();
    }

    private void nextDialog()
    {
        Dialog next = dialog.next();
        this.plugin.getChatboxPanelManager().close();
        if (next != null)
        {
            this.plugin.getChatboxPanelManager().openInput(new DialogBox(this.plugin, next));
        }
    }

    private void chooseOption(int optionIndex)
    {
        if (dialog instanceof DialogOption)
        {
            Dialog next = ((DialogOption) dialog).options[optionIndex].next();
            this.plugin.getChatboxPanelManager().close();
            if (next != null)
            {
                this.plugin.getChatboxPanelManager().openInput(new DialogBox(this.plugin, next));
            }
        }
    }

    public void keyTyped(ScriptEvent event)
    {
        if (event.getTypedKeyChar() == ' ')
        {
            this.nextDialog();
        }
    }
}
