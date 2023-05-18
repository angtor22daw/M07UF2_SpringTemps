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
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/ciutats")
public class CiutatController {
    // Classe que fa la petició a la API
    @Autowired
    private CiutatService ciutatService;
    // Repositori de ciutats
    @Autowired
    private CiutatRepository ciutatRepository;
    // Repositori d'usuaris
    @Autowired
    private UsuariRepository usuariRepository;
    /**
     * Mètode que crea una nova ciutat a mongodb
     * @param nom String amb el nom de la ciutat
     * @param consultaFetch String que conté la consulta a la API
     * @return vista llistaDeCiutats
     */
    @PostMapping("/crearCiutat")
    public String crearCiutat(@RequestParam("nom") String nom, @RequestParam("consultaFetch") String consultaFetch) {
        Ciutat novaCiutat = new Ciutat(nom, consultaFetch,null);
        ciutatRepository.save(novaCiutat);

        return new ModelAndView("redirect:/ciutats/llistaDeCiutats").getViewName();
    }
    /**
     * Mètode que carrega la vista llistaDeCiutats i passem array de ciutats
     * @return vista llistaDeCiutats
     */
    @GetMapping("/llistaDeCiutats")
    public String llistatDeCiutats(Model model) {
        List<Ciutat> ciutats = ciutatRepository.findAll();
        model.addAttribute("ciutats", ciutats);
        return "llistaDeCiutats";
    }
    /**
     * Mètode que modifica el camp consultaFetch de la ciutat que ha rebut per paràmetre
     * @nom String amb el nom de la ciutat
     * @param novaConsultaFetch String que conté la nova consulta a la API
     * @return vista llistaDeCiutats
     */
    @PostMapping("/modificarCiutat")
    public String modificarCiutat(@RequestParam("nom") String nom,@RequestParam("consultaFetch") String novaConsultaFetch) {
        Ciutat ciutat = ciutatRepository.findByNom(nom);

        if (ciutat != null) {
            ciutat.setConsultaFetch(novaConsultaFetch);
            ciutatRepository.save(ciutat);
        }

        return new ModelAndView("redirect:/ciutats/llistaDeCiutats").getViewName();
    }
    /**
     * Mètode que elimina la ciutat de mongodb
     * @param nom String amb el nom de la ciutat
     * @return vista llistaDeCiutats
     */
    @PostMapping("/eliminarCiutat")
    public String eliminarCiutat(@RequestParam("nom") String nom) {
        Ciutat ciutat = ciutatRepository.findByNom(nom);
        if (ciutat != null) {
            ciutatRepository.delete(ciutat);
        }
        return new ModelAndView("redirect:/ciutats/llistaDeCiutats").getViewName();
    }

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