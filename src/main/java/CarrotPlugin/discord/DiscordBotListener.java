package CarrotPlugin.discord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class DiscordBotListener extends ListenerAdapter {

    HashMap<String, String> discordLinks;
    Logger log;
    Map<String, String> users;
    HashMap<String, CompletableFuture<Message>> tasks;


    public DiscordBotListener(Logger log, HashMap<String, String> discordLinks,
                              Map<String, String> users,
                              HashMap<String, CompletableFuture<Message>> tasks) {
        this.discordLinks = discordLinks;
        this.log = log;
        this.users = users;
        this.tasks = tasks;
    }


    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message msg = event.getMessage();
        if (msg.getContentRaw().equals("!verify") && msg.getChannelType() == ChannelType.PRIVATE) {
            String discordID = event.getAuthor().getId();

            if (discordLinks.containsValue(discordID) || users.containsValue(discordID)) return;

            String randKey = randString();
            discordLinks.put(randKey, discordID);

            MessageChannel channel = event.getChannel();

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("CarrotCraft Verification", "https://carrotcraft.club/");
            eb.setColor(new Color(0xFFAA00));
            eb.setDescription("Please run the following command within **5 minutes** on CarrotCraft to link your discord account!");
            eb.addField("Command:", "/verifydiscord " + randKey, false);
            eb.setAuthor("CarrotBot", "https://carrotcraft.club/", "https://i.imgur.com/7G8iB7d.png");

            CompletableFuture<Message> messageTask = channel.sendMessage(eb.build())
                    .delay(5, TimeUnit.MINUTES)
                    .flatMap((it) -> {
                        if (discordLinks.containsValue(discordID)) {
                            discordLinks.remove(randKey);

                            EmbedBuilder eb2 = new EmbedBuilder();
                            eb2.setTitle("Verification Timeout");
                            eb2.setColor(new Color(0xff1a00));
                            eb2.setDescription("You didn't verify your account in time! Please re-verify with !verify");
                            eb2.setAuthor("CarrotBot", "https://carrotcraft.club/", "https://i.imgur.com/7G8iB7d.png");
                            return it.editMessage(eb2.build());
                        }
                        return null;
                    })
                    .submit();
            tasks.put(discordID, messageTask);
            messageTask.thenRun(() -> tasks.remove(discordID));
        }
    }

    public String randString() {
        int leftLimit = 48;
        int rightLimit = 122;
        int targetStringLength = 12;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return generatedString;
    }

}
