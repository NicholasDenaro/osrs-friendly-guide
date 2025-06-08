package dev.denaro.dialog;

public class DialogMessage extends Dialog
{
    public enum DialogSpeaker {Player, Guide}
    public DialogSpeaker speaker;
    public String message;

    public DialogMessage(DialogSpeaker speaker, String message)
    {
        this.speaker = speaker;
        this.message = message;
    }
}
