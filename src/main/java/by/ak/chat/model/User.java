package by.ak.chat.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Document
public class User {
  @Id
  private String id;
  @NotBlank
  @Size(min = 3, max = 30)
  private String username;

  @NotBlank
  @Size(min = 6, max = 40)
  private String password;

  @NotBlank
  @Size(min = 3, max = 30)
  private String firstName;

  @NotBlank
  @Size(min = 3, max = 30)
  private String lastName;

  @Past
  @NotNull
  private LocalDate dob;

  @NotBlank
  @Email
  private String email;

  private Boolean enabled = true;

  private byte[] avatar;

  private Role role = Role.USER;

  @PastOrPresent
  private LocalDateTime created = LocalDateTime.now();
//
//  public String getRole() {
//    return role.name();
//  }
//
//  public void setRole(String role) {
//    this.role = Role.valueOf(role);
//  }
}
