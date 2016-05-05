
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBobject {

    JDBC_part3 db;
    String username, password;
    String[] methods = {"None", "createUser", "initiateFriendship",
        "establishFriendship", "displayFriends", "createGroup", "addToGroup",
        "sendMessageToUser", "sendMessageToGroup", "displayMessages",
        "displayNewMessages", "SearchForUser", "threeDegrees", "topMessagers",
        "dropUser"};

    public DBobject(String username, String password) {
        try {
            db = new JDBC_part3(username, password);
            this.username = username;
            this.password = password;
        } catch (SQLException ex) {
            System.out.println(ex);
        }
    }

    public void parseFile(String filename, int command) throws SQLException {
        BufferedReader file = null;

        try {
            file = new BufferedReader(new FileReader(filename));
            String currentLine;
            String[] args;
            System.out.println("Testing " + methods[command]);
            String lastoutput = ":No output for this query:";

            while ((currentLine = file.readLine()) != null) {
                args = currentLine.split(",");
                lastoutput = decisionTree(args, command);
            }

            System.out.println("Finished testing " + methods[command]);
            printlastoutput(lastoutput, command);

            getTableSizes();
            //   reconnect();
        } catch (FileNotFoundException ex) {
            System.out.println(ex);
            System.exit(1);
        } catch (IOException ex) {
            System.out.println(ex);
            System.exit(1);
        } finally {
            try {
                file.close();
            } catch (IOException ex) {
                System.out.println(ex);
                System.exit(1);
            }
        }
    }

    public void printlastoutput(String output, int command) {
        switch (command) {
            case 4:
                System.out.println("The last output for displayFriends() was " + output);
                break;
            case 9:
                System.out.println("The last output for displayMessages() was " + output);
                break;
            case 10:
                System.out.println("The last output for displayNewMessages() was " + output);
                break;
            case 11:
                System.out.println("The last output for SearchForUser() was " + output);
                break;
            case 12:
                System.out.println("The last output for ThreeDegrees() was " + output);
                break;
            case 13:
                System.out.println("The last output for TopMessagers() was " + output);
                break;
            default:
                break;
        }

    }

    public void getTableSizes() {
        db.getTableSizes();
    }

    /*
     * This may fix cursor errors
     */
    public void reconnect() {
        try {
            db.close();
            db = new JDBC_part3(username, password);
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }

    }

    public String userInput(String[] args) throws SQLException {
        String result = db.JDBC_part2_decision_tree(args);
        getTableSizes();
        return result;
    }

    public String decisionTree(String[] args, int command) throws SQLException {
        int sender, receiver, owner, group, recipient;
        String fname, lname, email, dob, subject, body;
        String result = null;
        switch (command) {
            case 1:
                //   System.out.println("createUsers");
                fname = args[1];
                lname = args[2];
                email = args[3];
                dob = args[4];
                result = db.createUser(fname, lname, email, dob);
                break;
            case 2:
                //    System.out.println("initiateFriendship");
                sender = Integer.parseInt(args[0].trim());
                receiver = Integer.parseInt(args[1].trim());
                result = db.initiateFriendship(sender, receiver);
                break;
            case 3:
                //      System.out.println("establishFriendship");
                sender = Integer.parseInt(args[0].trim());
                receiver = Integer.parseInt(args[1].trim());
                result = db.establishFriendship(sender, receiver);
                break;
            case 4:
                //         System.out.println("displayFriends");
                owner = Integer.parseInt(args[0].trim());
                result = db.displayFriends(owner);
                break;
            case 5:
                //         System.out.println("createGroup");
                String name = args[0];
                String description = args[1];
                int limit = Integer.parseInt(args[2].trim());
                result = db.createGroup(name, description, limit);
                break;
            case 6:
                //        System.out.println("addToGroup");
                int user = Integer.parseInt(args[0].trim());
                group = Integer.parseInt(args[1].trim());
                result = db.addToGroup(user, group);
                break;
            case 7:
                //        System.out.println("sendMessageToUser");
                subject = args[0];
                body = args[1];
                recipient = Integer.parseInt(args[2].trim());
                owner = Integer.parseInt(args[3].trim());
                result = db.sendMessageToUser(subject, body, recipient, owner);
                break;
            case 8:
                //           System.out.println("sendMessageToGroup");
                subject = args[0];
                body = args[1];
                group = Integer.parseInt(args[2].trim());
                owner = Integer.parseInt(args[3].trim());
                result = db.sendMessageToGroup(subject, body, group, owner);
                break;
            case 9:
                //         System.out.println("Displaying Messages");
                owner = Integer.parseInt(args[0].trim());
                result = db.displayMessages(owner);
                break;
            case 10:
                //         System.out.println("Displaying new messages");
                owner = Integer.parseInt(args[0].trim());
                result = db.displayNewMessages(owner);
                break;
            case 11:
                //        System.out.println("Displaying search results");
                //         System.out.println("-----------------------------------------------");
                result = db.searchForUser(args);
                break;
            case 12:
                //       System.out.println("Displaying third degree connections");
                sender = Integer.parseInt(args[0].trim());
                receiver = Integer.parseInt(args[1].trim());
                result = db.threeDegrees(sender, receiver);
                break;
            case 13:
                //        System.out.println("Displaying top messages");
                result = db.topMessagers(Integer.parseInt(args[0].trim()), Integer.parseInt(args[1].trim()));
                break;
            case 14:
                //        System.out.println("Dropping user");
                user = Integer.parseInt(args[0].trim());
                result = db.dropUser(user);
                break;
            default:
                System.out.println("PLEASE FOLLOW PROPER USAGE\n"
                        + "-----------------------------------------------------------------------------------------------\n"
                        + "java JDBC_part2  method number        [arguments]\n"
                        + "             1: [createUser]          (String fname, String lname, String email, String dob)\n"
                        + "             2: [initiateFriendship]  (int sender, int receiver)\n"
                        + "             3: [establishFriendship] (int sender, int receiver)\n"
                        + "             4: [displayFriends]      (int owner]\n"
                        + "             5: [createGroup]         (String name, String description, int limit)\n"
                        + "             6: [addToGroup]          (int user, int group]\n"
                        + "             7: [sendMessageToUser]   (String subject, String body, int recipient, int owner)\n"
                        + "             8: [sendMessageToGroup]  (String subject, String body, int group, int owner)\n"
                        + "             9: [displayMessages]     (int owner)\n"
                        + "            10: [displayNewMessages]  (int owner)\n"
                        + "            11: [searchForUser]       (String searchString)\n"
                        + "            12: [threeDegrees]        (String userA, String userB)\n"
                        + "            13: [topMessages]         (String k, String x)\n"
                        + "            14: [dropUser]            (int id)\n");
        }
        return result;
    }
}
