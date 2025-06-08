package dev.denaro.dialog;

import java.util.function.Supplier;

public class DialogOption extends Dialog
{
    public static class Option
    {
        public String text;
        private Dialog next;
        private Supplier<Dialog> decider;

        public Option(String text, Dialog next)
        {
            this.text = text;
            this.next = next;
        }

        public Option(String option, Supplier<Dialog> decider)
        {
            this.text = option;
            this.decider = decider;
        }

        public Dialog next()
        {
            if (this.next != null)
            {
                return this.next;
            }
            else
            {
                return this.decider.get();
            }
        }
    }

    public final Option[] options;

    public DialogOption(Option[] options)
    {
        this.options = options;
    }
}