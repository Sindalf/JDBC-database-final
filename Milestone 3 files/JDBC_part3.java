import oracle.jdbc.OracleCallableStatement;
import java.sql.*;
import static java.sql.Connection.TRANSACTION_READ_COMMITTED;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * JDBC_part3 class.
 */
public class JDBC_part3 {

    private static Connection connection;
    private Statement statement;
    private Statement ExtraStatement;
    private PreparedStatement prepStatement;
    private ResultSet resultSet;
    private ResultSet ExtraResultSet;
    private String query;
    private String ExtraQuery;
    private boolean path_found;
    OracleCallableStatement cs;

    /**
     * JDBC_part2 constructor.
     *
     * @param args => Array of command line arguments.
     */
    public JDBC_part3(String username, String password) throws SQLException {
        DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
        String url = "";
        connection = DriverManager.getConnection(url, username, password);

        //  ExtraConnection = DriverManager.getConnection(url, username, password);
        connection.setTransactionIsolation(TRANSACTION_READ_COMMITTED);
    }

    /**
     * Given a name, email address, and date of birth, add a new user to the
     * system.
     *
     * @param fname => New user first name.
     * @param lname => New user last name.
     * @param email => New user email address.
     * @param dob => New user date of birth.
     */
    public String createUser(String fname, String lname, String email, String dob) {
        int success = -1;
        try {
            query = "INSERT INTO users VALUES (1, ?, ?, ?, ?, ?)";
            prepStatement = connection.prepareStatement(query);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-DDD");
            java.sql.Date date_reg_dob;
            date_reg_dob = new java.sql.Date(df.parse(dob).getTime());
            prepStatement.setString(1, fname);
            prepStatement.setString(2, lname);
            prepStatement.setString(3, email);
            prepStatement.setDate(4, date_reg_dob);
            prepStatement.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            prepStatement.executeUpdate();
            prepStatement.close();
            success = 1;
        } catch (SQLException Ex) {
            // System.out.println("Error running the sample queries.  Machine Error: "
            //         + Ex.toString());
            success = 0;
        } catch (ParseException e) {
            //System.out.println("Error parsing the date. Machine Error: "
            //       + e.toString());
            success = 0;
        } finally {
            try {
                if (prepStatement != null) {
                    prepStatement.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (ExtraStatement != null) {
                    ExtraStatement.close();
                }
                if (resultSet != null) {
                    resultSet.close();
                }
                if (ExtraResultSet != null) {
                    ExtraResultSet.close();
                }
                if (cs != null) {
                    cs.close();
                }
            } catch (SQLException e) {
                //   System.out.println("Cannot close Statement. Machine error: " + e.toString());
                success = 0;
            }
        }
        return " ";
    }

    /**
     * Create a pending friendship from one user to another.
     *
     * @param sender => Friend requester.
     * @param receiver => Friend requestee.
     */
    public String initiateFriendship(int sender, int receiver) {
        query = "INSERT INTO Friendships VALUES (?, ?, 0, ?)";
        int success = -1;
        try {
            prepStatement = connection.prepareStatement(query);
            prepStatement.setInt(1, sender);
            prepStatement.setInt(2, receiver);
            prepStatement.setString(3, null);
            prepStatement.executeUpdate();
            prepStatement.close();
            success = 1;
        } catch (SQLException Ex) {
            //  System.out.println("Error running the sample queries.  Machine Error: "
        //    +Ex.toString()
            
          //  System.out.println(sender);
          //  System.out.println(receiver);

            success = 0;
        } finally {
            try {
                if (prepStatement != null) {
                    prepStatement.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (ExtraStatement != null) {
                    ExtraStatement.close();
                }
                if (resultSet != null) {
                    resultSet.close();
                }
                if (ExtraResultSet != null) {
                    ExtraResultSet.close();
                }
                if (cs != null) {
                    cs.close();
                }
            } catch (SQLException e) {
                //  System.out.println("Cannot close Statement. Machine error: " + e.toString());
                success = 0;
            }
        }
        return " ";
    }

    /**
     * Create a bilateral friendship between two users.
     *
     * @param sender => Friend requester.
     * @param receiver => Friend requestee.
     */
    public String establishFriendship(int sender, int receiver) {
        int success = -1;
        query = "update Friendships set status = 1, date_started = ? where id_1 = " + sender + " and id_2 = " + receiver + " ";
        try {
            prepStatement = connection.prepareStatement(query);
            prepStatement.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            int result = prepStatement.executeUpdate();
            prepStatement.close();
            if (result == 0) {
                query = "INSERT INTO Friendships VALUES (?, ?, 1, ?)";
                prepStatement = connection.prepareStatement(query);
                prepStatement.setInt(1, sender);
                prepStatement.setInt(2, receiver);
                prepStatement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                prepStatement.executeUpdate();
                prepStatement.close();
            }

            query = "delete from Friendships where id_1 = " + receiver + " and id_2 = " + sender;
            statement = connection.createStatement();
            result = statement.executeUpdate(query);

            /*
             * if (result == 1) { System.out.println("reverse friendship
             * detected in DB"); } else if (result == 0) {
             * System.out.println("reverse friendship not found in DB"); }
             */
            success = 1;
        } catch (SQLException Ex) {
            //    System.out.println("Error running the sample queries.  Machine Error: "
            //            + Ex.toString());
            ///    System.out.println(sender);
            //    System.out.println(receiver);
            success = 0;
        } finally {
            try {
                if (prepStatement != null) {
                    prepStatement.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (ExtraStatement != null) {
                    ExtraStatement.close();
                }
                if (resultSet != null) {
                    resultSet.close();
                }
                if (ExtraResultSet != null) {
                    ExtraResultSet.close();
                }
                if (cs != null) {
                    cs.close();
                }
            } catch (SQLException e) {
                //      System.out.println("Cannot close Statement. Machine error: " + e.toString());
                success = 0;
            }
        }
        return " ";
    }

    /**
     * Given a user, look up all of that user's establish and pending
     * friendships. Print out this information in a nicely formatted way.
     *
     * @param owner => User being queried for friendship.
     */
    public String displayFriends(int owner) {
        String result = null;
        int success = -1;
        try {
            String[] ids = {"id_1", "id_2"};
            String[] status_string = {"has a pending friendship", "has an established friendship"};
            for (int i = 0; i < 2; i++) {
                query = "Select * from friendships where " + ids[i] + " = " + owner;
                statement = connection.createStatement();
                resultSet = statement.executeQuery(query);

                String owner_name = getNameFromUserID(owner);
                String friend_name = null;
                while (resultSet.next()) {
                    if (i == 0) {
                        int friend = resultSet.getInt(2);
                        int status = Integer.parseInt(resultSet.getString(3));
                        friend_name = getNameFromUserID(friend);
                        result = owner_name + " is friends with " + friend_name + " and " + status_string[status];
                        //    System.out.println(result);
                    } else if (i == 1) {
                        int friend = resultSet.getInt(1);
                        int status = Integer.parseInt(resultSet.getString(3));
                        friend_name = getNameFromUserID(friend);
                        result = friend_name + " is friends with " + owner_name + " and " + status_string[status];
                        //    System.out.println(result);
                    }
                }
                statement.close();
                resultSet.close();
            }
            success = 1;
        } catch (SQLException Ex) {
            // System.out.println("Error running the sample queries.  Machine Error: "
            //        + Ex.toString());
            success = 0;
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (ExtraStatement != null) {
                    ExtraStatement.close();
                }
                if (resultSet != null) {
                    resultSet.close();
                }
                if (cs != null) {
                    cs.close();
                }
            } catch (SQLException e) {
                //  System.out.println("Cannot close Statement. Machine error: " + e.toString());
                success = 0;
            }
        }
        return result;
    }

    /**
     * Given a name, description, and membership limit, add a new group to the
     * system.
     *
     * @param name => Name of group.
     * @param description => Description of group.
     * @param limit => MAX number of members permitted in group.
     */
    public String createGroup(String name, String description, int limit) {
        int success = -1;
        try {
            query = "INSERT INTO user_groups(name, descrip, limit) VALUES (?, ?, ?)";
            prepStatement = connection.prepareStatement(query);
            prepStatement.setString(1, name);
            prepStatement.setString(2, description);
            prepStatement.setInt(3, limit);
            prepStatement.executeUpdate();
            prepStatement.close();
            success = 1;
        } catch (SQLException Ex) {
            // System.out.println("Error running the sample queries.  Machine Error: "
            //               + Ex.toString());
            //       System.out.println(name);
            //      System.out.println(description);
            //     System.out.println(limit);
            success = 0;
        } finally {
            try {
                if (prepStatement != null) {
                    prepStatement.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (ExtraStatement != null) {
                    ExtraStatement.close();
                }
                if (resultSet != null) {
                    resultSet.close();
                }
                if (ExtraResultSet != null) {
                    ExtraResultSet.close();
                }
                if (cs != null) {
                    cs.close();
                }
            } catch (SQLException e) {
                //     System.out.println("Cannot close Statement. Machine error: " + e.toString());
                //      System.out.println(name);
                //      System.out.println(description);
                //      System.out.println(limit);
                success = 0;
            }
        }
        return " ";
    }

    /**
     * Given a user and a group, add the user to the group so long as that would
     * not violate the group's membership limit.
     *
     * @param user => User to add to group
     * @param group => Group for user to be added
     */
    public String addToGroup(int user, int group) {
        int success = -1;
        try {
            query = "INSERT INTO Group_membership VALUES (?, ?)";
            prepStatement = connection.prepareStatement(query);
            prepStatement.setInt(1, user);
            prepStatement.setInt(2, group);
            prepStatement.executeUpdate();
            prepStatement.close();
            success = 1;
        } catch (SQLException Ex) {
            //       System.out.println("Error running the sample queries.  Machine Error: "
            //                + Ex.toString());
            success = 0;
        } finally {
            try {
                if (prepStatement != null) {
                    prepStatement.close();
                }
                if (ExtraStatement != null) {
                    ExtraStatement.close();
                }
                if (resultSet != null) {
                    resultSet.close();
                }
                if (ExtraResultSet != null) {
                    ExtraResultSet.close();
                }
                if (cs != null) {
                    cs.close();
                }
            } catch (SQLException e) {
                //        System.out.println("Cannot close Statement. Machine error: " + e.toString());
                //        success = 0;
            }
        }
        return " ";
    }

    /**
     * Given a message subject, body, recipient, and sender, create a new
     * message.
     *
     * @param subject => Subject of message.
     * @param body => Body of sent message.
     * @param recipient => User who recieved message.
     * @param owner => User who sent message.
     */
    public String sendMessageToUser(String subject, String body, int recipient, int owner) {
        int success = -1;
        try {
            //Insert into messages
            query = "BEGIN INSERT INTO messages(sender_id, time_sent, subject, body) VALUES (?, ?, ?, ?) RETURNING msg_ID INTO ?; END;";
            cs = (OracleCallableStatement) connection.prepareCall(query);
            cs.setInt(1, owner);
            cs.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            cs.setString(3, subject);
            cs.setString(4, body);
            cs.registerOutParameter(5, Types.NUMERIC);
            cs.executeUpdate();

            //Insert into recipients the returned key
            query = "INSERT INTO Recipients VALUES(?, ?)";
            int key = cs.getInt(5);
            prepStatement = connection.prepareStatement(query);
            prepStatement.setInt(1, key);
            prepStatement.setInt(2, recipient);
            prepStatement.executeUpdate();
            success = 1;
        } catch (SQLException Ex) {
            //      System.out.println("Error running the sample queries.  Machine Error: "
            //              + Ex.toString());
            success = 0;
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (prepStatement != null) {
                    prepStatement.close();
                }
                if (cs != null) {
                    cs.close();
                }
            } catch (SQLException e) {
                //          System.out.println("Cannot close Statement. Machine error: " + e.toString());
                success = 0;
            }
        }

        return " ";
    }

    /**
     * This should operate similarly to sendMessageToUser only it should send
     * the message to every member currently in the specified group.
     *
     * @param subject => Subject of message (column)
     * @param body => Body of sent message (column)
     * @param group => Group who recieved message (column)
     * @param owner => User who sent message
     */
    public String sendMessageToGroup(String subject, String body, int group, int owner) {
        int success = -1;
        try {
            //Insert into messages
            query = "BEGIN INSERT INTO messages(sender_id, time_sent, subject, body) VALUES (?, ?, ?, ?) RETURNING msg_ID INTO ?; END;";
            cs = (OracleCallableStatement) connection.prepareCall(query);
            cs.setInt(1, owner);
            cs.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            cs.setString(3, subject);
            cs.setString(4, body);
            cs.registerOutParameter(5, Types.NUMERIC);
            cs.executeUpdate();
            int key = cs.getInt(5);

            query = "Select user_ID from Group_membership where group_id = " + group;

            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);

            int recipient;
            query = "INSERT INTO Recipients VALUES(?, ?)";
            while (resultSet.next()) {
                recipient = resultSet.getInt(1);

                if (recipient != owner) {
                    //Insert into recipients the returned key
                    prepStatement = connection.prepareStatement(query);
                    prepStatement.setInt(1, key);
                    prepStatement.setInt(2, recipient);
                    prepStatement.executeUpdate();
                    prepStatement.close();
                }
            }
            success = 1;
        } catch (SQLException Ex) {
            //    System.out.println("Error running the sample queries.  Machine Error: "
            //            + Ex.toString());
            success = 0;
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (ExtraStatement != null) {
                    ExtraStatement.close();
                }
                if (resultSet != null) {
                    resultSet.close();
                }
                if (ExtraResultSet != null) {
                    ExtraResultSet.close();
                }
                if (cs != null) {
                    cs.close();
                }
            } catch (SQLException e) {
                //       System.out.println("Cannot close Statement. Machine error: " + e.toString());
                success = 0;
            }
        }
        return " ";
    }

