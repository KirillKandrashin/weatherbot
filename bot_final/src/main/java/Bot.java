import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.MessageContext;

import static org.telegram.abilitybots.api.objects.Flag.TEXT;
import static org.telegram.abilitybots.api.objects.Locality.ALL;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;

public class Bot extends AbilityBot {
    private static String BOT_TOKEN = "1482426653:AAEJ4jFaUAhh1V9sX9WcKsQAmsTGCsRPSUE";
    private static String BOT_NAME = "kandrashin_WeatherBot";
    private JSONsParser BotsParser = new JSONsParser();

    public Bot() {
        super(BOT_TOKEN, BOT_NAME);
    }

    @Override
    public int creatorId() {
        return 536001777;
    }

    public Ability Starting() {
        return Ability
                .builder()
                .name("start")
                .locality(ALL)
                .privacy(PUBLIC)
                .action(ctx -> silent.send("Hello! Send us your city and type of information in format 'City now/today'" , ctx.chatId()))
                .build();
    }

    public Ability GettingAnswer() {
        return Ability.builder()
                .name(DEFAULT)
                .flag(TEXT)
                .privacy(PUBLIC)
                .locality(ALL)
                .input(0)
                .action((MessageContext ctx) -> silent.send(BotsParser.FormingAns(ctx.firstArg(), ctx.secondArg()), ctx.chatId()))
                .build();
    }

}