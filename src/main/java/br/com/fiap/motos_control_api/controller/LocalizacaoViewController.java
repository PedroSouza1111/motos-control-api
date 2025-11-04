package br.com.fiap.motos_control_api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.fiap.motos_control_api.dto.LocalizacaoDTO;
import br.com.fiap.motos_control_api.service.LocalizacaoService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/localizacoes")
public class LocalizacaoViewController {

    @Autowired
    private LocalizacaoService localizacaoService;

    @GetMapping
    public String listLocalizacoes(Model model, @PageableDefault(size = 10) Pageable pageable) {
        model.addAttribute("localizacoes", localizacaoService.findAll("", pageable).getContent());
        return "localizacoes";
    }

    // Método para exibir o formulário de nova localização
    @GetMapping("/new")
    public String showForm(Model model) {
        model.addAttribute("localizacao", new LocalizacaoDTO("", null));
        return "localizacao-form";
    }

    // Método para salvar a nova localização
    @PostMapping("/save")
    public String saveLocalizacao(@Valid LocalizacaoDTO localizacaoDTO, BindingResult result,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "localizacao-form";
        }
        localizacaoService.criar(localizacaoDTO);
        redirectAttributes.addFlashAttribute("message", "Localização cadastrada com sucesso!");
        return "redirect:/localizacoes";
    }

    // Método para deletar uma localização
    @PostMapping("/delete/{id}")
    public String deleteLocalizacao(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            localizacaoService.delete(id);
            redirectAttributes.addFlashAttribute("message", "Localização removida com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao remover: " + e.getMessage());
        }
        return "redirect:/localizacoes";
    }
}