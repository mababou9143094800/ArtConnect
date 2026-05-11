package com.project.artconnect.ui;

import com.project.artconnect.model.Artist;
import com.project.artconnect.model.Artwork;
import com.project.artconnect.service.ArtistService;
import com.project.artconnect.service.ArtworkService;
import com.project.artconnect.util.ServiceProvider;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.util.Optional;

public class ArtworkController {

    @FXML private TableView<Artwork> artworkTable;
    @FXML private TableColumn<Artwork, String> titleColumn;
    @FXML private TableColumn<Artwork, String> typeColumn;
    @FXML private TableColumn<Artwork, Double> priceColumn;
    @FXML private TableColumn<Artwork, String> statusColumn;
    @FXML private TableColumn<Artwork, String> artistColumn;

    private final ArtworkService artworkService = ServiceProvider.getArtworkService();
    private final ArtistService artistService = ServiceProvider.getArtistService();

    @FXML
    public void initialize() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        artistColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getArtist() != null ? cellData.getValue().getArtist().getName() : "Unknown"));
        refreshTable();
    }

    @FXML
    private void handleAdd() {
        showArtworkDialog(null).ifPresent(artwork -> {
            try {
                artworkService.createArtwork(artwork);
                refreshTable();
            } catch (Exception e) {
                showError("Error adding artwork", e.getMessage());
            }
        });
    }

    @FXML
    private void handleEdit() {
        Artwork selected = artworkTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Please select an artwork to edit.");
            return;
        }
        showArtworkDialog(selected).ifPresent(updated -> {
            try {
                artworkService.updateArtwork(updated);
                refreshTable();
            } catch (Exception e) {
                showError("Error updating artwork", e.getMessage());
            }
        });
    }

    @FXML
    private void handleDelete() {
        Artwork selected = artworkTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Please select an artwork to delete.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete artwork \"" + selected.getTitle() + "\"?", ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirm Delete");
        confirm.showAndWait().filter(b -> b == ButtonType.YES).ifPresent(b -> {
            try {
                artworkService.deleteArtwork(selected.getTitle());
                refreshTable();
            } catch (Exception e) {
                showError("Error deleting artwork", e.getMessage());
            }
        });
    }

    private Optional<Artwork> showArtworkDialog(Artwork existing) {
        boolean isEdit = existing != null;
        Dialog<Artwork> dialog = new Dialog<>();
        dialog.setTitle(isEdit ? "Edit Artwork" : "Add Artwork");

        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField titleField = new TextField(isEdit ? existing.getTitle() : "");
        titleField.setEditable(!isEdit);

        ComboBox<Artist> artistCombo = new ComboBox<>(FXCollections.observableArrayList(artistService.getAllArtists()));
        if (isEdit && existing.getArtist() != null) {
            artistService.getArtistByName(existing.getArtist().getName()).ifPresent(artistCombo::setValue);
        }

        TextField typeField = new TextField(isEdit && existing.getType() != null ? existing.getType() : "");
        TextField mediumField = new TextField(isEdit && existing.getMedium() != null ? existing.getMedium() : "");
        TextField yearField = new TextField(isEdit && existing.getCreationYear() != null ? String.valueOf(existing.getCreationYear()) : "");
        TextField priceField = new TextField(isEdit ? String.valueOf(existing.getPrice()) : "0");
        TextField descField = new TextField(isEdit && existing.getDescription() != null ? existing.getDescription() : "");

        ComboBox<Artwork.Status> statusCombo = new ComboBox<>(
                FXCollections.observableArrayList(Artwork.Status.values()));
        statusCombo.setValue(isEdit && existing.getStatus() != null ? existing.getStatus() : Artwork.Status.FOR_SALE);

        grid.add(new Label("Title:"), 0, 0);         grid.add(titleField, 1, 0);
        grid.add(new Label("Artist:"), 0, 1);        grid.add(artistCombo, 1, 1);
        grid.add(new Label("Type:"), 0, 2);          grid.add(typeField, 1, 2);
        grid.add(new Label("Medium:"), 0, 3);        grid.add(mediumField, 1, 3);
        grid.add(new Label("Creation Year:"), 0, 4); grid.add(yearField, 1, 4);
        grid.add(new Label("Price (€):"), 0, 5);     grid.add(priceField, 1, 5);
        grid.add(new Label("Status:"), 0, 6);        grid.add(statusCombo, 1, 6);
        grid.add(new Label("Description:"), 0, 7);   grid.add(descField, 1, 7);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setPrefWidth(500);

        dialog.setResultConverter(bt -> {
            if (bt != saveBtn) return null;
            Artwork aw = isEdit ? existing : new Artwork();
            aw.setTitle(titleField.getText().trim());
            aw.setArtist(artistCombo.getValue());
            aw.setType(typeField.getText().trim());
            aw.setMedium(mediumField.getText().trim());
            String yr = yearField.getText().trim();
            if (!yr.isEmpty()) {
                try { aw.setCreationYear(Integer.parseInt(yr)); } catch (NumberFormatException ignored) {}
            }
            try { aw.setPrice(Double.parseDouble(priceField.getText().trim())); } catch (NumberFormatException ignored) {}
            aw.setStatus(statusCombo.getValue());
            aw.setDescription(descField.getText().trim());
            return aw;
        });

        return dialog.showAndWait();
    }

    private void refreshTable() {
        artworkTable.setItems(FXCollections.observableArrayList(artworkService.getAllArtworks()));
    }

    private void showWarning(String msg) {
        new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK).showAndWait();
    }

    private void showError(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setTitle(title);
        a.showAndWait();
    }
}
