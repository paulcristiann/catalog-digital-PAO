package Server;

import javafx.util.Pair;
import org.apache.commons.dbutils.QueryRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import static Server.db.getCon;

@Controller
public class ParintiWebController {

    @GetMapping("/parinti" )
    public String parinti(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Connection con = getCon();
        QueryRunner run = new QueryRunner();

        try {
            List<Elev> elevi = new ArrayList<Elev>();
            String sql = "SELECT id,nume,prenume from elevi WHERE id_parinte=(SELECT id from parinti WHERE email=? LIMIT 1)";
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, authentication.getName());
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Elev e = new Elev();
                e.setId(rs.getInt("id" ));
                e.setNume(rs.getString("nume" ) + " " + rs.getString("prenume" ));
                elevi.add(e);
            }
            for (Elev elev : elevi) {

                sql = "SELECT " +
                        "  n.note, materii.nume, medii.nota medie FROM " +
                        "  ( " +
                        "  SELECT " +
                        "    note.id_clasa_profesor_materie id, " +
                        "    note.id_elev id_elev, " +
                        "    note.semestru semestru, GROUP_CONCAT( " +
                        "      CONCAT( " +
                        "        IF( " +
                        "          note.nota = -1, " +
                        "          \"Absenta nemotivata\", " +
                        "          IF( " +
                        "            note.nota = 0, " +
                        "            \"Absenta motivata\", " +
                        "            note.nota " +
                        "          ) " +
                        "        ), " +
                        "        \" \", " +
                        "        note.data " +
                        "      ) SEPARATOR '<br>' " +
                        "    ) AS note " +
                        "  FROM " +
                        "    `note` " +
                        "  WHERE " +
                        "    note.id_elev = ? AND note.semestru = ? " +
                        "  GROUP BY " +
                        "    note.id_clasa_profesor_materie " +
                        ") AS `n` " +
                        "JOIN " +
                        "  clasa_profesor_materie ON( " +
                        "    n.id = clasa_profesor_materie.id " +
                        "  ) " +
                        "JOIN " +
                        "  materii ON( " +
                        "    clasa_profesor_materie.id_materie = materii.id " +
                        "  ) " +
                        "LEFT JOIN " +
                        "  medii ON( " +
                        "    medii.elevi_id = n.id_elev AND medii.semestru = n.semestru AND medii.clasa_profesor_materie_id=n.id " +
                        "  )";
                /** semstrulI **/
                preparedStatement = con.prepareStatement(sql);
                preparedStatement.setInt(1, elev.getId());
                preparedStatement.setInt(2, 1);
                rs = preparedStatement.executeQuery();
                List<Pair<String, String>> sem1 = new ArrayList<Pair<String, String>>();
                while (rs.next())
                    sem1.add(new Pair(new Pair(rs.getString("nume" ), rs.getString("note" )), rs.getString("medie" )));
                elev.setSemestrul1(sem1);

                /** semestrul II **/
                preparedStatement = con.prepareStatement(sql);
                preparedStatement.setInt(1, elev.getId());
                preparedStatement.setInt(2, 2);
                rs = preparedStatement.executeQuery();
                List<Pair<String, String>> sem2 = new ArrayList<Pair<String, String>>();
                while (rs.next())
                    sem2.add(new Pair(new Pair(rs.getString("nume" ), rs.getString("note" )), rs.getString("medie" )));
                elev.setSemestrul2(sem2);
            }

            model.addAttribute("elevi", elevi);
        } catch (Exception e) {
            System.out.println(e);
        }


        return "parinti";
    }

    class Elev {
        public String nume;
        int id;

        List<Pair<String, String>> semestrul1;
        List<Pair<String, String>> semestrul2;

        public Elev() {

        }

        public List<Pair<String, String>> getSemestrul1() {
            return semestrul1;
        }

        public void setSemestrul1(List<Pair<String, String>> semestrul1) {
            this.semestrul1 = semestrul1;
        }

        public List<Pair<String, String>> getSemestrul2() {
            return semestrul2;
        }

        public void setSemestrul2(List<Pair<String, String>> semestrul2) {
            this.semestrul2 = semestrul2;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getNume() {
            return nume;
        }

        public void setNume(String nume) {
            this.nume = nume;
        }
    }
}

