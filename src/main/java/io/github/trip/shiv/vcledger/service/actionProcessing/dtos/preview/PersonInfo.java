package io.github.trip.shiv.vcledger.service.actionProcessing.dtos.preview;
import io.github.trip.shiv.vcledger.service.actionProcessing.dtos.enums.PersonType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonInfo {

    private Long id;

    private String name;

    private String photoUrl;

    private PersonType type;
}