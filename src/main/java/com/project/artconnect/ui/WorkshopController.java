package com.project.artconnect.ui;

import com.project.artconnect.model.CommunityMember;
import com.project.artconnect.model.Workshop;
import com.project.artconnect.service.CommunityService;
import com.project.artconnect.service.WorkshopService;
import com.project.artconnect.util.ServiceProvider;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDateTime;

public class WorkshopController {

    @FXML private TableView<Workshop> workshopTable;
    @FXML private TableColumn<Workshop, String> titleColumn;
    @FXML private TableColumn<Workshop, LocalDateTime> dateColumn;
    @FXML private TableColumn<Workshop, String> instructorColumn;
    @FXML private TableColumn<Workshop, Double> priceColumn;
    @FXML private TableColumn<Workshop, String> levelColumn;

    private final WorkshopService workshopService = ServiceProvider.getWorkshopService();
    private final CommunityService communityService = ServiceProvider.getCommunityService();

    @FXML
    public void initialize() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        levelColumn.setCellValueFactory(new PropertyValueFactory<>("level"));
        instructorColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getInstructor() != null
                        ? cellData.getValue().getInstructor().getName() : "Unknown"));
        refreshTable();
    }

    @FXML
    private void handleBook() {
        Workshop selected = workshopTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a workshop to book.", ButtonType.OK).showAndWait();
            return;
        }

        ChoiceDialog<CommunityMember> dialog = new ChoiceDialog<>(null,
                communityService.getAllMembers());
        dialog.setTitle("Book Workshop");
        dialog.setHeaderText("Workshop: " + selected.getTitle());
        dialog.setContentText("Select member:");

        dialog.showAndWait().ifPresent(member -> {
            try {
                workshopService.bookWorkshop(selected, member);
                new Alert(Alert.AlertType.INFORMATION,
                        "Booking confirmed for " + member.getName() + "!", ButtonType.OK).showAndWait();
                refreshTable();
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "Booking failed: " + e.getMessage(), ButtonType.OK).showAndWait();
            }
        });
    }

    private void refreshTable() {
        workshopTable.setItems(FXCollections.observableArrayList(workshopService.getAllWorkshops()));
    }
}
