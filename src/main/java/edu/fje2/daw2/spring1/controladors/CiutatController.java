package edu.fje2.daw2.spring1.controladors;


import edu.fje2.daw2.spring1.model.Ciutat;

import edu.fje2.daw2.spring1.model.Usuari;
import edu.fje2.daw2.spring1.repositoris.CiutatRepository;
import edu.fje2.daw2.spring1.repositoris.UsuariRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/ciutats")
public class CiutatController {

    @Autowired
    private CiutatService ciutatService;

    @Autowired
    private CiutatRepository ciutatRepository;

    @Autowired
    private UsuariRepository usuariRepository;

    /**
     * Retorna a la vista previsio un array de ciutats i un array dels dies
     * @param model per pasar dades a la vista
     * @return view previsio.html
     */
    @GetMapping("/previsio")
    public String previsioCiutats(Model model) {
        // Actualitza les previsions de les ciutats (Fetch + guardar a mongodb)
        ciutatService.actualitzarPrevisioCiutats();

        // Obtenir l'usuari que ha iniciat sessió
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString()
                .substring(authentication.getPrincipal().toString().indexOf("username=") + 9);

        // Obtenir les ciutats de l'usuari
        List<Ciutat> llistaCiutats = new ArrayList<>();
        Usuari usuari = usuariRepository.findByOauthID(username);
        List<String> ciutatsUsuari = usuari.getCiutats();

        // Afegir les ciutats existents al array llistaCiutats
        ciutatRepository.findAll().forEach(ciutat -> {
            if (ciutatsUsuari.contains(ciutat.getNom())) {
                llistaCiutats.add(ciutat);
            }
        });

        System.out.println(llistaCiutats);
        // Array amb les previsions de cada ciutat (3 dies per ciutat)
        List<List<String>> llistaPrevisions = new ArrayList<>();
        for (Ciutat ciutat : llistaCiutats) {
            try {
                // crida al metode getPrevisionsFromJSON per obtenir previsions de cada ciutat
                List<String> previsions = ciutat.getPrevisionsFromJSON();
                llistaPrevisions.add(previsions);
            } catch (JSONException e) {
                llistaPrevisions.add(new ArrayList<>());
                e.printStackTrace();
            }
        }
        System.out.println(llistaPrevisions);

        // Afegir els arrays a la vista
        model.addAttribute("ciutats", llistaCiutats);
        model.addAttribute("previsions", llistaPrevisions);
        return "previsio";
    }
}