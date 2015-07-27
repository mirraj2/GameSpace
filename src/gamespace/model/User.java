package gamespace.model;

import java.time.LocalDate;
import java.time.Period;
import com.google.common.base.Objects;

public class User {

  public Long id;
  public final long facebookId;
  public final String email, firstName, lastName, gender;
  public final LocalDate birthday;
  public final String picUrl;

  public User(Long id, long facebookId, String email, String firstName, String lastName, String gender,
      LocalDate birthday,
      String picUrl) {
    this.id = id;
    this.facebookId = facebookId;
    this.email = email;
    this.firstName = firstName;
    this.lastName = lastName;
    this.gender = gender;
    this.birthday = birthday;
    this.picUrl = picUrl;
  }

  public String getFullName() {
    return firstName + " " + lastName;
  }

  @Override
  public String toString() {
    return getFullName();
  }

  public String ageSex() {
    if (birthday == null) {
      return "";
    }

    String sex = gender;
    if (sex.equals("male")) {
      sex = "M";
    } else if (sex.equals("female")) {
      sex = "F";
    }
    Integer years = Period.between(birthday, LocalDate.now()).getYears();
    return years + " " + sex;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof User)) {
      return false;
    }
    User that = (User) obj;
    return Objects.equal(this.id, that.id);
  }

}
