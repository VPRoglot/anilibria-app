package ru.radiationx.anilibria.data.api.releases;

import java.util.ArrayList;

/**
 * Created by radiationx on 31.10.17.
 */

public class ReleaseItem {
    private int id;
    private String title;
    private String originalTitle;
    private String torrentLink;
    private String link;
    private String image;
    private String episodesCount;
    private String description;
    private ArrayList<String> seasons = new ArrayList<>();
    private ArrayList<String> voices = new ArrayList<>();
    private ArrayList<String> genres = new ArrayList<>();
    private ArrayList<String> types = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getTorrentLink() {
        return torrentLink;
    }

    public void setTorrentLink(String torrentLink) {
        this.torrentLink = torrentLink;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getEpisodesCount() {
        return episodesCount;
    }

    public void setEpisodesCount(String episodesCount) {
        this.episodesCount = episodesCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getSeasons() {
        return seasons;
    }

    public ArrayList<String> getVoices() {
        return voices;
    }

    public ArrayList<String> getGenres() {
        return genres;
    }

    public ArrayList<String> getTypes() {
        return types;
    }

    public void addSeason(String data) {
        seasons.add(data);
    }

    public void addVoice(String data) {
        voices.add(data);
    }

    public void addGenre(String data) {
        genres.add(data);
    }

    public void addType(String data) {
        types.add(data);
    }
}
