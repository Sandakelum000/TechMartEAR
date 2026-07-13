package lk.techmart.core.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@NoArgsConstructor
@Data
@NamedQuery(name = "User.getByEmail", query = "FROM User u WHERE u.email=:email")
public class User extends BaseEntity implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "first_name",nullable = false,length = 45)
    private String firstName;

    @Column(name = "last_name",nullable = false,length = 45)
    private String lastName;

    @Column(name = "email",nullable = false,length = 60)
    private String email;

    @Column(name = "password",nullable = false,length = 20)
    private String password;

    @Column(name = "active",nullable = false)
    private boolean active;

//    @Column(name = "created_at",updatable = false,nullable = false)
//    private LocalDateTime createdAt;
//
//    @Column(name = "updated_at",nullable = false)
//    private LocalDateTime updatedAt;

}
