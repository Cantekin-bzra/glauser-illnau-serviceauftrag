package ch.glauserillnau.serviceauftrag.controller;

import ch.glauserillnau.serviceauftrag.dto.AuftragDTO;
import ch.glauserillnau.serviceauftrag.dto.DispositionDTO;
import ch.glauserillnau.serviceauftrag.entity.Auftrag;
import ch.glauserillnau.serviceauftrag.enums.AuftragStatus;
import ch.glauserillnau.serviceauftrag.service.AuftragService;
import ch.glauserillnau.serviceauftrag.service.RapportService;
import ch.glauserillnau.serviceauftrag.service.RechnungService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auftraege")
@RequiredArgsConstructor
public class AuftragController {

    private final AuftragService auftragService;
    private final RapportService rapportService;
    private final RechnungService rechnungService;

    // GET /auftraege — Liste aller Aufträge, optional nach Status
    @GetMapping
    public String liste(@RequestParam(required = false) AuftragStatus status, Model model) {
        model.addAttribute("auftraege",
                status != null ? auftragService.findByStatus(status) : auftragService.findAll());
        model.addAttribute("statusFilter", status);
        model.addAttribute("alleStatus", AuftragStatus.values());
        return "auftrag/liste";
    }

    // GET /auftraege/neu — Erfassungsformular
    @GetMapping("/neu")
    public String neuFormular(Model model) {
        model.addAttribute("auftragDTO", new AuftragDTO());
        model.addAttribute("kunden", auftragService.findAlleKunden());
        model.addAttribute("arbeitstypen", auftragService.findAlleArbeitstypen());
        return "auftrag/erfassen";
    }

    // POST /auftraege — Neuen Auftrag speichern
    @PostMapping
    public String speichern(@Valid @ModelAttribute AuftragDTO auftragDTO,
                            BindingResult result, Model model,
                            RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("kunden", auftragService.findAlleKunden());
            model.addAttribute("arbeitstypen", auftragService.findAlleArbeitstypen());
            return "auftrag/erfassen";
        }
        Auftrag auftrag = auftragService.erfassen(auftragDTO);
        redirect.addFlashAttribute("success", "Auftrag #" + auftrag.getAuftragId() + " erfasst.");
        return "redirect:/auftraege";
    }

    // GET /auftraege/{id} — Detailansicht
    @GetMapping("/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        Auftrag auftrag = auftragService.findById(id);
        model.addAttribute("auftrag", auftrag);
        model.addAttribute("rapporte", rapportService.findByAuftragId(id));
        model.addAttribute("rechnung", rechnungService.findByAuftragId(id).orElse(null));
        return "auftrag/detail";
    }

    // GET /auftraege/{id}/disponieren — Dispositionsformular
    @GetMapping("/{id}/disponieren")
    public String dispositionFormular(@PathVariable Integer id, Model model) {
        model.addAttribute("auftrag", auftragService.findById(id));
        model.addAttribute("dispositionDTO", new DispositionDTO());
        model.addAttribute("mitarbeiter", auftragService.findAlleMitarbeiter());
        return "auftrag/disponieren";
    }

    // POST /auftraege/{id}/disponieren — Disposition speichern
    @PostMapping("/{id}/disponieren")
    public String disponieren(@PathVariable Integer id,
                              @Valid @ModelAttribute DispositionDTO dto,
                              BindingResult result, Model model,
                              RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("auftrag", auftragService.findById(id));
            model.addAttribute("mitarbeiter", auftragService.findAlleMitarbeiter());
            return "auftrag/disponieren";
        }
        // TODO: BL-ID aus Session/SecurityContext — vorerst hart auf 2
        auftragService.disponieren(id, dto, 2);
        redirect.addFlashAttribute("success", "Auftrag #" + id + " disponiert.");
        return "redirect:/auftraege";
    }

    // POST /auftraege/{id}/ausgefuehrt — Als ausgeführt markieren
    @PostMapping("/{id}/ausgefuehrt")
    public String ausgefuehrt(@PathVariable Integer id, RedirectAttributes redirect) {
        auftragService.alsAusgefuehrtMarkieren(id);
        redirect.addFlashAttribute("success", "Auftrag #" + id + " als ausgeführt markiert.");
        return "redirect:/auftraege/" + id;
    }

    // POST /auftraege/{id}/freigeben — Rapport freigeben
    @PostMapping("/{id}/freigeben")
    public String freigeben(@PathVariable Integer id, RedirectAttributes redirect) {
        auftragService.freigeben(id);
        redirect.addFlashAttribute("success", "Auftrag #" + id + " freigegeben.");
        return "redirect:/auftraege/" + id;
    }

    // POST /auftraege/{id}/verrechnet — Als verrechnet markieren
    @PostMapping("/{id}/verrechnet")
    public String verrechnet(@PathVariable Integer id, RedirectAttributes redirect) {
        auftragService.alsVerrechnetMarkieren(id);
        redirect.addFlashAttribute("success", "Auftrag #" + id + " als verrechnet markiert.");
        return "redirect:/auftraege/" + id;
    }

    // GET /auftraege/{id}/drucken — Druckansicht
    @GetMapping("/{id}/drucken")
    public String drucken(@PathVariable Integer id, Model model) {
        model.addAttribute("auftrag", auftragService.findById(id));
        model.addAttribute("rapporte", rapportService.findByAuftragId(id));
        model.addAttribute("rechnung", rechnungService.findByAuftragId(id).orElse(null));
        return "auftrag/drucken";
    }

    // Globale Fehlerbehandlung für ungültige Statusübergänge
    @ExceptionHandler(IllegalStateException.class)
    public String handleStatusFehler(IllegalStateException ex, RedirectAttributes redirect) {
        redirect.addFlashAttribute("error", ex.getMessage());
        return "redirect:/auftraege";
    }
}
