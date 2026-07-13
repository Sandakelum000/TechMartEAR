package lk.techmart.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Address implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "line_one", length = 45, nullable = false)
    private String lineOne;

    @Column(name = "line_two", length = 45)
    private String lineTwo;

    @Column(name = "postal_code", length = 10)
    private String postalCode;

    @Column(length = 10, nullable = false)
    private String mobile;

    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name = "users_id")
    private User user;

    @Column(name = "is_primary", nullable = false)
    private boolean isPrimary=false;

}