    /**
     * Given a user, look up all of the messages sent to that user (either
     * directly or via a group that they belong to). Your Java program should
     * print out the user's messages in a nicely formatted way
     *
     * @param owner => the user who sent a message (represented by ID number)
     */
    public String displayMessages(int owner) {
        int success = -1;
        String result = null;
        try {

            query = "Select msg_ID from Recipients where user_ID = " + owner;
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);

            int msg_ID;

            // System.out.println(); //For formatting I guess
            while (resultSet.next()) {
                msg_ID = resultSet.getInt(1);
                query = "select * from messages where msg_ID = " + msg_ID;

                ExtraStatement = connection.createStatement();
                ExtraResultSet = ExtraStatement.executeQuery(query);

                while (ExtraResultSet.next()) {
                    String one;
                    String two;
                    one = String.valueOf(ExtraResultSet.getInt(1));
                    two = String.valueOf(ExtraResultSet.getInt(2));
                    //    System.out.println("MSG_ID: " + one);
                    //  System.out.println("Sender_ID: " + two);
                    //   System.out.println("time_sent: " + ExtraResultSet.getTimestamp(3));
                    //  System.out.println("Subject: " + ExtraResultSet.getString(4));
                    //  System.out.println("Body " + ExtraResultSet.getString(5));
                    // System.out.println("---------------------------");
                    result = "\n " + "Msg_ID: " + one + " \n"
                            + "Sender_ID: " + two + " \n"
                            + "time_sent: " + ExtraResultSet.getTimestamp(3).toString() + " \n"
                            + "subject: " + ExtraResultSet.getString(4) + " \n"
                            + "Body " + ExtraResultSet.getString(5);

                }
                ExtraStatement.close();
                ExtraResultSet.close();
            }
            resultSet.close();

            query = "Update users SET last_login = ? where user_ID = " + owner;
            prepStatement = connection.prepareStatement(query);
            prepStatement.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            prepStatement.executeUpdate();
            prepStatement.close();

            success = 1;
        } catch (SQLException Ex) {
            //    System.out.println("Error running the sample queries.  Machine Error: "
            //           + Ex.toString());
            success = 0;
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (ExtraStatement != null) {
                    ExtraStatement.close();
                }
                if (resultSet != null) {
                    resultSet.close();
                }
                if (ExtraResultSet != null) {
                    ExtraResultSet.close();
                }
            } catch (SQLException e) {
                //     System.out.println("Cannot close Statement. Machine error: " + e.toString());
                success = 0;
            }
        }
        return result;
    }

    /**
     * Operates similarly to displayMessages, but only displays messages sent
     * since the user's last login.
     *
     * @param owner => the user who received a message
     */
    public String displayNewMessages(int owner) {
        int success = 0;
        String result = null;
        try {
            Timestamp time = null;
            query = "Select last_login from users where user_ID = " + owner;
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                time = resultSet.getTimestamp(1);
            }
            statement.close();
            resultSet.close();
            query = "Select msg_ID from Recipients where user_ID = " + owner;

            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {

                int msg_ID = resultSet.getInt(1);
                query = "select * from messages where msg_ID = " + msg_ID
                        + " and to_timestamp('" + time + "','YYYY-MM-DD HH24:MI:SS:ff') "
                        + " < time_sent";

                ExtraStatement = connection.createStatement();
                ExtraResultSet = ExtraStatement.executeQuery(query);
                while (ExtraResultSet.next()) {
                    String one;
                    String two;
                    one = String.valueOf(ExtraResultSet.getInt(1));
                    two = String.valueOf(ExtraResultSet.getInt(2));
                    //    System.out.println("MSG_ID: " + one);
                    //  System.out.println("Sender_ID: " + two);
                    //   System.out.println("time_sent: " + ExtraResultSet.getTimestamp(3));
                    //  System.out.println("Subject: " + ExtraResultSet.getString(4));
                    //  System.out.println("Body " + ExtraResultSet.getString(5));
                    // System.out.println("---------------------------");
                    result = "\n " + "Msg_ID: " + one + " \n"
                            + "Sender_ID: " + two + " \n"
                            + "time_sent: " + ExtraResultSet.getTimestamp(3).toString() + " \n"
                            + "subject: " + ExtraResultSet.getString(4) + " \n"
                            + "Body " + ExtraResultSet.getString(5);
                }
                ExtraStatement.close();
                ExtraResultSet.close();
            }
            success = 1;
        } catch (SQLException Ex) {
            // System.out.println("Error running the sample queries.  Machine Error: "
            //         + Ex.toString());
            success = 0;
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (ExtraStatement != null) {
                    ExtraStatement.close();
                }
                if (resultSet != null) {
                    resultSet.close();
                }
                if (ExtraResultSet != null) {
                    ExtraResultSet.close();
                }
            } catch (SQLException e) {
                //     System.out.println("Cannot close Statement. Machine error: " + e.toString());
                success = 0;
            }
        }
        return result;
    }

    /**
     * Method returns a string of the User's first and last name determined by
     * passed in ID number
     *
     * @param id column of Users table
     * @return the name of a User referenced by their ID number
     */
    public String getNameFromUserID(int id) {
        try {
            ExtraStatement = connection.createStatement();
            ExtraQuery = "Select fname,lname from users where user_id = " + id;
            ExtraResultSet = ExtraStatement.executeQuery(ExtraQuery);
            String fname = null;
            String lname = null;

            while (ExtraResultSet.next()) {
                fname = ExtraResultSet.getString(1);
                lname = ExtraResultSet.getString(2);
            }
            ExtraResultSet.close();
            ExtraStatement.close();
            return fname.concat(lname);

        } catch (SQLException Ex) {
          //  System.out.println("Error running the sample queries.  Machine Error: "
         //           + Ex.toString());
        } catch (NullPointerException N) {
          //  System.out.println("Null pointer! " + N.toString());
        } finally {
            try {
                if (ExtraStatement != null) {
                    ExtraStatement.close();
                }
                if (ExtraResultSet != null) {
                    ExtraResultSet.close();
                }

            } catch (SQLException e) {
                System.out.println("Cannot close Statement. Machine error: " + e.toString());
            }
        }
        return "ERROR";
    }

    /**
     * This provides a simple search function for the system. Given a string on
     * which to match any user in the system, any item in this string must be
     * matched against any significant field of a user's profile. That is if the
     * user searches for "xyz abc", the results should be the set of all
     * profiles that match "xyz" union the set of all profiles that matches
     * "abc". The names of all matching users should be printed out in a nicely
     * formatted way
     *
     * @param searchString
     * @throws java.sql.SQLException
     */
    public String searchForUser(String[] searchString) throws SQLException {
        //list of resultsets
        String result = null;
        LinkedList<ResultSet> rsList = new LinkedList<ResultSet>();

        //if there is no search term, exit
        //    if (searchString.length < 2) {
        //           return;
        //       }
        //convert the search term list to arraylist for easy manipulation
        ArrayList<String> searchStringAr = new ArrayList<String>(Arrays.asList(searchString));

        //removes the number 11 from the ARlist
        searchStringAr.remove("11");

        //iterates over the arraylist. if an item is numeric, it tests it against fields that can be numeric
        //for each string in the arraylist, it queries and adds the resultset to rslist
        for (String s : searchStringAr) {
            if (isNumber(s)) {
                try {
                    resultSet = null;
                    query = "SELECT USER_ID FROM USERS WHERE CAST(USER_ID AS VARCHAR(32)) like '%" + s + "%' OR CAST(DOB AS VARCHAR(32)) like '%" + s + "%'";
                    statement = connection.createStatement();

                    resultSet = statement.executeQuery(query);

                    rsList.addLast(resultSet);

                } catch (SQLException Ex) {
                    //      System.out.println("Error running the sample queries.  Machine Error: "
                    //              + Ex.toString());
                }
            } else {
                try {
                    resultSet = null;
                    query = "SELECT USER_ID FROM USERS WHERE UPPER(FNAME) LIKE UPPER('%" + s + "%') OR UPPER(LNAME) LIKE UPPER('%" + s
                            + "%') OR UPPER(EMAIL) LIKE UPPER('%" + s + "%') OR CAST(DOB AS VARCHAR(32)) LIKE '%" + s + "%'";
                    statement = connection.createStatement();

                    resultSet = statement.executeQuery(query);

                    rsList.addLast(resultSet);

                } catch (SQLException Ex) {
                    //    System.out.println("Error running the sample queries.  Machine Error: "
                    //             + Ex.toString());
                }
            }
        }

        //takes all the user IDs that have been selected and adds them to a set (sets dont allow for unique entries)
        HashSet<Integer> userIDs = new HashSet<Integer>();
        for (ResultSet r : rsList) {
            while (r.next()) {
                userIDs.add(r.getInt(1));
            }
        }

        //paranoia causing me to nullify all needed variables
        resultSet.close();
        statement.close();
        //   ExtraResultSet.close();
        resultSet = null;
        query = null;
        statement = null;
        ExtraResultSet = null;
        rsList = null;

        //query and print all of the ID that are in the hashset
        for (int j : userIDs) {
            try {
                query = "SELECT * FROM USERS WHERE USER_ID=" + j;
                statement = connection.createStatement();

                ExtraResultSet = statement.executeQuery(query);

                while (ExtraResultSet.next()) {
                    //   System.out.println("   USER_ID: " + ExtraResultSet.getInt(1));
                    //    System.out.println("     FNAME: " + ExtraResultSet.getString(2));
                    //   System.out.println("     LNAME: " + ExtraResultSet.getString(3));
                    //    System.out.println("     EMAIL: " + ExtraResultSet.getString(4));
                    //    System.out.println("       DOB: " + ExtraResultSet.getDate(5));
                    //   System.out.println("LAST_LOGIN: " + ExtraResultSet.getTimestamp(6));
                    //   System.out.println("-----------------------------------------------");

                    result = "\n    USER_ID: " + ExtraResultSet.getInt(1) + " \n"
                            + "     FNAME: " + ExtraResultSet.getString(2) + " \n"
                            + "     LNAME: " + ExtraResultSet.getString(3) + " \n"
                            + "     EMAIL: " + ExtraResultSet.getString(4) + " \n"
                            + "       DOB: " + ExtraResultSet.getDate(5) + " \n"
                            + "LAST_LOGIN: " + ExtraResultSet.getTimestamp(6);
                    
                }

            } catch (SQLException Ex) {
                //    System.out.println("Error running the sample queries.  Machine Error: "
                //             + Ex.toString());
            } finally {
                tryStatement(statement, ExtraStatement, resultSet, ExtraResultSet, cs);
            }

        }
        return result;
    }

    /**
     * This task explores the user's social network. Given two users (userA and
     * userB), find a path, if one exists, between the userA and the userB with
     * at most 3 hop between them. A hop is defined as a friendship between any
     * two users. The path should be printed out in a nicely formatted way.
     *
     * @param userA
     * @param userB
     */
    public String threeDegrees(int userA, int userB) {
                    String result = null;

        try {
            int[] path = new int[4];
            path[0] = userA;
            path[3] = userB;
            int hops = 1;
            ArrayList<Integer> source_friends = new ArrayList<Integer>();
            //first find all friends of source.  If any are userB, we've found a 1 hop path.
            statement = connection.createStatement();
            query = "Select id_1, id_2 from Friendships where status=1 AND (id_1=" + path[0] + " OR id_2=" + path[0] + ")";
            resultSet = statement.executeQuery(query);
            int id_1;
            int id_2;
            while (resultSet.next()) {
                id_1 = resultSet.getInt(1);
                id_2 = resultSet.getInt(2);
                if (id_1 != path[0]) {
                    source_friends.add(id_1);
                }
                if (id_2 != path[0]) {
                    source_friends.add(id_2);
                }
            }
            resultSet.close();
            statement.close();
            for (int friend : source_friends) {
                if (friend == userB) {
                    result = printThreeDegrees(path, hops);
                    return result;
                }
            }
            HashSet<Integer> dest_friends = new HashSet<Integer>();
            //now find all friends of the destination.  We'll need this to find 2 hop paths.
            hops++;
            statement = connection.createStatement();
            query = "Select id_1, id_2 from Friendships where status=1 AND (id_1=" + path[3] + " OR id_2=" + path[3] + ")";
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                id_1 = resultSet.getInt(1);
                id_2 = resultSet.getInt(2);
                if (id_1 != path[0]) {
                    dest_friends.add(id_1);
                }
                if (id_2 != path[0]) {
                    dest_friends.add(id_2);
                }
            }
            resultSet.close();
            statement.close();
            //for each friend in source_friends, if they are in dest_friends, we've found a 2 hop  path
            for (int friend : source_friends) {
                if (dest_friends.contains(friend)) {
                    path[1] = friend;
                   result = printThreeDegrees(path, hops);
                    return result;
                }
            }
            //else check the friends of all the source's friends.  If they're friends someone in dest_friends, that's our 3 hop path
            hops++;
            for (int friend : source_friends) {
                statement = connection.createStatement();
                query = "Select id_1, id_2 from Friendships where status=1 AND (id_1=" + friend + " OR id_2=" + friend + ")";
                resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    id_1 = resultSet.getInt(1);
                    id_2 = resultSet.getInt(2);
                    if (id_1 == friend && dest_friends.contains(id_2)) {
                        path[1] = friend;
                        path[2] = id_2;
                        result = printThreeDegrees(path, hops);
                        resultSet.close();
                        statement.close();
                        return result;
                    }
                    if (id_2 == friend && dest_friends.contains(id_1)) {
                        path[1] = friend;
                        path[2] = id_1;
                      result =  printThreeDegrees(path, hops);
                        resultSet.close();
                        statement.close();
                        return result;
                    }
                }
                resultSet.close();
                statement.close();
            }
            //if we've gotten this far, there is no path.
           // System.out.println("There is no path of 3 or fewer hops connecting " + getNameFromUserID(userA) + " and " + getNameFromUserID(userB));
            result = "There is no path of 3 or fewer hops connecting " + getNameFromUserID(userA) + " and " + getNameFromUserID(userB);
            return result;
        } catch (SQLException Ex) {
          //  System.out.println("Error running the sample queries.  Machine Error: "
          //          + Ex.toString());
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (ExtraStatement != null) {
                    ExtraStatement.close();
                }
                if (resultSet != null) {
                    resultSet.close();
                }
                if (ExtraResultSet != null) {
                    ExtraResultSet.close();
                }
                if (cs != null) {
                    cs.close();
                }
            } catch (SQLException e) {
            //    System.out.println("Cannot close Statement. Machine error: " + e.toString());
            }
        }
        return result;
    }

    /**
     * Helper method to print three-hops path
     *
     * @param userA
     * @param userB
     */
    public String printThreeDegrees(int[] path, int hops) {
        String result = ""; 
       // System.out.println("There is a path of " + hops + " hop(s) between " + getNameFromUserID(path[0]) + " and " + getNameFromUserID(path[3]));
        for (int j = 0; j < hops; j++) {
        //    System.out.print(getNameFromUserID(path[j]) + " -> ");
        result = result + getNameFromUserID(path[j]) + " -> ";
        }
        result = result + getNameFromUserID(path[3]);
     //   System.out.println(getNameFromUserID(path[3]));
        return result;
    }

    /**
     * Display the top k who have sent or received the highest number of
     * messages during for the past x months. x and k should be an input
     * parameters to this function.
     *
     * @param k
     * @param x
     */
    public String topMessagers(int user_count, int months) {
        String result = null;
        try {
            ArrayList<Integer> users = new ArrayList<Integer>();
            ArrayList<Integer> msg_count = new ArrayList<Integer>();
            int count;
            int user;
            int index;

            query = "Select * from (Select COUNT(msg_ID) COUNT,user_ID from (Select * from "
                    + "recipients where MSG_ID IN (SELECT MSG_ID from messages "
                    + "where time_sent > add_months(SYSDATE, -" + months + "))) group by user_ID "
                    + "order by COUNT DESC) where rownum <= " + user_count;

            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            //   System.out.println("entering old ResultSet");

            while (resultSet.next()) {
                count = resultSet.getInt(1);
                user = resultSet.getInt(2);
                users.add(user);
                msg_count.add(count);
            }
            query = "select * from (Select COUNT(sender_ID) COUNT, sender_ID "
                    + "from messages where time_sent > add_months(SYSDATE, -" + months + ") "
                    + "group by sender_ID order by COUNT DESC) Where rownum <= " + user_count;

            statement.close();
            resultSet.close();

            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                count = resultSet.getInt(1);
                user = resultSet.getInt(2);

                if (users.contains(user)) {
                    index = users.indexOf(user);
                    msg_count.set(index, msg_count.get(index) + count);
                } else {
                    users.add(user);
                    msg_count.add(count);
                }
            }

            //Ugly bubble sort but it works.
            int swap;
            int msg_swap;
            for (int i = 0; i < msg_count.size(); i++) {
                for (int j = 0; j < msg_count.size() - 1; j++) {
                    if (msg_count.get(j) < msg_count.get(j + 1)) {
                        swap = users.get(j);
                        msg_swap = msg_count.get(j);

                        users.set(j, users.get(j + 1));
                        users.set(j + 1, swap);

                        msg_count.set((j), msg_count.get(j + 1));
                        msg_count.set(j + 1, msg_swap);
                    }
                }
            }

           // System.out.println("Top " + user_count + " for the past " + months + " months");
            result = "\n Top " + user_count + " for the past " + months + " months";
                  
            if (user_count > users.size()) {
                user_count = users.size(); //Prevent a crash
            }

            for (int i = 0; i < user_count; i++) {
                result = result + "\n " + users.get(i) + " handled a total of " + msg_count.get(i) + " messages";
              //  System.out.println(users.get(i) + " handled a total of " + msg_count.get(i) + " messages");
            }

        } catch (SQLException Ex) {
          //  System.out.println("Error running the sample queries.  Machine Error: "
            //        + Ex.toString());
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (ExtraStatement != null) {
                    ExtraStatement.close();
                }
                if (resultSet != null) {
                    resultSet.close();
                }
                if (ExtraResultSet != null) {
                    ExtraResultSet.close();
                }
            } catch (SQLException e) {
            //    System.out.println("Cannot close Statement. Machine error: " + e.toString());
            }
        }
        return result;
    }

    /**
     * Remove a user and all of their information from the system. When a user
     * is removed, the system should then delete the user from the groups he or
     * she was a member of using a trigger. Note that messages require special
     * handling because they are owned by both the sender and the receiver.
     * Therefore, a message is deleted only when both the sender and all
     * receivers are deleted. Attention should be paid handling integrity
     * constraints.
     *
     * @param id
     */
    public String dropUser(int id) {
        //Much of the finer details of this method are handled by the USER_DELETE trigger
        //DELETE FROM USERS WHERE USER_ID=101;
        // System.out.println("dropping " + id);
        String result = null;
        try {
            //  System.out.println("trying...");
            statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM USERS WHERE USER_ID =" + id);

        } catch (SQLException Ex) {
        //    System.out.println("Error running the sample queries.  Machine Error: "
        //            + Ex.toString());
        //    System.out.println("user_id " + id);
        } finally {
            // System.out.println("finally");
            try {
                if (statement != null) {
                    statement.close();
                }
                if (ExtraStatement != null) {
                    ExtraStatement.close();
                }
                if (resultSet != null) {
                    resultSet.close();
                }
                if (ExtraResultSet != null) {
                    ExtraResultSet.close();
                }
                if (cs != null) {
                    cs.close();
                }
            } catch (SQLException e) {
           //     System.out.println("Cannot close Statement. Machine error: " + e.toString());
            }
        }
        return result;
    }

    /**
     * This code was repeated so many times I just turned it into a method.
     *
     * @param s
     * @param es
     * @param rs
     * @param ers
     * @param ocs
     */
    public void tryStatement(Statement s, Statement es, ResultSet rs, ResultSet ers, OracleCallableStatement ocs) {
        try {
            if (s != null) {
                s.close();
            }
            if (es != null) {
                es.close();
            }
            if (rs != null) {
                rs.close();
            }
            if (ers != null) {
                ers.close();
            }
            if (ocs != null) {
                ocs.close();
            }
        } catch (SQLException e) {
            System.out.println("Cannot close Statement. Machine error: " + e.toString());
        }
    }

    /**
     * Checks to see if a string is a number. This prevents crashes on lines
     * such a s Integer.parseInt(s) If the string is numeric and can be
     * converted to an integer, this method returns true;
     *
     * @param s
     * @return
     */
    public boolean isNumber(String s) {
        char[] charArray = s.toCharArray();

        for (char c : charArray) {
            if (c != '1' && c != '2' && c != '3' && c != '4' && c != '5' && c != '6' && c != '7' && c != '8' && c != '9' && c != '0') {
                return false;
            }
        }

        return true;
    }

    public void close() {
        try {
            connection.close();
        } catch (SQLException ex) {
            System.out.println(ex.toString());
        }
    }

    public void getTableSizes() {
        String[] tables = {"Users", "Friendships", "User_groups",
            "Group_membership", "Messages", "Recipients"};
        System.out.println();
        for (String table : tables) {
            try {
                query = "select count(*) from " + table;
                statement = connection.createStatement();
                resultSet = statement.executeQuery(query);
                int size = -1;
                
                while (resultSet.next()) {
                    size = resultSet.getInt(1);
                    System.out.println("This size of " + table + " is " + size);
              //      System.err.println("This size of " + table + " is " + size);
                }
                
            } catch (SQLException ex) {
                System.out.println(ex.toString());
            } finally {
                try {
                    if (statement != null) {
                        statement.close();
                    }
                    if (ExtraStatement != null) {
                        ExtraStatement.close();
                    }
                    if (resultSet != null) {
                        resultSet.close();
                    }
                    if (ExtraResultSet != null) {
                        ExtraResultSet.close();
                    }
                    if (cs != null) {
                        cs.close();
                    }
                } catch (SQLException e) {
                    System.out.println("Cannot close Statement. Machine error: " + e.toString());
                }
            }
        }
        System.out.println();
    }

    public String JDBC_part2_decision_tree(String[] args) throws SQLException {

        int command;
        String result = null;
        if (args.length < 1) {
            command = -1;
        } else {
            command = Integer.parseInt(args[0]);
        }

        int sender, receiver, owner, recipient, group;
        String subject, body;

        switch (command) {
            case 1:
                System.out.println("createUsers");
                String fname = args[1];
                String lname = args[2];
                String email = args[3];
                String dob = args[4];
                result = createUser(fname, lname, email, dob);
                break;
            case 2:
                System.out.println("initiateFriendship");
                sender = Integer.parseInt(args[1]);
                receiver = Integer.parseInt(args[2]);
                result = initiateFriendship(sender, receiver);
                break;
            case 3:
                System.out.println("establishFriendship");
                sender = Integer.parseInt(args[1]);
                receiver = Integer.parseInt(args[2]);
               result =  establishFriendship(sender, receiver);
                break;
            case 4:
                System.out.println("displayFriends");
                owner = Integer.parseInt(args[1]);
                result = displayFriends(owner);
                break;
            case 5:
                System.out.println("createGroup");
                String name = args[1];
                String description = args[2];
                int limit = Integer.parseInt(args[3]);
               result =  createGroup(name, description, limit);
                break;
            case 6:
                System.out.println("addToGroup");
                int user = Integer.parseInt(args[1]);
                group = Integer.parseInt(args[2]);
                result = addToGroup(user, group);
                break;
            case 7:
                System.out.println("sendMessageToUser");
                subject = args[1];
                body = args[2];
                recipient = Integer.parseInt(args[3]);
                owner = Integer.parseInt(args[4]);
               result =  sendMessageToUser(subject, body, recipient, owner);
                break;
            case 8:
                System.out.println("sendMessageToGroup");
                subject = args[1];
                body = args[2];
                group = Integer.parseInt(args[3]);
                owner = Integer.parseInt(args[4]);
                result = sendMessageToGroup(subject, body, group, owner);
                break;
            case 9:
                owner = Integer.parseInt(args[1]);
                System.out.println("Displaying Messages: user " + owner);
               result =  displayMessages(owner);
                break;
            case 10:
                owner = Integer.parseInt(args[1]);
                System.out.println("Displaying new messages: user " + owner);
                result = displayNewMessages(owner);
                break;
            case 11:
                System.out.println("Displaying search results");
                System.out.println("-----------------------------------------------");
                result = searchForUser(args);
                break;
            case 12:
                System.out.println("Displaying third degree connections");
                sender = Integer.parseInt(args[1]);
                receiver = Integer.parseInt(args[2]);
               result =  threeDegrees(sender, receiver);
                break;
            case 13:
                System.out.println("Displaying top messages");
               result =  topMessagers(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
                break;
            case 14:
                System.out.println("Dropping user");
                user = Integer.parseInt(args[1]);
              result =   dropUser(user);
                break;
            default:
                System.out.println("PLEASE FOLLOW PROPER USAGE\n"
                        + "-----------------------------------------------------------------------------------------------\n"
                        + "java JDBC3main   method number        [arguments]\n"
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
                        + "            12: [threeDegrees]        (int userA, int userB)\n"
                        + "            13: [topMessagers]        (int user_count, int months)\n"
                        + "            14: [dropUser]            (int id)\n"
                        + "            15: [StressTest]          No Input\n");
        }
        return result;
    }

}
