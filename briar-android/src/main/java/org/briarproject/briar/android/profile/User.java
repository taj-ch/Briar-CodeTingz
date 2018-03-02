package org.briarproject.briar.android.profile;

/**
 * Created by Laxman on 2/21/2018.
 */

public class User {

	private String nickname;
	private String firstName;
	private String lastName;
	private String email;
	private String description;

	public User() {
	}

	public User(String nickname, String firstName, String lastName,
			String email, String description) {
		this.nickname = nickname;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.description = description;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "User{" +
				"nickname='" + nickname + '\'' +
				", firstName='" + firstName + '\'' +
				", lastName='" + lastName + '\'' +
				", email='" + email + '\'' +
				", description='" + description + '\'' +
				'}';
	}
}
