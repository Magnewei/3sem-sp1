package app.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActorDTO {

    @JsonIgnore
    private int id;

    @JsonProperty("id")
    private int actorId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("gender")
    private int gender;

    @JsonIgnore
    private List<MovieDTO> knownFor = new ArrayList<>();
}
