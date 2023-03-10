package com.example.demo.DTO;

import com.example.demo.Model.Review;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
@NoArgsConstructor
public class UserDTO {
    private String username;
    private String password;
    private String birthday;
    private String country;
    private String gender;
    private List<ReviewDTO> mostRecentReviews;

    private boolean isMyself;

    private boolean isfollowed;

    private boolean isAdmin;
    private int numFollowers;
    private int numFollowed;

    private int CardOwned;

    public UserDTO(String username, String password, boolean isAdmin) {
        this.username = username;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    private boolean isMyself(){
        return isMyself;
    }

    public UserDTO(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }
    public boolean getAdmin(){return isAdmin;}

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setMostRecentReviews(List<ReviewDTO> mostRecentReviews) {
        this.mostRecentReviews = mostRecentReviews;
    }

    public void setMostRecentReviews(List<Review> reviewList, String username) {
        List<ReviewDTO> reviewDTOList = new ArrayList<>();
        for (Review review : reviewList) {
            ReviewDTO reviewDTO = new ReviewDTO();
            reviewDTO.setId(review.getId());
            reviewDTO.setAnime(review.getAnime());
            reviewDTO.setProfile(review.getUser());
            reviewDTO.setTimestamp(review.getTimestamp());
            reviewDTO.setText(review.getText());
            reviewDTO.setScore(review.getScore());
            reviewDTOList.add(reviewDTO);
        }
        this.mostRecentReviews =  reviewDTOList;
    }
    public List<ReviewDTO> getMostRecentReviews() {
        return mostRecentReviews;
    }

    public void setFollowers(int followerNumberByUsername) {
        this.numFollowers = followerNumberByUsername;
    }
    public void setFollowedNum(int followedNumberByUsername) {
        this.numFollowed = followedNumberByUsername;
    }



    public void setCardOwned(int numcardOwned) {
        this.CardOwned = numcardOwned;
    }


    public void setMyself(boolean equals) {
    }

    public void setGender(String gender) { this.gender = gender;
    }

    public void setBirthday(String birthday) { this.birthday = birthday;
    }

    public void setFollowed(boolean followed) {
        this.isfollowed = followed;
    }
}
