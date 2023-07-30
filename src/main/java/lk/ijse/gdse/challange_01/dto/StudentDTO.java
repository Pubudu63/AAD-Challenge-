package lk.ijse.gdse.challange_01.dto;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class StudentDTO implements Serializable {
    private int id;
    private String name;
    private String email;
    private String city;
    private int level;
}
