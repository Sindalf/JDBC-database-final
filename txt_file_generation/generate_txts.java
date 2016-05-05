import java.io.*;
import java.util.*; //list, scanner, etc.
import java.text.SimpleDateFormat;
import java.util.Date;


/* Generate random text input for milestone 3
 * USAGE: uncomment the applicable function call and select a number of input lines
 * RUN generate_txts > filename
 */
public class generate_txts {
	int MAX_ID=500;
	
    public generate_txts() {
        //insert_users(10000);
        //insert_friends(5000, 0);
        //insert_friends(10000, 1);
        //display_friends(500);
        //insert_groups(5000);
        //add_to_group(10000);
        gen_msg(5000, 500); //send to user
        //gen_msg(5000, 5000); //send to group
			//gen_search_queries(10000); //using surname file for now
        //gen_3Hops(3000);
        //gen_TopMessagers(3000);
        //gen_dropUser(3000, 10000);
    }

    public void insert_users(int count_max) {
        int counter = 1;
        ArrayList<String> fnames = new ArrayList<String>();
		ArrayList<String> lnames = new ArrayList<String>();
		try{
			Scanner s = new Scanner(new File("fnames_1892.txt"));
			while (s.hasNext()) {
				fnames.add(s.next());
			}
			s.close();
			s = new Scanner(new File("lnames.txt"));
			while (s.hasNext()) {
				lnames.add(s.next());
			}
			s.close();
		} catch (FileNotFoundException e) {
			System.out.println("Missing name files!");
			System.exit(-1);
		}
		String ext = "@fakeemail.com";
		String fname;
		String lname;
		String email;
		String dob_string;
		int email_rand_suffix;
		int fname_limit = 2920;
        int lname_limit = 4999;
        Date dob = new Date();
		Random r = new Random();
		java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("yyyy-DDD");
		java.text.SimpleDateFormat df_out = new java.text.SimpleDateFormat("yyyy-MM-dd");
		while (counter <= count_max) {
			lname = lnames.get(r.nextInt(lname_limit));
			fname = fnames.get(r.nextInt(fname_limit));
			email_rand_suffix = r.nextInt(100);
			email = lname + email_rand_suffix + ext;
			dob_string = "" + (r.nextInt(2016 - 1900) + 1900) + "-" + r.nextInt(365);
			try {
				dob = df.parse(dob_string);
				dob_string = df_out.format(dob);
			} catch (Exception e) {
				System.out.println("Error parsing date. " + e.toString());
				return;
			}
			System.out.println(counter + ", " + fname + ", " + lname + ", " + email + ", " + dob_string);
			counter++;
		}
    return;
   }
   
       public void insert_friends(int count_max, int est_or_init) {
        int counter = 0;
		int max_ID = MAX_ID;
		Random r = new Random();
		int id_1;
		int id_2;
		while (counter < count_max) {
			id_1 = r.nextInt(max_ID)+1;
			id_2 = r.nextInt(max_ID)+1;
			while (id_2==id_1)
				id_2 = r.nextInt(max_ID);
			System.out.println(id_1 + ", " + id_2 + ", " + est_or_init);
			counter++;
		}
		return;
	}
   
   public void display_friends(int count_max) {
        int counter = 1;
		while (counter <= count_max) {
			System.out.println(counter);
			counter++;
		}
		return;
   }
   
   
   public void insert_groups(int count_max) {
        try {
            ArrayList<String> g_names = new ArrayList<String>();
            try {
                Scanner s = new Scanner(new File("hobbies.txt"));
                s.useDelimiter("\r?\n|\r");
                while (s.hasNext()) {
                    g_names.add(s.next());
                }
                s.close();
                s = new Scanner(new File("nouns.txt"));
                s.useDelimiter("\r?\n|\r");
                while (s.hasNext()) {
                    g_names.add(s.next());
                }
                s.close();
            } catch (FileNotFoundException e) {
                System.out.println("Missing name files!");
                System.exit(-1);
            }
            int counter = 0;
            Random r = new Random();
            String group_name;
            String group_desc = "We talk about ";
            int max_members = 0;
            while (counter < count_max) {
                max_members = r.nextInt(250); //arbitrarily decided to cap our max membership at 250 for this function
                //make a group
                if(counter<g_names.size()-1) {
					group_name = g_names.get(counter);
					System.out.println(group_name + ", " + group_desc + group_name + ", " + max_members);
				}
				else {
					group_name = g_names.get(counter-g_names.size());
					System.out.println("The " +  group_name + " club" + ", " + group_desc + group_name + "s, " + max_members);	
				}
			counter++;
            }
        } catch (Exception Ex) {
            System.out.println("Error running the sample queries.  Machine Error: "
                    + Ex.toString());
        }
    }
   
