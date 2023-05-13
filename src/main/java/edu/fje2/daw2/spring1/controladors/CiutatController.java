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

    @GetMapping
    public List<Ciutat> llistarCiutats() {
        return ciutatRepository.findAll();
    }

    @GetMapping("/actualitzar")
    public ResponseEntity<Void> actualitzarPrevisions() {
        ciutatService.actualitzarPrevisioCiutats();
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/previsio")
    public String previsioCiutats(Model model) {
        ciutatService.actualitzarPrevisioCiutats();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString()
                .substring(authentication.getPrincipal().toString().indexOf("username=") + 9);

        List<Ciutat> llistaCiutats = new ArrayList<>();
        Usuari usuari = usuariRepository.findByOauthID(username);
        List<String> ciutatsUsuari = usuari.getCiutats();

        ciutatRepository.findAll().forEach(ciutat -> {
            if (ciutatsUsuari.contains(ciutat.getNom())) {
                llistaCiutats.add(ciutat);
            }
        });

        System.out.println(llistaCiutats);
        System.out.println(usuariRepository.findByOauthID(username).getCiutats());

        List<List<String>> llistaPrevisions = new ArrayList<>();
        for (Ciutat ciutat : llistaCiutats) {
            try {
                List<String> previsions = ciutat.getPrevisionsFromJSON();
                llistaPrevisions.add(previsions);
            } catch (JSONException e) {
                llistaPrevisions.add(new ArrayList<>());
                e.printStackTrace();
            }
        }

        model.addAttribute("ciutats", llistaCiutats);
        model.addAttribute("previsions", llistaPrevisions);
        return "previsio";
    }

    /*
    @RequestMapping(value={"/jugar", "/aaa"})
    String mostrarpepe() {
        return("previsio");
    }*/
}

