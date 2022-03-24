package telegramBot.commands;

public class ParsedCommand {
    Command command;
    String text;

    public ParsedCommand(Command c, String t){
        command=c;
        text=t;
    }
    public Command getCommand(){
        return this.command;
    }
    public String getText(){
        return this.text;
    }
    public void setCommand(Command c){
        this.command=c;
    }
    public void setText(String t){
        this.text=t;
    }
}