   public void add_to_group(int count_max) {
        int counter = 1;
        Random r = new Random();
        int id;
        int group = r.nextInt(3000);
		while (counter <= count_max) {
			id = r.nextInt(MAX_ID)+1;
			group = r.nextInt(3000);
			System.out.println(id + ", " + group);
			counter++;
		}
		return;
   }
   
      public void gen_msg(int count_max, int recipient_max) {
        try {
            ArrayList<String> words = new ArrayList<String>();
            try {
                Scanner s = new Scanner(new File("words.txt"));
                s.useDelimiter("\r?\n|\r");
                while (s.hasNext()) {
                    words.add(s.next());
                }
                s.close();
            } catch (FileNotFoundException e) {
                System.out.println("Missing name files!");
                System.exit(-1);
            }
            int counter = 0;
            Random r = new Random();
            StringBuilder message;
            String subject;
            int num_words;
            int recipient;
            int sender;
            while (counter < count_max) {
				sender = r.nextInt(MAX_ID)+1;
				recipient = r.nextInt(recipient_max)+1;
                num_words = r.nextInt(7); //arbitrarily decided to cap our max membership at fifty for this function
                //make a group
                subject = words.get(r.nextInt(words.size()));
                message = new StringBuilder(subject);
                for( int i=0; i<num_words; i++) {
					message.append(" " + words.get(r.nextInt(words.size())));
				}
				System.out.println(subject + ", " + message + "., " + recipient + ", " + sender);
			counter++;
            }
        } catch (Exception Ex) {
            System.out.println("Error running the sample queries.  Machine Error: "
                    + Ex.toString());
        }
    }
    
    public void gen_search_queries(int count_max) {
		Random r = new Random();
		int num_search;
		boolean space;
		int counter=0;
		while (counter < count_max) {
			num_search=r.nextInt(4)+2;
			for( int i=0; i<num_search; i++) {
				char c = (char) (r.nextInt(26) + 'a');
				System.out.print(c);
				space = r.nextBoolean();
				if (space)
					System.out.print(" ");
			}
			System.out.println();
		counter++;	
		}
		return;
	}
   
    public void gen_3Hops(int count_max) {
        int counter = 0;
		int max_ID = MAX_ID;
		Random r = new Random();
		int id_1;
		int id_2;
		while (counter < count_max) {
			id_1 = r.nextInt(max_ID)+1;
			id_2 = r.nextInt(max_ID)+1;
			while (id_2==id_1)
				id_2 = r.nextInt(max_ID);
			System.out.println(id_1 + ", " + id_2);
			counter++;
		}
		return;
   }
   
    public void gen_TopMessagers(int count_max) {
        int counter = 0;
		Random r = new Random();
		int num_users;
		int months;
		while (counter < count_max) {
			num_users = r.nextInt(20)+1;
			months = r.nextInt(24)+1;
			System.out.println(num_users + ", " + months);
			counter++;
		}
		return;
   }
   
   public void gen_dropUser(int count_max, int user_max) {
        int counter = 1;
		Random r = new Random();
		int user_ID;
		while (counter <= count_max) {
			user_ID = r.nextInt(user_max);
			System.out.println(user_ID);
			counter++;
		}
		return;
   }
    
   public static void main(String args[])  {
		generate_txts demo = new generate_txts();
		return;
	}
}
