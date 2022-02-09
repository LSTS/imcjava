package pt.lsts.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonValue;

import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.lsf.LsfIndex;
import pt.lsts.imc.net.UDPTransport;

public class ImcReplay {

    int messageIds[];
    int entityIds[];
    int sourceIds[];
    LsfIndex index;

    enum Protocol {
        UDP, TCP
    };

    final static Logger LOGGER = Logger.getLogger(ImcReplay.class.getSimpleName());

    private static class Destination {
        String hostname;
        int port;
        Protocol protocol;

        @Override
        public String toString() {
            return protocol + "://" + hostname + ":" + port;
        }
    }

    public static void JsonReplay(Path jsonFile, String... destinations) throws Exception {

        JsonValue val = Json.parse(new FileReader(jsonFile.toFile()));
        double currentTime = 0;

        ArrayList<Destination> replayDestinations = new ArrayList<>();
        for (String s : destinations) {
            Destination d = parseDestination(s);
            if (d == null) {
                LOGGER.severe("Invalid destination: " + s);
                return;
            } else {
                replayDestinations.add(d);
            }
        }

        for (JsonValue msgJson : val.asArray()) {
            IMCMessage msg = IMCMessage.parseJsonObject(msgJson.asObject());
            double timestamp = msg.getTimestamp();
            double sleepTime = timestamp - currentTime;
            if (sleepTime > 0) {
                LOGGER.info(String.format(Locale.US, "Sleeping for %.1f seconds", sleepTime));
                Thread.sleep((long) (1000 * sleepTime));
            }
            currentTime = timestamp;
            for (Destination d : replayDestinations) {
                sendTo(msg, d);
                LOGGER.info("Sent " + msg.getAbbrev() + " to " + d);
            }
        }
        LOGGER.info("Replay completed");
    }

    private static void sendTo(IMCMessage msg, Destination destination) {
        if (destination.protocol == Protocol.UDP) {
            try {
                UDPTransport.sendMessage(msg, destination.hostname, destination.port);
            } catch (Exception e) {
                System.err.println("Could not send message to " + destination + ": " + e.getMessage());
            }

        } else {
            System.out.println("TCP not yet implemented.");
        }
    }

    private static Destination parseDestination(String destination) {
        Pattern pattern = Pattern.compile("^(.*)://(.*):([0-9]*)");

        Matcher matcher = pattern.matcher(destination);
        if (matcher.matches()) {
            Destination dest = new Destination();
            dest.protocol = matcher.group(1).toLowerCase().equals("tcp") ? Protocol.TCP : Protocol.UDP;
            dest.hostname = matcher.group(2);
            dest.port = Integer.parseInt(matcher.group(3));
            return dest;
        }
        return null;
    }

    public static void LsfToJson(Path lsfFile, String... imcMessages) throws Exception {
        LsfIndex index = new LsfIndex(lsfFile.toFile());

        File parentDir = index.getLsfFile().getParentFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(parentDir, "replay.lsf")));
        double startTime = index.getMessage(0).getTimestamp();
        HashSet<Integer> messageIds = new HashSet<>();

        for (int i = 0; i < imcMessages.length; i++) {
            messageIds.add(index.getDefinitions().getMessageId(imcMessages[i]));
        }
        System.out.print("[");
        writer.write("[");
        boolean firstMessage = true;

        for (int m = 0; m < index.getNumberOfMessages(); m++) {
            int mid = index.typeOf(m);
            if (messageIds.contains(mid)) {
                IMCMessage message = index.getMessage(m);
                message.setTimestamp((Math.max(0, message.getTimestamp() - startTime)));
                String out = "\n";
                if (!firstMessage)
                    out = ",\n";
                firstMessage = false;
                out += message.asJSON();

                System.out.print(out);
                writer.write(out);
            }
        }
        System.out.print("\n]\n");
        writer.write("\n]\n");
        writer.close();
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Invalid usage.");
            System.exit(1);
        }

        String[] extraArgs = new String[args.length - 1];
        for (int i = 1; i < args.length; i++)
            extraArgs[i - 1] = args[i];

        if (args[0].toLowerCase().endsWith("json")) {
            JsonReplay(Paths.get(args[0]), extraArgs);
        } else {
            LsfToJson(Paths.get(args[0]), extraArgs);
        }
    }
}
