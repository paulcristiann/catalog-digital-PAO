package Client.controller;

import Client.Main;
import Client.ModalWindow;
import Client.Send;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import model.*;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import static Server.db.getCon;

public class AlegeClasaController implements Initializable {

    @FXML
    private Label titlu;

    @FXML
    private Label eroare;

    @FXML
    ComboBox<Clasa> ClaseDisponibile;

    @FXML
    Button Deschide;

    private Main main;

    private Login user;

    public Login getUser() {
        return user;
    }

    public void setUser(Login user) {
        this.user = user;
    }

    public void setApp(Main application) {

        this.main = application;
        ObservableList ClaseOptiuni;

        Clasa c = new Clasa();
        c.setActiune(Clasa.Actiuni.read);

        c.setSolicitant(user.getUser());

        ClaseOptiuni = FXCollections.observableArrayList((ArrayList<Clasa>) new Send().send(c));

        ClaseDisponibile.setItems(ClaseOptiuni);

        /** Lista elevi cu mai mult de 10 absente */
        Preaviz p = new Preaviz();
        p.setDiriginte(user.getId());
        List<Preaviz> absente = (List<Preaviz>) new Send().send(p);
        if (absente.size() > 0) {
            new ModalWindow("/Client/view/Preaviz.fxml", absente);
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {


    }

    public void deschideCatalog(ActionEvent event) throws IOException {
        if (main != null) {

            if(ClaseDisponibile.getSelectionModel().getSelectedIndex() > -1) {

                Clasa clasaSelectata = ClaseDisponibile.getSelectionModel().getSelectedItem();

                System.out.println(clasaSelectata.getMat().getNume());

                main.openCatalog(user, clasaSelectata);
            }
            else {

                eroare.setText("Va rugam selectati o clasa");

            }

        }
    }

}
