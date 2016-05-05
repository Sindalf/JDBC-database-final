
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JDBC3main {

    public static void main(String[] args) throws IOException, FileNotFoundException, SQLException {

        String username, password;
        Scanner scan = new Scanner(System.in);
        System.err.println("Input your user name");
        username = scan.nextLine(); //This is your username in oracle
        System.err.println("Input your password");
        password = scan.nextLine(); //This is your password in oracle

        DBobject db = new DBobject(username, password);
        db.getTableSizes();

        if (args[0].equals("15")) {

            // Big dataset: 
            db.parseFile("big_users.txt", 1); //insert 10,000 users
            db.parseFile("big_init_friends.txt", 2); //make 5000 initiated friendships
            db.parseFile("gen_est_friends.txt", 3); //make 10,000 established friendships
            db.parseFile("gen_query_users.txt", 4); //query the 500 users between whom we've made friendships
            db.parseFile("big_groups.txt", 5); //make 4,500 groups
            db.parseFile("big_add_to_group.txt", 6); //add 10,000 users to groups
            db.parseFile("big_msg_to_user.txt", 7); //send 5,000 messages to users
            db.parseFile("gen_query_users.txt", 9); //query the 500 users between whom we've been sending messages

            db.parseFile("big_msg_to_group.txt", 8); //send 5,000 messages to groups
            db.parseFile("gen_query_users.txt", 10); //query the 500 users between whom we've been sending messages
            db.parseFile("gen_search_queries.txt", 11); //100 search series
            db.parseFile("gen_3Hops.txt", 12); //query 100 2-user pairs 
            db.parseFile("big_topMessagers.txt", 13); //make 100 top-messager queries
            db.parseFile("big_dropUser.txt", 14);  //drop 3,000 users

        } else {
            String result = db.userInput(args);
            db.printlastoutput(result, Integer.parseInt(args[0]));
            
            //db.getTableSizes();
        }

    }
}
