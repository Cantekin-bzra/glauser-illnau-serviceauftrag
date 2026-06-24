package ch.glauserillnau.serviceauftrag.controller;

import ch.glauserillnau.serviceauftrag.dto.RapportDTO;
import ch.glauserillnau.serviceauftrag.service.AuftragService;
import ch.glauserillnau.serviceauftrag.service.RapportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auftraege/{auftragId}/rapport")
@RequiredArgsConstructor
public class RapportController {

    private final RapportService rapportService;
    private final AuftragService auftragService;

    // GET /auftraege/{id}/rapport — Rapportformular
    @GetMapping
    public String formular(@PathVariable Integer auftragId, Model model) {
        model.addAttribute("auftrag", auftragService.findById(auftragId));
        model.addAttribute("rapportDTO", new RapportDTO());
        return "auftrag/rapport";
    }

    // POST /auftraege/{id}/rapport — Rapport speichern
    @PostMapping
    public String speichern(@PathVariable Integer auftragId,
                            @Valid @ModelAttribute RapportDTO rapportDTO,
                            BindingResult result, Model model,
                            RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("auftrag", auftragService.findById(auftragId));
            return "auftrag/rapport";
        }
        // TODO: MA-ID aus Session/SecurityContext — vorerst hart auf 4
        rapportService.erstellen(auftragId, 4, rapportDTO);
        redirect.addFlashAttribute("success", "Rapport für Auftrag #" + auftragId + " gespeichert.");
        return "redirect:/auftraege/" + auftragId;
    }

    // POST /auftraege/{id}/rapport/{rapportId}/freigeben
    @PostMapping("/{rapportId}/freigeben")
    public String freigeben(@PathVariable Integer auftragId,
                            @PathVariable Integer rapportId,
                            RedirectAttributes redirect) {
        // TODO: BL-ID aus Session/SecurityContext — vorerst hart auf 2
        rapportService.freigeben(rapportId, 2);
        redirect.addFlashAttribute("success", "Rapport freigegeben.");
        return "redirect:/auftraege/" + auftragId;
    }
}
