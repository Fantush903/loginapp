    package com.example.demo.model;

    import com.fasterxml.jackson.annotation.JsonIgnore;
    import com.fasterxml.jackson.annotation.JsonProperty;
    import jakarta.persistence.*;
    import jakarta.validation.constraints.Email;
    import jakarta.validation.constraints.NotBlank;
    import lombok.*;

    @Getter
    @Setter
    @AllArgsConstructor
    //@Data                     // Lombok: auto-generates getters, setters, toString
    @NoArgsConstructor
    @ToString(exclude = "password")
    @EqualsAndHashCode (of = "email")
    @Entity                  // Tells Spring: "This class = a database table"
    @Table(name = "users")  // The table will be named "users" in MySQL

    public class User {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)  //Auto increment id
        private Long id;

        @NotBlank(message = "Username cannot be empty")
        @Column(nullable = false)
        private String username;

        @Email(message = "Enter a valid email")
        @NotBlank(message = "Email cannot be empty")
        @Column(unique = true, nullable = false)     // No two users can have the same email
        private String email;

        @NotBlank(message = "Password cannot be empty")
        @Column(nullable = false)
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)    //✅ hides password from all API responses
        private String password;

        @Column(nullable = false, columnDefinition = "VARCHAR(50) DEFAULT 'USER' ")
        private String role = "USER";
    }
