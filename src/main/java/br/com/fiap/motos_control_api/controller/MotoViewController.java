package br.com.fiap.motos_control_api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.fiap.motos_control_api.dto.MotoDTO;
import br.com.fiap.motos_control_api.model.Moto;
import br.com.fiap.motos_control_api.service.LocalizacaoService;
import br.com.fiap.motos_control_api.service.MotoService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/motos")
public class MotoViewController {

    @Autowired
    private MotoService motoService;

    @Autowired
    private LocalizacaoService localizacaoService;

    @GetMapping
    public String listMotos(Model model, @PageableDefault(size = 10) Pageable pageable) {
        model.addAttribute("motos", motoService.findAll("", pageable).getContent());
        return "motos";
    }

    // Método para exibir o formulário de uma nova moto
    @GetMapping("/new")
    public String showForm(Model model) {
        model.addAttribute("moto", new Moto());
        return "moto-form";
    }

    // Método para CRIAR uma nova moto
    @PostMapping("/save")
    public String createMoto(@Valid Moto moto, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "moto-form";
        }

        MotoDTO dto = new MotoDTO(moto.getIdentificador(), moto.getModelo(), moto.getPlaca(), moto.getLocalizacao());
        motoService.save(dto);
        redirectAttributes.addFlashAttribute("message", "Moto cadastrada com sucesso!");

        return "redirect:/motos";
    }

    // Método para ATUALIZAR uma moto existente
    @PostMapping("/update/{id}")
    public String updateMoto(@PathVariable Long id, @Valid Moto moto, BindingResult result,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "moto-form";
        }

        MotoDTO dto = new MotoDTO(moto.getIdentificador(), moto.getModelo(), moto.getPlaca(), moto.getLocalizacao());
        motoService.update(id, dto);
        redirectAttributes.addFlashAttribute("message", "Moto atualizada com sucesso!");

        return "redirect:/motos";
    }

    @GetMapping("/edit/{id}")
    public String editMoto(@PathVariable Long id, Model model) {
        model.addAttribute("moto", motoService.findById(id));
        return "moto-form";
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteMoto(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        motoService.delete(id);
        redirectAttributes.addFlashAttribute("message", "Moto removida com sucesso!");
        return "redirect:/motos";
    }

    // Método para exibir a página de associação
    @GetMapping("/{id}/associar")
    public String showAssociarForm(@PathVariable Long id, Model model) {
        model.addAttribute("moto", motoService.findById(id));
        model.addAttribute("localizacoesDisponiveis", localizacaoService.findAvailable());
        return "associar-localizacao";
    }

    // Método para processar a associação
    @PostMapping("/associar")
    public String associarLocalizacao(@RequestParam Long idMoto, @RequestParam Long idLocalizacao,
            RedirectAttributes redirectAttributes) {
        try {
            motoService.associarLocalizacao(idMoto, idLocalizacao);
            redirectAttributes.addFlashAttribute("message", "Moto associada com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao associar moto: " + e.getMessage());
        }
        return "redirect:/motos";
    }

    @PostMapping("/{id}/desassociar")
    public String desassociarLocalizacao(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            motoService.desassociarLocalizacao(id);
            redirectAttributes.addFlashAttribute("message", "Localização desassociada com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao desassociar: " + e.getMessage());
        }
        return "redirect:/motos";
    }

    // Endpoint para enviar a moto para manutenção
    @PostMapping("/{id}/manutencao/iniciar")
    @PreAuthorize("hasRole('ADMIN')")
    public String iniciarManutencao(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            motoService.enviarParaManutencao(id);
            redirectAttributes.addFlashAttribute("message", "Moto enviada para manutenção.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao enviar para manutenção: " + e.getMessage());
        }
        return "redirect:/motos";
    }

    // Endpoint para finalizar a manutenção da moto
    @PostMapping("/{id}/manutencao/finalizar")
    public String finalizarManutencao(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            motoService.finalizarManutencao(id);
            redirectAttributes.addFlashAttribute("message", "Manutenção finalizada. A moto está ativa novamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao finalizar manutenção: " + e.getMessage());
        }
        return "redirect:/motos";
    }
}