package com.robertomaillard.musiclibrary.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Roberto Maillard.
 * Datasource class for accessing the music.db database.
 */

public class Datasource {


    private static final String DB_NAME = "music.db";

    private static final String CONNECTION_STRING = "jdbc:sqlite:" + DB_NAME;

    private static final String TABLE_ARTISTS = "artists";
    private static final String COLUMN_ARTISTS_ID = "_id";
    private static final String COLUMN_ARTISTS_NAME = "name";
    private static final int INDEX_ARTISTS_ID = 1;
    private static final int INDEX_ARTISTS_NAME = 2;

    private static final String TABLE_ALBUMS = "albums";
    private static final String COLUMN_ALBUMS_ID = "_id";
    private static final String COLUMN_ALBUMS_NAME = "name";
    private static final String COLUMN_ALBUMS_ARTIST = "artist";
    private static final int INDEX_ALBUMS_ID = 1;
    private static final int INDEX_ALBUMS_NAME = 2;
    private static final int INDEX_ALBUMS_ARTIST =3;


    private static final String TABLE_SONGS = "songs";
    private static final String COLUMN_SONGS_ID = "_id";
    private static final String COLUMNS_SONGS_TRACK = "track";
    private static final String COLUMNS_SONGS_TITLE = "title";
    private static final String COLUMNS_SONGS_ALBUM = "album";
    private static final int INDEX_SONG_ID = 1;
    private static final int INDEX_SONGS_TRACK = 2;
    private static final int INDEX_SONGS_TITLE = 3;
    private static final int INDEX_SONGS_ALBUM = 4;

    public static final int ORDER_BY_NONE = 1;
    public static final int ORDER_BY_ASC = 2;
    public static final int ORDER_BY_DESC = 3;

    private static final String QUERY_ALL_BY_ARTIST_START =
            "SELECT * FROM " + TABLE_ARTISTS;
    private static final String QUERY_ALL_BY_ARTIST_SORT =
            " ORDER BY " + COLUMN_ARTISTS_NAME + " COLLATE NOCASE ";


    private static final String TABLE_ARTIST_SONG_VIEW = "artist_list";


    private static final String QUERY_VIEW_SONG_INFO_PREP =
            "SELECT " + COLUMN_ARTISTS_NAME +
                    ", " + COLUMNS_SONGS_ALBUM +
                    ", " + COLUMNS_SONGS_TRACK +
                    " FROM " + TABLE_ARTIST_SONG_VIEW +
                    " WHERE " + COLUMNS_SONGS_TITLE + " = ?";


    private static final String QUERY_ARTIST =
            "SELECT " + COLUMN_ARTISTS_ID + " FROM " + TABLE_ARTISTS +
                    " WHERE " + COLUMN_ARTISTS_NAME + " = ?";


    private static final String QUERY_ALBUMS_BY_ARTIST_ID =
            "SELECT * FROM " + TABLE_ALBUMS +
                    " WHERE " + COLUMN_ALBUMS_ARTIST +
                    " = ? ORDER BY " + COLUMN_ALBUMS_NAME + " COLLATE NOCASE";

    private static final String QUERY_SONGS_BY_ALBUM_ID =
            "SELECT * FROM " + TABLE_SONGS +
                    " WHERE " + COLUMNS_SONGS_ALBUM +
                    " = ? ORDER BY " + COLUMNS_SONGS_TRACK + " COLLATE NOCASE";


    private Connection connection;

    private PreparedStatement querySongInfoView;

    private PreparedStatement queryArtist;

    private PreparedStatement queryAlbumsByArtistId;
    private PreparedStatement querySongsByAlbumId;

    private static Datasource instance = new Datasource();

    private Datasource() {
    }

