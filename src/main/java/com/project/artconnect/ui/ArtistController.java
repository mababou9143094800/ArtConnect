package com.project.artconnect.ui;

import com.project.artconnect.model.Artist;
import com.project.artconnect.model.Discipline;
import com.project.artconnect.service.ArtistService;
import com.project.artconnect.util.ServiceProvider;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ArtistController {

    @FXML private TextField searchField;
    @FXML private ComboBox<Discipline> disciplineFilter;
    @FXML private TableView<Artist> artistTable;
    @FXML private TableColumn<Artist, String> nameColumn;
    @FXML private TableColumn<Artist, String> cityColumn;
    @FXML private TableColumn<Artist, String> emailColumn;
    @FXML private TableColumn<Artist, Integer> yearColumn;

    private final ArtistService artistService = ServiceProvider.getArtistService();

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("contactEmail"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("birthYear"));
        disciplineFilter.setItems(FXCollections.observableArrayList(artistService.getAllDisciplines()));
        refreshTable();
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText();
        Discipline d = disciplineFilter.getValue();
        String dName = (d != null) ? d.getName() : null;
        artistTable.setItems(FXCollections.observableArrayList(artistService.searchArtists(query, dName, null)));
    }

    @FXML
    private void handleReset() {
        searchField.clear();
        disciplineFilter.setValue(null);
        refreshTable();
    }

    @FXML
    private void handleAdd() {
        showArtistDialog(null).ifPresent(artist -> {
            try {
                artistService.createArtist(artist);
                refreshTable();
            } catch (Exception e) {
                showError("Error adding artist", e.getMessage());
            }
        });
    }

    @FXML
    private void handleEdit() {
        Artist selected = artistTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("No selection", "Please select an artist to edit.");
            return;
        }
        showArtistDialog(selected).ifPresent(updated -> {
            try {
                artistService.updateArtist(updated);
                refreshTable();
            } catch (Exception e) {
                showError("Error updating artist", e.getMessage());
            }
        });
    }

    @FXML
    private void handleDelete() {
        Artist selected = artistTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("No selection", "Please select an artist to delete.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete artist \"" + selected.getName() + "\"?", ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirm Delete");
        confirm.showAndWait().filter(b -> b == ButtonType.YES).ifPresent(b -> {
            try {
                artistService.deleteArtist(selected.getName());
                refreshTable();
            } catch (Exception e) {
                showError("Error deleting artist", e.getMessage());
            }
        });
    }

    private Optional<Artist> showArtistDialog(Artist existing) {
        boolean isEdit = existing != null;
        Dialog<Artist> dialog = new Dialog<>();
        dialog.setTitle(isEdit ? "Edit Artist" : "Add Artist");

        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField(isEdit ? existing.getName() : "");
        nameField.setEditable(!isEdit);
        TextField bioField = new TextField(isEdit && existing.getBio() != null ? existing.getBio() : "");
        TextField yearField = new TextField(isEdit && existing.getBirthYear() != null ? String.valueOf(existing.getBirthYear()) : "");
        TextField emailField = new TextField(isEdit && existing.getContactEmail() != null ? existing.getContactEmail() : "");
        TextField phoneField = new TextField(isEdit && existing.getPhone() != null ? existing.getPhone() : "");
        TextField cityField = new TextField(isEdit && existing.getCity() != null ? existing.getCity() : "");
        TextField websiteField = new TextField(isEdit && existing.getWebsite() != null ? existing.getWebsite() : "");
        TextField disciplinesField = new TextField(isEdit
                ? existing.getDisciplines().stream().map(Discipline::getName).collect(Collectors.joining(", "))
                : "");

        grid.add(new Label("Name:"), 0, 0);       grid.add(nameField, 1, 0);
        grid.add(new Label("Bio:"), 0, 1);         grid.add(bioField, 1, 1);
        grid.add(new Label("Birth Year:"), 0, 2);  grid.add(yearField, 1, 2);
        grid.add(new Label("Email:"), 0, 3);       grid.add(emailField, 1, 3);
        grid.add(new Label("Phone:"), 0, 4);       grid.add(phoneField, 1, 4);
        grid.add(new Label("City:"), 0, 5);        grid.add(cityField, 1, 5);
        grid.add(new Label("Website:"), 0, 6);     grid.add(websiteField, 1, 6);
        grid.add(new Label("Disciplines\n(comma-separated):"), 0, 7); grid.add(disciplinesField, 1, 7);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setPrefWidth(500);

        dialog.setResultConverter(bt -> {
            if (bt != saveBtn) return null;
            Artist a = isEdit ? existing : new Artist();
            a.setName(nameField.getText().trim());
            a.setBio(bioField.getText().trim());
            String yr = yearField.getText().trim();
            if (!yr.isEmpty()) {
                try { a.setBirthYear(Integer.parseInt(yr)); } catch (NumberFormatException ignored) {}
            }
            a.setContactEmail(emailField.getText().trim());
            a.setPhone(phoneField.getText().trim());
            a.setCity(cityField.getText().trim());
            a.setWebsite(websiteField.getText().trim());
            a.setActive(true);
            String discText = disciplinesField.getText().trim();
            if (!discText.isEmpty()) {
                List<Discipline> disciplines = Arrays.stream(discText.split(","))
                        .map(String::trim).filter(s -> !s.isEmpty())
                        .map(Discipline::new).collect(Collectors.toList());
                a.setDisciplines(disciplines);
            }
            return a;
        });

        return dialog.showAndWait();
    }

    private void refreshTable() {
        artistTable.setItems(FXCollections.observableArrayList(artistService.getAllArtists()));
    }

    private void showWarning(String title, String msg) {
        new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK).showAndWait();
    }

    private void showError(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setTitle(title);
        a.showAndWait();
    }
}
