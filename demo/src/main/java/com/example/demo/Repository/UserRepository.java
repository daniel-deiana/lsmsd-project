package com.example.demo.Repository;


import com.example.demo.DTO.FigureDTO;
import com.example.demo.DTO.UserDTO;
import com.example.demo.Model.Review;
import com.example.demo.Model.User;
import com.example.demo.Repository.MongoDB.ReviewRepositoryMongo;
import com.example.demo.Repository.MongoDB.UserRepositoryMongo;
import com.example.demo.Repository.Neo4j.UserNeo4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.neo4j.driver.Record;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Repository
public class UserRepository {
	Logger logger = LoggerFactory.getLogger(UserRepository.class);
	@Autowired
	private UserRepositoryMongo userMongo;
	@Autowired
	private MongoOperations mongoOperations;
	@Autowired
	private ReviewRepositoryMongo revMongo;

	UserNeo4j neo4j = new UserNeo4j();


	public boolean addUser(User user) {
		boolean result = true;
		try {
			userMongo.save(user);
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}
		return result;
	}

	//This function update the list of the most recent reviews of a user when a new review is added
	public void updateMostReviewed(Review review) {
		try {
			Optional<User> user = userMongo.findByUsername(review.getProfile());
			List<Review> reviewList = user.get().getMostRecentReviews();
			if (reviewList.size() >= 5) {
				reviewList.remove(4);
				reviewList.add(0, review);
			} else {
				reviewList.add(0, review);
			}
			user.get().setMostRecentReviews(reviewList);
			userMongo.save(user.get());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public boolean deleteUser(User user) {
		boolean result = true;
		try {
			userMongo.delete(user);
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}
		return result;
	}

	public Optional<User> getUserByUsername(String username) {
		Optional<User> user = Optional.empty();
		try {
			user = userMongo.findByUsername(username);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return user;
	}

	public boolean checkAdmin(String username) {
		Optional<User> result;
		try {
			result = getUserByUsername(username);
			if (result.isEmpty())
				return false;
			if (!result.get().isAdmin())
				return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	//////////////////////////////// NEO4J /////////////////////////////////

	public List<FigureDTO> getTop10(String username) {
		List<FigureDTO> figures = new ArrayList<>();
		List<Record> records = neo4j.getTop10ByUsername(username);
		for (Record r : records) {
			String name = r.values().get(0).get("name").asString();
			String anime = r.values().get(0).get("anime").asString();
			String img = r.values().get(0).get("img").asString();
			FigureDTO fig = new FigureDTO(name, anime, img);
			figures.add(fig);
		}
		return figures;
	}

	public boolean followUserByUsername(String current, String toFollow){
		return neo4j.followUserByUsername(current, toFollow);
	}
	public boolean unfollowUserByUsername(String current, String toUnfollow){
		return neo4j.unfollowUserByUsername(current, toUnfollow);
	}
	public boolean isFollowed(String myself, String username){
		return neo4j.isFollowed(myself, username).get(0).get("isFollowed").asBoolean();
	}

	public int findFollowerNumberByUsername(String username){
		return neo4j.findFollowerNumberByUsername(username).get(0).get("numFollowers").asInt();
	}

	public int findFollowedNumberByUsername(String username) {
		return neo4j.findFollowedNumberByUsername(username).get(0).get("numFollowed").asInt();
	}

	public int findCardNumberByUsername(String username) {
		return neo4j.findCardNumberByUsername(username).get(0).get("numCard").asInt();
	}
	public List<FigureDTO> getCharacters(String username) {
		List<FigureDTO> figures = new ArrayList<>();
		List<Record> records = neo4j.getCharacters(username);
		for (Record r : records) {
			String name = r.values().get(0).get("name").asString();
			String anime = r.values().get(0).get("anime").asString();
			String img = r.values().get(0).get("img").asString();
			FigureDTO fig = new FigureDTO(name, anime, img);
			figures.add(fig);
		}
		return figures;
	}

	public FigureDTO findCharacter(String name, String username) {
		List<Record> records = neo4j.getCharacter(name, username);
		if (records.isEmpty()) {
			return null;
		}
		return new FigureDTO(
				records.get(0).values().get(0).get("name").asString(),
				records.get(0).values().get(0).get("anime").asString(),
				records.get(0).values().get(0).get("img").asString()
		);
	}

	public boolean addToTop10(String name, String username) {
		List<Record> records = neo4j.getCharacter(name, username);
		if (records.isEmpty()) {
			return false;
		}
		return true;
	}

	public int AddToTop10(String username, String name_character) {

		// è già nella top10? errore 2
		List<FigureDTO> top10 = getTop10(username);
		for (FigureDTO r : top10) {
			String name = r.getName();
			if(name.equals(name_character))
				return 1;
		}

		//Top10 piena. Errore 10
		if(top10.size() == 10)
			return -1;

		neo4j.AddToTop10(name_character, username);
		return 0;
	}

	public int removeFromTop10(String username, String name_character) {

		List<FigureDTO> top10 = getTop10(username);
		for (FigureDTO r : top10) {
			String name = r.getName();
			if(name.equals(name_character)){
				neo4j.removeFromTop10(name_character, username);
				return 0;
			}
		}
		return 1;
	}

	public void addHasCharacter(String username, List<FigureDTO> list_characters) {
		for (FigureDTO fig : list_characters) {
			if (!neo4j.getCharacter(fig.getName(), username).isEmpty())
				continue;
			neo4j.addHasCharacter(username, fig.getName());
		}
	}


    public List<UserDTO> getSuggestedUsers(String myself) {
		List<UserDTO> users = new ArrayList<>();
		List<Record> records = neo4j.getSuggestedUsersByTop10(myself);
		for (Record r : records) {
			String username = r.get("suggestedUser").asString();
			int CardOwned = r.get("CommonCharacters").asInt();
			if(CardOwned<2)
				continue;
			UserDTO user = new UserDTO();
			user.setUsername(username);
			users.add(user);
		}
		if(users.size()<5){
			records = neo4j.getSuggestedUsersByHas(myself);
			for (Record r : records) {
				if(users.size()==5)
					continue;
				String username = r.get("suggestedUser").asString();
				int CardOwned = r.get("CommonCharacters").asInt();
				if(CardOwned<5)
					continue;
				UserDTO user = new UserDTO();
				user.setUsername(username);
				users.add(user);
			}
		}

		if(users.size()<5){
			records = neo4j.getSuggestedUsersByFollowed(myself);
			for (Record r : records) {
				if(users.size()==5)
					continue;
				String username = r.get("suggestedUser").asString();
				UserDTO user = new UserDTO();
				user.setUsername(username);
				users.add(user);
			}
		}

		return users;

    }

	public List<String> GetSuggestedAnime(String username){
		// TROVO LA LISTA DEGLI ANIME DEGLI AMICI
		List<String> following_list  = neo4j.findFollowingByUsername(username);

		// Lista degli anime che ha recensito l'utente
		List<Record> your_character_list = neo4j.getCharacters(username);
		List<String> anime_reviewedList = new ArrayList<>();
		for (Record your_character : your_character_list) {
			String anime = your_character.values().get(0).get("anime").asString();
			if(!anime_reviewedList.contains(anime)) {
				anime_reviewedList.add(anime);
			}
		}

		//SE NON HA AMICI
		if (following_list.isEmpty()){

			// Ritorna gli anime più visti
			return null;
		}


		//Lista degli anime consigliati non ancora recensiti
		List<String> anime_from_top10FollowingList = new ArrayList<>();
		//Per ogni amico
		for (String following : following_list) {
			//Trovo la top 10
			List<FigureDTO> top_10_following = getTop10(following);
			//per ogni personaggio aggiungo l'anime nella lista da ritornare
			for (FigureDTO fig: top_10_following){
				String anime_name = fig.getAnime();
				if(!anime_from_top10FollowingList.contains(anime_name)) {
					if(!anime_reviewedList.contains(anime_name)) {
						anime_from_top10FollowingList.add(anime_name);
					}
				}
			}
		}

		return anime_from_top10FollowingList;
	}
}
