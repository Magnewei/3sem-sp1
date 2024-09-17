package app.services;

import app.entities.Actor;
import app.entities.Director;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

/**
 * Purpose:
 *
 * @Author: Anton Friis Stengaard
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreditsResponse {
    private List<Actor> cast;
    private List<Director> crew;
}
