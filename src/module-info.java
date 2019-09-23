module MusicLibrary {
    requires javafx.fxml;
    requires javafx.controls;
    requires java.sql;

    opens com.robertomaillard.musiclibrary;
    opens com.robertomaillard.musiclibrary.model;
}