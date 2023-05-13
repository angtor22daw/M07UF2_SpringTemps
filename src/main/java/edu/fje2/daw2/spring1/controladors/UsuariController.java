package edu.fje2.daw2.spring1.controladors;

import edu.fje2.daw2.spring1.model.Usuari;
import edu.fje2.daw2.spring1.repositoris.UsuariRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;


@Controller
public class UsuariController {

    @Autowired
    private UsuariRepository repositori;

    @PostMapping("/guardarCiutats")
    public String guardarCiutats(@RequestParam("ciutatsSeleccionades") List<String> ciutatsSeleccionades, HttpSession session) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getPrincipal().toString()
                .substring(authentication.getPrincipal().toString().indexOf("username=") + 9);
        System.out.println("username: " + username);
        System.out.println("Ciutats seleccionades: " + ciutatsSeleccionades);

        Usuari usuari = new Usuari(username,ciutatsSeleccionades);

        repositori.save(usuari);

        return "previsio";
    }
}
