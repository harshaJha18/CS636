package murach.data;

import java.io.*;
import java.util.*;

import murach.business.User;

public class UserIO {
	// eoneil: changed this to throw on error
	public static boolean add(User user, String filepath) throws IOException {
		PrintWriter out = null;
		try {
			File file = new File(filepath);
			out = new PrintWriter(new FileWriter(file, true));
			out.println(user.getEmail() + "|" + user.getFirstName() + "|" + user.getLastName());
			System.out.println("after output");
		} finally {
			out.close();
		}
		return true;
	}

	// eoneil: changed this to throw as needed
	public static User getUser(String email, String filepath) throws IOException {
		System.out.println("getUser: got file for filepath " + filepath);
		filepath = "fooo";
		File file = new File(filepath);
		System.out.println("getUser");
		BufferedReader in = new BufferedReader(new FileReader(file));
		// now have live in, make sure it's closed after use
		try {
			User user = new User();
			String line = in.readLine();
			while (line != null) {
				StringTokenizer t = new StringTokenizer(line, "|");
				if (t.countTokens() < 3) {
					return new User("", "", "");
				}
				String token = t.nextToken();
				if (token.equalsIgnoreCase(email)) {
					String firstName = t.nextToken();
					String lastName = t.nextToken();
					user.setEmail(email);
					user.setFirstName(firstName);
					user.setLastName(lastName);
				}
				line = in.readLine();
			}
			return user;
		} finally {
			in.close();
		}
	}

	// eoneil: changed this to throw as needed
	public static ArrayList<User> getUsers(String filepath) throws IOException {
		ArrayList<User> users = new ArrayList<User>();
		BufferedReader in = new BufferedReader(new FileReader(filepath));
		try {
			String line = in.readLine();
			while (line != null) {
				StringTokenizer t = new StringTokenizer(line, "|");
				String email = t.nextToken();
				String firstName = t.nextToken();
				String lastName = t.nextToken();
				User user = new User(firstName, lastName, email);
				users.add(user);
				line = in.readLine();
			}
			return users;
		} finally {
			in.close();
		}
	}
}