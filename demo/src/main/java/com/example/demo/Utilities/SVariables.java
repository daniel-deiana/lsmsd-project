package com.example.demo.Utilities;

import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

public class SVariables {
    public String myself;

    //public List<Anime> current_results;
    public int currentPage;
    public boolean lastPage;

    public boolean admin;

    @Override
    public String toString() {
        return "SessionVariables{" +
                "myself='" + myself + '\'' +
                ", animeToDisplay='" + animeToDisplay + '\'' +
                ", userToDisplay='" + userToDisplay + '\'' +
                '}';
    }

}