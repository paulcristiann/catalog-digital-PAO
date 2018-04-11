package Client.controller;

import Client.Main;
import Client.Send;
import Client.interfaces.ModalWindow;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.fxml.LoadException;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import model.*;

import javax.sound.midi.MidiChannel;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CatalogController implements Initializable {

    public javafx.scene.control.Label clasa;
    public javafx.scene.control.Label prof;
    public javafx.scene.control.Label eroare;

    @FXML
    private TableView<Elev> elevi;
    @FXML
    private TableColumn<Elev, String> nume;
    @FXML
    private TableColumn<Elev, String> prenume;

    private ObservableList<Elev> data;


    private Main main;

    private Clasa clasaDeschisa;

    private Login profesorLogat;

    private Elev inEditare;

    public Clasa getClasaDeschisa() {
        return clasaDeschisa;
    }

    public void setClasaDeschisa(Clasa clasaDeschisa) {
        this.clasaDeschisa = clasaDeschisa;
    }

    public Login getProfesorLogat() {
        return profesorLogat;
    }

    public void setProfesorLogat(Login profesorLogat) {
        this.profesorLogat = profesorLogat;
    }

    @FXML
    private void Mouse(MouseEvent event) {
        //mesaj.setText("");
        Elev obj = elevi.getSelectionModel().selectedItemProperty().get();
        if (obj != null) {
            inEditare = obj;
            System.out.println("Selectat: " + obj.getNume());
        }
    }

    public void setApp(Main application) {
        this.main = application;
        clasa.setText(clasaDeschisa.toString() + ", materia: " + clasaDeschisa.getMat().getNume() + " (Semestrul "
                + SemestruController.getSemestrulCurent() + ")");
        prof.setText(profesorLogat.getUser());
        try {
            nume.setMinWidth(100);
            prenume.setMinWidth(100);

            nume.setCellValueFactory(new PropertyValueFactory<Elev, String>("nume"));
            prenume.setCellValueFactory(new PropertyValueFactory<Elev, String>("prenume"));

            Elev e = new Elev();
            e.setClasa(clasaDeschisa);

            e.setSolicitant(profesorLogat);
            e.setActiune(Elev.Actiuni.read);

            data = FXCollections.observableArrayList((List<Elev>) new Send().send(e));
            elevi.setItems(data);
        }catch (Exception e){
            System.out.println(e);
        }
    }

    public void logout() {
        main.userLogout();
    }


    public void noteaza() {

        if(inEditare != null) {
            inEditare.setMat(clasaDeschisa.getMat());
            inEditare.setSolicitant(profesorLogat);
            inEditare.setClasa(clasaDeschisa);
            inEditare.setCpm(clasaDeschisa.getCpm());
            new Client.ModalWindow("/Client/view/Noteaza.fxml", inEditare);

        }else{
            eroare.setText("Selectati un elev din catalog!");
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
