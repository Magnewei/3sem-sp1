package app.services;

import app.dtos.MovieDTO;

import java.util.List;

public interface ApiService <T>{
    List<MovieDTO> fetchMoviesFromApiEndpoint(String endpoint);
}