    public static Datasource getInstance() {
        return instance;
    }

//    OPENS CONNECTION RESOURCE TO THE DATABASE
    public boolean open() {
        try {
            connection = DriverManager.getConnection(CONNECTION_STRING);

            // PREPARING STATEMENTS
            querySongInfoView = connection.prepareStatement(QUERY_VIEW_SONG_INFO_PREP);

            queryArtist = connection.prepareStatement(QUERY_ARTIST);

            queryAlbumsByArtistId = connection.prepareStatement(QUERY_ALBUMS_BY_ARTIST_ID);
            querySongsByAlbumId = connection.prepareStatement(QUERY_SONGS_BY_ALBUM_ID);

            return true;

        } catch (SQLException e) {
            System.out.println("Couldn't connect to database: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

//    CLOSES CONNECTION RESOURCE TO THE DATABASE
    public void close() {
        try {

            if(querySongInfoView != null) {
                querySongInfoView.close();
            }
            if(queryArtist != null) {
                queryArtist.close();
            }
            if(queryAlbumsByArtistId != null) {
                queryAlbumsByArtistId.close();
            }
            if(querySongsByAlbumId != null) {
                querySongsByAlbumId.close();
            }
            if(connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            System.out.println("Couldn't close connection: " + e.getMessage());
        }
    }
//    CALLED AT START AND BY THE FXML EVENT HANDLER. BUTTON IN GUI
//    RETURN AN ARRAYLIST POPULATED WITH ARTIST OBJECTS
    public List<Artist> queryArtist(int sortOrder) {

        StringBuilder sb = new StringBuilder(QUERY_ALL_BY_ARTIST_START);
        if(sortOrder != ORDER_BY_NONE) {
            sb.append(QUERY_ALL_BY_ARTIST_SORT);
            if(sortOrder == ORDER_BY_DESC) {
                sb.append("DESC");
            } else {
                sb.append("ASC");
            }
        }

        // CODE IN TRY BLOCK STATEMENT, CLOSES BOTH RESULTSET AND STATEMENT RESOURCES AFTER, AUTOMATICALLY
        try(Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sb.toString())) {

            // LOOP THROUGH THE QUERY RESULTSET AND POPULATED THE ARRAYLIST WITH ARTIST OBJECTS
            List<Artist> artists = new ArrayList<>();
            while(resultSet.next()) {
                Artist artist = new Artist();
                artist.setId(resultSet.getInt(INDEX_ARTISTS_ID));
                artist.setName(resultSet.getString(INDEX_ARTISTS_NAME));
                artists.add(artist);
            }

            return artists;

        } catch (SQLException e) {
            System.out.println("Query failed: " + e.getMessage());
            return null;
        }
    }

//    CALLED BY THE FXML EVENT HANDLER. BUTTON IN GUI
    public List<Album> queryAlbumsForArtistId(int id) {

        try {
            queryAlbumsByArtistId.setInt(1, id);
            ResultSet resultSet = queryAlbumsByArtistId.executeQuery();

            List<Album> albums = new ArrayList<>();
            while(resultSet.next()) {
                Album album = new Album();
                album.setId(resultSet.getInt(INDEX_ALBUMS_ID));
                album.setName(resultSet.getString(INDEX_ALBUMS_NAME));
                album.setArtistId(resultSet.getInt(INDEX_ALBUMS_ARTIST));

                albums.add(album);
            }
            return albums;

        } catch (SQLException e) {
            System.out.println("queryAlbumForArtistId failed: " + e.getMessage());
            return null;
        }
    }

//    CALLED BY THE FXML EVENT HANDLER. BUTTON IN GUI
    public List<Song> querySongsForAlbumId(int id) {

        try {
            querySongsByAlbumId.setInt(1, id);
            ResultSet resultSet = querySongsByAlbumId.executeQuery();

            List<Song> songs = new ArrayList<>();
            while(resultSet.next()) {
                Song song = new Song();
                song.setId(resultSet.getInt(INDEX_SONG_ID));
                song.setTrack(resultSet.getInt(INDEX_SONGS_TRACK));
                song.setName(resultSet.getString(INDEX_SONGS_TITLE));
                song.setAlbumId(resultSet.getInt(INDEX_SONGS_ALBUM));

                songs.add(song);
            }
            return songs;

        } catch (SQLException e) {
            System.out.println("querySongsForAlbumId failed: " + e.getMessage());
            return null;
        }
    }
}
