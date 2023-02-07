package com.example.demo.Controller.api;

import com.example.demo.Service.AnimeService;
import com.example.demo.Utilities.SVariables;
import com.google.gson.Gson;
import com.example.demo.DOT.AnimeDTO;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

@RestController
@SessionAttributes("sessionVariables")
public class currentAnime {
    @Autowired
    AnimeService animeService;

    @RequestMapping("/api/currentAnime")
    public @ResponseBody String returnAnime(Model model) {
        Gson gson = new GsonBuilder().create();
        SVariables sv = (SVariables) model.getAttribute("sessionVariables");
        if (sv == null || sv.animeToDisplay == null) {
            return gson.toJson("{\"type\":1, \"message\": \"Something went wrong\"}");
        }
        AnimeDTO anime =  animeService.getAnime(sv.animeToDisplay);
        if(anime==null)
            return gson.toJson("{\"type\":1, \"message\": \"Inexistent anime\"}");
        String desc = anime.getSynopsis().replace("\n", " ");
        return gson.toJson("{\"title\":\"" + sv.animeToDisplay + "\"," + "\"desc\":\"" + desc + "\"," + "\"img\":\"" + anime.getImg_url() + "\"}");
    }
}