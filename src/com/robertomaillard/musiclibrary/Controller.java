package com.robertomaillard.musiclibrary;

import com.robertomaillard.musiclibrary.model.Album;
import com.robertomaillard.musiclibrary.model.Artist;
import com.robertomaillard.musiclibrary.model.Datasource;
import com.robertomaillard.musiclibrary.model.Song;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableView;

/**
 * Created by Roberto Maillard.
 * Controller Class.
 */

public class Controller {

    @FXML
    private TableView tableView;

    @FXML
    private ProgressBar progressBar;

    @FXML
    public void listartist() {

        // RUNNING BACKGROUND WORK/TASK AND RETURNS RESULT
        Task<ObservableList<Artist>> task = new GetAllArtistTask();

        // BINDING THE RESULT OF THE TASK TO THE TableView ITEM'S PROPERTY
        // BINDS RUNS THE UI CODE AUTOMATIC ON THE UI THREAD
        tableView.itemsProperty().bind(task.valueProperty());

        // BINDING THE PROGRESS PROPERTY TO THE ProgressBar
        // BINDS RUNS THE UI CODE AUTOMATIC ON THE UI THREAD
        progressBar.progressProperty().bind(task.progressProperty());
        progressBar.setVisible(true);

        // task.setOn RUNS THE UI CODE ON THE UI THREAD WHEN SUCCEEDED/FAILED
        task.setOnSucceeded(e -> progressBar.setVisible(false));
        task.setOnFailed(e -> progressBar.setVisible(false));

        new Thread(task).start();

    }

    @FXML
    public void listAlbumsForArtist() {

        // THE SELECTED ARTIST ITEM
        final Artist artist = (Artist) tableView.getSelectionModel().getSelectedItem();
        if(artist == null) {
            System.out.println("No artist selected");
            return;
        }

        // RUNNING BACKGROUND WORK/TASK AND RETURNS RESULT
        Task<ObservableList<Album>> task = new Task<ObservableList<Album>>() {
            // ANONYMOUS TASK CLASS
            @Override
            protected ObservableList<Album> call() throws Exception {
                return FXCollections.observableArrayList(
                        Datasource.getInstance().queryAlbumsForArtistId(artist.getId()));
            }
        };

        // BINDING THE RESULT OF THE TASK TO THE TableView ITEM'S PROPERTY
        // BINDS RUNS THE UI CODE AUTOMATIC ON THE UI THREAD
        tableView.itemsProperty().bind(task.valueProperty());

        new Thread(task).start();
    }

    @FXML
    public void listSongsForAlbum() {

        // THE SELECTED ALBUM ITEM
        final Album album = (Album) tableView.getSelectionModel().getSelectedItem();
        if(album == null) {
            System.out.println("No album selected");
            return;
        }

        // RUNNING BACKGROUND WORK/TASK AND RETURNS RESULT
        Task<ObservableList<Song>> task = new Task<ObservableList<Song>>() {
            // ANONYMOUS TASK CLASS
            @Override
            protected ObservableList<Song> call() throws Exception {
                return FXCollections.observableArrayList(
                        Datasource.getInstance().querySongsForAlbumId(album.getId()));
            }
        };

        // BINDING THE RESULT OF THE TASK TO THE TableView ITEM'S PROPERTY
        // BINDS RUNS THE UI CODE AUTOMATIC ON THE UI THREAD
        tableView.itemsProperty().bind(task.valueProperty());

        new Thread(task).start();
    }

}

class GetAllArtistTask extends Task {

    // MIGHT NEED TO USE THIS CLASS SEPARATE
    @Override
    public ObservableList<Artist> call() {
        // EXECUTE BACKGROUND WORK/TASK AND RETURNS RESULT
        return FXCollections.observableArrayList(Datasource.getInstance().queryArtist(Datasource.ORDER_BY_ASC));
    }
}
