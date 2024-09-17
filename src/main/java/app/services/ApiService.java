package app.services;

import app.dtos.MovieDTO;

import java.util.List;

public interface ApiService <T>{
    List<MovieDTO>  fetchMoviesFromApiEndpoint(String endpoint);
    List<MovieDTO> fetchActorsFromApiEndPoint(String endpoint);
    List<MovieDTO> fetchDirectorsFromApiEndPoint(String endpoint);
}

